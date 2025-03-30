//package com.example.mviimageeditor.ui.ar
//
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.fragment.compose.AndroidFragment
//import com.google.ar.core.Pose
//import com.google.ar.sceneform.AnchorNode
//import com.google.ar.sceneform.assets.RenderableSource
//import com.google.ar.sceneform.rendering.ModelRenderable
//import com.google.ar.sceneform.ux.ArFragment
//import com.google.ar.sceneform.ux.TransformableNode
//import java.io.File
//
//class ImageFilterFragment : ArFragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View? {
//        val rootView = super.onCreateView(inflater, container, savedInstanceState)
//        load3DModel()
//        return rootView
//    }
//
//    private fun load3DModel() {
//        ModelRenderable
//            .builder()
//            .setSource(
//                requireContext(),
//                RenderableSource.builder()
//                    .setSource(
//                        requireContext(),
//                        Uri.parse("models/sonic_head.glb"),
//                        RenderableSource.SourceType.GLB
//                    )
//                    .setScale(0.25f).setRecenterMode(RenderableSource.RecenterMode.ROOT).build()
//            ).setRegistryId("models/sonic_head.glb")
////            .setIsFilamentGltf(true) // Quan trọng với file .glb
//            .build()
//            .thenAccept { renderable ->
//                placeModel(renderable)
//            }.exceptionally {
//                Log.e("AR", "Lỗi tải mô hình: ${it.message}")
//                null
//            }
//    }
//
//    private fun placeModel(renderable: ModelRenderable) {
//        val anchor = arSceneView.session?.createAnchor(Pose.makeTranslation(0f, 0f, -1f))
//        anchor?.let {
//            val anchorNode = AnchorNode(it)
//            anchorNode.setParent(arSceneView.scene)
//
//            val modelNode = TransformableNode(transformationSystem)
//            modelNode.renderable = renderable
//            modelNode.setParent(anchorNode)
//
//            modelNode.select() // Cho phép kéo/thả mô hình
//        }
//    }
//
//    fun copyAssetToCache(): File {
//        val file = File("src/main/assets/models/sonic_head.gbl")
//        return file
//    }
//}
//
//@Composable
//fun ARScreen(modifier: Modifier) {
//    AndroidFragment<ImageFilterFragment>(modifier = modifier)
//}
