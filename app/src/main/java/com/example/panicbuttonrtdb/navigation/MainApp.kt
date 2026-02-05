package com.example.panicbuttonrtdb.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panicbuttonrtdb.prensentation.components.OnBoarding
import com.example.panicbuttonrtdb.prensentation.screens.UserProfileScreen
import com.example.panicbuttonrtdb.prensentation.screens.DetailRekapScreen
import com.example.panicbuttonrtdb.prensentation.screens.DataRekapScreen
import com.example.panicbuttonrtdb.prensentation.screens.DashboardAdminScreen
import com.example.panicbuttonrtdb.prensentation.screens.DashboardUserScreen
import com.example.panicbuttonrtdb.prensentation.screens.HelpScreen
import com.example.panicbuttonrtdb.prensentation.screens.LoginScreen
import com.example.panicbuttonrtdb.prensentation.screens.SignUpScreen
import com.example.panicbuttonrtdb.viewmodel.ViewModel
import com.example.panicbuttonrtdb.viewmodel.ViewModelFactory

@Composable
fun MainApp() {
    val context = LocalContext.current
    val viewModel: ViewModel = viewModel(factory = ViewModelFactory(context))
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val navController = rememberNavController()

    // <-- AWAL BLOK PERUBAHAN: Logika startDestination diperbarui -->
    val isOnboardingShown = sharedPreferences.getBoolean("OnBoardingShown", false)
    val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
    val userRole = sharedPreferences.getString("user_role", null)

    val startDestination = when {
        !isOnboardingShown -> "onboarding"
        isLoggedIn -> {
            if (userRole == "admin") "dashboard_admin" else "dashboard"
        }
        else -> "login"
    }
    // <-- AKHIR BLOK PERUBAHAN -->

    NavHost(navController = navController, startDestination = startDestination) {

        //onBoarding
        composable("onboarding") {
            OnBoarding(navController = navController)
            LaunchedEffect(Unit) {
                sharedPreferences.edit().putBoolean("OnBoardingShown", true).commit()
            }
        }

        // Halaman Sign Up
        composable("signup") {
            SignUpScreen(
                navController = navController,
                context = context
            )
        }

        // Halaman Login
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = viewModel,
                context = context
            )
        }

        // Halaman Dashboard
        composable("dashboard") {
            DashboardUserScreen(
                context = context,
                viewModel = viewModel,
                navController = navController,
                onLogout = {
                    viewModel.logout()
                    // Navigasi kembali ke layar login dan hapus backstack
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("dashboard_admin") {
            DashboardAdminScreen(
                context = context,
                navController = navController,
                viewModel = viewModel,
                onLogout = {
                    // <-- PERUBAHAN: adminLogout() diganti menjadi logout() -->
                    viewModel.logout()
                    // Navigasi kembali ke layar login dan hapus backstack
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("data_rekap") {
            DataRekapScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("detail_rekap/{houseNumber}") {backStackEntry ->
            val nomorRumah = backStackEntry.arguments?.getString("houseNumber")
            DetailRekapScreen(
                houseNumber = nomorRumah ?:"",
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("user_profile") {
            UserProfileScreen(
                context = context,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            "help",
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500))},
            exitTransition = {slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500))}
        ) {
            HelpScreen(
                navController =navController
            )
        }
    }
}