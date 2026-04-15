package ice

import java.io.File
import java.io.RandomAccessFile

fun main() {
  // repName(File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites\\remains\\spine_parasite"))
  // val file = File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites_out")
 //  req(file)
  //removeLeadingZeros("B:\\inCommonUse\\Videos\\bilibili\\37331271901\\37331271901-1-30280.m4s")

}


fun removeLeadingZeros(filePath: String, zeroCount: Int = 9) {
  val file = File(filePath)
  if (!file.exists() || file.length() < zeroCount) return

  RandomAccessFile(file, "rw").use { raf ->
    val header = ByteArray(zeroCount)
    raf.readFully(header)

    if (!header.all { it == '0'.code.toByte() }) {
      throw IllegalArgumentException("文件开头非0")
    }

    val bufferSize = 8192 * 1024
    val buffer = ByteArray(bufferSize)
    var readPos = zeroCount.toLong()
    var writePos = 0L

    while (true) {
      raf.seek(readPos)
      val bytesRead = raf.read(buffer)
      if (bytesRead == -1) break

      raf.seek(writePos)
      raf.write(buffer, 0, bytesRead)

      readPos += bytesRead
      writePos += bytesRead
    }

    raf.setLength(file.length() - zeroCount)
  }
}

open class A
interface dw

fun req(file: File) {
  file.listFiles()?.forEach {
    if (it.isDirectory) req(it)
    if (it.extension == "png") {
      val replace1 = "${it.nameWithoutExtension}.png_"
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

