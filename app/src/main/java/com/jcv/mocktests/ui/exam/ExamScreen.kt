package com.jcv.mocktests.ui.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcv.mocktests.data.model.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    onFinalSubmit: () -> Unit
) {
    val sections by viewModel.sections.collectAsState()
    val answers by viewModel.userAnswers.collectAsState()
    val currentSecIdx by viewModel.currentSectionIndex.collectAsState()
    val currentQIdx by viewModel.currentQuestionIndex.collectAsState()
    val tempSelected by viewModel.tempSelection.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentSection = sections.getOrNull(currentSecIdx)
    val currentQuestion = currentSection?.questions?.getOrNull(currentQIdx)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Text(
                    text = "Question Palette",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                
                // Palette Grid
                if (currentSection != null) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentSection.questions.size) { idx ->
                            val st = answers[Pair(currentSecIdx, idx)]?.status ?: Status.NOT_VISITED
                            val bgColor = when (st) {
                                Status.ANSWERED -> Color(0xFF27AE60)
                                Status.NOT_ANSWERED -> Color(0xFFE74C3C)
                                Status.MARKED_FOR_REVIEW -> Color(0xFF9B59B6)
                                Status.ANSWERED_AND_MARKED -> Color(0xFF9B59B6)
                                Status.NOT_VISITED -> Color.White
                            }
                            val textColor = if (st == Status.NOT_VISITED) Color.DarkGray else Color.White

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(bgColor, shape = RoundedCornerShape(4.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.loadQuestion(currentSecIdx, idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${idx + 1}", color = textColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("JCV CBT Simulator", fontSize = 16.sp, color = Color.White) },
                    actions = {
                        val minutes = timeLeft / 60
                        val seconds = timeLeft % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF104E8B))
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.handleMarkForReviewAndNext() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B59B6))
                    ) {
                        Text("Mark & Next")
                    }
                    Button(
                        onClick = { viewModel.handleClearResponse() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Clear")
                    }
                    Button(
                        onClick = { viewModel.handleSaveAndNext() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E90FF))
                    ) {
                        Text("Save & Next")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Question Text
                if (currentQuestion != null) {
                    Text(
                        text = "Q${currentQIdx + 1}. ${currentQuestion.text}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Radio Button Options
                    currentQuestion.options.forEachIndexed { optIdx, optText ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (tempSelected == optIdx) Color(0xFF1E90FF) else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.tempSelection.value = optIdx }
                                .padding(12.dp)
                        ) {
                            RadioButton(
                                selected = (tempSelected == optIdx),
                                onClick = { viewModel.tempSelection.value = optIdx }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = optText, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
