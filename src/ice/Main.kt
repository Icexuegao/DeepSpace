package ice

import java.io.File

fun main() {
  // repName(File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\remains\\spine_parasite"))
   val file = File("B:\\Programming\\MDT\\DeepSpace\\assets\\spritese\\items")
   req(file)
  //removeLeadingZeros("B:\\inCommonUse\\Videos\\bilibili\\37331271901\\37331271901-1-30280.m4s")

}


fun req(file: File) {
  file.listFiles()?.forEach {
    if (it.name.contains("item_crystal_FEX_power")) {
      val replace1 =it.name.replace("-t","")
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

