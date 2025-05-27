import android.animation.ValueAnimator
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.Surface
import android.view.TextureView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.FilamentHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import com.google.android.filament.utils.KTX1Loader
import java.nio.ByteBuffer

class ModelTextureView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : TextureView(context, attrs),
        TextureView.SurfaceTextureListener {
        // UiHelper is provided by Filament to manage SurfaceView and SurfaceTexture
        private lateinit var uiHelper: UiHelper

        private var gltfAsset: FilamentAsset? = null

        // DisplayHelper is provided by Filament to manage the display
        private var displayHelper: DisplayHelper = DisplayHelper(context)

        // Choreographer is used to schedule new frames
        private var choreographer: Choreographer = Choreographer.getInstance()

        // Engine creates and destroys Filament resources
        // Each engine must be accessed from a single thread of your choosing
        // Resources cannot be shared across engines
        private lateinit var engine: Engine

        // A renderer instance is tied to a single surface (SurfaceView, TextureView, etc.)
        private lateinit var renderer: Renderer

        // A scene holds all the renderable, lights, etc. to be drawn
        private lateinit var scene: Scene

        // A view defines a viewport, a scene and a camera for rendering
        private lateinit var view: View

        // Should be pretty obvious :)
        private lateinit var camera: Camera

        private lateinit var material: Material
        private lateinit var vertexBuffer: VertexBuffer
        private lateinit var indexBuffer: IndexBuffer
        lateinit var materialProvider: UbershaderProvider
        lateinit var assetLoader: AssetLoader
        lateinit var resourceLoader: ResourceLoader

        // Filament entity representing a renderable object
        @Entity
        private var renderable = 0

        // A swap chain is Filament's representation of a surface
        private var swapChain: SwapChain? = null

        // Performs the rendering and schedules new frames
        private val frameScheduler = FrameCallback()

        private val animator = ValueAnimator.ofFloat(0.0f, 360.0f)

        inner class FrameCallback : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                // Schedule the next frame
                choreographer.postFrameCallback(this)

                // This check guarantees that we have a swap chain
                if (uiHelper.isReadyToRender) {
                    // If beginFrame() returns false you should skip the frame
                    // This means you are sending frames too quickly to the GPU
                    if (renderer.beginFrame(swapChain!!, frameTimeNanos)) {
                        renderer.render(view)
                        renderer.endFrame()
                    }
                }
            }
        }

        inner class SurfaceCallback : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                swapChain?.let { engine.destroySwapChain(it) }
                swapChain = engine.createSwapChain(surface, uiHelper.swapChainFlags)
                displayHelper.attach(renderer, this@ModelTextureView.display)
            }

            override fun onDetachedFromSurface() {
                displayHelper.detach()
                swapChain?.let {
                    engine.destroySwapChain(it)
                    // Required to ensure we don't return before Filament is done executing the
                    // destroySwapChain command, otherwise Android might destroy the Surface
                    // too early
                    engine.flushAndWait()
                    swapChain = null
                }
            }

            override fun onResized(
                width: Int,
                height: Int,
            ) {
                val zoom = 1.5
                val aspect = width.toDouble() / height.toDouble()
                camera.setProjection(
                    Camera.Projection.ORTHO,
                    -aspect * zoom,
                    aspect * zoom,
                    -zoom,
                    zoom,
                    0.0,
                    10.0,
                )

                view.viewport = Viewport(0, 0, width, height)

                FilamentHelper.synchronizePendingFrames(engine)
            }
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()

            surfaceTextureListener = this

            if (isAvailable) {
                surfaceTexture?.let {
                    onSurfaceTextureAvailable(it, width, height)
                }
            }
        }

        override fun onSurfaceTextureAvailable(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int,
        ) {
            setupFilament()
            setupSurfaceView()
            setupView()
            setupScene()
            choreographer.postFrameCallback(frameScheduler)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int,
        ) {
            // view.viewport = Viewport(0, 0, width, height)
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            choreographer.removeFrameCallback(frameScheduler)
            animator.cancel()

            // Always detach the surface before destroying the engine
            uiHelper.detach()

            // Cleanup all resources
            engine.destroyEntity(renderable)
            engine.destroyRenderer(renderer)
            engine.destroyVertexBuffer(vertexBuffer)
            engine.destroyIndexBuffer(indexBuffer)
            engine.destroyMaterial(material)
            engine.destroyView(view)
            engine.destroyScene(scene)
            engine.destroyCameraComponent(camera.entity)

            // Engine.destroyEntity() destroys Filament related resources only
            // (components), not the entity itself
            val entityManager = EntityManager.get()
            entityManager.destroy(renderable)
            entityManager.destroy(camera.entity)

            // Destroying the engine will free up any resource you may have forgotten
            // to destroy, but it's recommended to do the cleanup properly
            engine.destroy()
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        private fun setupSurfaceView() {
            uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
            uiHelper.renderCallback = SurfaceCallback()

            // NOTE: To choose a specific rendering resolution, add the following line:
            uiHelper.setDesiredSize(1280, 720)
            uiHelper.attachTo(this)
        }

        private fun setupFilament() {
            engine = Engine.create()
            renderer = engine.createRenderer()
            scene = engine.createScene()
            view = engine.createView()
            camera = engine.createCamera(engine.entityManager.create())
            val entityManager = EntityManager.get()
            materialProvider = UbershaderProvider(engine)
            assetLoader = AssetLoader(engine, materialProvider, entityManager)
            resourceLoader = ResourceLoader(engine)
        }

        private fun setupView() {
            scene.skybox = Skybox.Builder().color(0.035f, 0.035f, 0.035f, 1.0f).build(engine)

            // NOTE: Try to disable post-processing (tone-mapping, etc.) to see the difference
            // view.isPostProcessingEnabled = false

            // Tell the view which camera we want to use
            view.camera = camera

            // Tell the view which scene we want to render
            view.scene = scene
        }

        private fun setupScene() {
            //   setupIndirectLight()
            createIndirectLight()
            createDefaultRenderables()
        }

        private fun createIndirectLight() {
            val ibl = "default_env"
            readCompressedAsset("envs/$ibl/${ibl}_ibl.ktx").let {
                val indirectLight = KTX1Loader.createIndirectLight(engine, it)
                scene.indirectLight = indirectLight
            }
            readCompressedAsset("envs/$ibl/${ibl}_skybox.ktx").let {
                val skybox = KTX1Loader.createSkybox(engine, it)
                scene.skybox = skybox
            }
        }

        private fun setupIndirectLight() {
            // Tạo ánh sáng giả lập cơ bản
            val ibl =
                IndirectLight
                    .Builder()
                    .reflections(Texture.Builder().build(engine)) // dummy texture
                    .intensity(30_000.0f)
                    .build(engine)

            scene.indirectLight = ibl
        }

        private fun readCompressedAsset(assetName: String): ByteBuffer {
            val input = context.assets.open(assetName)
            val bytes = ByteArray(input.available())
            input.read(bytes)
            return ByteBuffer.wrap(bytes)
        }

        private fun createDefaultRenderables() {
            val buffer =
                context.assets.open("models/sonic_head.glb").use { input ->
                    val bytes = ByteArray(input.available())
                    input.read(bytes)
                    ByteBuffer.wrap(bytes)
                }

            gltfAsset = assetLoader.createAsset(buffer)
            gltfAsset?.let { asset ->
                resourceLoader.loadResources(asset)
                scene.addEntities(asset.entities)
            } ?: run {
                Log.e("Filament", "Failed to load asset")
            }
        }
    }
