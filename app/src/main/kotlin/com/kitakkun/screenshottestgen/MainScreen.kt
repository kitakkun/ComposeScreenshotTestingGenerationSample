package com.kitakkun.screenshottestgen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Compose Preview Screenshot Testing Auto-Generation",
            style = MaterialTheme.typography.displaySmall,
        )
        HorizontalDivider()
        Text("1. Generate screenshot tests", style = MaterialTheme.typography.headlineMedium)
        Text("Run `./gradlew generateScreenshotTest` to generate screenshotTest source files.", style = MaterialTheme.typography.bodyLarge)
        Text("2. Generate reference images for screenshot tests.", style = MaterialTheme.typography.headlineMedium)
        Text("Run `./gradlew updateDebugScreenshotTest`", style = MaterialTheme.typography.bodyLarge)
        Text("3. Make some changes on UI components", style = MaterialTheme.typography.headlineMedium)
        Text("For example, modify 'this line' of text in your editor.", style = MaterialTheme.typography.bodyLarge)
        Text("4. Validate and Check results.", style = MaterialTheme.typography.headlineMedium)
        Text("Run `./gradlew validateDebugScreenshotTest`, then open `app/build/reports/screenshotTest/preview/debug/index.html`.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
