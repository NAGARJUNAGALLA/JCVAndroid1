package com.jcv.mocktests.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(onCourseSelected: (String) -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Screen (Courses will load here)")
        Button(onClick = { onCourseSelected("demo_course_id") }) {
            Text("Open Demo Course")
        }
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}
