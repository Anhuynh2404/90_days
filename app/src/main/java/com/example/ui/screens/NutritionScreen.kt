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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.EcoBackground
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.MarineAqua
import com.example.ui.theme.MintBorder
import com.example.ui.theme.MintLight
import com.example.ui.theme.MintSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.Project90ViewModel

data class FoodSuggestion(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tooltipDetails: String,
    val benefits: String
)

@Composable
fun NutritionScreen(
    viewModel: Project90ViewModel,
    modifier: Modifier = Modifier
) {
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
    val logsMap = remember(todayLogs) { todayLogs.associateBy { it.habitId } }

    val noSugarLog = logsMap["no_sugar"]
    val isNoSugarDone = noSugarLog?.isCompleted == true

    val waterLog = logsMap["water_2l"]
    val waterProgress = waterLog?.progressValue ?: 0
    val waterGoal = 2000
    val waterPercent = (waterProgress.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f)

    var activeTooltipIndex by remember { mutableStateOf<Int?>(0) }

    val foodSuggestions = remember {
        listOf(
            FoodSuggestion(
                title = "Trứng Gà",
                subtitle = "3-4 quả/ngày",
                icon = Icons.Default.Egg,
                tooltipDetails = "Gợi ý: Luộc lòng đào hoặc áp chảo dầu olive. Cung cấp Choline giúp tập trung não bộ, đạm hấp thu hoàn hảo và chất béo tốt không làm tăng đường huyết.",
                benefits = "24g Đạm | Đầy đủ Axit Amin"
            ),
            FoodSuggestion(
                title = "Đạm Nạc",
                subtitle = "Ức gà, bò nạc, cá hồi",
                icon = Icons.Default.FitnessCenter,
                tooltipDetails = "Gợi ý: Chế biến hấp hoặc nướng không đường. Duy trì khối lượng cơ nạc và tạo cảm giác no kéo dài 4-5 tiếng, dập tắt cơn thèm ngọt.",
                benefits = "Xây dựng cơ bắp | No lâu"
            ),
            FoodSuggestion(
                title = "Rau Xanh Đậm",
                subtitle = "Bông cải, cải xoăn, bina",
                icon = Icons.Default.Grass,
                tooltipDetails = "Gợi ý: Ăn tối thiểu 300g mỗi ngày. Giàu Magie và chất xơ, hỗ trợ hệ vi sinh đường ruột và hạ thấp phản ứng insulin sau bữa ăn.",
                benefits = "Magie | Giảm Insulin"
            ),
            FoodSuggestion(
                title = "Trái Cây Nguyên Quả",
                subtitle = "Bơ, táo xanh, dâu tây, ổi",
                icon = Icons.Default.Spa,
                tooltipDetails = "Gợi ý: Chỉ ăn trái cây nguyên quả, tuyệt đối KHÔNG uống nước ép (nước ép làm mất chất xơ và đẩy chỉ số GI đường huyết vọt lên).",
                benefits = "Chỉ số GI thấp | Vitamin C"
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EcoBackground)
            .testTag("nutrition_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "MÔ-ĐUN DINH DƯỠNG (NO SUGAR)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ForestGreen
                )
                Text(
                    text = "Cắt đứt đồ ngọt, giải độc cơ thể và duy trì hydrat hóa 2L nước",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Binary No Sugar Commitment Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isNoSugarDone) SuccessGreen.copy(alpha = 0.5f) else CardBorder,
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isNoSugarDone) SuccessGreen.copy(alpha = 0.15f)
                                        else MintLight
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NoDrinks,
                                    contentDescription = "No Sugar",
                                    tint = if (isNoSugarDone) SuccessGreen else ForestGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CAM KẾT NO SUGAR",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Habit Nhị Phân: Đạt / Không Đạt",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isNoSugarDone) SuccessGreen.copy(alpha = 0.15f)
                                    else Color(0xFFFEE2E2)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isNoSugarDone) "ĐẠT ✅" else "CHƯA ĐẠT ⚠️",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isNoSugarDone) SuccessGreen else DangerCrimson,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Không tiêu thụ bánh kẹo, trà sữa, nước ngọt có gas, sốt ngọt, siro. Đưa insulin về trạng thái cân bằng tự nhiên và kích hoạt đốt mỡ nội tạng.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.toggleHabit("no_sugar", isNoSugarDone, if (isNoSugarDone) 1 else 0, 1)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNoSugarDone) SuccessGreen else ForestGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isNoSugarDone) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = "Commitment Toggle",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isNoSugarDone) "Hôm Nay: Đã Giữ Vững Kỷ Luật No Sugar!" else "Đánh Dấu: Cam Kết Không Đụng Vào Đường",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Water Tracker Card (2 Liters)
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalDrink,
                                    contentDescription = "Water",
                                    tint = MarineAqua,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "BỘ ĐẾM NƯỚC (WATER TRACKER)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Mục tiêu tối thiểu 2 Lít (2.000 ml) / ngày",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Reset button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MintLight)
                                .clickable {
                                    viewModel.updateHabitProgress("water_2l", 0, 2000)
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${waterProgress} ml",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = if (waterProgress >= waterGoal) SuccessGreen else MarineAqua
                        )
                        Text(
                            text = "/ 2.000 ml (${(waterPercent * 100).toInt()}%)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { waterPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (waterProgress >= waterGoal) SuccessGreen else MarineAqua,
                        trackColor = Color(0xFFE8EFE9)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Water Cup buttons (+250ml, +500ml)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.addWater(250) },
                            colors = ButtonDefaults.buttonColors(containerColor = MintLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocalDrink, contentDescription = "250ml", tint = MarineAqua, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+250 ml (1 Ly)", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.addWater(500) },
                            colors = ButtonDefaults.buttonColors(containerColor = MintLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocalDrink, contentDescription = "500ml", tint = MarineAqua, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+500 ml (1 Bình)", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Dynamic Food Suggestions (Tooltips)
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Gợi ý",
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GỢI Ý THỰC PHẨM CHUẨN KỶ LUẬT (TOOLTIP ĐỘNG)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = ForestGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestion Pill Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(foodSuggestions.indices.toList()) { index ->
                        val item = foodSuggestions[index]
                        val isSelected = activeTooltipIndex == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) ForestGreen else CardWhite)
                                .border(1.dp, if (isSelected) ForestGreen else CardBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    activeTooltipIndex = if (isSelected) null else index
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) Color.White else ForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Active Tooltip Explanation Box
                activeTooltipIndex?.let { index ->
                    val selectedItem = foodSuggestions[index]
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MintSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MintBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedItem.title + " - " + selectedItem.subtitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ForestGreen
                                )
                                Text(
                                    text = selectedItem.benefits,
                                    fontSize = 10.sp,
                                    color = MarineAqua,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = selectedItem.tooltipDetails,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
