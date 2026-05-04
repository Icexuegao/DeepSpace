import arc.files.Fi
import arc.util.serialization.JsonReader
import arc.util.serialization.JsonWriter
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

buildscript {
  extra["proUser"] = System.getProperty("user.name")
  extra["sdkRoot"] = System.getenv("ANDROID_HOME")
  extra["kotlinCompatibility"] = "2.3.20"
  extra["java"] = 25
  extra["mdtVersion"] = "com.github.Anuken.Mindustry:core:v157.4"
  extra["modFileName"] ="mod.json"
  var mdtVersion: String by extra

  repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    ivy {
      url = uri("https://github.com/")
      patternLayout {
        artifact("/[organisation]/[module]/releases/download/[revision]/dependencies.jar")
      }
      metadataSources {
        artifact()
      }
    }
  }
  dependencies {
    classpath(mdtVersion)
    classpath(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
  }

}
val kotlinCompatibility: String by extra
val proUser: String by extra
val sdkRoot: String by extra
val java: Int by extra
var mdtVersion: String by extra
var modFileName: String by extra
plugins {
  var kotlinCompatibility: String by extra
  java
  kotlin("jvm") version kotlinCompatibility
  id("com.gradleup.shadow") version "9.3.0"
}


repositories {

  mavenCentral()
  mavenLocal()
  maven { url = uri("https://jitpack.io") }
  maven { url = uri("https://www.jitpack.io") }
  ivy {
    url = uri("https://github.com/")
    patternLayout {
      artifact("/[organisation]/[module]/releases/download/[revision]/dependencies.jar")
    }
    metadataSources {
      artifact()
    }
  }

}
dependencies {
  implementation("com.github.EB-wilson.UniverseKit:reflection:1.1")
  implementation("com.github.EB-wilson.UniverseKit:markdown:1.1")
  implementation("com.github.EB-wilson.UniverseKit:graphic:1.1")
  compileOnly(mdtVersion)
  //compileOnly("com.github.EB-wilson:TooManyItems:2.5.1")
  implementation("org.commonmark:commonmark:0.20.0")
  implementation("org.commonmark:commonmark-ext-gfm-tables:0.20.0")
  implementation("org.commonmark:commonmark-ext-gfm-strikethrough:0.20.0")
  implementation("org.commonmark:commonmark-ext-ins:0.20.0")

  compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.zip"))))
  compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
  implementation("com.github.tommyettinger:RegExodus:0.1.10")
  implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinCompatibility}")
  //  implementation("com.github.EB-wilson.UniverseCore:dynamilizer:${uncVersion}")
  //implementation("com.github.Anuken.Arc:arc-core:v146")
  //compileOnly files("lib\\UniverseCore-v2.2.0.jar")
  //  implementation("com.github.EB-wilson.UniverseCore:markdown:${uncVersion}")
//markdown工具支持
  //  compileOnly("com.github.EB-wilson.UniverseCore:scenes:${uncVersion}")
//ui相关工具类型
  //compileOnly files("lib/UniverseCore-v2.2.0.jar")
  //  compileOnly "com.github.Anuken.Arc:arc-core:v149"
  //  compileOnly files("lib/bmx.jar")
  //  implementation files("lib/pinyin4j-2.5.0.jar")
  //  implementation 'org.tomlj:tomlj:1.1.1'
  // compileOnly(files("B:\\game\\mindustry-windows-64-bit\\jre\\Mindustry.jar"))
  //compileOnly("com.github.TinyLake:MindustryX:v2026.02.X27")
  // compileOnly("com.github.Anuken.Arc:flabel:v149")
  //compileOnly(mdtVersion)
  compileOnly("com.github.EB-wilson:TooManyItems:3.1a")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinCompatibility")
}

sourceSets {
  main {
    java.setSrcDirs(listOf("src"))
    kotlin.setSrcDirs(listOf("src"))
    resources.setSrcDirs(listOf("assets"))
  }
  kotlin {
    jvmToolchain(java)
  }
  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(java))
    }
  }
}

fun execute(string: String, path: File? = null, vararg args: Any?) {
  val cmd = string.split(Regex("\\s+")).toMutableList().apply { addAll(args.map { it?.toString() ?: "null" }) }.toTypedArray()
  val process = ProcessBuilder(*cmd).directory(path ?: rootDir).redirectOutput(ProcessBuilder.Redirect.INHERIT)
    .redirectError(ProcessBuilder.Redirect.INHERIT).start()
  val message = process.errorReader().readText()
  if (message.isNotEmpty()) throw Exception(message)
}
tasks.processResources {
  dependsOn("encryptSprites")
}

tasks {
  withType<JavaCompile>().configureEach {
    sourceCompatibility = java.toString()
    targetCompatibility = java.toString()
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
      arrayOf(
        "--add-exports",
        "java.base/jdk.internal.misc=ALL-UNNAMED",
        "--add-exports",
        "java.base/jdk.internal.module=ALL-UNNAMED",
        "--add-exports",
        "java.base/jdk.internal.reflect=ALL-UNNAMED",
        "--add-exports",
        "java.base/sun.nio.ch=ALL-UNNAMED"
      )
    )
  }

  withType<ShadowJar> {

    dependsOn("updateVersion", "sourcesJar", "encryptSprites")
    group = "alon"
    archiveFileName.set("${project.name}Desktop.jar")
    from(files("README.md", "LICENSE", modFileName))
    manifest.attributes("Main-Class" to "ice.Ice")
    exclude("spritese/**")
  }

  register("encryptSprites") {
    group = "alon"
    val sourceDir = file("assets/spritese")
    val targetDir = file("assets/sprites-out")

    inputs.dir(sourceDir)
    outputs.dir(targetDir)

    doLast {
      if (!targetDir.exists()) {
        targetDir.mkdirs()
      }
      targetDir.walkTopDown().filter { it.isFile && it.name.endsWith(".png_") }.forEach { encryptedFile ->
        val relativePath = encryptedFile.relativeTo(targetDir).path
        val sourceFile = File(sourceDir, relativePath.replace(".png_", ".png"))
        if (!sourceFile.exists()) {
          encryptedFile.delete()
        }
      }

      sourceDir.walkTopDown().filter { it.isFile && it.name.endsWith(".png") }.forEach { sourceFile ->
        val relativePath = sourceFile.relativeTo(sourceDir).path
        val targetEncrypted = File(targetDir, relativePath.replace(".png", ".png_"))

        if (targetEncrypted.exists() && targetEncrypted.lastModified() >= sourceFile.lastModified()) {
          return@forEach
        }

        val data = sourceFile.readBytes()


        for(i in data.indices) {
          data[i] = (data[i].toInt() xor 920).toByte()
        }
        targetEncrypted.parentFile?.mkdirs()
        targetEncrypted.writeBytes(data)
      }
    }
  }
  register<Jar>("sourcesJar") {
    group = "alon"
    archiveClassifier.set("sources")
    from("src")
    include("**/*.java", "**/*.kt")
  }

  register<Copy>("myCopyJar") {
    group = "alon"
    dependsOn(shadowJar)
    from("build/libs/${project.name}Desktop.jar")
    into("C:/Users/$proUser/AppData/Roaming/Mindustry/mods")
  }
  register<Copy>("myCopyJarSteam") {
    group = "alon"
    dependsOn(shadowJar)
    from("build/libs/${project.name}Desktop.jar")
    into("B:\\game\\steam\\steamapps\\common\\Mindustry\\saves\\mods")
  }
  register("updateVersion") {
    group = "alon"
    val file = Fi(modFileName)
    val parse = JsonReader().parse(file)
    val message = parse.get("version").asString().split("-")[1].toInt() + 1
    parse.get("version").set("Alpha-$message")
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")
    val formattedDate = currentDate.format(formatter)
    parse.get("updateDate").set(formattedDate)
    file.writeString(parse.prettyPrint(JsonWriter.OutputType.json, 4))
  }

  register<JavaExec>("d8Compile") {
    group = "alon"
    dependsOn(shadowJar)
    val sdkDir = File(sdkRoot)
    val platformDir = File(sdkDir, "platforms")
    val platformRoot = platformDir.listFiles { f ->
      f.isDirectory && File(f, "android.jar").exists()
    }?.maxByOrNull { it.name } ?: throw GradleException("找不到有效的安卓平台")
    classpath(files("$sdkRoot/build-tools/37.0.0-rc2/lib/d8.jar"))
    mainClass.set("com.android.tools.r8.D8")
    val classpathFiles =
      (configurations.compileClasspath.get().files + configurations.runtimeClasspath.get().files + File(platformRoot, "android.jar"))
    val argsList = mutableListOf<String>()
    classpathFiles.forEach { file ->
      argsList.add("--classpath")
      argsList.add(file.absolutePath)
    }

    argsList.add("--min-api")
    argsList.add("26")
    argsList.add("--output")
    argsList.add("${project.rootDir}/build/libs/${project.name}Android.jar")
    argsList.add("${project.rootDir}/build/libs/${project.name}Desktop.jar")

    args = argsList

    workingDir = File(project.rootDir, "build/libs")
  }

  register<Jar>("deploy") {
    group = "alon"
    dependsOn("d8Compile")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("${project.name}.jar") //存档文件名
    from(zipTree("build/libs/${project.name}Desktop.jar"), zipTree("build/libs/${project.name}Android.jar"))
  }
  register("removeVer") {
    Fi("build/libs/version").deleteDirectory()
  }
  register<Jar>("deployVersion") {
    group = "alon"
    dependsOn("removeVer")
    dependsOn("deploy")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val versionInfo = provider {
      val file = Fi(modFileName)
      val parse = JsonReader().parse(file)
      val message = parse.get("version")
      val split: List<String> = message.asString().split("-")
      split[0] to split[1].toInt()
    }

    archiveFileName.set(versionInfo.map { "${project.name}-${it.first}-${it.second}.jar" })
    destinationDirectory.set(file("build/libs/version"))

    from(zipTree("build/libs/${project.name}.jar"))
  }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
  freeCompilerArgs.set(listOf("-XXLanguage:+NestedTypeAliases"))
}