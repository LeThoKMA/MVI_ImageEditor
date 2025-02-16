import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorMatrixColorFilter

object Utils {
    // fun setSpanForString(
//    text: String,
//    drawable: Drawable,
// ): SpannableString {
//    val spannableString = SpannableString(text)
//    spannableString.setSpan(
//        DrawableMarginSpan(drawable, 20),
//        0,
//        spannableString.length,
//        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
//    )
//    return spannableString
// }
//
// fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
//    val buffer = image.planes[0].buffer
//    val bytes = ByteArray(buffer.remaining())
//    buffer.get(bytes)
//
//    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
// }
//
    fun colorFilterList(): List<ColorFilter> {
        val colorFilters = mutableListOf<ColorFilter>()

// Tạo ColorMatrix cho mỗi màu cơ bản và tạo ColorFilter từ ColorMatrix
// Màu đen
        val blackMatrix =
            ColorMatrix().apply {
                setToSaturation(0f) // Vô hiệu hóa sắc tố
            }
        colorFilters.add(ColorMatrixColorFilter(blackMatrix))

// Màu đỏ
        val redMatrix =
            ColorMatrix().apply {
                setToScale(1f, 0f, 0f, 1f) // Chỉ giữ màu đỏ
            }
        colorFilters.add(ColorMatrixColorFilter(redMatrix))

// Màu xanh lá cây
        val greenMatrix =
            ColorMatrix().apply {
                setToScale(0f, 1f, 0f, 1f) // Chỉ giữ màu xanh lá cây
            }
        colorFilters.add(ColorMatrixColorFilter(greenMatrix))

// Màu xanh dương
        val blueMatrix =
            ColorMatrix().apply {
                setToScale(0f, 0f, 1f, 1f) // Chỉ giữ màu xanh dương
            }
        colorFilters.add(ColorMatrixColorFilter(blueMatrix))

// Màu trắng
        val whiteMatrix =
            ColorMatrix().apply {
                setToSaturation(0f) // Vô hiệu hóa sắc tố
                setToScale(1f, 1f, 1f, 1f) // Tất cả thành phần màu giữ nguyên
            }
        colorFilters.add(ColorMatrixColorFilter(whiteMatrix))

// Màu mờ
        val grayscaleMatrix =
            ColorMatrix().apply {
                setToSaturation(0f) // Vô hiệu hóa sắc tố
                setToScale(0.33f, 0.33f, 0.33f, 1f) // Biến đổi thành màu xám
            }
        colorFilters.add(ColorMatrixColorFilter(grayscaleMatrix))

// Màu âm bản (negative)
        val invertMatrix =
            ColorMatrix(
                floatArrayOf(
                    -1f,
                    0f,
                    0f,
                    0f,
                    255f,
                    0f,
                    -1f,
                    0f,
                    0f,
                    255f,
                    0f,
                    0f,
                    -1f,
                    0f,
                    255f,
                    0f,
                    0f,
                    0f,
                    1f,
                    0f,
                ),
            )
        colorFilters.add(ColorMatrixColorFilter(invertMatrix))
        return colorFilters
    }

    fun invertColor(color: Color): Color =
        Color(
            red = 1f - color.red,
            green = 1f - color.green,
            blue = 1f - color.blue,
            alpha = color.alpha, // Giữ nguyên độ trong suốt
        )
//
// fun emojiToDrawable(
//    emoji: String,
//    context: Context,
// ): Drawable {
//    val processedEmoji = EmojiCompat.get().process(emoji)
//
//    // Tạo TextView ẩn để hiển thị emoji
//    val textView = TextView(context)
//    textView.text = processedEmoji
//
//    // Đảm bảo TextView có kích thước đủ lớn để hiển thị emoji
//    textView.measure(
//        View.MeasureSpec.UNSPECIFIED,
//        View.MeasureSpec.UNSPECIFIED,
//    )
//    textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
//
//    // Chụp TextView vào một Bitmap
//    val bitmap =
//        Bitmap.createBitmap(
//            textView.measuredWidth,
//            textView.measuredHeight,
//            Bitmap.Config.ARGB_8888,
//        )
//    val canvas = Canvas(bitmap)
//    textView.draw(canvas)
//
//    // Tạo Drawable từ Bitmap
//
//    return BitmapDrawable(context.resources, bitmap)
// }
//
// fun getEmojiDrawable(
//    emoji: ImageView,
//    imageView: ImageView,
// ) {
//    val bitmap = imageView.drawToBitmap()
//
// // Tạo một Canvas từ Bitmap
//    val canvas = Canvas(bitmap)
//
// // Vẽ Drawable hoặc Bitmap lên Canvas tại vị trí mong muốn
//    val drawable = emoji.drawToBitmap().toDrawable(emoji.resources)
//    val x = emoji.x.toInt() // Tọa độ X mong muốn
//    val y = emoji.y.toInt() // Tọa độ Y mong muốn
//    drawable.setBounds(x, y, x + drawable.intrinsicWidth, y + drawable.intrinsicHeight)
//    drawable.draw(canvas)
//
// // Đặt Bitmap này làm Drawable cho ImageView
//    imageView.setImageDrawable(BitmapDrawable(imageView.resources, bitmap))
// }
//
// fun Float.dpToPx(context: Context): Int {
//    return (this * context.resources.displayMetrics.density).toInt()
// }
}
