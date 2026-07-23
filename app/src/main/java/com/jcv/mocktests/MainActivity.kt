package com.jcv.mocktests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jcv.mocktests.ui.auth.LoginScreen
import com.jcv.mocktests.ui.exam.ExamScreen
import com.jcv.mocktests.ui.exam.ExamViewModel
import com.jcv.mocktests.ui.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    
    // Determine start destination based on Firebase Auth state
    val startDest = if (auth.currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDest) {
        
        composable("login") {
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onCourseSelected = { courseId ->
                    navController.navigate("instructions/$courseId")
                },
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            )
        }

        // Exam Flow
        composable("exam") {
            // Shared ViewModel scoped to the NavBackStackEntry or Activity
            val examViewModel: ExamViewModel = viewModel()
            ExamScreen(
                viewModel = examViewModel,
                onFinalSubmit = {
                    navController.navigate("results") { popUpTo("exam") { inclusive = true } }
                }
            )
        }
    }
}
