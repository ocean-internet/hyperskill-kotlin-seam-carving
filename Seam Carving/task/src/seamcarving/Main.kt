package seamcarving

import java.io.File
import javax.imageio.ImageIO

private const val IMAGE_FORMAT = "png"

fun main(args: Array<String>) {

    val argString = args.joinToString(" ").trim()
    val matches = Regex("^-in\\s*(.*)\\s+-out\\s*(.*)\\.png\\s+-width\\s*(\\d+)\\s*-height\\s*(\\d+)\$").find(argString)?.groups ?: throw RuntimeException("Invalid input image filename")

    val inputFilename = matches[1]?.value?.trim() ?: throw RuntimeException("Invalid input image filename")
    val outputFilename = matches[2]?.value?.trim() ?: throw RuntimeException("Invalid output image filename")
    val colsToRemove = matches[3]?.value?.trim()?.toInt() ?: throw RuntimeException("Invalid output image filename")
    val rowsToRemove = matches[4]?.value?.trim()?.toInt() ?: throw RuntimeException("Invalid output image filename")

    println("reading file from: $inputFilename")
    val originalImage = ImageIO.read(File(inputFilename)) ?: throw RuntimeException("Invalid input image filename $inputFilename")

    var seamImage = originalImage

    val targetWidth = originalImage.width - colsToRemove
    while (seamImage.width > targetWidth) {
        seamImage = SeamImage(seamImage).toBufferedImage()
    }

    val targetHeight = originalImage.height - rowsToRemove
    while (seamImage.height > targetHeight) {
        seamImage = SeamImage(seamImage, true).toBufferedImage()
    }

    println("writing file to: $outputFilename.${IMAGE_FORMAT}")
    ImageIO.write(seamImage, IMAGE_FORMAT, File("$outputFilename.${IMAGE_FORMAT}"))
}
