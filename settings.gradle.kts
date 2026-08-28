pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("http://maven.scuba-sc.org/repository/maven-public/")
            isAllowInsecureProtocol = true
        }
        maven { url = uri("https://nexus.isofirm.com/repository/maven-public/") }
        maven { url = uri("https://repository.aspose.com/repo/") }
    }
}

rootProject.name = "ocr-v3"
include(":app")
