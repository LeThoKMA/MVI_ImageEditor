package com.example.mviimageeditor.ui.ar

import android.content.Context
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.SwapChain
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import java.nio.ByteBuffer

class ImageFilamentFragment(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private lateinit var engine: Engine
    private lateinit var renderer: Renderer
    private lateinit var scene: Scene
    private lateinit var view: com.google.android.filament.View
    private lateinit var swapChain: SwapChain
    val modelViewer = ModelViewer(this)

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        engine = Engine.create()
        renderer = engine.createRenderer()
        swapChain = engine.createSwapChain(holder.surface)
        scene = engine.createScene()
        view = engine.createView()
        view.scene = scene

        // Load GLB model
        val assetLoader = AssetLoader(engine, UbershaderProvider(engine), EntityManager.get())
        val resourceLoader = ResourceLoader(engine)
        val glb = context.assets.open("models/little_cartoon_dog.glb").use {
            assetLoader.createAsset(ByteBuffer.wrap(it.readBytes()))
        }
        glb?.let {
//            resourceLoader.loadResources(it)
            resourceLoader.loadResources(it) // Load resources
            it.releaseSourceData() // Giải phóng dữ liệu nguồn sau khi load xong
            scene.addEntities(it.entities) // Thêm tất cả các entity của mô hình vào scene
        }
//        glb?.root?.let { scene.addEntity(it) }

        // Render loop
        Choreographer.getInstance().postFrameCallback { renderFrame() }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    private fun renderFrame() {
        Choreographer.getInstance().postFrameCallback { frameTimeNanos ->
            if (renderer.beginFrame(swapChain, frameTimeNanos)) {
                renderer.render(view)
                renderer.endFrame()
            }
            Choreographer.getInstance().postFrameCallback { renderFrame() }
        }
//        if (renderer.beginFrame(swapChain)) {
//            renderer.render(view)
//            renderer.endFrame()
//        }
//        Choreographer.getInstance().postFrameCallback { renderFrame() }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        engine.destroy()
    }
}


@Composable
fun FilamentView(modifier: Modifier) {
    AndroidView(modifier = modifier, factory = { context ->
        ImageFilamentFragment(context)
    })
}


//@Composable
//fun ARScreen(modifier: Modifier) {
//    AndroidFragment<ImageFilterFragment>(modifier = modifier)
//}
