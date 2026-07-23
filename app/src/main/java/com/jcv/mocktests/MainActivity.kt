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
import com.jcv.mocktests.ui.course.CourseDetailsScreen
import com.jcv.mocktests.ui.course.InstructionsScreen
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
                    val encodedId = URLEncoder.encode(courseId, StandardCharsets.UTF_8.toString())
                    // STEP 1: Go to Course Details first
                    navController.navigate("course_details/$encodedId")
                },
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            )
        }

        // STEP 2: Course Details Screen (Shows Overview / Content Tabs)
        composable("course_details/{courseId}") { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8.toString())
            
            CourseDetailsScreen(
                courseId = courseId,
                onTestSelected = { testId ->
                    val encodedTestId = URLEncoder.encode(testId, StandardCharsets.UTF_8.toString())
                    // STEP 3: Go to Instructions before the exam
                    navController.navigate("instructions/$encodedTestId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // STEP 4: Instructions Screen
        composable("instructions/{testId}") { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("testId") ?: ""
            val testId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8.toString())
            
            InstructionsScreen(
                testId = testId,
                onStartExam = { 
                    val encodedTestIdForExam = URLEncoder.encode(testId, StandardCharsets.UTF_8.toString())
                    navController.navigate("exam/$encodedTestIdForExam") 
                },
                onBack = { navController.popBackStack() }
            )
        }

        // STEP 5: Actual Exam Simulator
        composable("exam/{testId}") { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("testId") ?: ""
            val testId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8.toString())
            
            val examViewModel: ExamViewModel = viewModel()
            // TODO: examViewModel.loadTest(testId)
            
            ExamScreen(
                viewModel = examViewModel,
                onFinalSubmit = {
                    navController.navigate("results") { popUpTo("exam") { inclusive = true } }
                }
            )
        }
        
        composable("results") {
            // ResultsScreen()
        }
    }
}
