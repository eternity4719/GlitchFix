import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.20"
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    `maven-publish`
}

group = "me.albert"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.tcoded.com/releases") {
        name = "tcoded-releases"
    }
    maven("https://repo.codemc.io/repository/maven-public/")

}

java {
    // 关键：必须加上这行，发布时才会生成并附带源码！
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

val targetJavaVersion = 25

kotlin {
    jvmToolchain(targetJavaVersion)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}





publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // Java 项目，或 components["kotlin"] 对于 Kotlin Multiplatform
            artifactId = "glitchfix"
        }
    }

    repositories {
        google()
        mavenLocal() // 发布到本地仓库
    }
}

dependencies {
    paperweight.foliaDevBundle("26.1.2.build.+")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    compileOnly("me.albert:corelib:1.0.0")
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21")
    }
    shadowJar {
        relocate("com.tcoded.folialib", "me.albert.core.folialib")
    }
}

tasks.build {
    dependsOn("shadowJar")
}


tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
