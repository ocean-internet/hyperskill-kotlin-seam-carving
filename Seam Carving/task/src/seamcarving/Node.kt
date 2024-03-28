package seamcarving

class Node(private val pixel: EnergyPixel, var distToSource: Double) {
    var isProcessed = false
    val x = pixel.x
    val y = pixel.y
    val weight
        get() = pixel.energy

    override fun toString() = pixel.toString()
}