package com.example.healthbook.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.healthbook.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp) // Space for bottom nav
    ) {
        HomeHeader()
        HomeBanner()
        QuickActions()
        UpcomingAppointmentSection()
        PopularSpecialties()
        PopularHospitals()
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Profile Image placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Xin chào,", color = TextSecondary, fontSize = 12.sp)
                Text(MockData.currentPatient.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            }
        }
        
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextPrimary)
        }
    }
}

@Composable
fun HomeBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryBlue)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Chăm sóc sức khỏe\ncho bạn và gia đình",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Tìm bác sĩ, chuyên khoa, bệnh viện...", color = Color.Gray, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(25.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(25.dp),
                singleLine = true
            )
        }
    }
}

@Composable
fun QuickActions() {
    val actions = listOf(
        Triple("Đặt lịch\nkhám", Icons.Filled.CalendarToday, Color(0xFFE3F2FD)),
        Triple("Khám từ\nxa", Icons.Filled.VideoCall, Color(0xFFE8F5E9)),
        Triple("Gói khám", Icons.Filled.LocalHospital, Color(0xFFF3E5F5)),
        Triple("Kết quả\nxét nghiệm", Icons.Filled.Description, Color(0xFFFFF3E0)),
        Triple("Hồ sơ\nsức khỏe", Icons.Filled.Favorite, Color(0xFFFFEBEE))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions.forEach { action ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(action.third),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(action.second, contentDescription = null, tint = PrimaryBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(action.first, fontSize = 12.sp, color = TextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
fun UpcomingAppointmentSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lịch hẹn sắp tới", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("Xem tất cả >", color = PrimaryBlue, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Background),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Doctor Image Placeholder
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.LightGray))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(MockData.upcomingAppointment.doctor.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(MockData.upcomingAppointment.doctor.specialty, color = TextSecondary, fontSize = 12.sp)
                        Text(MockData.upcomingAppointment.doctor.hospitalName, color = TextSecondary, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier.background(SuccessBackground, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Đã xác nhận", color = Success, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(MockData.upcomingAppointment.time, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(MockData.upcomingAppointment.date, color = TextSecondary, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                    ) {
                        Text("Chi tiết")
                    }
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Nhắc lịch")
                    }
                }
            }
        }
    }
}

@Composable
fun PopularSpecialties() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Chuyên khoa phổ biến", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("Xem tất cả >", color = PrimaryBlue, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        val specialties = listOf(
            Pair("Tim mạch", Icons.Filled.Favorite),
            Pair("Hô hấp", Icons.Filled.Air),
            Pair("Tiêu hóa", Icons.Filled.Restaurant),
            Pair("Nhi khoa", Icons.Filled.ChildCare),
            Pair("Cơ xương khớp", Icons.Filled.Accessibility)
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(specialties) { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.second, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.first, fontSize = 12.sp, color = TextPrimary)
                    Text("120+ bác sĩ", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun PopularHospitals() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bệnh viện nổi bật", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("Xem tất cả >", color = PrimaryBlue, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(3) { index ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.width(160.dp).height(120.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Placeholder for Hospital Image
                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Bệnh viện Đa khoa Tâm Anh", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(12.dp))
                                    Text(" 4.8", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
