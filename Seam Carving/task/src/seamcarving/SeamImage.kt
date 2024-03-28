package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

private const val SOURCE_NODE_X = -1
private const val SOURCE_NODE_Y = -1

private const val SOURCE = "source"
private const val TARGET = "target"

class SeamImage(private val _image: BufferedImage, private val isHorizontal: Boolean = false) {

    private val image = if (isHorizontal) _image.rotate() else _image
    private val energyImage = EnergyImage(image)

    private val nodes = mapOf(SOURCE to getSourceNode()) + getImageNodes() + mapOf(TARGET to getTargetNode())

    private fun getImageNodes() = (0 until energyImage.height).map { row ->
        (0 until energyImage.width).map { col ->
            energyImage.energyPixels[col][row]
        }
    }
        .flatten()
        .associate { it.toString() to Node(it, Double.POSITIVE_INFINITY) }

    init {
        nodes.values.forEach { node ->
            node.isProcessed = true

            getNeighbours(node).filter { !it.isProcessed }.forEach { v ->
                val newDistance: Double = node.distToSource + v.weight
                if (newDistance < v.distToSource) {
                    v.distToSource = newDistance
                }
            }
        }
    }

    fun toBufferedImage(): BufferedImage {

        val bufferedImage = BufferedImage(image.width - 1, image.height, TYPE_INT_RGB)
        val seam = getSeam()

        (0 until bufferedImage.height).forEach { y ->
            (0 until image.width).map { x ->
                ColorPixel(x, y, Color(image.getRGB(x, y)))
            }
                .toMutableList()
                .filter { !seam.containsKey(it.toString()) }
                .forEachIndexed { x, colorPixel ->
                    bufferedImage.setRGB(x, y, colorPixel.color.rgb)
                }
        }

        return if (isHorizontal) bufferedImage.rotate(false) else bufferedImage
    }

    private fun getSeam(): Map<String, Node> {
        val target = nodes.values.toList().last()
        val seamNodes = mutableListOf<Node>()

        var nextNode = getNextSeamNode(target)

        while (nextNode != null) {
            seamNodes.add(nextNode)
            nextNode = getNextSeamNode(nextNode)
        }

        return seamNodes.associateBy { it.toString() }
    }

    private fun getNextSeamNode(node: Node): Node? = when (node.y) {
        energyImage.height -> (0 until energyImage.width).map { x ->
            nodes["$x,${image.height - 1}"] ?: throw RuntimeException("Invalid node: $x,0")
        }.minByOrNull { it.distToSource }

        SOURCE_NODE_Y -> null

        else -> listOf(
            "${node.x - 1},${node.y - 1}",
            "${node.x},${node.y - 1}",
            "${node.x + 1},${node.y - 1}",
        ).mapNotNull { nodes[it] }.minByOrNull { it.distToSource }
    }


    private fun getNeighbours(node: Node): List<Node> = when (node.y) {
        SOURCE_NODE_Y -> (0 until energyImage.width).map { x ->
            nodes["$x,0"] ?: throw RuntimeException("Invalid node: $x,0")
        }

        energyImage.height - 1 -> listOf(nodes[TARGET] ?: throw RuntimeException("Invalid node: $TARGET"))

        else -> listOf(
            "${node.x - 1},${node.y + 1}",
            "${node.x},${node.y + 1}",
            "${node.x + 1},${node.y + 1}",
        ).mapNotNull { nodes[it] }
    }

    private fun getSourceNode() = Node(EnergyPixel(SOURCE_NODE_X, SOURCE_NODE_Y, 0.0), 0.0)
    private fun getTargetNode() =
        Node(EnergyPixel(energyImage.width, energyImage.height, 0.0), Double.POSITIVE_INFINITY)
}

fun BufferedImage.rotate(cw: Boolean = true): BufferedImage {
    val rotatedImage = BufferedImage(this.height, this.width, TYPE_INT_RGB)
    when (cw) {
        true -> (0 until this.height).forEach { x ->
            (0 until this.width).forEach { y ->
                rotatedImage.setRGB(
                    x,
                    y,
                    this.getRGB(y, x)
                )
            }
        }

        false -> (0 until this.height).reversed().forEach { x ->
            (0 until this.width).reversed().forEach { y ->
                rotatedImage.setRGB(
                    x,
                    y,
                    this.getRGB(y, x)
                )
            }
        }
    }
    return rotatedImage
}