package ice

import java.io.File

fun main() {
   repName(File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\remains\\spine_parasite"))
  // val file = File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\blocks\\environment\\ore")
  // req(file)

}
open class A
class B:A()
fun req(file: File) {
  file.listFiles()?.forEach {
    if (it.isDirectory) req(it)
    if (it.extension == "png" && !it.name.contains("item_")) {
      val replace1 = "item_${it.name}"
      it.renameTo(File("${file.absolutePath}\\$replace1"))
    }
  }
}

fun repName(file: File) {

  file.listFiles()?.forEach {
    if (it.isDirectory){
      repName(it)
      return@forEach
    }
    val replace1 = it.name.replace("0","")
    it.renameTo(File("${file.path}\\$replace1"))
  }
}

