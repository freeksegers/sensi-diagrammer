import java.io.File
import java.io.PrintWriter
import kotlin.math.PI
import kotlin.math.tan

const val INNER_RADIUS: Int = 180
const val OUTER_RADIUS: Int = 200
const val OUTER_TEXT_WIDTH: Int = 100

fun main(args: Array<String>) {
    println("""Er volgen nu drie prompts om woorden in the voeren. 
        |Type steeds één woord per regel. Je kunt ook een reeks woorden 
        |plakken uit een tekstdocument of bijvoorbeeld Excel.
        |Sluit de reeks steeds af met een enkele punt op de regel na het laatste woord.
        |
        |Het aantal punten in het diagram wordt bepaald door het grootste 
        |aantal ingevoerde woorden voor één van de sets.
        |
        |Het eerste woord wordt geplaats op de "3 uur"-positie. De volgende woorden
        |worden kloksgewijs geplaatst.
        |
        |Als je op een bepaalde positie geen woord wilt, kun je daar een lege regel invoeren.
        |Als je helemaal geen woorden wilt plaatsen voor één van de drie sets, typ je meteen een punt.
        |""".trimMargin())

    print("Lettergrootte? [12] ")
    val fontSize: Int = readLine()?.toIntOrNull() ?: 12

    println("""> Woorden voor de binnenkant?""")
    val innerWords = generateSequence(::readWord).toList();
    println("> Woorden voor de buitenkant?")
    val outerWords = generateSequence(::readWord).toList();
    println("> Tussenwoorden voor de buitenkant?")
    val outerSubWords = generateSequence(::readWord).toList();

    val words: Array<Array<String?>> = getWords(innerWords, outerWords, outerSubWords)

    File("diagram.svg").printWriter().use { writer -> writeSvg(writer, fontSize, words) }

    println("Diagram written to file 'diagram.svg'")
}

fun getWords(innerWords: List<String>, outerWords: List<String>, outerSubWords: List<String>): Array<Array<String?>> {
    val n: Int = maxOf(innerWords.size, outerWords.size, outerSubWords.size, 6)
    return Array(n) { i ->
        arrayOf(
            innerWords.getOrElse(i) { "" },
            outerWords.getOrElse(i) { "" },
            outerSubWords.getOrElse(i) { "" })
    }
}

fun readWord(): String? {
    val word: String? = readLine()
    return if (!word.equals(".")) word else null
}

/**
 * @param words is an Array of Arrays with three strings, that contain the inner (inside the kites),
 * outer (outside the kite at the kite point) and outer sub (in between the kite points) words.
 * The size of words will be the number of kites that are drawn.
 */
fun writeSvg(
    writer: PrintWriter,
    fontSize: Int,
    words: Array<Array<String?>>,
    outerRadius: Int = OUTER_RADIUS,
    innerRadius: Int = INNER_RADIUS
) {
    val n = words.size
    val angleDeg: Double = 360.0 / n
    val width = innerRadius * tan(PI / n)
    val brim = outerRadius - innerRadius
    val canvasSize: Int = (3.5 * outerRadius).toInt()

    println(
        """
        canvas = $canvasSize
        n = $n
        angle = $angleDeg deg
        width = $width
        """.trimIndent()
    )

    writer.println(
        """<svg xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" 
        |  width="100%" 
        |  height="100%" 
        |  viewBox="0 0 $canvasSize $canvasSize">
        |    <defs>
        |      <path id="kite" stroke="black" stroke-width="1px" fill="none"
        |            d="M 0 0 l$innerRadius,${-width} $brim,$width ${-brim},$width z" />
        |    </defs>
        |    <style>
        |      .innerText { font: ${fontSize}px sans-serif; }
        |      .debug { display: none; }
        |    </style>
        |    <rect class="debug" width="100%" height="100%" fill="gray"/>
        |    <g transform="translate(${canvasSize / 2},${canvasSize / 2})">
        |""".trimMargin()
    )

    writeKite(writer, canvasSize, outerRadius, innerRadius, width, angleDeg, words[0])
    for (i in 1 until n) {
        writeRotatedKite(writer, i, canvasSize, outerRadius, innerRadius, width, angleDeg, words[i])
    }
    writer.println("</g></svg>")
}

fun writeKite(
    writer: PrintWriter,
    canvasSize: Int,
    outerRadius: Int,
    innerRadius: Int,
    width: Double,
    angle: Double,
    words: Array<String?>
) {
    writer.println("""    <use xlink:href="#kite" x="0" y="0" />""")
    writer.println(
        """
    |    <!-- A 2nd canvas that is pulled half the width up. To get the text properly aligned. -->
    |    <g transform="translate(0,${-width})">
    |      <svg width="${canvasSize / 2}" height="${2 * width}">
    |        <!-- The inner word -->
    |        <rect class="debug" x="0" y="0" width="$outerRadius" height="${2 * width}" fill="none" stroke="red" stroke-width="1px"/>
    |        <text x="$innerRadius" y="50%" class="innerText" dominant-baseline="middle" text-anchor="end">${words[0]}</text>
    |""".trimMargin()
    )
    // Outer word
    if (words[1] != "") {
        writer.println(
            """
    |        <!-- The outer word -->
    |        <rect class="debug" x="$outerRadius" y="0" width="${canvasSize / 2 - outerRadius}" height="${2 * width}" fill="none" stroke="blue" stroke-width="1px"/>
    |        <text x="${2 * outerRadius - innerRadius}" y="50%" class="innerText" dominant-baseline="middle">${words[1]}</text>            
        """.trimMargin()
        )
    }
    writer.println("""
    |      </svg>
    |    </g>
    |""".trimMargin()
    )

    // Outer sub word, rotated by a half angle
    if (words[2] != "") {
        writer.println(
            """
    |        <!-- A 3rd canvas. Same as the 2nd, but this one is first rotated by half the rotation angle of the kites. -->
    |        <g transform="rotate(${angle / 2}) translate(0,${-width})">
    |          <svg width="${canvasSize / 2}" height="${2 * width}">
    |            <!-- The outer sub word -->
    |            <rect class="debug" x="$outerRadius" y="0" width="${canvasSize / 2 - outerRadius}" height="${2 * width}" fill="none" stroke="green" stroke-width="1px"/>
    |            <text x="${2 * outerRadius - innerRadius}" y="50%" class="innerText" dominant-baseline="middle">${words[2]}</text>
    |          </svg>
    |        </g>
        """.trimMargin()
        )
    }
}

fun writeRotatedKite(
    writer: PrintWriter,
    step: Int,
    canvasSize: Int,
    outerRadius: Int,
    innerRadius: Int,
    width: Double,
    angle: Double,
    words: Array<String?>
) {
    writer.println("""    <g transform ="rotate(${step * angle})">""")
    writeKite(writer, canvasSize, outerRadius, innerRadius, width, angle, words)
    writer.println("    </g>")
}

