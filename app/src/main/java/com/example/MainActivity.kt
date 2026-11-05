package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DigitalWellbeingScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.MovementScreen
import com.example.ui.screens.NutritionScreen
import com.example.ui.screens.SelfGrowthScreen
import com.example.ui.screens.WakeUpAlarmScreen
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.NeonFlame
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.Project90Theme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.Project90ViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project90Theme {
                Project90App()
            }
        }
    }
}

@Composable
fun Project90App(
    viewModel: Project90ViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    // Handle system back button: return to Dashboard if on any other tab
    BackHandler(enabled = currentTab != AppTab.DASHBOARD) {
        viewModel.selectTab(AppTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = ObsidianBlack,
        bottomBar = {
            Project90BottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_fade") { tab ->
                when (tab) {
                    AppTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { viewModel.selectTab(it) }
                    )
                    AppTab.MOVEMENT -> MovementScreen(viewModel = viewModel)
                    AppTab.NUTRITION -> NutritionScreen(viewModel = viewModel)
                    AppTab.DIGITAL -> DigitalWellbeingScreen(viewModel = viewModel)
                    AppTab.ALARM_5AM -> WakeUpAlarmScreen(viewModel = viewModel)
                    AppTab.FINANCE -> FinanceScreen(viewModel = viewModel)
                    AppTab.GROWTH -> SelfGrowthScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Project90BottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        color = CharcoalCard,
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF222836))
            .testTag("project90_bottom_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                AppTab.DASHBOARD to ("Tổng Quan" to Icons.Default.Dashboard),
                AppTab.MOVEMENT to ("Vận Động" to Icons.Default.DirectionsRun),
                AppTab.NUTRITION to ("Dinh Dưỡng" to Icons.Default.LocalDrink),
                AppTab.DIGITAL to ("Detox" to Icons.Default.Nightlight),
                AppTab.ALARM_5AM to ("Báo Thức" to Icons.Default.Alarm),
                AppTab.FINANCE to ("Tài Chính" to Icons.Default.Savings),
                AppTab.GROWTH to ("Phát Triển" to Icons.Default.Book)
            )

            tabs.forEach { (tab, meta) ->
                val (label, icon) = meta
                val isSelected = currentTab == tab

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonFlame.copy(alpha = 0.2f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) NeonFlame else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) NeonFlame else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
