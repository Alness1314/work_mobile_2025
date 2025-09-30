package com.susess.cv360.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.databinding.ActivityMainBinding
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.auth.AuthRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var sessionManager: SessionManager

    // inyectamos ViewModel con Hilt (by viewModels())
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

        Log.i("data session", sessionManager.toString())

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            val auth: AuthRequest = AuthRequest().apply {
                username = binding.inputUsername.text.toString().trim()
                password = binding.inputPassword.text.toString().trim()
            }
            loginViewModel.login(auth)
        }
    }

    private fun setupObservers() {
        loginViewModel.uiState.observe(this) { state ->
            when (state) {
                is MainViewModel.UiState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }

                is MainViewModel.UiState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    goToDashboard(state.token, state.username)
                }

                is MainViewModel.UiState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG)
                        .show()
                }

                else -> Unit
            }

        }

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