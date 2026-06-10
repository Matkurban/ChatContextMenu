import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

group = "io.github.matkurban"
version = libs.versions.library.get()

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (project.findProperty("signing.keyId") != null) {
        signAllPublications()
    }
    coordinates("io.github.matkurban", "chatcontextmenu", version.toString())
    pom {
        name.set("ChatContextMenu")
        description.set("Compose Multiplatform chat context menu library")
        url.set("https://github.com/Matkurban/ChatContextMenu")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("matkurban")
                name.set("Matkurban")
            }
        }
        scm {
            url.set("https://github.com/Matkurban/ChatContextMenu")
            connection.set("scm:git:git://github.com/Matkurban/ChatContextMenu.git")
            developerConnection.set("scm:git:ssh://git@github.com/Matkurban/ChatContextMenu.git")
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ChatContextMenu"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    android {
        namespace = "io.github.matkurban.chatcontextmenu"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
        }
    }
}
