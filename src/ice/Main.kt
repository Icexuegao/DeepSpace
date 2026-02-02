package ice

import java.io.File

fun main() {
   //repName("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\blocks\\distribution\\conveyor\\reinforcedConveyor")

}




fun repName(path: String) {
  val file = File(path)

  file.listFiles()?.forEach {
    val replace1 = it.name.replace("newConveyor", "reinforcedConveyor")
    it.renameTo(File("$path\\$replace1"))
  }
}

