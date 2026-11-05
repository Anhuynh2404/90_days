package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.DailyLogEntity
import com.example.data.local.entity.HabitCategory
import com.example.data.local.entity.HabitEntity
import com.example.ui.components.Circular90DaysProgress
import com.example.ui.components.HabitChecklistItem
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalCardElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.NeonFlame
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.Project90ViewModel

@Composable
fun DashboardScreen(
    viewModel: Project90ViewModel,
    onNavigateToTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()

    var selectedCategoryFilter by remember { mutableStateOf<String?>("ALL") }

    val logsMap = remember(todayLogs) {
        todayLogs.associateBy { it.habitId }
    }

    val filteredHabits = remember(habits, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL" || selectedCategoryFilter == null) {
            habits
        } else {
            habits.filter { it.category == selectedCategoryFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "PROJECT 90 DAYS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            ),
                            color = NeonFlame
                        )
                        Text(
                            text = "Lột xác hoàn toàn trước Tháng 2",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Hardcore Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(NeonFlame.copy(alpha = 0.2f))
                            .border(1.dp, NeonFlame, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "EXTREME",
                            color = NeonFlame,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 90-Day Circular Countdown Ring
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Circular90DaysProgress(
                    dayNumber = summary.dayNumber,
                    totalDays = summary.totalDays,
                    daysRemainingToFeb = summary.daysRemainingToFeb,
                    currentStreak = summary.currentStreak
                )
            }
        }

        // Streak Loss Warning Alert
        item {
            if (summary.streakWarning) {
                val remainingHabits = summary.totalDailyHabitsCount - summary.completedTodayCount
                Card(
                    colors = CardDefaults.cardColors(containerColor = DangerCrimson.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DangerCrimson.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .testTag("streak_warning_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DangerCrimson)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Cảnh báo",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "CẢNH BÁO MẤT CHUỖI!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = DangerCrimson
                            )
                            Text(
                                text = "Còn $remainingHabits mục chưa hoàn thành hôm nay. Bỏ lỡ bất kỳ mục nào sẽ lập tức reset chuỗi ${summary.currentStreak} ngày về 0!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SuccessGreen.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Tuyệt vời",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "MỤC TIÊU HÔM NAY ĐẠT HOÀN HẢO!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = SuccessGreen
                            )
                            Text(
                                text = "Bạn đã hoàn thành toàn bộ checklist 7 hạng mục. Chuỗi kỷ luật tiếp tục tăng lên!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Category Navigation Cards
        item {
            Column {
                Text(
                    text = "7 MÔ-ĐUN CỐT LÕI",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        QuickModuleCard(
                            title = "Vận Động",
                            sub = "10k bước, Hít đất",
                            icon = Icons.Default.DirectionsRun,
                            color = NeonFlame,
                            onClick = { onNavigateToTab(AppTab.MOVEMENT) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Dinh Dưỡng",
                            sub = "No Sugar, 2L Nước",
                            icon = Icons.Default.LocalDrink,
                            color = CyberCyan,
                            onClick = { onNavigateToTab(AppTab.NUTRITION) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Detox & Ngủ",
                            sub = "Ngừng Cafe, 22:30",
                            icon = Icons.Default.Nightlight,
                            color = GoldenStreak,
                            onClick = { onNavigateToTab(AppTab.DIGITAL) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Báo Thức 5AM",
                            sub = "Tắt bằng Camera",
                            icon = Icons.Default.Alarm,
                            color = DangerCrimson,
                            onClick = { onNavigateToTab(AppTab.ALARM_5AM) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Tài Chính",
                            sub = "Trích 10% Tiết kiệm",
                            icon = Icons.Default.Savings,
                            color = SuccessGreen,
                            onClick = { onNavigateToTab(AppTab.FINANCE) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Phát Triển",
                            sub = "Đọc 20p, 3 dòng tối",
                            icon = Icons.Default.Book,
                            color = GoldenStreak,
                            onClick = { onNavigateToTab(AppTab.GROWTH) }
                        )
                    }
                }
            }
        }

        // Daily Checklist Header & Filter Chips
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CHECKLIST KỶ LUẬT HÔM NAY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )

                    Text(
                        text = "${summary.completedTodayCount} / ${summary.totalDailyHabitsCount} Đạt",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (summary.completedTodayCount >= summary.totalDailyHabitsCount) SuccessGreen else GoldenStreak
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "ALL" to "Tất cả",
                        HabitCategory.MOVEMENT.name to "Vận Động",
                        HabitCategory.NUTRITION.name to "Dinh Dưỡng",
                        HabitCategory.DIGITAL_WELLBEING.name to "Detox",
                        HabitCategory.WAKE_UP_5AM.name to "5:00 AM",
                        HabitCategory.FINANCE.name to "Tài Chính",
                        HabitCategory.SELF_GROWTH.name to "Phát Triển"
                    )

                    items(filterOptions) { (key, label) ->
                        val isSelected = selectedCategoryFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = key },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonFlame,
                                selectedLabelColor = Color.White,
                                containerColor = CharcoalCard,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) NeonFlame else Color(0xFF2C3446)
                            )
                        )
                    }
                }
            }
        }

        // Interactive Checklist Items
        items(filteredHabits, key = { it.id }) { habit ->
            val log = logsMap[habit.id]
            HabitChecklistItem(
                habit = habit,
                log = log,
                onToggle = {
                    viewModel.toggleHabit(
                        habitId = habit.id,
                        currentCompleted = log?.isCompleted == true,
                        currentProgress = log?.progressValue ?: 0,
                        target = habit.targetValue
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun QuickModuleCard(
    title: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .width(140.dp)
            .border(1.dp, Color(0xFF282F3E), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}
