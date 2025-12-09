package ice

import java.io.File

fun main() {

    repName("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\blocks\\environment\\floor\\bloodBlastocyst")

}


fun repName1(path: String) {
    val file = File(path)
    file.listFiles()?.forEach {
        val ath = it.invariantSeparatorsPath
        it.listFiles()?.forEach {
            val replace = it.name.last().toString()
            val replace1 = it.name.replace(replace, "Ore$replace.png")
            it.renameTo(File("$ath\\$replace1"))
        }
    }
}

fun repName(path: String) {
    val file = File(path)

    file.listFiles()?.forEach {
        val replace1 = it.name.replace("bloodMorite", "bloodBlastocyst")
        it.renameTo(File("$path\\$replace1"))
    }
}

