pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QuranEasy"

include(":app")
include(":core")
include(":feature_home")
include(":feature_quran")
include(":feature_tasbih")
include(":feature_prayer")
include(":feature_dhikr")

