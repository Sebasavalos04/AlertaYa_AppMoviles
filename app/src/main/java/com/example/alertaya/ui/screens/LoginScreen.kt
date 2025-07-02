package com.example.alertaya.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alertaya.datos.vistamodelo.LoginState
import com.example.alertaya.datos.vistamodelo.LoginViewModel
import com.example.alertaya.ui.theme.Black
import com.example.alertaya.ui.theme.RedAlert
import com.example.alertaya.ui.theme.White

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AlertaYa",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            placeholder = { Text("••••••••") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.login(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black)
        ) {
            Text(text = "Iniciar Sesión", color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("¿No tienes una cuenta? ")
            Text(
                text = "Regístrate",
                color = RedAlert,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar mensajes según el estado del login
        when (val state = loginState) {
            is LoginState.Loading -> {
                Text("Iniciando sesión...", color = Black)
            }
            is LoginState.Error -> {
                Text("Error: ${state.message}", color = RedAlert)
            }
            is LoginState.Success -> {
                // Navegar al home solo una vez
                LaunchedEffect(Unit) {
                    navController.navigate("home")
                }
            }
            else -> {}
        }
    }
}
