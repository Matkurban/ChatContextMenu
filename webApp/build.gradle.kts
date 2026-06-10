import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser {
            binaries.executable()
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            binaries.executable()
        }
    }

    sourceSets {
        val webMain by creating {
            dependencies {
                implementation(projects.sample)
                implementation(libs.compose.ui)
            }
        }
        jsMain {
            dependsOn(webMain)
        }
        wasmJsMain {
            dependsOn(webMain)
        }
    }
}
