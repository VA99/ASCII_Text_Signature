package signature

import java.io.File
import java.util.Scanner
import kotlin.math.abs

class Font(fontType: String) {
    var fontType = if (fontType in arrayOf("medium", "roman")) fontType else ""
    // linux version:
    // var fontFile = File("/home/av99/Desktop/Fonts/${this.fontType}.txt")
    // Windows version:
    var fontFile = File("C:\\Users\\Armen\\Downloads\\Font\\${this.fontType}.txt")
    val fontScanner = Scanner(fontFile)
    val info = fontScanner.nextLine()
    val infoScanner = Scanner(info)
    val height = infoScanner.next().toInt()
    val length = infoScanner.next().toInt()

    fun skipRow(step: Int, scanner: Scanner): String {
        var value = ""
        for (i in 0 until step) {
            value = if (!scanner.hasNextLine()) ""
            else scanner.nextLine()
        }
        return value
    }

    var currentChar = ' '
    val isValidChar: Boolean
        get() = this.currentChar.toUpperCase() in 'A'..'Z'
    val rowOfChar: Int
        get() {
            var row = -1
            if (isValidChar) {
                val scanner = Scanner(fontFile)
                scanner.nextLine()
                row = 1
                for (i in 0 until (this.length * (this.length + 1))) {
                    if (scanner.hasNextLine()) {
                        val aboutChar = scanner.nextLine()
                        val infoChar = Scanner(aboutChar)
                        val letter = infoChar.next().first()
                        if (letter == currentChar) return row + i
                    }
                    val value = skipRow(height, scanner)
                    if (value.isNotEmpty()) row += height
                }
            }
            return row
        }
    val width: Int
        get() {
            return if (!isValidChar) 1
            else {
                val scanner = Scanner(fontFile)
                scanner.nextLine()
                val value = skipRow(rowOfChar, scanner)
                val valueScanner = Scanner(value)
                valueScanner.next()
                valueScanner.next().toInt()
            }
        }
    var charLayer = 1
        set(value) {
            field = if (value <= height) value
            else height
        }
    val char: String
        get() {
            return if (!isValidChar) "$currentChar"
            else {
                val scanner = Scanner(fontFile)
                scanner.nextLine()
                skipRow(rowOfChar + charLayer, scanner)
            }
        }
}

class Signature(var text: String, val fontType: String) {
    val length: Int
        get() {
            val font = Font(fontType)
            var widthOfChars = 0
            for (char in text) {
                font.currentChar = char
                widthOfChars += font.width
            }
            return widthOfChars
        }
    val value: String
        get() {
            val font = Font(fontType)
            var result = ""
            for (charLayer in 1..font.height) {
                font.charLayer = charLayer
                var isValid = false
                for (char in text) {
                    font.currentChar = char
                    if (font.isValidChar) isValid = true
                    result += font.char
                }
                result += "\n"
                if (!isValid) break
            }
            return result
        }
}

fun genSymbols(value: String, boolVal: Boolean, repVal1: Int, repVal2: Int = 0): String {
    return if (boolVal) value.repeat(repVal1)
    else value.repeat(repVal2)
}

fun main() {
    val scanner = Scanner(System.`in`)

    //Name-------------------------------------------------------------
    print("Enter name and surname: ")
    val nameScanner = Scanner(scanner.nextLine())

    val name = if (nameScanner.hasNext()) nameScanner.next()else ""
    val surname = if (nameScanner.hasNext()) nameScanner.next() else ""
    val text1 = "$name          $surname"
    //-----------------------------------------------------------------

    //Surname------------------------------------------------------------
    print("Enter person's status: ")
    val status = scanner.nextLine()
    val statusScanner = Scanner(status)

    var hasNext = statusScanner.hasNext()
    var nextVal = if (hasNext) statusScanner.next() else ""
    var text2 = nextVal
    do {
        hasNext = statusScanner.hasNext()
        if (hasNext) {
            val tmp = status.substringAfterLast(nextVal).trim()
            val lastIndex = status.lastIndexOf(nextVal)
            val firstIndex = status.indexOf(tmp)
            val repVal = firstIndex - lastIndex - nextVal.length
            nextVal = statusScanner.next()
            text2 += "     ".repeat(repVal) + nextVal
        }
    } while (hasNext)
    //-------------------------------------------------------------------

    val fontType1 = "roman"
    val fontType2 = "medium"

    val l1 = Signature(text1, fontType1).length
    val l2 = Signature(text2, fontType2).length

    val boolVal1 = l1 < l2
    val boolVal2 = l1 > l2

    //Generating-spaces-----------------------------------------------------------------------
    val space = " "
    val centreVal = abs(l1 - l2)/2
    val leftSpace1 = genSymbols(space, boolVal1, centreVal)
    val leftSpace2 = genSymbols(space, boolVal2, centreVal)

    val bothAreDiff = (l1 % 2 == 0) xor (l2 % 2 == 0)
    val rightSpace1 = genSymbols(space, boolVal1 && bothAreDiff, 1) + leftSpace1
    val rightSpace2 = genSymbols(space, boolVal2 && bothAreDiff, 1) + leftSpace2
    //----------------------------------------------------------------------------------------

    val s1 = Signature("88  $leftSpace1$text1$rightSpace1  88", fontType1)
    val s2 = Signature("88  $leftSpace2$text2$rightSpace2  88", fontType2)

    //Generating-eights--------------------------------------
    var eight = "8"
    eight = genSymbols(eight, boolVal1, s1.length, s2.length)
    //-------------------------------------------------------

    println(eight)
    print("${s1.value}${s2.value}")
    println(eight)

}
