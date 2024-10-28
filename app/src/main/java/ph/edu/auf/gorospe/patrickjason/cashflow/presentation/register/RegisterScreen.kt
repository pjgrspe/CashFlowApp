package ph.edu.auf.gorospe.patrickjason.cashflow.presentation.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import ph.edu.auf.gorospe.patrickjason.cashflow.R
import ph.edu.auf.gorospe.patrickjason.cashflow.domain.RegisterViewModel
import ph.edu.auf.gorospe.patrickjason.cashflow.presentation.home.sections.accounts.dialogs.StyledTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = RegisterViewModel()) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var registrationResult by remember { mutableStateOf<Result<String>?>(null) }
    var loading by remember { mutableStateOf(false) }

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
            .background(Color(0xFF121212)), // Dark gray background color

    ){

    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackgroundColor),
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = darkBackgroundColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
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
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                StyledTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Username",
                    isError = username.isEmpty() && registrationResult != null,
                    errorMessage = "Username is required"
                )

                Spacer(modifier = Modifier.height(8.dp))

                StyledTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    isError = email.isEmpty() && registrationResult != null,
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
                    isError = password.isEmpty() && registrationResult != null,
                    errorMessage = "Password is required"
                )

                Spacer(modifier = Modifier.height(8.dp))

                StyledTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm Password",
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painterResource(id = if (confirmPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    isError = confirmPassword.isEmpty() || confirmPassword != password,
                    errorMessage = "Passwords do not match"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            loading = true
                            registrationResult = null
                        } else {
                            registrationResult = Result.failure(Exception("Passwords do not match"))
                        }
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
                    Text("Sign Up", color = Color.White)
                }

                registrationResult?.let { result ->
                    result.onSuccess {
                        LaunchedEffect(Unit) {
                            navController.navigate("home")
                        }
                    }
                    result.onFailure { exception ->
                        Toast.makeText(context, "Registration failed: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already have an account?", color = Color.LightGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Login",
                        color = darkGreen,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
            }
        }
    )

    if (loading) {
        LaunchedEffect(Unit) {
            val result = viewModel.registerUser(username, email, password)
            registrationResult = result
            loading = false
        }
    }
}