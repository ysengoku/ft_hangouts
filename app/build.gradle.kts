plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ysengoku.ft_hangouts"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ysengoku.ft_hangouts"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
}