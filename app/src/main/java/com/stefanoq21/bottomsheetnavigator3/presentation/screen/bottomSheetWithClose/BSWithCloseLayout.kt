package com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetWithClose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BSWithCloseLayout(onClickClose: () -> Unit) {
    Column(modifier = Modifier) {
        Text(
            text = "BottomSheet Title",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = "This is the content of the bottom sheet. " +
                        "You can replace this with your custom content, " +
                        "such as a list, a form, or any other composable you like.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onClickClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Close")
        }
    }
}