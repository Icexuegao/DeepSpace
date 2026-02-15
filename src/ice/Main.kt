package ice

import java.io.File

fun main() {
  // repName("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\ice\\2dwd")
 // val file = File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\blocks\\environment\\ore")
 // req(file)
}

fun req(file: File){
  file.listFiles()?.forEach {
    if (it.isDirectory)req(it)
    if (it.extension == "png"&& !it.name.contains("item_")){
      val replace1 ="item_${it.name}"
      it.renameTo(File("${file.absolutePath}\\$replace1"))
    }
  }
}


fun repName(path: String) {
  val file = File(path)

  file.listFiles()?.forEach {
    val replace1 = it.name.replace("contributors", "contributors-")
    it.renameTo(File("$path\\$replace1"))
  }
}

