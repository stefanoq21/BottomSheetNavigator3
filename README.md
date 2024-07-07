# Material3 BottomSheet Navigation

This library provides a navigation solution for Compose projects using Material3 BottomSheets. It allows you to define your BottomSheet as navigation routes, eliminating the need for the `androidx.compose.material.navigation` and ` androidx.compose.material:material` libraries. This simplifies your app's dependencies and ensures a consistent Material3 experience.
This library also leverages the new functionality from `androidx.navigation:navigation-compose:2.8.0-beta0X` to allow you to define routes with serialized classes.

## Implementation

You can follow the implementation approach used in the  [app](https://github.com/stefanoq21/BottomSheetNavigator3/tree/main/app "app") module. Alternatively, you can find a detailed explanation below.

### Dependencies
Refine your `settings.gradle.kts`
```
...
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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
```
Add your github credential to your `local.properties`
```
...
githubToken=blablabla
githubUserName=your-github-username
```
Add the dependencies to your `libs.versions.toml`
```
[versions]
...
navigationCompose = "2.8.0-beta04"
material3Navigation = "0.0.3"
material3 = "1.3.0-beta04"

[libraries]
...
androidx-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3"}
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigationCompose" }
material3-navigation = { group = "io.github.stefanoq21", name = "material3-navigation", version.ref = "material3Navigation" }

```
In your `build.gradle.kts` implement your dependencies:
```
...
dependencies {
...
 implementation(libs.androidx.material3)
 implementation(libs.androidx.navigation.compose)
 implementation(libs.material3.navigation)
```
### Usage
Define your **BottomSheetNavigator**
```
...
   val bottomSheetNavigator =
                    rememberBottomSheetNavigator(skipPartiallyExpanded = true/false)
                val navController = rememberNavController(bottomSheetNavigator)
```
Add the **ModalBottomSheetLayout** on top of the **NavHost** component and pass the **bottomSheetNavigator** as parameter:
```
ModalBottomSheetLayout(
                        modifier = Modifier
                            .fillMaxSize(),
                        bottomSheetNavigator = bottomSheetNavigator
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home
                        ) {
...
```
Define your routes as strings or data class (depend on the compose navigation version you are using):
```
...
   bottomSheet<Screen.BottomSheetFullScreen> {
                                BSFullScreenLayout()
                            }
  bottomSheet("BottomSheetFullScreen") {
                                BSFullScreenLayout()
                            }
...
```
Everythign is ready! Just navigate to your new destination as usual:
```
...
Button(onClick = { navController.navigate(Screen.BottomSheetFullScreen) }) {
                                        Text(text = "BottomSheetFullScreen")
                                    }
...
```
### Customization

The library currently supports the same customization options of the standard `androidx.compose.material3.ModalBottomSheet`. You can customize the appearance of the all the bottomsheets used in your navigation graph by passing the parameters to the `ModalBottomSheetLayout`.

## Contributing

We welcome contributions to this library! If you have bug reports, feature requests, or code improvements, please feel free to create a pull request. I appreciate your help in making this library even better.

## License

   Copyright 2024 stefanoq21

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

