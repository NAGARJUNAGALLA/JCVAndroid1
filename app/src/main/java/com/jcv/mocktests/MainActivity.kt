package com.jcv.mocktests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jcv.mocktests.ui.auth.LoginScreen
import com.jcv.mocktests.ui.exam.ExamScreen
import com.jcv.mocktests.ui.exam.ExamViewModel
import com.jcv.mocktests.ui.home.HomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
                    // 1. Encode the ID to safely handle special characters or spaces
                    val encodedId = URLEncoder.encode(courseId, StandardCharsets.UTF_8.toString())
                    
                    // 2. Navigate directly to the exam screen route since there is no instructions route
                    navController.navigate("exam/$encodedId")
                },
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            )
        }

        // 3. Update the exam route to accept the courseId argument
        composable("exam/{courseId}") { backStackEntry ->
            // Retrieve and decode the ID
            val encodedId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8.toString())
            
            // Shared ViewModel scoped to the NavBackStackEntry or Activity
            val examViewModel: ExamViewModel = viewModel()
            
            // TODO: If your ExamViewModel needs to load data based on the ID, 
            // you should call something like: examViewModel.loadExam(courseId) here

            ExamScreen(
                viewModel = examViewModel,
                onFinalSubmit = {
                    navController.navigate("results") { popUpTo("exam") { inclusive = true } }
                }
            )
        }
        
        // Placeholder for results screen so it doesn't crash when hitting "onFinalSubmit"
        composable("results") {
            // Add your ResultsScreen() here later
        }
    }
}
