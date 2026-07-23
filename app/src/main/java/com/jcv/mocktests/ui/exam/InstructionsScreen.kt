package com.jcv.mocktests.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionsScreen(
    testId: String,        // Changed from courseId to testId
    onStartExam: () -> Unit,
    onBack: () -> Unit     // Added onBack parameter
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instructions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF104E8B))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("General Instructions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("1. Read all questions carefully.")
            Text("2. Do not close the app during the test.")
            Text("3. You can review your answers before submitting.")
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onStartExam,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E90FF))
            ) {
                Text("Start Test", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
