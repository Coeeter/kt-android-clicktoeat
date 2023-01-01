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

include(":app")
include(":core:network")
include(":core:data")
include(":core:domain")
include(":feature:common")
include(":feature:auth")
include(":feature:restaurant")
