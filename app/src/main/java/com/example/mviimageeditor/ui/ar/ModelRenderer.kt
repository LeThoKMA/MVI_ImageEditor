package com.example.mviimageeditor.ui.ar

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Matrix
import android.util.AttributeSet
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.filament.Fence
import com.google.android.filament.Material
import com.google.android.filament.Renderer
import com.google.android.filament.View
import com.google.android.filament.android.UiHelper
import com.google.android.filament.utils.AutomationEngine
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer

@SuppressLint("ClickableViewAccessibility")
class ModelViewerView(
    context: Context,
    attrs: AttributeSet? = null,
) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {
    private var loadStartTime = 0L
    private var loadStartFence: Fence? = null
    private val viewerContent = AutomationEngine.ViewerContent()

    // Create model viewer
    private val modelViewer: ModelViewer =
        ModelViewer(
            this,
            // make background is transparent (1)
            uiHelper =
                UiHelper().apply {
                    isOpaque = false
                    isMediaOverlay = true
                },
        )

    private val choreographer = Choreographer.getInstance()
    private val frameCallback = FrameCallback()

    init {
        setZOrderOnTop(true)
        holder.addCallback(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        modelViewer.let {
            viewerContent.view = modelViewer.view
            viewerContent.sunlight = modelViewer.light
            viewerContent.lightManager = modelViewer.engine.lightManager
            viewerContent.scene = modelViewer.scene
            viewerContent.renderer = modelViewer.renderer
            this@ModelViewerView.setOnTouchListener { view, event ->
                modelViewer.onTouchEvent(event)
                true
            }
            createDefaultRenderables()

            // make background is transparent (2)
            modelViewer.view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT
            modelViewer.scene.skybox = null
            //    createIndirectLight()
            val view = modelViewer.view
            // on mobile, better use lower quality color buffer
            view.renderQuality =
                view.renderQuality.apply {
                    hdrColorBuffer = View.QualityLevel.MEDIUM
                }

            // dynamic resolution often helps a lot
            view.dynamicResolutionOptions =
                view.dynamicResolutionOptions.apply {
                    enabled = true
                    quality = View.QualityLevel.MEDIUM
                }

            // MSAA is needed with dynamic resolution MEDIUM
            view.multiSampleAntiAliasingOptions =
                view.multiSampleAntiAliasingOptions.apply {
                    enabled = true
                }

            // FXAA is pretty cheap and helps a lot
            view.antiAliasing = View.AntiAliasing.FXAA

            // ambient occlusion is the cheapest effect that adds a lot of quality
            view.ambientOcclusionOptions =
                view.ambientOcclusionOptions.apply {
                    enabled = true
                }

            // bloom is pretty expensive but adds a fair amount of realism
            view.bloomOptions =
                view.bloomOptions.apply {
                    enabled = true
                }

            // Start render loop
            choreographer.postFrameCallback(frameCallback)
            modelViewer.renderer.clearOptions =
                Renderer.ClearOptions().apply {
                    clear = true
                    clearColor = floatArrayOf(0f, 0f, 0f, 0f)
                }
        }
    }

    override fun surfaceChanged(
        p0: SurfaceHolder,
        p1: Int,
        p2: Int,
        p3: Int,
    ) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        choreographer.removeFrameCallback(frameCallback)
        modelViewer.destroyModel()
    }

    fun setRotation(
        x: Float,
        y: Float,
        z: Float,
    ) {
        val entity = modelViewer.asset?.root ?: return

        // Khởi tạo ma trận danh tính
        val transform = FloatArray(16)
        Matrix.setIdentityM(transform, 0)

        // Áp dụng các xoay
        Matrix.rotateM(transform, 0, y, 0f, 1f, 0f) // quay mặt trái/phải
        Matrix.rotateM(transform, 0, x, 1f, 0f, 0f) // gật đầu
        Matrix.rotateM(transform, 0, z, 0f, 0f, 1f) // nghiêng đầu

        val tm = modelViewer.engine.transformManager
        val ti = tm.getInstance(entity)
        tm.setTransform(ti, transform)
    }

    private fun createDefaultRenderables() {
        val buffer =
            context.assets.open("models/sonic_head.glb").use { input ->
                val bytes = ByteArray(input.available())
                input.read(bytes)
                ByteBuffer.wrap(bytes)
            }

        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()
        loadStartFence = modelViewer.engine.createFence()
    }

    // indirectLight from ibl and create skybox
    private fun createIndirectLight() {
        val engine = modelViewer.engine
        val scene = modelViewer.scene
        val ibl = "default_env"
        readCompressedAsset("envs/$ibl/${ibl}_ibl.ktx").let {
            val indirectLight = KTX1Loader.createIndirectLight(engine, it)
            scene.indirectLight = indirectLight
//            modelViewer.indirectLightCubemap = bundle.cubemap
            scene.indirectLight!!.intensity = 30_000.0f
            viewerContent.indirectLight = modelViewer.scene.indirectLight
        }
        readCompressedAsset("envs/$ibl/${ibl}_skybox.ktx").let {
            val skybox = KTX1Loader.createSkybox(engine, it)
            scene.skybox = skybox
        }
    }

    private fun readCompressedAsset(assetName: String): ByteBuffer {
        val input = context.assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    inner class FrameCallback : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()

        override fun doFrame(frameTimeNanos: Long) {
            choreographer.postFrameCallback(this)
            loadStartFence?.let {
                if (it.wait(Fence.Mode.FLUSH, 0) == Fence.FenceStatus.CONDITION_SATISFIED) {
                    val end = System.nanoTime()
                    val total = (end - loadStartTime) / 1_000_000
//                    Log.i(TAG, "The Filament backend took $total ms to load the model geometry.")
                    modelViewer.engine.destroyFence(it)
                    loadStartFence = null

                    val materials = mutableSetOf<Material>()
                    val rcm = modelViewer.engine.renderableManager
                    modelViewer.scene.forEach {
                        val entity = it
                        if (rcm.hasComponent(entity)) {
                            val ri = rcm.getInstance(entity)
                            val c = rcm.getPrimitiveCount(ri)
                            for (i in 0 until c) {
                                val mi = rcm.getMaterialInstanceAt(ri, i)
                                val ma = mi.material
                                materials.add(ma)
                            }
                        }
                    }
                    materials.forEach {
                        it.compile(
                            Material.CompilerPriorityQueue.HIGH,
                            Material.UserVariantFilterBit.DIRECTIONAL_LIGHTING or
                                Material.UserVariantFilterBit.DYNAMIC_LIGHTING or
                                Material.UserVariantFilterBit.SHADOW_RECEIVER,
                            null,
                            null,
                        )
                        it.compile(
                            Material.CompilerPriorityQueue.LOW,
                            Material.UserVariantFilterBit.FOG or
                                Material.UserVariantFilterBit.SKINNING or
                                Material.UserVariantFilterBit.SSR or
                                Material.UserVariantFilterBit.VSM,
                            null,
                            null,
                        )
                    }
                }
            }

            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    val elapsedTimeSeconds = (frameTimeNanos - startTime).toDouble() / 1_000_000_000
                    applyAnimation(0, elapsedTimeSeconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewer.render(frameTimeNanos)

//            // Check if a new download is in progress. If so, let the user know with toast.
//            val currentDownload = remoteServer?.peekIncomingLabel()
//            if (RemoteServer.isBinary(currentDownload) && currentDownload != latestDownload) {
//                latestDownload = currentDownload
//                Log.i(TAG, "Downloading $currentDownload")
//                setStatusText("Downloading $currentDownload")
//            }
//
//            // Check if a new message has been fully received from the client.
//            val message = remoteServer?.acquireReceivedMessage()
//            if (message != null) {
//                if (message.label == latestDownload) {
//                    latestDownload = null
//                }
//                if (RemoteServer.isJson(message.label)) {
//                    loadSettings(message)
//                } else {
//                    loadModelData(message)
//                }
//            }
        }
    }
}
