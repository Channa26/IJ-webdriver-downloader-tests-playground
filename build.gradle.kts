plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.playground"
version = "1.0-SNAPSHOT"

val robotVersion = "0.11.23"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.6")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.15.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.24")
    testImplementation("com.intellij.remoterobot:remote-robot:$robotVersion")
    testImplementation("com.intellij.remoterobot:ide-launcher:$robotVersion")
    testImplementation("com.intellij.remoterobot:remote-fixtures:$robotVersion")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.mockito:mockito-core:5.14.0")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("robot-server.host.public", "true")
        systemProperty("idea.trust.all.projects", "true")
        systemProperty("ide.open.last.projects", "false")
        systemProperty("jb.consents.confirmation.enabled", "false")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("ide.mac.file.chooser.native", "false")
        systemProperty("jbScreenMenuBar.enabled", "false")
        systemProperty("apple.laf.useScreenMenuBar", "false")
        systemProperty("ide.show.tips.on.startup.default.value", "false")
    }

    downloadRobotServerPlugin {
        version = robotVersion
    }
}
