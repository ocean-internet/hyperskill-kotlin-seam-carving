package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class EnergyImage(private val _image: BufferedImage) {

    val energyPixels: List<List<EnergyPixel>> =
        (0 until _image.width).map { x ->
            (0 until _image.height).map { y ->
                val energy = getEnergy(Pixel(x, y))
                maxEnergy = max(maxEnergy, energy)
                EnergyPixel(x, y, energy)
            }
        }

    val width
        get() = _image.width
    val height
        get() = _image.height

    private var maxEnergy = 0.0

    fun toBufferedImage(): BufferedImage {
        val bufferedImage = BufferedImage(_image.width, _image.height, TYPE_INT_RGB)

        (0 until bufferedImage.width).forEach { x ->
            (0 until bufferedImage.height).forEach { y ->
                val intensity = getEnergyIntensity(Pixel(x, y))
                bufferedImage.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
            }
        }

        return bufferedImage
    }

    fun getEnergy(pixel: Pixel): Double {
        val pixels = getEnergyPixels(pixel)
        fun getFirstPixel(axis: Char) = pixels[axis]?.first()?.color
        fun getLastPixel(axis: Char) = pixels[axis]?.last()?.color

        val firstX = getFirstPixel('x')
        val lastX = getLastPixel('x')
        val xGradient = listOf(
            ((firstX?.red ?: 255) - (lastX?.red ?: 255)),
            ((firstX?.green ?: 255) - (lastX?.green ?: 255)),
            ((firstX?.blue ?: 255) - (lastX?.blue ?: 255)),
        )
            .map { it.toDouble().pow(2) }
            .sumOf { it }

        val firstY = getFirstPixel('y')
        val lastY = getLastPixel('y')
        val yGradient = listOf(
            ((firstY?.red ?: 255) - (lastY?.red ?: 255)),
            ((firstY?.green ?: 255) - (lastY?.green ?: 255)),
            ((firstY?.blue ?: 255) - (lastY?.blue ?: 255)),
        )
            .map { it.toDouble().pow(2) }
            .sumOf { it }

        return sqrt((xGradient + yGradient))
    }

    fun getEnergyPixels(target: Pixel): Map<Char, List<ColorPixel>> {
        val x = target.x.coerceAtLeast(1).coerceAtMost(_image.width - 2)
        val y = target.y.coerceAtLeast(1).coerceAtMost(_image.height - 2)

        return mapOf(
            'x' to listOf(
                getColorPixel(Pixel(x - 1, target.y)),
                getColorPixel(Pixel(x + 1, target.y)),
            ),
            'y' to listOf(
                getColorPixel(Pixel(target.x, y - 1)),
                getColorPixel(Pixel(target.x, y + 1)),
            ),
        )
    }

    private fun getEnergyIntensity(pixel: Pixel): Int {
        return (255.0 * energyPixels[pixel.x][pixel.y].energy / maxEnergy).toInt()
    }

    private fun getColorPixel(pixel: Pixel) = ColorPixel(pixel.x, pixel.y, Color(_image.getRGB(pixel.x, pixel.y)))
}