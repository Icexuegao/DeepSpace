import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File

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

    override fun finish() {
        generateSoundsFile()
    }
}

class DeepSpaceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DeepSpaceProcessor(environment, environment.codeGenerator)
    }
}