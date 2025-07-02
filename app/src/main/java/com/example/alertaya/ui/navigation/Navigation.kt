package com.example.alertaya.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alertaya.ui.screens.HomeScreen
import com.example.alertaya.ui.screens.LoginScreen
import com.example.alertaya.datos.vistamodelo.LoginState
import com.example.alertaya.datos.vistamodelo.LoginViewModel
import com.example.alertaya.ui.screens.RegisterScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val REGISTER = "register"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.HOME) {
            val loginViewModel: LoginViewModel = viewModel()
            val loginState = loginViewModel.loginState.collectAsState().value
            Log.d("HomeScreen", "Estado actual: $loginState")
            val userName = if (loginState is LoginState.Success)
                (loginState as LoginState.Success).name
            else
                "Usuario"
            HomeScreen(
                navController = navController,
                userName = userName,
                onLogout = {
                    // Volver al login limpiando el backstack
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

    }
}