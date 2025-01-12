package com.stefanoq21.bottomsheetnavigator3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
                                    },
                                    onClickBack = {
                                        onBackPressedDispatcher.onBackPressed()
                                    },
                                    onClickGoToBottomSheet = {
                                        navController.navigate(Screen.BottomSheetWithParameters("testId-123"))
                                    }
                                )
                            }
                            bottomSheet<Screen.BottomSheetWithParameters> { backStackEntry ->
                                val id =
                                    backStackEntry.toRoute<Screen.BottomSheetWithParameters>().id
                                BSWithParametersLayout(id)
                            }
                        }
                    }
                }
            }
        }
    }
}



