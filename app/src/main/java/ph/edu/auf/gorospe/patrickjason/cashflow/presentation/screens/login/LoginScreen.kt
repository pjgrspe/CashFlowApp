package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ph.edu.auf.gorospe.patrickjason.cashflow.R
import ph.edu.auf.gorospe.patrickjason.cashflow.domain.LoginViewModel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.screens.home.sections.accounts.dialogs.StyledTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = LoginViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginResult by remember { mutableStateOf<Result<String>?>(null) }
    var loading by remember { mutableStateOf(false) }
    var triggerLoginEffect by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val darkBackgroundColor = Color(0xFF121212)
    val darkGreen = MaterialTheme.colorScheme.secondary
    val lightGreen = MaterialTheme.colorScheme.primary

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(darkGreen, lightGreen)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackgroundColor)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackgroundColor),
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = darkBackgroundColor)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                StyledTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    isError = email.isEmpty() && loginResult != null,
                    errorMessage = "Email is required"
                )

                Spacer(modifier = Modifier.height(8.dp))

                StyledTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painterResource(id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    isError = password.isEmpty() && loginResult != null,
                    errorMessage = "Password is required"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Forgot password?",
                    color = darkGreen,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { /* Handle forgot password action */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        loading = true
                        triggerLoginEffect = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(36.dp))
                        .background(gradientBrush)
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Login", color = Color.White)
                }

                loginResult?.let { result ->
                    result.onFailure { exception ->
                        Toast.makeText(context, "Login failed: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "No account yet?", color = Color.LightGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign up",
                        color = darkGreen,
                        modifier = Modifier.clickable { navController.navigate("register") }
                    )
                }
            }
        }
    )

    if (triggerLoginEffect) {
        LoginEffectHandler(viewModel, email, password, navController) { result ->
            loginResult = result
            loading = false
            triggerLoginEffect = false
        }
    }
}

@Composable
fun LoginEffectHandler(
    viewModel: LoginViewModel,
    email: String,
    password: String,
    navController: NavController,
    onResult: (Result<String>) -> Unit
) {
    LaunchedEffect(Unit) {
        val result = viewModel.loginUser(email, password)
        result.onSuccess {
            navController.navigate("home")
        }
        onResult(result)
    }
}