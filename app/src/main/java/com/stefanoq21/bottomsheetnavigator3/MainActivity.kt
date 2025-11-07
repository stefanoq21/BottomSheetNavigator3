package com.stefanoq21.bottomsheetnavigator3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.stefanoq21.bottomsheetnavigator3.presentation.navigation.Screen
import com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetFullSize.BSFullScreenLayout
import com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetWithClose.BSWithCloseLayout
import com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetWithParameters.BSWithParametersLayout
import com.stefanoq21.bottomsheetnavigator3.presentation.theme.BottomSheetNavigator3Theme
import com.stefanoq21.material3.navigation.ModalBottomSheetLayout
import com.stefanoq21.material3.navigation.bottomSheet
import com.stefanoq21.material3.navigation.rememberBottomSheetNavigator
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BottomSheetNavigator3Theme {

                val bottomSheetNavigator =
                    rememberBottomSheetNavigator(skipPartiallyExpanded = true)
                val navController = rememberNavController(bottomSheetNavigator)
                val routes = navController.currentBackStackEntryAsState()
                navController.addOnDestinationChangedListener { controller, _, _ ->
                    Log.d("BackStackLog", "BackStack: ${routes.value}")
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    ModalBottomSheetLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(12.dp),
                        /*sheetModifier = Modifier.windowInsetsPadding(
                            WindowInsets.systemBars.only(WindowInsetsSides.Top)
                        ),*/
                        bottomSheetNavigator = bottomSheetNavigator,
                        /*properties = ModalBottomSheetProperties(
                            isAppearanceLightStatusBars = !isSystemInDarkTheme(),
                            isAppearanceLightNavigationBars = !isSystemInDarkTheme()
                        )*/
                    ) {

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home
                        ) {
                            composable<Screen.Home> {
                                Column {
                                    Text(
                                        text = "Bottom sheet navigation examples",
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center,
                                    )
                                    Spacer(modifier = Modifier.size(12.dp))

                                    Button(onClick = { navController.navigate(Screen.BottomSheetFullScreen) }) {
                                        Text(text = "BottomSheetFullScreen")
                                    }

                                    Button(onClick = { navController.navigate(Screen.BottomSheetWithCloseScreen) }) {
                                        Text(text = "BottomSheetWithCloseScreen")
                                    }

                                    Button(onClick = {
                                        navController.navigate(
                                            Screen.BottomSheetWithParameters(
                                                "testId-123"
                                            )
                                        )
                                    }) {
                                        Text(text = "BottomSheetWithParameters")
                                    }

                                    Button(onClick = {
                                        navController.navigate(
                                            Screen.Zoom(
                                                "testId-123"
                                            )
                                        )
                                    }) {
                                        Text(text = "Zoom")
                                    }
                                    Button(onClick = { navController.navigate(Screen.DialogTestScreen) }) {
                                        Text("dialog")
                                    }

                                }

                            }
                            composable<Screen.Zoom> { backStackEntry ->
                                val id = backStackEntry.toRoute<Screen.Zoom>().id
                                Column {
                                    Text(
                                        text = "Zoom  param:$id",
                                    )
                                    Button(onClick = { navController.navigate(Screen.BottomSheetWithCloseScreen) }) {
                                        Text(text = "BottomSheetWithCloseScreen")
                                    }
                                }

                            }

                            bottomSheet<Screen.BottomSheetFullScreen> {
                                BSFullScreenLayout()
                            }

                            bottomSheet<Screen.BottomSheetWithCloseScreen> {
                                BSWithCloseLayout(
                                    onClickGoToZoom = {
                                        navController.navigate(
                                            Screen.Zoom(
                                                "${Random.nextInt(0, 100)}"
                                            )
                                        )
                                    },
                                    onClickClose = {
                                        navController.popBackStack()
                                        // navController.navigate(Screen.DialogTestScreen)
                                    },
                                    onClickBack = {
                                        navController.navigateUp()
                                        // We should not be calling onBackPressed because the event is sent to both navigators at the same time.
//                                        onBackPressedDispatcher.onBackPressed()
                                        // You can uncomment this line and do the following to see the issue:
                                        // 1. Home - go to Zoom.
                                        // 2. Zoom - go to BottomSheetWithCloseScreen.
                                        // 3. Click "Close for back".

                                        // Expected: The bottom sheet closes and Zoom page is displayed.
                                        // Actual: The bottom sheet closes as expected but Zoom is also popped from the backstack.
                                    },
                                    onClickGoToBottomSheet = {
                                        navController.navigate(Screen.BottomSheetWithParameters("testId-123"))
                                    }
                                )

                                Button(onClick = { navController.navigate(Screen.DialogTestScreen) }) {
                                    Text("navigation dialog")
                                }
                                var showDialog by rememberSaveable { mutableStateOf(false) }

                                Button(onClick = { showDialog = true }) {
                                    Text("normal dialog")
                                }

                                if (showDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDialog = false },
                                        title = { Text("Test Dialog") },
                                        text = { Text("test") },
                                        confirmButton = {
                                            Button(onClick = {
                                                showDialog = false
                                            }) { Text("Dismiss") }
                                        }
                                    )
                                }
                            }
                            bottomSheet<Screen.BottomSheetWithParameters> { backStackEntry ->
                                val id =
                                    backStackEntry.toRoute<Screen.BottomSheetWithParameters>().id
                                BSWithParametersLayout(id, onPop = navController::popBackStack, openFullscreen = {
                                    navController.navigate(Screen.BottomSheetFullScreen)
                                })
                            }


                            dialog<Screen.DialogTestScreen> {

                                Column(Modifier.background(Color.White)) {
                                    Text(
                                        text = "Dialog",
                                    )
                                    Button(onClick = { onBackPressedDispatcher.onBackPressed() }) {
                                        Text(text = "exit")
                                    }
                                    Button(onClick = { navController.navigate(Screen.DialogTestScreen) }) {
                                        Text(text = "Button")
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
    }
}



