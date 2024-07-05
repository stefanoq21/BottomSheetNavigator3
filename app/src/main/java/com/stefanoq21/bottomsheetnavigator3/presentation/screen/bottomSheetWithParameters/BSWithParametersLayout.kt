package com.stefanoq21.bottomsheetnavigator3.presentation.screen.bottomSheetWithParameters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun BSWithParametersLayout(id: String) {
    Column(Modifier.padding(12.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = AnnotatedString.fromHtml( "BSWithParametersLayout - id: <b>$id</b>"),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}


