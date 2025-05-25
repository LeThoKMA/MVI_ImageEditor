package com.example.mviimageeditor.ui.ar

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.Matrix
import android.util.AttributeSet
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import com.google.android.filament.utils.KTX1Loader
import java.nio.ByteBuffer

/**
 * FilamentOverlayView - A transparent SurfaceView that renders 3D models on top of CameraX PreviewView
 * This approach avoids conflicts with CameraX's surface management
 */
class FilamentOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private lateinit var engine: Engine
    private lateinit var renderer: Renderer
    private lateinit var view: View
    private lateinit var scene: Scene
    private lateinit var camera: Camera
    private lateinit var swapChain: SwapChain
    private lateinit var uiHelper: UiHelper

    private lateinit var assetLoader: AssetLoader
    private lateinit var resourceLoader: ResourceLoader
    private var filamentAsset: FilamentAsset? = null

    private val choreographer = Choreographer.getInstance()
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (::renderer.isInitialized && ::swapChain.isInitialized && ::view.isInitialized) {
                try {
                    if (renderer.beginFrame(swapChain, frameTimeNanos)) {
                        renderer.render(view)
                        renderer.endFrame()
                    }
                } catch (e: Exception) {
                    // Handle rendering errors gracefully
                }
                choreographer.postFrameCallback(this)
            }
        }
    }

    init {
        // Make this SurfaceView transparent and on top
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Surface created, but wait for surfaceChanged to get dimensions
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        setupFilament(width, height)
        loadGlbFromAssets()
        choreographer.postFrameCallback(frameCallback)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        choreographer.removeFrameCallback(frameCallback)
        cleanupFilament()
    }

    private fun setupFilament(width: Int, height: Int) {
        try {
            engine = Engine.create()
            renderer = engine.createRenderer()
            view = engine.createView()
            scene = engine.createScene()
            camera = engine.createCamera(EntityManager.get().create())

            // Configure UiHelper for transparent overlay
            uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
                isOpaque = false // Important: transparent background
                renderCallback = object : UiHelper.RendererCallback {
                    override fun onNativeWindowChanged(surface: android.view.Surface) {
                        swapChain = engine.createSwapChain(surface)
                    }

                    override fun onDetachedFromSurface() {
                        if (::swapChain.isInitialized) {
                            engine.destroySwapChain(swapChain)
                        }
                    }

                    override fun onResized(width: Int, height: Int) {
                        view.viewport = Viewport(0, 0, width, height)
                        setupCamera(width, height)
                    }
                }
            }

            uiHelper.attachTo(this)

            view.scene = scene
            view.camera = camera
            view.viewport = Viewport(0, 0, width, height)

            setupCamera(width, height)
            setupLighting()
            createIndirectLight()

            val materialProvider = UbershaderProvider(engine)
            assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
            resourceLoader = ResourceLoader(engine)

        } catch (e: Exception) {
            android.util.Log.e("FilamentOverlay", "Error setting up Filament", e)
        }
    }

    private fun setupCamera(width: Int, height: Int) {
        val aspect = width.toDouble() / height.toDouble()
        camera.setProjection(45.0, aspect, 0.1, 20.0, Camera.Fov.VERTICAL)

        val eye = doubleArrayOf(0.0, 0.0, 4.0)
        val center = doubleArrayOf(0.0, 0.0, 0.0)
        val up = doubleArrayOf(0.0, 1.0, 0.0)
        camera.lookAt(eye[0], eye[1], eye[2], center[0], center[1], center[2], up[0], up[1], up[2])
    }

    private fun setupLighting() {
        val lightEntity = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 1.0f, 1.0f)
            .intensity(10000.0f)
            .direction(0.0f, -1.0f, 0.0f)
            .build(engine, lightEntity)
        scene.addEntity(lightEntity)
    }

    private fun createIndirectLight() {
        try {
            val iblAsset = context.assets.open("envs/venetian_crossroads_2k.ktx")
            val iblBuffer = ByteArray(iblAsset.available())
            iblAsset.read(iblBuffer)
            iblAsset.close()

            val indirectLight = KTX1Loader.createIndirectLight(engine, ByteBuffer.wrap(iblBuffer))
            indirectLight.intensity = 30000.0f
            scene.indirectLight = indirectLight
        } catch (e: Exception) {
            android.util.Log.w("FilamentOverlay", "Could not load IBL, using default lighting")
        }
    }

    private fun loadGlbFromAssets() {
        try {
            val glbAsset = context.assets.open("models/DamagedHelmet.glb")
            val glbBuffer = ByteArray(glbAsset.available())
            glbAsset.read(glbBuffer)
            glbAsset.close()

            filamentAsset = assetLoader.createAsset(ByteBuffer.wrap(glbBuffer))
            filamentAsset?.let { asset ->
                resourceLoader.loadResources(asset)
                scene.addEntities(asset.entities)

                // Scale and position the model
                val transform = engine.transformManager
                val rootEntity = asset.root
                val transformInstance = transform.getInstance(rootEntity)
                val matrix = FloatArray(16)
                Matrix.setIdentityM(matrix, 0)
                Matrix.scaleM(matrix, 0, 0.5f, 0.5f, 0.5f)
                transform.setTransform(transformInstance, matrix)
            }
        } catch (e: Exception) {
            android.util.Log.e("FilamentOverlay", "Error loading GLB model", e)
        }
    }

    private fun cleanupFilament() {
        try {
            filamentAsset?.let {
                scene.removeEntities(it.entities)
                assetLoader.destroyAsset(it)
            }
            
            if (::uiHelper.isInitialized) {
                uiHelper.detach()
            }
            
            if (::engine.isInitialized) {
                engine.destroy()
            }
        } catch (e: Exception) {
            android.util.Log.e("FilamentOverlay", "Error cleaning up Filament", e)
        }
    }

    fun setRotation(headEulerAngleX: Float, headEulerAngleY: Float, headEulerAngleZ: Float) {
        filamentAsset?.let { asset ->
            val transform = engine.transformManager
            val rootEntity = asset.root
            val transformInstance = transform.getInstance(rootEntity)
            val matrix = FloatArray(16)
            Matrix.setIdentityM(matrix, 0)
            Matrix.scaleM(matrix, 0, 0.5f, 0.5f, 0.5f)
            Matrix.rotateM(matrix, 0, headEulerAngleX, 1.0f, 0.0f, 0.0f)
            Matrix.rotateM(matrix, 0, headEulerAngleY, 0.0f, 1.0f, 0.0f)
            Matrix.rotateM(matrix, 0, headEulerAngleZ, 0.0f, 0.0f, 1.0f)
            transform.setTransform(transformInstance, matrix)
        }
    }
} 