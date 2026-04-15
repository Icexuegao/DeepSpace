package ice.core

import java.io.File

fun main() {
  val file = File("B:\\Programming\\MDT\\DeepSpace\\assets\\spritese")
  val target = File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites-out")
  df(file, target)
}
fun df(file: File,target: File){
  target.clearDirectory()
  file.copyDirectoryTo(target){
    val newName = "${it.nameWithoutExtension}.png_"
    val newFile = File(it.parent, newName)
    it.renameTo(newFile)

    val data = newFile.readBytes()
    PngCrypto.processInPlace(data)
    newFile.writeBytes(data)
  }
}
fun File.clearDirectory() {
  require(this.isDirectory) { "必须是文件夹" }

  this.listFiles()?.forEach { item ->
    if (item.isDirectory) {
      item.deleteRecursively()
    } else {
      item.delete()
    }
  }
}
object PngCrypto {
  private const val KEY = 0x5A.toByte() // 可自定义密钥

  fun encrypt(input: File, output: File) {
    val data = input.readBytes()
    val encrypted = data.map { (it.toInt() xor KEY.toInt()).toByte() }.toByteArray()
    output.writeBytes(encrypted)
  }

  fun decrypt(input: File, output: File) {
    encrypt(input, output) // XOR 是对称的
  }

  // 直接处理字节数组（更快）
  fun processInPlace(data: ByteArray) {
    for (i in data.indices) {
      data[i] = (data[i].toInt() xor KEY.toInt()).toByte()
    }
  }
}
fun File.copyDirectoryTo(targetFolder: File,run:(target:File)-> Unit ={}) {
  require(this.isDirectory) { "源必须是文件夹" }
  require(targetFolder.isDirectory || !targetFolder.exists()) { "目标必须是文件夹或不存在" }

  if (!targetFolder.exists()) {
    targetFolder.mkdirs()
  }

  this.listFiles()?.forEach { item ->
    val destPath = File(targetFolder, item.name)
    if (item.isDirectory) {
      item.copyDirectoryTo(destPath,run)
    } else {
      run.invoke(item.copyTo(destPath, overwrite = true))
    }
  }
}

fun File.copyToFolder(targetFolder: File): File {
  require(targetFolder.isDirectory) { "目标必须是文件夹" }

  if (!targetFolder.exists()) {
    targetFolder.mkdirs()
  }

  val destFile = File(targetFolder, this.name)
  return this.copyTo(destFile, overwrite = true)
}

fun File.createFile(fileName: String): File {
  if (!this.exists()) {
    this.mkdirs()
  }
  val newFile = File(this, fileName)
  if (!newFile.exists()) {
    newFile.createNewFile()
  }
  return newFile
}