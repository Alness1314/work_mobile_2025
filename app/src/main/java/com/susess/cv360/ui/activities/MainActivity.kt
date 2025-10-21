package com.susess.cv360.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private val permissionViewModel: PermissionViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        permissionViewModel.onPermissionsResult(allGranted)
    }

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
        requestPermissionsIfNeeded()
    }

    private fun setupListeners() {
        // Validar en cada cambio de texto
        binding.inputUsername.doAfterTextChanged {
            loginViewModel.validateFieldLogin(
                "username",
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
        permissionViewModel.permissionsGranted.observe(this) { granted ->
            if (!granted) {
                showPermissionDialog()
            }
        }
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

        loginViewModel.formState.observe(this) { formState ->
            when (val result = formState.usernameResult) {
                is ValidationResult.Invalid -> {
                    binding.inputLayoutUsername.error = result.message
                }

                ValidationResult.Valid -> {
                    binding.inputLayoutUsername.error = null
                }
            }
            when (val result = formState.passwordResult) {
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

    private fun requestPermissionsIfNeeded() {
        val permissionsNeeded = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            permissionLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            permissionViewModel.onPermissionsResult(true)
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permisos requeridos")
            .setMessage("La aplicación necesita permisos para leer archivos multimedia.")
            .setPositiveButton("Conceder") { _, _ ->
                requestPermissionsIfNeeded()
            }
            .setNegativeButton("Configurar manualmente") { _, _ ->
                openPermissionSettings()
            }
            .setCancelable(false)
            .show()
    }

    private fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}