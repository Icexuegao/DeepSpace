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
}
val kotlinCompatibility = "2.2.0"
val proUser: String by extra
val sdkRoot: String by extra
val buildLibDir: String = layout.buildDirectory.dir("libs").get().asFile.path
val javaCompatibility = "17"
plugins {
    java
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    //implementation("com.github.Anuken.Arc:arc-core:v146")
    // implementation files("lib/backend.jar")
    // implementation "com.github.EB-wilson.UniverseCore:core:2.2.0"
    //compileOnly files("lib/UniverseCore-v2.2.0.jar")
    //  compileOnly 'com.github.Anuken.Mindustry:core:v147.1'
    //  compileOnly "com.github.Anuken.Arc:arc-core:v149"
    //  compileOnly files("lib/bmx.jar")
    //  implementation files("lib/pinyin4j-2.5.0.jar")
    //  implementation 'org.tomlj:tomlj:1.1.1'
    //  compileOnly "com.github.Tinylake:MindustryX:core:v2025.06.X10"
   // compileOnly(files("B:\\game\\mindustry-windows-64-bit\\jre\\Mindustry.jar"))
    compileOnly("com.github.Anuken.Mindustry:core:v150.1")
    // compileOnly("com.github.Anuken.Arc:flabel:v149")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinCompatibility")
    // compileOnly "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        kotlin.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("assets"))
    }
    kotlin {
        jvmToolchain(javaCompatibility.toInt())
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        sourceCompatibility = javaCompatibility
        targetCompatibility = javaCompatibility
        options.encoding = "UTF-8"
    }
    val jar by existing(Jar::class) {
        manifest.attributes("Main-Class" to "ice.Ice")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("${project.name}Desktop.jar")
        from(files("README.md", "LICENSE", "mod.json"))
        from(sourceSets.main.get().resources)
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
    }

    register<Copy>("myCopyJar") {
        dependsOn(jar)
        from("$buildLibDir/${project.name}Desktop.jar")
        into("C:/Users/$proUser/AppData/Roaming/Mindustry/mods")
    }

    register<JavaExec>("runWithJavaExec") {
        group = "kj"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("ice.MainKt")
        // main("ice.Main")
        args = listOf("我喜欢你", "你喜欢我")
    }

    register<Exec>("d8Compile") {
        dependsOn(jar)
        // 在配置阶段设置参数
        val sdkDir = File(sdkRoot)
        val platformDir = File(sdkDir, "platforms")
        val platformRoot = platformDir.listFiles { f ->
            f.isDirectory && File(f, "android.jar").exists()
        }?.maxByOrNull { it.name }
            ?: throw GradleException("No valid Android platform found")
        val dependencies = configurations.compileClasspath.get() +
                configurations.runtimeClasspath.get() +
                listOf(File(platformRoot, "android.jar"))
        val d8Path = File(sdkDir, "build-tools/36.0.0/d8.bat").absolutePath

        commandLine(
            listOf(d8Path) +
                    dependencies.map { it.absolutePath } +
                    listOf(
                        "--min-api", "26",
                        "--output", "$buildLibDir/${project.name}Android.jar",
                        "$buildLibDir/${project.name}Desktop.jar"
                    )
        )

        workingDir = file(buildLibDir)
    }

    register<Jar>("deploy") {
        dependsOn("d8Compile")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("${project.name}.jar") //存档文件名
        from(
            zipTree("$buildLibDir/${project.name}Desktop.jar"),
            zipTree("$buildLibDir/${project.name}Android.jar")
        )
    }

    register<Copy>("myCopy") {
        dependsOn("deploy")
        from("$buildLibDir/${project.name}.jar") //源
        into("C:/Users/$proUser/AppData/Roaming/Mindustry/mods")
    }
}

























/*

buildscript {
    ext {
        proUser = System.getProperty("user.name")
        sdkRoot = System.getenv("ANDROID_HOME")
    }
    repositories {
        mavenLocal()
        mavenCentral()
        maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://maven.aliyun.com/nexus/content/repositories/jcenter' }
        maven { url 'https://repo.huaweicloud.com/repository/maven/' }
        maven { url "https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository" }
        maven { url "https://maven.aliyun.com/repository/public" }
        maven { url 'https://jitpack.io' }
    }

}
plugins {
    id("java")
    id 'org.jetbrains.kotlin.jvm' version '2.2.0'
}
repositories {
    mavenCentral()
    mavenLocal()
    maven{ url 'https://maven.aliyun.com/nexus/content/groups/public/'}
    maven { url 'https://maven.aliyun.com/nexus/content/repositories/jcenter' }
    maven { url 'https://repo.huaweicloud.com/repository/maven/' }
    maven { url "https://maven.aliyun.com/repository/public" }
    maven { url "https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository" }
    maven { url 'https://jitpack.io' }
}

dependencies {
    //implementation("com.github.Anuken.Arc:arc-core:v146")
    // implementation files("lib/backend.jar")
    // implementation "com.github.EB-wilson.UniverseCore:core:2.2.0"
    //compileOnly files("lib/UniverseCore-v2.2.0.jar")
   // compileOnly 'com.github.Anuken.Mindustry:core:v147.1'
  //  compileOnly "com.github.Anuken.Arc:arc-core:v149"


   // compileOnly files("lib/bmx.jar")
  //  implementation files("lib/pinyin4j-2.5.0.jar")
  //  implementation 'org.tomlj:tomlj:1.1.1'
   // compileOnly "com.github.Tinylake:MindustryX:core:v2025.06.X10"
    compileOnly files("B:\\game\\mindustry-windows-64-bit\\jre\\Mindustry.jar")
    compileOnly "com.github.Anuken.Arc:flabel:v149"
    implementation "org.jetbrains.kotlin:kotlin-stdlib"
    // compileOnly "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
}

sourceSets {
    main {
        java.srcDirs = ['src']
        //noinspection GrUnresolvedAccess
//        kotlin.srcDirs = ['src']
        resources.srcDir('assets')
    }
}

def buildLibDir = project.layout.buildDirectory.asFile.get().name + "\\libs"

jar {
    manifest.attributes('Main-Class': 'ice.Ice')
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName = "${project.name}Desktop.jar"
    from files("README.md", "LICENSE","mod.json")

    from(sourceSets.main.resources.srcDirs)
    from {
        //noinspection GroovyMissingReturnStatement
        configurations.runtimeClasspath.collect {
            print(it.name)
            it.isDirectory() ? it : zipTree(it)
        }
    }
}

tasks.register("myCopyJar", Copy) {
    dependsOn(jar)
    from "${buildLibDir}\\$project.name" + "Desktop.jar"
    into {
        "C:/Users/$proUser/AppData/Roaming/Mindustry/mods"
    }
}
tasks.register("text") {

}
tasks.register('runWithJavaExec', JavaExec) {
    group = "kj"
    classpath = sourceSets.main.runtimeClasspath

    mainClass = "ice.MainKt"
    // main("ice.Main")
    args("我喜欢你", "你喜欢我")
}

tasks.withType(JavaCompile).configureEach {
    //目标的兼容性
    options.encoding = "UTF-8"
}

tasks.register('jarAndroid') {
    dependsOn(jar)
    doLast {

        if (!sdkRoot || !new File("$sdkRoot").exists()) throw new GradleException("No valid Android SDK found. ANDROID_HOME")

        def platformRoot = new File("$sdkRoot/platforms/").listFiles().sort().reverse().find { f -> new File(f, "android.jar").exists() }

        if (!platformRoot) throw new GradleException("No android.jar found. Ensure that you have an Android platform installed.")

        //collect dependencies needed for desugaring
        def dependencies = (configurations.compileClasspath.asList() + configurations.runtimeClasspath.asList() + [new File(platformRoot, "android.jar")]).collect { "--classpath $it.path" }.join(" ")

        //dex 和 desugar 文件 - 这需要 PATH 中的 d8
        "$sdkRoot/build-tools/35.0.0/d8.bat $dependencies --min-api 26 --output ${project.name}Android.jar ${project.name}Desktop.jar"
                .execute(null, new File(buildLibDir)).waitForProcessOutput(System.out, System.err)
    }
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
