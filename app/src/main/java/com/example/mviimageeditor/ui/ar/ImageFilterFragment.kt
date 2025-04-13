import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mviimageeditor.R

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

@Composable
fun Test() {
    var totalHeight by remember { mutableIntStateOf(0) }
    var textHeight by remember { mutableIntStateOf(0) }
    val minContentHeight = 200
    val minContentHeightPx = with(LocalDensity.current) { minContentHeight.dp.toPx().toInt() }
    val listState = rememberLazyListState()
    val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    Column(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned { totalHeight = it.size.height }) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Text(
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    modifier = Modifier.onGloballyPositioned {
                        textHeight = it.size.height
                    })
            }
            item {
                Column(
                    modifier = Modifier.then(
                        if (totalHeight - textHeight < minContentHeight) {
                            Modifier.height(minContentHeight.dp)
                        } else {
                            Modifier.weight(1f)
                        }
                    )
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                }
            }

        }
        Button(modifier = Modifier
            .fillMaxWidth()
            .height(52.dp), onClick = {
        }) {
            Text(text = "Button")
        }
    }
}

@Preview(name = "Small screen", widthDp = 320, heightDp = 480)
@Composable
fun TestPreview() {
    Test()
}
