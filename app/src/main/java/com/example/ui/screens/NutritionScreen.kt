package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
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
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalCardElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.NeonFlame
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.Project90ViewModel

data class FoodSuggestion(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
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
    val waterGoal = 2000 // 2 Liters in ml
    val waterPercent = (waterProgress.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f)

    var activeTooltipIndex by remember { mutableStateOf<Int?>(0) }

    val foodSuggestions = remember {
        listOf(
            FoodSuggestion(
                title = "Trứng gà ta",
                subtitle = "Protein hoàn hảo & Choline",
                icon = Icons.Default.Egg,
                tooltipDetails = "Gợi ý: 3-4 quả luộc hoặc ốp la dầu ô liu mỗi ngày. Trứng chứa đủ 9 axit amin thiết yếu và choline bảo vệ tế bào thần kinh não bộ.",
                benefits = "Không đường | No lâu | Tăng cơ"
            ),
            FoodSuggestion(
                title = "Đạm Nạc (Proteins)",
                subtitle = "Ức gà, bò, cá hồi, đậu phụ",
                icon = Icons.Default.FitnessCenter,
                tooltipDetails = "Gợi ý: 1.6g - 2.0g đạm trên mỗi kg trọng lượng cơ thể. Giúp duy trì khối lượng cơ bắp tối đa trong quá trình siết mỡ 90 ngày.",
                benefits = "Bảo toàn cơ | Đốt calo tiêu hóa"
            ),
            FoodSuggestion(
                title = "Rau Xanh Đậm",
                subtitle = "Súp lơ, cải kale, rau bina",
                icon = Icons.Default.Park,
                tooltipDetails = "Gợi ý: Ăn ít nhất 400g rau mỗi ngày (hấp hoặc luộc). Cung cấp magie, kali chống chuột rút và hỗ trợ hệ vi sinh đường ruột phát triển.",
                benefits = "Chất xơ cao | Hạ đường huyết"
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
            .background(ObsidianBlack)
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
                    color = NeonFlame
                )
                Text(
                    text = "Cắt đứt đồ ngọt, giải độc cơ thể và duy trì hydrat hóa 2L nước",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Binary No Sugar Commitment Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isNoSugarDone) SuccessGreen.copy(alpha = 0.6f) else DangerCrimson.copy(alpha = 0.5f),
                        RoundedCornerShape(20.dp)
                    )
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isNoSugarDone) SuccessGreen.copy(alpha = 0.2f)
                                        else DangerCrimson.copy(alpha = 0.2f)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NoDrinks,
                                    contentDescription = "No Sugar",
                                    tint = if (isNoSugarDone) SuccessGreen else DangerCrimson,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CAM KẾT NO SUGAR",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                                Text(
                                    text = "Habit Nhị Phân: Đạt / Không Đạt",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isNoSugarDone) SuccessGreen.copy(alpha = 0.2f)
                                    else DangerCrimson.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isNoSugarDone) "ĐẠT" else "CHƯA ĐẠT",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isNoSugarDone) SuccessGreen else DangerCrimson
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Không tiêu thụ bánh kẹo, trà sữa, nước ngọt có gas, sốt ngọt, siro. Đưa insulin về trạng thái cân bằng tự nhiên và kích hoạt đốt mỡ nội tạng.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.toggleHabit("no_sugar", isNoSugarDone, if (isNoSugarDone) 1 else 0, 1)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNoSugarDone) SuccessGreen else DangerCrimson
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
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Water Tracker Card (2 Liters)
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
                                    .background(CyberCyan.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalDrink,
                                    contentDescription = "Water",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "BỘ ĐẾM NƯỚC (WATER TRACKER)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Mục tiêu tối thiểu 2 Lít (2.000 ml) / ngày",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Reset button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF232A39))
                                .clickable {
                                    viewModel.updateHabitProgress("water_2l", 0, 2000)
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = if (waterProgress >= waterGoal) SuccessGreen else CyberCyan
                        )
                        Text(
                            text = "/ 2.000 ml (${(waterPercent * 100).toInt()}%)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenStreak,
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
                        color = if (waterProgress >= waterGoal) SuccessGreen else CyberCyan,
                        trackColor = Color(0xFF232A38)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Water Cup buttons (+250ml, +500ml)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.addWater(250) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203248)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocalDrink, contentDescription = "250ml", tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+250 ml (1 Ly)", fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = { viewModel.addWater(500) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3B52)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocalDrink, contentDescription = "500ml", tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+500 ml (1 Bình)", fontSize = 12.sp, color = Color.White)
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
                        tint = GoldenStreak,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GỢI Ý THỰC PHẨM CHUẨN KỶ LUẬT (TOOLTIP ĐỘNG)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = GoldenStreak
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonFlame else CharcoalCard)
                                .border(1.dp, if (isSelected) NeonFlame else Color(0xFF2D3546), RoundedCornerShape(12.dp))
                                .clickable {
                                    activeTooltipIndex = if (isSelected) null else index
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) Color.White else NeonFlame,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
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
                        colors = CardDefaults.cardColors(containerColor = CharcoalCardElevated),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GoldenStreak.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
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
                                    color = GoldenStreak
                                )
                                Text(
                                    text = selectedItem.benefits,
                                    fontSize = 10.sp,
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = selectedItem.tooltipDetails,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
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
