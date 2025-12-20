

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DeepSpaceProcessor(
    val environment: SymbolProcessorEnvironment,
    val codeGenerator: CodeGenerator
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 生成ISounds.kt文件
        return emptyList()
    }


    private fun generateSoundsFile() {
        // 获取assets/sounds目录
        val soundsDir = File("assets/sounds")

        if (!soundsDir.exists()) {
            environment.logger.warn("Sounds directory not found: ${soundsDir.absolutePath}")
            return
        }
        // 获取所有ogg文件并提取文件名（不含扩展名）
        val soundFiles = soundsDir.listFiles { file ->
            file.isFile && file.name.endsWith(".ogg")
        }?.map { it.nameWithoutExtension }?.sorted() ?: emptyList()
        // 生成ISounds.kt文件内容
        val fileContent = buildString {
            appendLine("package ice.audio")
            appendLine()
            appendLine("import arc.Core")
            appendLine("import arc.audio.Sound")
            appendLine("import ice.library.IFiles")
            appendLine()
            appendLine("object ISounds {")
            // 为每个音效文件生成属性
            soundFiles.forEach { soundName ->
                appendLine("    val $soundName = getSound(\"$soundName\")")
            }
            appendLine()
            appendLine("    private fun getSound(name: String): Sound {")
            appendLine("        val file = IFiles.findSound(name+\".ogg\")")
            appendLine("        return Core.audio.newSound(file)")
            appendLine("    }")
            appendLine("}")
        }
        // 创建输出文件
        val outputFile = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "ice.audio",
            fileName = "ISounds",
            extensionName = "kt"
        )

        // 记录生成文件的位置
        environment.logger.info("Generated ISounds.kt in package: ice.audio")
        // 写入内容
        outputFile.write(fileContent.toByteArray())
        outputFile.close()

        environment.logger.info("Generated ISounds.kt with ${soundFiles.size} sound files")
    }
    fun encryptByRemovingHeader(inputFile: File, outputFile: File): Boolean {
        return try {
            // 确保输入文件存在且是文件而不是目录
            if (!inputFile.exists() || !inputFile.isFile) {
                println("输入文件不存在或是目录: ${inputFile.absolutePath}")
                return false
            }

            // 确保输出目录存在
            val outputParent = outputFile.parentFile
            if (outputParent != null && !outputParent.exists()) {
                outputParent.mkdirs()
            }

            // 读取原始PNG文件
            val originalData = inputFile.readBytes()

            // PNG文件头是8字节，所以我们从第8字节开始取数据
            val encryptedData = originalData.copyOfRange(8, originalData.size)

            // 将修改后的数据写入输出文件
            FileOutputStream(outputFile).use { output ->
                output.write(encryptedData)
            }

            true
        } catch (e: Exception) {
            println("加密文件时出错: ${inputFile.absolutePath}")
            e.printStackTrace()
            false
        }
    }

    fun processDirectory(sourceDir: File, outputDir: File) {
        // 确保源目录存在
        if (!sourceDir.exists()) {
            println("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        // 确保输出目录存在
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // 获取源目录中的所有文件和子目录
        val files = sourceDir.listFiles()

        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // 如果是子目录，创建对应的输出目录并递归处理
                    val subOutputDir = File(outputDir, file.name)
                    processDirectory(file, subOutputDir)
                } else if (file.isFile && file.extension.lowercase() == "png") {
                    // 如果是PNG文件，加密并保存到输出目录
                    val outputFile = File(outputDir, file.name+"_")
                    val success = encryptByRemovingHeader(file, outputFile)
                    if (success) {
                        println("已加密: ${file.absolutePath} -> ${outputFile.absolutePath}")
                    } else {
                        println("加密失败: ${file.absolutePath}")
                    }
                }
            }
        }
    }

    // 在您的jiami函数中，应该是这样调用：
    fun jiami() {
        val sourceDir = File("assets-src/sprites")
        val outputDir = File("assets/sprites-out")

        // 确保源目录存在
        if (!sourceDir.exists()) {
            println("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        // 确保输出目录存在
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // 递归处理所有PNG文件
        processDirectory(sourceDir, outputDir)
    }


    override fun finish() {
        generateSoundsFile()

        jiami()
    }

    fun decryptByAddingHeader(inputFile: File, outputFile: File): Boolean {
        return try {
            // 读取加密文件
            val encryptedData = FileInputStream(inputFile).readBytes()
            // PNG文件头是固定的8字节: 89 50 4E 47 0D 0A 1A 0A
            val pngHeader = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte())
            // 将PNG文件头和加密数据合并
            val decryptedData = pngHeader + encryptedData
            // 将解密数据写入输出文件
            FileOutputStream(outputFile).use { output ->
                output.write(decryptedData)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}

class DeepSpaceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DeepSpaceProcessor(environment, environment.codeGenerator)
    }
}