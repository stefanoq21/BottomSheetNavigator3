package com.stefanoq21.bottomsheetnavigator3

import android.os.Bundle
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.stefanoq21.bottomsheetnavigator3.presentation.navigation.Screen
import com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetFullSize.BSFullScreenLayout
import com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetWithParameters.BSWithParametersLayout
import com.stefanoq21.bottomsheetnavigator3.presentation.theme.BottomSheetNavigator3Theme
import com.stefanoq21.material3_bs_navigation.ModalBottomSheetLayout
import com.stefanoq21.material3_bs_navigation.bottomSheet
import com.stefanoq21.material3_bs_navigation.rememberBottomSheetNavigator

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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    ModalBottomSheetLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(12.dp),
                        bottomSheetNavigator = bottomSheetNavigator
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

                                    Button(onClick = {
                                        navController.navigate(
                                            Screen.BottomSheetWithParameters(
                                                "testId-123"
                                            )
                                        )
                                    }) {
                                        Text(text = "BottomSheetWithParameters")
                                    }

                                    var showBottomSheet by remember {
                                        mutableStateOf(false)
                                    }
                                    Button(onClick = {showBottomSheet= true }) {
                                        Text(text = "BottomSheetOutsideNav")
                                    }

                                    if(showBottomSheet){
                                        ModalBottomSheet(
                                            onDismissRequest = {
                                                showBottomSheet= false
                                            },
                                            content ={
                                                Text(text = "ModalBottomSheet outside navigation")
                                            },
                                        )
                                    }

                                }

                            }
                            composable<Screen.Zoom> { backStackEntry ->
                                val id = backStackEntry.toRoute<Screen.Zoom>().id
                                Text(
                                    text = "Zoom  param:$id",
                                )
                            }

                            bottomSheet<Screen.BottomSheetFullScreen> {
                                BSFullScreenLayout()
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



