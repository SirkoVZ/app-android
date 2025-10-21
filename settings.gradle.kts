// Diese Datei definiert die Projekt-Settings und Plugin-Repositories

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
        // Für MPAndroidChart (falls du das nutzen willst)
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "VolkszaehlerApp"
include(":app")