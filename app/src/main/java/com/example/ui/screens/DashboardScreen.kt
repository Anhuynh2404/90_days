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
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.HabitCategory
import com.example.ui.components.Circular90DaysProgress
import com.example.ui.components.HabitChecklistItem
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.EcoBackground
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.MarineAqua
import com.example.ui.theme.MintLight
import com.example.ui.theme.MintSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmAmber
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
            .background(EcoBackground)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header (Clean, Inspiring & Modern)
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "PROJECT 90 DAYS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = ForestGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Lột xác hoàn toàn trước Tháng 2",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Settings Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CardWhite)
                                .border(1.dp, CardBorder, CircleShape)
                                .clickable { onNavigateToTab(AppTab.SETTINGS) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Cài đặt & Chuông",
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Discipline Badge (Eco-Green Pill)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MintLight)
                                .border(1.dp, ForestGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "KỶ LUẬT",
                                color = ForestGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Inspiring Eco Impact Banner
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MintLight)
                        .border(1.dp, ForestGreen.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Impact",
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mỗi hành động nhỏ hôm nay tạo nên sự bứt phá phi thường ngày mai.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = ForestGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 90-Day Circular Countdown Ring Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Circular90DaysProgress(
                        dayNumber = summary.dayNumber,
                        totalDays = summary.totalDays,
                        daysRemainingToFeb = summary.daysRemainingToFeb,
                        currentStreak = summary.currentStreak
                    )
                }
            }
        }

        // Streak Loss Warning Alert
        item {
            if (summary.streakWarning) {
                val remainingHabits = summary.totalDailyHabitsCount - summary.completedTodayCount
                Card(
                    colors = CardDefaults.cardColors(containerColor = DangerCrimson.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DangerCrimson.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
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
                                color = TextPrimary
                            )
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MintLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ForestGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
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
                                .background(ForestGreen)
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
                                color = ForestGreen
                            )
                            Text(
                                text = "Bạn đã hoàn thành toàn bộ checklist 7 hạng mục. Chuỗi kỷ luật tiếp tục vững chắc!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Quick Category Navigation Cards (Horizontal Row)
        item {
            Column {
                Text(
                    text = "7 MÔ-ĐUN CỐT LÕI",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextPrimary
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
                            color = ForestGreen,
                            onClick = { onNavigateToTab(AppTab.MOVEMENT) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Dinh Dưỡng",
                            sub = "No Sugar, 2L Nước",
                            icon = Icons.Default.LocalDrink,
                            color = MarineAqua,
                            onClick = { onNavigateToTab(AppTab.NUTRITION) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Detox & Ngủ",
                            sub = "Ngừng Cafe, 22:30",
                            icon = Icons.Default.Nightlight,
                            color = WarmAmber,
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
                            color = LeafGreen,
                            onClick = { onNavigateToTab(AppTab.FINANCE) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Phát Triển",
                            sub = "Đọc 20p, 3 dòng tối",
                            icon = Icons.Default.Book,
                            color = ForestGreen,
                            onClick = { onNavigateToTab(AppTab.GROWTH) }
                        )
                    }
                    item {
                        QuickModuleCard(
                            title = "Cài Đặt",
                            sub = "Chuông & Quyền 24/7",
                            icon = Icons.Default.Settings,
                            color = ForestGreen,
                            onClick = { onNavigateToTab(AppTab.SETTINGS) }
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
                        color = TextPrimary
                    )

                    Text(
                        text = "${summary.completedTodayCount} / ${summary.totalDailyHabitsCount} Đạt",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (summary.completedTodayCount >= summary.totalDailyHabitsCount) SuccessGreen else WarmAmber
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
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color.White,
                                containerColor = CardWhite,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) ForestGreen else CardBorder
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
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(140.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f))
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
                color = TextPrimary
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}
