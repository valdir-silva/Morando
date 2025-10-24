pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Morando"
include(":app")
include(":core")
include(":domain")
include(":data")
include(":sdui")
include(":feature:feature-tasks")
include(":feature:feature-inventory")
include(":feature:feature-shopping")
include(":feature:feature-barcode")
include(":feature:feature-cooking")
include(":feature:feature-contas")
