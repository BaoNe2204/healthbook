package com.example.healthbook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthbook.ui.screens.main.MainScreen

sealed class Screen(val route: String) {
    object Main : Screen("main") // Main screen with BottomNav
    object DoctorList : Screen("doctor_list")
    object DoctorDetail : Screen("doctor_detail/{doctorId}") {
        fun createRoute(doctorId: String) = "doctor_detail/$doctorId"
    }
    object Booking : Screen("booking/{doctorId}") {
        fun createRoute(doctorId: String) = "booking/$doctorId"
    }
    object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: String) = "appointment_detail/$appointmentId"
    }
    object Payment : Screen("payment/{appointmentId}") {
        fun createRoute(appointmentId: String) = "payment/$appointmentId"
    }
}

@Composable
fun HealthBookNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen()
        }
        composable(Screen.DoctorList.route) {
            // DoctorListScreen(navController)
        }
        composable(Screen.DoctorDetail.route) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            // DoctorDetailScreen(navController, doctorId)
        }
        composable(Screen.Booking.route) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            // BookingScreen(navController, doctorId)
        }
        composable(Screen.AppointmentDetail.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            // AppointmentDetailScreen(navController, appointmentId)
        }
        composable(Screen.Payment.route) { backStackEntry ->
             val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
             // PaymentScreen(navController, appointmentId)
        }
    }
}
