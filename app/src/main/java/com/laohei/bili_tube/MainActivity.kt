package com.laohei.bili_tube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.laohei.bili_tube.app.App
import com.laohei.bili_tube.ui.theme.Bili_tubeTheme
import com.laohei.bili_tube.utill.HttpClientFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bili_tubeTheme {
                App()
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        HttpClientFactory.client.close()
    }
}