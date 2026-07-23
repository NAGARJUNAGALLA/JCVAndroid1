package com.jcv.mocktests.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    courseId: String,
    onTestSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(1) } // 0 = Overview, 1 = Content
    
    // Placeholder Data - Replace with your ViewModel data fetch later
    val mockTests = listOf(
        "English Test 1", "English_Prepositions", "English_Basic Grammar",
        "English_Conditional Clauses", "English_Paragraph", "APTET", "MATHS", "MOCK TEST 1"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JCV MOCK TESTS", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Go Home logic if needed */ }) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
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
                .background(Color(0xFFF8FAFC))
        ) {
            Text(
                text = "APTET ENGLISH", // Replace with actual course title
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF1E90FF)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("OVERVIEW", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color(0xFF1E90FF) else Color.Gray) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("CONTENT", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color(0xFF1E90FF) else Color.Gray) }
                )
            }

            if (selectedTab == 1) {
                // Content Tab (List of Tests)
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockTests) { testName ->
                        TestListItem(testName = testName) {
                            // Pass the specific test ID to the next screen
                            onTestSelected(testName)
                        }
                    }
                }
            } else {
                // Overview Tab
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Course Overview Details Here")
                }
            }
        }
    }
}

@Composable
fun TestListItem(testName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = testName,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color.Gray
            )
        }
    }
}
