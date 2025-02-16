package com.example.mviimageeditor.ui.detail.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.mviimageeditor.R
import com.example.mviimageeditor.ui.detail.DetailContract
import com.example.mviimageeditor.ui.detail.EditState
import com.example.mviimageeditor.ui.theme.GrayE0

@Suppress("ktlint:standard:function-naming")
@Composable
fun ItemOption(
    icon: Int,
    color: Color = Color.Transparent,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = {
            onClick.invoke()
        },
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier =
                Modifier.size(36.dp),
            tint = color,
        )
    }
}

@Composable
fun OptionView(
    state: DetailContract.State,
    event: (DetailContract.Event) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .zIndex(2f),
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .animateContentSize()
                    .padding(end = 8.dp, top = 32.dp),
        ) {
            if (state.editState != EditState.DONE && state.editState != EditState.NONE) {
                Text(
                    text = "DONE",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier =
                        Modifier.clickable {
                            event.invoke(
                                DetailContract.Event.OnChangeEditState(EditState.DONE),
                            )
                        },
                )
            }
            IconButton(
                onClick = { event.invoke(DetailContract.Event.OnChangeEditState(EditState.DRAW)) },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "draw",
                    modifier = Modifier.size(40.dp),
                    tint = GrayE0,
                )
            }

            ItemOption(icon = R.drawable.ic_eraser, color = Color.White) {
                event.invoke(
                    DetailContract.Event.OnChangeEditState(
                        EditState.ERASER,
                    ),
                )
            }

            if (state.editState == EditState.NONE || state.editState == EditState.DONE) {
                ItemOption(icon = R.drawable.icon_crop, color = Color.White) {
                    event.invoke(
                        DetailContract.Event.OnChangeEditState(
                            EditState.CROP,
                        ),
                    )
                }
                ItemOption(icon = R.drawable.icon_filter, color = Color.White) {
                    event.invoke(
                        DetailContract.Event.OnChangeEditState(
                            EditState.FILTER,
                        ),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(4.dp))
        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            if (state.editState == EditState.DRAW) {
                state.colorList.forEachIndexed { index, color ->
                    RadioButton(
                        selected = state.selectedColor == color,
                        onClick = { event.invoke(DetailContract.Event.SelectColor(color)) },
                        modifier =
                            Modifier
                                .background(Color.Transparent)
                                .padding(10.dp),
                        colors =
                            RadioButtonDefaults.colors(
                                selectedColor = color,
                                unselectedColor = color,
                            ),
                    )
                }
            }
        }
    }
}
