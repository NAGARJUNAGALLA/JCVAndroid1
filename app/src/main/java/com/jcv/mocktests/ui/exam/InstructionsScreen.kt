package com.jcv.mocktests.ui.exam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun InstructionsScreen(courseId: String, onStartExam: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Instructions for Course: $courseId")
        Button(onClick = onStartExam) {
            Text("I Agree - Start Exam")
        }
    }
}
