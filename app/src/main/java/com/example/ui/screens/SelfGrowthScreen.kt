package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.EcoBackground
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.MarineAqua
import com.example.ui.theme.MintLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.Project90ViewModel

@Composable
fun SelfGrowthScreen(
    viewModel: Project90ViewModel,
    modifier: Modifier = Modifier
) {
    val timerSeconds by viewModel.readingTimerSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isReadingRunning.collectAsStateWithLifecycle()
    val todayJournal by viewModel.todayJournal.collectAsStateWithLifecycle()

    var line1 by remember { mutableStateOf("") }
    var line2 by remember { mutableStateOf("") }
    var line3 by remember { mutableStateOf("") }
    var savedSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(todayJournal) {
        todayJournal?.let {
            if (line1.isEmpty()) line1 = it.line1GoodThings
            if (line2.isEmpty()) line2 = it.line2LessonLearned
            if (line3.isEmpty()) line3 = it.line3TomorrowGoal
        }
    }

    val totalSeconds = 20 * 60 // 1200
    val progress = ((totalSeconds - timerSeconds).toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EcoBackground)
            .testTag("self_growth_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "MÔ-ĐUN PHÁT TRIỂN BẢN THÂN",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ForestGreen
                )
                Text(
                    text = "20 phút đọc sách không xao nhãng & 3 dòng đúc kết buổi tối",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // 20-Minute Focus Reading Timer Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isTimerRunning) ForestGreen else CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = "Reading",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ĐỌC SÁCH 20 PHÚT TẬP TRUNG",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Khóa xao nhãng - Chỉ sách giấy hoặc Kindle",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (isTimerRunning) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(MintLight, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = ForestGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Focus Mode", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Circular Countdown Timer
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(190.dp)
                    ) {
                        Canvas(modifier = Modifier.size(180.dp)) {
                            val stroke = 10.dp.toPx()

                            drawCircle(
                                color = Color(0xFFE2EBE4),
                                radius = size.minDimension / 2 - stroke / 2,
                                style = Stroke(stroke)
                            )

                            drawArc(
                                color = if (progress >= 1f) SuccessGreen else ForestGreen,
                                startAngle = -90f,
                                sweepAngle = 360f * (1f - (timerSeconds.toFloat() / totalSeconds.toFloat())),
                                useCenter = false,
                                style = Stroke(stroke, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = ForestGreenDark
                            )
                            Text(
                                text = if (timerSeconds == 0) "HOÀN THÀNH!" else if (isTimerRunning) "Đang đếm ngược..." else "Tạm dừng",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (timerSeconds == 0) SuccessGreen else WarmAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Timer action controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.toggleReadingTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) WarningAmber else ForestGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Start/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTimerRunning) "Tạm Dừng" else "Bắt Đầu Đọc",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetReadingTimer() },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = ForestGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Minimalist 3-Line Evening Journal Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Journal",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "3 DÒNG NHẬT KÝ TỐI CÔ ĐỌNG",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Giới hạn 120 ký tự mỗi dòng - Viết thật ngắn gọn",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Line 1: What went well
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "1. Việc tôi đã làm tốt nhất hôm nay:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Text(
                                text = "${line1.length} / 120",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = line1,
                            onValueChange = { if (it.length <= 120) line1 = it },
                            placeholder = { Text("VD: Hoàn thành 10k bước và 50 hít đất không bỏ cuộc...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = CardBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Line 2: Lesson learned
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "2. Bài học rút ra từ một sai lầm / thử thách:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                            Text(
                                text = "${line2.length} / 120",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = line2,
                            onValueChange = { if (it.length <= 120) line2 = it },
                            placeholder = { Text("VD: Cơn thèm đường sẽ qua sau 10 phút nếu uống một cốc nước...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WarmAmber,
                                unfocusedBorderColor = CardBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Line 3: Tomorrow's priority
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "3. Mục tiêu số 1 phải hoàn thành ngày mai:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MarineAqua
                            )
                            Text(
                                text = "${line3.length} / 120",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = line3,
                            onValueChange = { if (it.length <= 120) line3 = it },
                            placeholder = { Text("VD: Dậy đúng 5:00 AM, đọc xong chương 3 và hoàn thành buổi tập gym...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MarineAqua,
                                unfocusedBorderColor = CardBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.saveJournal(line1, line2, line3)
                            savedSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu 3 Dòng Nhật Ký", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    if (savedSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Đã lưu nhật ký thành công! Kỷ luật buổi tối đã được ghi nhận vào chuỗi.",
                            fontSize = 11.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
