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
  /*      maven{
            url = uri("https://maven.pkg.github.com/stefanoq21/BottomSheetNavigator3")
        }*/
    }

}

rootProject.name = "BottomSheetNavigator3"
include(":app")
include(":material3-navigation")
