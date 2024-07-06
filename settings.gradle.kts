import java.util.Properties

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
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://maven.pkg.github.com/stefanoq21/BottomSheetNavigator3")
           credentials {
                val properties = Properties()
                properties.load(file("local.properties").reader())
                username = properties.getProperty("githubUserName") as String
                password = properties.getProperty("githubToken") as String
            }
        }
    }

}

rootProject.name = "BottomSheetNavigator3"
include(":app")
include(":material3-navigation")
