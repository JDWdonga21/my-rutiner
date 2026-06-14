package com.example.mytodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mytodolist.notification.RequestNotificationPermission
import com.example.mytodolist.ui.navigation.AppNavGraph
import com.example.mytodolist.ui.theme.MyTodoListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTodoListTheme {
                RequestNotificationPermission()
                AppNavGraph()
            }
        }
    }
}
