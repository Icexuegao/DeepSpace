import arc.files.Fi
import arc.util.serialization.JsonReader
import arc.util.serialization.JsonWriter
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
  extra["proUser"] = System.getProperty("user.name")
  extra["sdkRoot"] = System.getenv("ANDROID_HOME")

  repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
    maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://jitpack.io") }
  }
  dependencies {
    classpath("com.github.Anuken.Mindustry:core:v154.3")
  }
}
val kotlinCompatibility = "2.2.10"
val proUser: String by extra
val sdkRoot: String by extra
plugins {
  java
  kotlin("jvm") version "2.2.10"
  id("com.gradleup.shadow") version "9.3.0"
  id("com.scalified.plugins.gradle.proguard") version "1.7.0"
}


repositories {
  mavenCentral()
  mavenLocal()
  maven { url = uri("https://jitpack.io") }
  maven { url = uri("https://www.jitpack.io") }
}
val uncVersion = "2.3.1"
dependencies {
  compileOnly("com.github.EB-wilson:TooManyItems:2.5.1")
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
  // compileOnly("com.github.Anuken.Mindustry:core:v152.2")
  // compileOnly("com.github.Anuken.Arc:flabel:v149")
  compileOnly("com.github.Anuken.Mindustry:core:v155.2")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinCompatibility")
}

sourceSets {
  main {
    java.setSrcDirs(listOf("src"))
    kotlin.setSrcDirs(listOf("src"))
    resources.setSrcDirs(listOf("assets"))
  }
  kotlin {
    jvmToolchain(17)
  }
}
proguard {
  configurations {
    overwriteArtifact.set(false)
    autoRun.set(false)
  }
}

fun execute(string: String, path: File? = null, vararg args: Any?) {
  val cmd = string.split(Regex("\\s+")).toMutableList().apply { addAll(args.map { it?.toString() ?: "null" }) }.toTypedArray()
  val process = ProcessBuilder(*cmd).directory(path ?: rootDir).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start()
  val message = process.errorReader().readText()
  if (message.isNotEmpty()) throw Exception(message)
}

tasks {
  withType<JavaCompile>().configureEach {
    sourceCompatibility = 17.toString()
    targetCompatibility = 17.toString()
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf( "--add-exports", "java.base/jdk.internal.misc=ALL-UNNAMED",
      "--add-exports", "java.base/jdk.internal.module=ALL-UNNAMED",
      "--add-exports", "java.base/jdk.internal.reflect=ALL-UNNAMED",
      "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED")

    )
  }

  withType<ShadowJar> {
    dependsOn("updateVersion")
    group = "alon"
    archiveFileName.set("${project.name}Desktop.jar")
    from(files("README.md", "LICENSE", "mod.json"))
    manifest.attributes("Main-Class" to "ice.Ice")
  }

  register<proguard.gradle.ProGuardTask>("proGuardTask") {
    // 重要：依赖 shadowJar，而不是 jar
    dependsOn(shadowJar)
    group = "alon"
    // 输入 JAR - 使用 ShadowJar 生成的 JAR
    injars("build/libs/${project.name}Desktop.jar")
    // 输出 JAR
    outjars("build/libs/${project.name}DesktopProGuard.jar")
    // 使用 Set 确保唯一性
    val jarSet = mutableSetOf<File>()
    // 添加编译类路径
    configurations.compileClasspath.get().files.filter { it.name.endsWith(".jar") }.forEach { jarSet.add(it) }
    // 库 JAR
    libraryjars(jarSet)

    libraryjars("${System.getProperty("java.home")}/jmods/")
    // ProGuard 配置文件
    configuration("proguard-rules.pro")
    // 添加详细输出选项

    printmapping("build/mapping.txt")
    printseeds("build/seeds.txt")
    printusage("build/usage.txt")
  }

  register<Copy>("myCopyJar") {
    group = "alon"
    dependsOn(shadowJar)
    from("build/libs/${project.name}Desktop.jar")
    into("C:/Users/$proUser/AppData/Roaming/Mindustry/mods")
  }
  register("updateVersion") {
    group = "alon"
    val file = Fi("mod.json")
    val parse = JsonReader().parse(file)
    val message = parse.get("version").asString().split("-")[1].toInt() + 1
    parse.get("version").set("Alpha-$message")
    file.writeString(parse.prettyPrint(JsonWriter.OutputType.json, 0))
  }

  register<JavaExec>("d8Compile") {
    group = "alon"
    dependsOn(shadowJar)
    val sdkDir = File(sdkRoot)
    val platformDir = File(sdkDir, "platforms")
    val platformRoot = platformDir.listFiles { f ->
      f.isDirectory && File(f, "android.jar").exists()
    }?.maxByOrNull { it.name } ?: throw GradleException("找不到有效的安卓平台")
    classpath(files("$sdkRoot/build-tools/36.0.0/lib/d8.jar"))
    mainClass.set("com.android.tools.r8.D8")
    val classpathFiles = (configurations.compileClasspath.get().files + configurations.runtimeClasspath.get().files + File(platformRoot, "android.jar"))
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
    val file = Fi("mod.json")
    val parse = JsonReader().parse(file)
    val message = parse.get("version")
    val split: List<String> = message.asString().split("-")
    val toInt= split[1].toInt()+1

    archiveFileName.set("${project.name}-${split[0]}-${toInt}.jar") //存档文件名
    val file1: File = file("build/libs/version")
    destinationDirectory.set(file1)
    from(zipTree("build/libs/${project.name}.jar"))
  }


  register<Copy>("myCopy") {
    group = "alon"
    dependsOn("deploy")
    from("build/libs/${project.name}.jar") //源
    into("C:/Users/$proUser/AppData/Roaming/Mindustry/mods")
  }
}/*

  register<JavaExec>("runWithJavaExec") {

        //  group = "zi"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("ice.MainKt")
        //  main("ice.Main")
        args = listOf("我喜欢你", "你喜欢我")
    }

tasks.register('deploy', Jar) {
    dependsOn(jarAndroid)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName = "${project.name}.jar"//存档文件名
    from {
        [zipTree("${buildLibDir}\\${project.name}Desktop.jar"),
         zipTree("${buildLibDir}\\${project.name}Android.jar")]
    }
}

tasks.register("myCopy", Copy) {
    dependsOn(deploy)
    from "${buildLibDir}\\$project.name" + ".jar"//源
    into {
        "C:/Users/$proUser/AppData/Roaming/Mindustry/mods"
    }
}
kotlin {
    jvmToolchain(17)
}
*/
