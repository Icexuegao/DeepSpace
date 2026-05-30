package ice

import java.io.File

fun main() {
  // repName(File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\remains\\spine_parasite"))
   val file = File("B:\\Programming\\MDT\\DeepSpace\\杂项\\CurseOfFlesh2.9.5.1\\sprites\\blocks\\炮塔\\罪碑")
   req(file)
  //removeLeadingZeros("B:\\inCommonUse\\Videos\\bilibili\\37331271901\\37331271901-1-30280.m4s")

}
fun req(file: File) {
  file.listFiles()?.forEach {
      val replace1 =it.name.replace("罪碑","turret_sin_monument")
      it.renameTo(File("${file.absolutePath}\\$replace1"))
  }
}

fun repName(file: File) {

  file.listFiles()?.forEach {
    if (it.isDirectory){
      repName(it)
      return@forEach
    }
    val replace1 = it.name.replace("","")
    it.renameTo(File("${file.path}\\$replace1"))
  }
}

