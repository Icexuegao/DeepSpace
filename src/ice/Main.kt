package ice

import java.io.File

fun main() {
  //  repName("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\blocks\\environment\\floor\\humanBones")
}

fun repName(path: String) {
    val file = File(path)
    var i=1
    file.listFiles()?.forEach {

        val replace = "humanBones$i.png"
        it.renameTo(File("$path\\$replace"))
        i++
    }
}

