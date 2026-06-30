package com.example.healthbook.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthbook.data.MockData
import com.example.healthbook.ui.theme.Background
import com.example.healthbook.ui.theme.PrimaryBlue
import com.example.healthbook.ui.theme.TextPrimary

@Composable
fun ProfileScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp) // Space for bottom nav
    ) {
        ProfileHeader()
        ProfileMenuItems()
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                color = PrimaryBlue,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 24.dp), // Adjust for system bar
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = MockData.currentPatient.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = MockData.currentPatient.phone,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItems() {
    Column(modifier = Modifier.padding(16.dp)) {
        ProfileMenuItem(Icons.Outlined.Person, "Thông tin cá nhân")
        ProfileMenuItem(Icons.Outlined.People, "Thành viên gia đình")
        ProfileMenuItem(Icons.Outlined.HealthAndSafety, "Bảo hiểm y tế")
        ProfileMenuItem(Icons.Outlined.LocationOn, "Địa chỉ")
        ProfileMenuItem(Icons.Outlined.Payment, "Phương thức thanh toán")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileMenuItem(Icons.Outlined.Settings, "Cài đặt")
        ProfileMenuItem(Icons.Outlined.HelpOutline, "Trợ giúp & Hỗ trợ")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileMenuItem(Icons.Outlined.Logout, "Đăng xuất", isDestructive = true)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, isDestructive: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) Color.Red else TextPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (isDestructive) Color.Red else TextPrimary
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
    HorizontalDivider(color = Background)
}
