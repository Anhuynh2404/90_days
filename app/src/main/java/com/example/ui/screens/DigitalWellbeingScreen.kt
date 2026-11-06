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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NoCell
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.MarineAqua
import com.example.ui.theme.MintLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.Project90ViewModel
import java.time.LocalTime

@Composable
fun DigitalWellbeingScreen(
    viewModel: Project90ViewModel,
    modifier: Modifier = Modifier
) {
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
    val logsMap = remember(todayLogs) { todayLogs.associateBy { it.habitId } }

    val caffeineDone = logsMap["caffeine_curfew"]?.isCompleted == true
    val screenLimitDone = logsMap["screen_limit_22"]?.isCompleted == true
    val bedtimeDone = logsMap["bedtime_2230"]?.isCompleted == true

    val currentTime = remember { LocalTime.now() }
    val isAfter1400 = currentTime.hour >= 14
    val isAfter2200 = currentTime.hour >= 22

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EcoBackground)
            .testTag("digital_wellbeing_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "MÔ-ĐUN DETOX & GIẤC NGỦ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ForestGreen
                )
                Text(
                    text = "Tối ưu hóa Adenosine, giải phóng Melatonin và phục hồi sóng não sâu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Live Real-Time Curfew Status Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isAfter1400) Color(0xFFFEF3C7) else MintLight
                ),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isAfter1400) WarmAmber.copy(alpha = 0.4f) else ForestGreen.copy(alpha = 0.2f),
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isAfter1400) WarmAmber.copy(alpha = 0.2f) else ForestGreen.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = if (isAfter2200) Icons.Default.Bedtime else if (isAfter1400) Icons.Default.Warning else Icons.Default.HourglassTop,
                            contentDescription = "Status",
                            tint = if (isAfter1400) WarmAmber else ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAfter2200) "GIAI ĐOẠN KHÓA MÀN HÌNH & CHUẨN BỊ NGỦ"
                                   else if (isAfter1400) "LỆNH GIỚI NGHIÊM: NGỪNG CAFFEINE ĐANG ÁP DỤNG"
                                   else "BUỔI SÁNG - NẠP CAFFEINE VỪA ĐỦ TRƯỚC 14:00",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = if (isAfter1400) WarmAmber else ForestGreen,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAfter2200) "Đã sau 22:00. Hãy tắt màn hình, bật chế độ máy bay hoặc Night Shift để bảo vệ não."
                                   else if (isAfter1400) "Thời gian bán rã của caffeine là 6-8 tiếng. Uống cafe sau 14h sẽ phá hủy pha ngủ sâu (Deep Sleep)."
                                   else "Sau 14:00, tuyệt đối chỉ uống nước lọc để giữ nhịp sinh học tự nhiên.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // 1. Caffeine Curfew Tracker (Sau 14:00)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Coffee,
                                    contentDescription = "Caffeine",
                                    tint = WarmAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "NGỪNG CAFFEINE SAU 14:00",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Bảo vệ thụ thể Adenosine",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.toggleHabit("caffeine_curfew", caffeineDone, if (caffeineDone) 1 else 0, 1)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (caffeineDone) SuccessGreen else MintLight
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (caffeineDone) "Đã Đạt" else "Xác nhận",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (caffeineDone) Color.White else ForestGreen,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Khoa học chứng minh: Dù bạn có thể dễ ngủ, caffeine còn tồn đọng trong máu sẽ triệt tiêu tới 40% pha ngủ sóng chậm (phục hồi cơ bắp & trí nhớ).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // 2. Screen Limit after 22:00
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NoCell,
                                    contentDescription = "Screen Limit",
                                    tint = MarineAqua,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "GIỚI HẠN MÀN HÌNH SAU 22:00",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Triệt tiêu dopamine kích thích ảo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.toggleHabit("screen_limit_22", screenLimitDone, if (screenLimitDone) 1 else 0, 1)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (screenLimitDone) SuccessGreen else MintLight
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (screenLimitDone) "Đã Đạt" else "Xác nhận",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (screenLimitDone) Color.White else ForestGreen,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Không lướt TikTok, Facebook, tin tức drama sau 22h. Não bộ cần 30-45 phút hạ nhiệt kích thích trước khi có thể trôi vào giấc ngủ một cách tự nhiên.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // 3. Bedtime before 22:30 (Fixed Layout Constraints)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = "Bedtime",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "LÊN GIƯỜNG NGỦ TRƯỚC 22:30",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Đảm bảo 6.5 tiếng ngủ chất lượng trước 5AM",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.toggleHabit("bedtime_2230", bedtimeDone, if (bedtimeDone) 1 else 0, 1)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (bedtimeDone) SuccessGreen else MintLight
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (bedtimeDone) "Đã Đạt" else "Xác nhận",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bedtimeDone) Color.White else ForestGreen,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Để thức dậy lúc 5:00 sáng đầy sinh lực, việc đi ngủ trước 22:30 là điều kiện tiên quyết. Đây là nền tảng của toàn bộ chu kỳ 90 ngày.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
