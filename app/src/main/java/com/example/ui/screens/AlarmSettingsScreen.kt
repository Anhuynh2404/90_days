package com.example.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.AlarmToneType
import com.example.service.AlarmVibrationType
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.EcoBackground
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GoldenStreak
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.MarineAqua
import com.example.ui.theme.MintBorder
import com.example.ui.theme.MintLight
import com.example.ui.theme.MintSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.Project90ViewModel

@Composable
fun AlarmSettingsScreen(
    viewModel: Project90ViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val isRinging by viewModel.isAlarmRinging.collectAsStateWithLifecycle()
    val selectedTone by viewModel.selectedTone.collectAsStateWithLifecycle()
    val selectedVib by viewModel.selectedVibration.collectAsStateWithLifecycle()
    val volume by viewModel.alarmVolume.collectAsStateWithLifecycle()

    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()

    val logsMap = remember(todayLogs) { todayLogs.associateBy { it.habitId } }
    val pendingHabits = remember(habits, todayLogs) {
        habits.filter { !it.isWeekly && logsMap[it.id]?.isCompleted != true }
    }

    // Permission states
    var hasNotifPermission by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }

    var isIgnoringBattery by remember {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
            } else true
        )
    }

    var canExactAlarm by remember {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager?.canScheduleExactAlarms() ?: false
            } else true
        )
    }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotifPermission = granted
        if (granted) {
            viewModel.refreshPersistentNotification()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EcoBackground)
            .testTag("alarm_settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "CÀI ĐẶT CHUÔNG & QUYỀN HỆ THỐNG",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ForestGreen
                )
                Text(
                    text = "Bảo đảm thông báo ghim 24/7 và chuông cảnh báo không bị ngăn chặn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Section 1: Non-dismissible Ongoing Notification Status & Preview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ForestGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
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
                                    .background(MintLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Ongoing Notification",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "THÔNG BÁO GHIM 24/7 (STATUS BAR)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Không thể vuốt tắt - Luôn hiển thị việc chưa làm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ForestGreen,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MintLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ONGOING",
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Xem trước thông báo đang ghim trên thanh trạng thái:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Live Notification Preview Box (Dashboard Mini Style)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MintSurface)
                            .border(1.dp, MintBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Notif",
                                        tint = ForestGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PROJECT 90 • COMMAND DASHBOARD",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = ForestGreen
                                    )
                                }

                                Text(
                                    text = "🔥 7N Chuỗi",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmAmber,
                                    modifier = Modifier
                                        .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mini 4-Tile Grid
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val stepCount = logsMap["pedometer_10k"]?.progressValue ?: 0
                                val waterMl = logsMap["water_2l"]?.progressValue ?: 0
                                val noSugarDone = logsMap["no_sugar"]?.isCompleted == true

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(CardWhite, RoundedCornerShape(8.dp))
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Column {
                                        Text("👟 BƯỚC", fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                        Text("${stepCount}/10k", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(CardWhite, RoundedCornerShape(8.dp))
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Column {
                                        Text("💧 NƯỚC", fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                        Text("${waterMl}ml", fontSize = 11.sp, color = MarineAqua, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(CardWhite, RoundedCornerShape(8.dp))
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Column {
                                        Text("🏋️ THỂ LỰC", fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                        Text("50 Pushup", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(CardWhite, RoundedCornerShape(8.dp))
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Column {
                                        Text("🚫 NO SUGAR", fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                        Text(if (noSugarDone) "ĐÃ ĐẠT" else "CHƯA", fontSize = 11.sp, color = if (noSugarDone) SuccessGreen else DangerCrimson, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pending tasks summary
                            Text(
                                text = if (pendingHabits.isNotEmpty())
                                    "⚠️ CÒN ${pendingHabits.size} MỤC CHƯA XONG HÔM NAY:"
                                else
                                    "🔥 100% HOÀN TẤT TOÀN BỘ MỤC TIÊU HÔM NAY!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (pendingHabits.isNotEmpty()) WarmAmber else SuccessGreen
                            )

                            if (pendingHabits.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                pendingHabits.take(3).forEach { habit ->
                                    Text(
                                        text = "• ${habit.title}",
                                        fontSize = 11.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons preview
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .background(ForestGreen, RoundedCornerShape(6.dp))
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text("📋 MỞ DASHBOARD", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(MintLight, RoundedCornerShape(6.dp))
                                        .border(1.dp, MintBorder, RoundedCornerShape(6.dp))
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text("💧 +250ML", fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(MintLight, RoundedCornerShape(6.dp))
                                        .border(1.dp, MintBorder, RoundedCornerShape(6.dp))
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text("👟 +500 BƯỚC", fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.refreshPersistentNotification() },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đồng Bộ Lại Thông Báo Trên Thanh Trạng Thái", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Section 2: Permission Checklist & Shield (Không bị chặn)
        item {
            Text(
                text = "TRUNG TÂM CẤP QUYỀN TOÀN DIỆN (CHỐNG BỊ CHẶN)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = ForestGreen
            )
        }

        // Permission 1: Notifications
        item {
            PermissionCard(
                title = "1. Quyền Thông Báo Đẩy (Post Notifications)",
                description = "Bắt buộc trên Android 13+ để ứng dụng hiển thị thông báo ghim và chuông khẩn cấp.",
                isGranted = hasNotifPermission,
                actionLabel = "Cấp Quyền",
                onAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }

        // Permission 2: Battery Optimization Exemption
        item {
            PermissionCard(
                title = "2. Chạy Nền Không Giới Hạn (Tắt Tối Ưu Hóa Pin)",
                description = "Ngăn chặn hệ điều hành Android tự ý dừng dịch vụ báo thức và đếm bước chân khi tắt màn hình.",
                isGranted = isIgnoringBattery,
                actionLabel = "Bỏ Giới Hạn Pin",
                onAction = {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    } catch (_: Exception) {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            )
        }

        // Permission 3: Exact Alarms
        item {
            PermissionCard(
                title = "3. Báo Thức Đúng Giờ Tuyệt Đối (Exact Alarms)",
                description = "Đảm bảo chuông 5:00 AM và các lệnh giới nghiêm 14:00, 22:00 reo chính xác từng giây.",
                isGranted = canExactAlarm,
                actionLabel = "Cho Phép Báo Thức",
                onAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }

        // Section 3: Alarm Tone & Vibration Customization
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CÀI ĐẶT CHUÔNG CẢNH BÁO & ĐỘ RUNG",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = ForestGreen
            )
        }

        // Alarm Test Hero Button
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isRinging) DangerCrimson.copy(alpha = 0.15f) else CardWhite
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isRinging) DangerCrimson else CardBorder,
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Icon(
                        imageVector = if (isRinging) Icons.Default.VolumeUp else Icons.Default.GraphicEq,
                        contentDescription = "Sound Test",
                        tint = if (isRinging) DangerCrimson else ForestGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isRinging) "CHUÔNG ĐANG PHÁT ÂM LƯỢNG LỚN & RUNG LIÊN TỤC!" else "KIỂM TRA CHUÔNG CẢNH BÁO & ĐỘ RUNG",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = if (isRinging) DangerCrimson else TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Đang chọn: ${selectedTone.title}",
                        fontSize = 12.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (isRinging) {
                                viewModel.dismissAlarmManually()
                            } else {
                                viewModel.playTonePreview(selectedTone)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRinging) DangerCrimson else ForestGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isRinging) Icons.Default.AlarmOff else Icons.Default.Alarm,
                            contentDescription = "Test Sound",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRinging) "Dừng Chuông Ngay" else "Phát Thử Chuông Cảnh Báo",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Tone Selector
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
                    Text(
                        text = "CHỌN GIAI ĐIỆU CẢNH BÁO",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    AlarmToneType.values().forEach { tone ->
                        val isSelected = tone == selectedTone
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MintLight else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) ForestGreen else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setAlarmTone(tone) }
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tone.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ForestGreen else TextPrimary
                                )
                                Text(
                                    text = tone.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // Vibration Pattern Selector
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = "Vibration", tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KIỂU RUNG CẢNH BÁO",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AlarmVibrationType.values().forEach { vib ->
                        val isSelected = vib == selectedVib
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MintLight else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) ForestGreen else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setAlarmVibration(vib) }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = vib.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ForestGreen else TextPrimary
                            )

                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = ForestGreen, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // Volume Slider
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ÂM LƯỢNG CHUÔNG",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "$volume%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = volume.toFloat(),
                        onValueChange = { viewModel.setAlarmVolume(it.toInt()) },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = ForestGreen,
                            activeTrackColor = ForestGreen,
                            inactiveTrackColor = Color(0xFFE2EBE4)
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isGranted) SuccessGreen.copy(alpha = 0.5f) else DangerCrimson.copy(alpha = 0.4f),
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isGranted) SuccessGreen.copy(alpha = 0.15f)
                                else DangerCrimson.copy(alpha = 0.15f)
                            )
                    ) {
                        Icon(
                            imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Shield,
                            contentDescription = "Status",
                            tint = if (isGranted) SuccessGreen else DangerCrimson,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isGranted) SuccessGreen.copy(alpha = 0.15f)
                            else DangerCrimson.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isGranted) "ĐÃ CẤP" else "CHƯA CẤP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGranted) SuccessGreen else DangerCrimson
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = DangerCrimson),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
