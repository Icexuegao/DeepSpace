package ice.core

import java.io.File

fun main() {
  val file = File("B:\\Programming\\MDT\\DeepSpace\\assets\\spritese")
  val target = File("B:\\Programming\\MDT\\DeepSpace\\assets\\sprites-out")

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