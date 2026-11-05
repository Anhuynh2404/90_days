package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalCardElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.NeonFlame
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.Project90ViewModel

@Composable
fun MovementScreen(
    viewModel: Project90ViewModel,
    modifier: Modifier = Modifier
) {
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
    val hasHardwareSensor by viewModel.stepCounterManager.hasHardwareSensor.collectAsStateWithLifecycle()

    val logsMap = remember(todayLogs) { todayLogs.associateBy { it.habitId } }

    val stepLog = logsMap["pedometer_10k"]
    val stepCount = stepLog?.progressValue ?: 0
    val stepGoal = 10000
    val stepPercent = (stepCount.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f)

    val pushupLog = logsMap["pushups_50"]
    val pushupCount = pushupLog?.progressValue ?: 0

    val squatLog = logsMap["squats_100"]
    val squatCount = squatLog?.progressValue ?: 0

    val coldShowerDone = logsMap["cold_shower"]?.isCompleted == true
    val workoutCount = logsMap["workout_weekly"]?.progressValue ?: 0
    val footballDone = logsMap["football_weekly"]?.isCompleted == true

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("movement_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "MÔ-ĐUN VẬN ĐỘNG",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = NeonFlame
                )
                Text(
                    text = "Đốt mỡ thụ động, bứt phá sức mạnh cơ bắp & sức bền",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Pedometer 10,000 Steps Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2B3344), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(NeonFlame.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsRun,
                                    contentDescription = "Pedometer",
                                    tint = NeonFlame,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "BỘ ĐẾM BƯỚC CHÂN",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Mục tiêu 10.000 bước/ngày",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Sensor status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF222836), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = "Sensor",
                                tint = if (hasHardwareSensor) SuccessGreen else CyberCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (hasHardwareSensor) "Sensor ON" else "Mô phỏng",
                                fontSize = 11.sp,
                                color = if (hasHardwareSensor) SuccessGreen else CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step Metrics display
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = String.format("%,d", stepCount),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stepCount >= stepGoal) SuccessGreen else Color.White
                        )
                        Text(
                            text = "/ 10.000 bước (${(stepPercent * 100).toInt()}%)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldenStreak,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { stepPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (stepCount >= stepGoal) SuccessGreen else NeonFlame,
                        trackColor = Color(0xFF242A38)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick step simulator buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.addManualSteps(500) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+500", fontSize = 12.sp, color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { viewModel.addManualSteps(1000) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+1.000", fontSize = 12.sp, color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { viewModel.addManualSteps(2500) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+2.500", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Daily Movement Checklist
        item {
            Text(
                text = "CHECKLIST VẬN ĐỘNG HÀNG NGÀY",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 50 Pushups
        item {
            ExerciseTrackerCard(
                title = "50 Lần Hít đất (Push-ups)",
                sub = "Cơ ngực & vai trước săn chắc",
                icon = Icons.Default.FitnessCenter,
                current = pushupCount,
                target = 50,
                unit = "cái",
                onIncrement = {
                    val next = (pushupCount + 10).coerceAtMost(50)
                    viewModel.updateHabitProgress("pushups_50", next, 50)
                },
                onComplete = {
                    viewModel.toggleHabit("pushups_50", pushupCount >= 50, pushupCount, 50)
                }
            )
        }

        // 100 Squats
        item {
            ExerciseTrackerCard(
                title = "100 Lần Squats",
                sub = "Tăng sức bật, cơ đùi và testosterone",
                icon = Icons.Default.DirectionsRun,
                current = squatCount,
                target = 100,
                unit = "cái",
                onIncrement = {
                    val next = (squatCount + 20).coerceAtMost(100)
                    viewModel.updateHabitProgress("squats_100", next, 100)
                },
                onComplete = {
                    viewModel.toggleHabit("squats_100", squatCount >= 100, squatCount, 100)
                }
            )
        }

        // Cold Shower
        item {
            SimpleToggleCard(
                title = "Tắm nước lạnh buổi sáng",
                sub = "Giải phóng adrenaline tự nhiên, tỉnh táo tuyệt đối",
                icon = Icons.Default.LocalDrink,
                isDone = coldShowerDone,
                color = CyberCyan,
                onToggle = {
                    viewModel.toggleHabit("cold_shower", coldShowerDone, if (coldShowerDone) 1 else 0, 1)
                }
            )
        }

        // Weekly Checklist
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "CHECKLIST HÀNG TUẦN (WEEKLY GOALS)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldenStreak
            )
        }

        // 2 Workout sessions
        item {
            ExerciseTrackerCard(
                title = "2 Buổi tập luyện thể thao / Gym",
                sub = "Kích thích cơ bắp toàn thân với cường độ cao",
                icon = Icons.Default.FitnessCenter,
                current = workoutCount,
                target = 2,
                unit = "buổi",
                onIncrement = {
                    val next = (workoutCount + 1).coerceAtMost(2)
                    viewModel.updateHabitProgress("workout_weekly", next, 2)
                },
                onComplete = {
                    viewModel.toggleHabit("workout_weekly", workoutCount >= 2, workoutCount, 2)
                }
            )
        }

        // 1 Football match
        item {
            SimpleToggleCard(
                title = "1 Trận đá bóng đối kháng",
                sub = "Cardio bứt tốc, nâng cao phản xạ và tinh thần chiến binh",
                icon = Icons.Default.SportsSoccer,
                isDone = footballDone,
                color = SuccessGreen,
                onToggle = {
                    viewModel.toggleHabit("football_weekly", footballDone, if (footballDone) 1 else 0, 1)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ExerciseTrackerCard(
    title: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    current: Int,
    target: Int,
    unit: String,
    onIncrement: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = current >= target
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isDone) SuccessGreen.copy(alpha = 0.5f) else Color(0xFF282F3E),
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDone) SuccessGreen.copy(alpha = 0.15f) else NeonFlame.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isDone) SuccessGreen else NeonFlame,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isDone) SuccessGreen else Color(0xFF252B38))
                        .border(1.dp, if (isDone) SuccessGreen else Color(0xFF3B465B), CircleShape)
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$current / $target $unit",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) SuccessGreen else GoldenStreak
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onIncrement,
                        enabled = !isDone,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tăng", fontSize = 12.sp, color = Color.White)
                    }

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDone) Color(0xFF2B3342) else NeonFlame
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (isDone) "Hủy" else "Xong", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleToggleCard(
    title: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDone: Boolean,
    color: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isDone) SuccessGreen.copy(alpha = 0.5f) else Color(0xFF282F3E),
                RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f))
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDone) SuccessGreen else Color(0xFF2C3345)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isDone) "Đã Đạt" else "Hoàn thành",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
