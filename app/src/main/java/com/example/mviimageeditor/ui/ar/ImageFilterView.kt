package com.example.mviimageeditor.ui.ar

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ImageFilterView : ArFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        load3DModel()
        return view
    }

    private fun load3DModel() {
        val modelUri = Uri.parse("models/bunny.glb") // Đường dẫn tới file .glb

        ModelRenderable
            .builder()
            .setSource(context, modelUri)
//            .setIsFilamentGltf(true) // Quan trọng với file .glb
            .build()
            .thenAccept { renderable ->
                placeModel(renderable)
            }.exceptionally {
                Log.e("AR", "Lỗi tải mô hình: ${it.message}")
                null
            }
    }

    private fun placeModel(renderable: ModelRenderable) {
        val anchor = arSceneView.session?.createAnchor(Pose.makeTranslation(0f, 0f, -1f))
        anchor?.let {
            val anchorNode = AnchorNode(it)
            anchorNode.setParent(arSceneView.scene)

            val modelNode = TransformableNode(transformationSystem)
            modelNode.renderable = renderable
            modelNode.setParent(anchorNode)

            modelNode.select() // Cho phép kéo/thả mô hình
        }
    }
}
