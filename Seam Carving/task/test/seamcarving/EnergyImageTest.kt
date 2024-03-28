package seamcarving

import org.junit.Assert.assertEquals
import org.junit.Test
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

class EnergyImageTest {

    @Test
    fun `it should get max of nested list`() {
        val nestedList = listOf(
            listOf(1, 2, 3),
            listOf(7, 9, 8),
            listOf(6, 5, 4),
        )

        assertEquals(9, nestedList.maxOfOrNull { it.maxOrNull() ?: 0 } ?: 0)
    }

    @Test
    fun `it should get correct pixels - 1`() {

        val imageEnergy = EnergyImage(BufferedImage(4, 4, TYPE_INT_RGB))

        val target = ColorPixel(2, 1, Color(0, 0, 0))
        val pixels = imageEnergy.getEnergyPixels(target);

        assertEquals(
            """
                x: 1,1 3,1
                y: 2,0 2,2
            """.trimIndent(),
            pixelsToString(pixels)
        )
    }

    @Test
    fun `it should get correct pixels - 2`() {

        val imageEnergy = EnergyImage(BufferedImage(4, 4, TYPE_INT_RGB))

        val target = ColorPixel(0, 0, Color(0, 0, 0))
        val pixels = imageEnergy.getEnergyPixels(target);

        assertEquals(
            """
                x: 0,0 2,0
                y: 0,0 0,2
            """.trimIndent(),
            pixelsToString(pixels)
        )
    }

    @Test
    fun `it should get correct pixels - 3`() {

        val imageEnergy = EnergyImage(BufferedImage(4, 4, TYPE_INT_RGB))

        val target = ColorPixel(3, 3, Color(0, 0, 0))
        val pixels = imageEnergy.getEnergyPixels(target);

        assertEquals(
            """
                x: 1,3 3,3
                y: 3,1 3,3
            """.trimIndent(),
            pixelsToString(pixels)
        )
    }

    @Test
    fun `it should get pixel energy`() {
        val image = BufferedImage(4, 4, TYPE_INT_RGB)

        image.setRGB(0, 0, Color(0, 255, 250).rgb)
        image.setRGB(0, 1, Color(50, 250, 0).rgb)
        image.setRGB(0, 2, Color(110, 250, 140).rgb)
        image.setRGB(0, 3, Color(200, 200, 100).rgb)
        image.setRGB(1, 0, Color(150, 200, 50).rgb)
        image.setRGB(1, 1, Color(255, 250, 155).rgb)
        image.setRGB(1, 2, Color(10, 250, 40).rgb)
        image.setRGB(1, 3, Color(150, 200, 50).rgb)
        image.setRGB(2, 0, Color(50, 255, 255).rgb)
        image.setRGB(2, 1, Color(250, 250, 250).rgb)
        image.setRGB(2, 2, Color(10, 250, 40).rgb)
        image.setRGB(2, 3, Color(150, 200, 50).rgb)
        image.setRGB(3, 0, Color(150, 200, 50).rgb)
        image.setRGB(3, 1, Color(150, 150, 100).rgb)
        image.setRGB(3, 2, Color(255, 255, 195).rgb)
        image.setRGB(3, 3, Color(180, 230, 70).rgb)

        val imageEnergy = EnergyImage(image)

        assertEquals(
            268.14,
            imageEnergy.getEnergy(ColorPixel(2, 1, Color(0, 0, 0))),
            0.01
        )
    }

    private fun pixelsToString(pixels: Map<Char, List<Pixel>>) = """
                    x: ${pixels['x']?.joinToString(" ") { it.toString() }}
                    y: ${pixels['y']?.joinToString(" ") { it.toString() }}
                """.trimIndent()
}