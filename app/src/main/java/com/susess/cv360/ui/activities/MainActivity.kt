package com.susess.cv360.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.databinding.ActivityMainBinding
import com.susess.cv360.model.auth.AuthRequest
import com.susess.cv360.validations.ValidationResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val loginViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        // Validar en cada cambio de texto
        binding.inputUsername.doAfterTextChanged {
            loginViewModel.validateFieldLogin("username",
                binding.inputUsername.text.toString().trim()
            )
        }

        binding.inputPassword.doAfterTextChanged {
            loginViewModel.validateFieldLogin(
                "password",
                binding.inputPassword.text.toString().trim()
            )
        }

        // Validar y loguear solo cuando se da click en login
        binding.buttonLogin.setOnClickListener {
            val username = binding.inputUsername.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()

            loginViewModel.validateFieldLogin("username", username)
            loginViewModel.validateFieldLogin("password", password)

            if (loginViewModel.formState.value?.isFormValid == true) {
                val auth = AuthRequest().apply {
                    this.username = username
                    this.password = password
                }
                loginViewModel.login(auth)
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.uiState.observe(this) { state ->
            when (state) {
                is MainViewModel.UiState.Idle -> Unit
                is MainViewModel.UiState.Loading -> showLoading(true)
                is MainViewModel.UiState.Success -> {
                    showLoading(false)
                    goToDashboard(state.token, state.username)
                }
                is MainViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        loginViewModel.formState.observe(this){ formState ->
            when(val result = formState.usernameResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutUsername.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutUsername.error = null
                }
            }
            when(val result = formState.passwordResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutPassword.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutPassword.error = null
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressLogin.visibility = if (show) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !show
    }

    private fun goToDashboard(token: String?, username: String) {
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra("token", token)
            putExtra("username", username)
            putExtra("session", true.toString())
        }
        startActivity(intent)
    }
}