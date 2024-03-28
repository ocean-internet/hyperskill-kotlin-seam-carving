package seamcarving

import java.awt.Color

class ColorPixel(x: Int, y: Int, private val _color:Color) : Pixel(x, y) {

    val color: Color = Color(_color.red, _color.green, _color.blue)
}