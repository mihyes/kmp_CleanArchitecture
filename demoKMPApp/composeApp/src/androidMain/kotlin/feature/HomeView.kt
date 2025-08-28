package feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.demokmpapp.Greeting
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import demokmpapp.composeapp.generated.resources.Res
import demokmpapp.composeapp.generated.resources.compose_multiplatform


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable


class HomeView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            TestApp()
        }
    }
}

@Composable
@Preview
fun TestApp() {
   MaterialTheme {
       var showContent by remember { mutableStateOf(false) }
       Column(
           modifier = Modifier
               .background(MaterialTheme.colorScheme.primaryContainer)
               .safeContentPadding()
               .fillMaxSize(),
           horizontalAlignment = Alignment.CenterHorizontally,
            ) {
           Button(onClick = { showContent = !showContent }) {
               Text("Click!")
           }
           AnimatedVisibility(showContent) {
               val greeting = remember { Greeting().greet() }
               Column(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalAlignment = Alignment.CenterHorizontally,
                   ) {
                   Image(painterResource(Res.drawable.compose_multiplatform), null)
                   Text("Compose: $greeting")
               }
           }
       }
   }
}


@Preview
@Composable
fun AppAndroidPreview() {
//    TestApp()
}