import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinPluginSerialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation("io.ktor:ktor-client-cio:3.0.0-wasm2")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0-wasm2")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(project.dependencies.platform("io.github.jan-tennert.supabase:bom:2.2.3-wasm0"))
            implementation("io.github.jan-tennert.supabase:gotrue-kt")
            implementation("io.github.jan-tennert.supabase:realtime-kt")
            implementation("io.github.jan-tennert.supabase:storage-kt")
            implementation("io.github.jan-tennert.supabase:functions-kt")
            implementation("io.github.jan-tennert.supabase:postgrest-kt")
            implementation("io.github.jan-tennert.supabase:compose-auth")
            implementation("io.github.jan-tennert.supabase:compose-auth-ui")

            implementation("io.ktor:ktor-client-core:3.0.0-wasm2")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm2")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm2")

            api("io.insert-koin:koin-core:3.6.0-alpha3")
            api("io.insert-koin:koin-compose:1.2.0-alpha3")

            api("moe.tlaster:precompose:1.6.0")
            api("moe.tlaster:precompose-viewmodel:1.6.0") // For ViewModel intergration
            api("moe.tlaster:precompose-koin:1.6.0") // For Koin intergration

            implementation("io.github.aakira:napier:2.7.1")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("io.ktor:ktor-client-cio:3.0.0-wasm2")
        }
    }
}

android {
    namespace = "com.helloanwar.donow"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.helloanwar.donow"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.helloanwar.donow"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}