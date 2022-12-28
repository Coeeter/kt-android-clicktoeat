pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ClickToEat"

include(
    ":app",
    ":core:network",
    ":core:data",
    ":core:domain",
    ":feature:auth",
    ":feature:common"
)
