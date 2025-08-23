package com.stefanoq21.bottomsheetnavigator3.presentation.navigation

import kotlinx.serialization.Serializable


sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object BottomSheetFullScreen : Screen

    @Serializable
    data class Zoom(val id: String) : Screen

    @Serializable
    data class BottomSheetWithParameters(val id: String) : Screen

    @Serializable
    data object BottomSheetWithCloseScreen : Screen

    @Serializable
    data object DialogTestScreen : Screen

}


