package com.susess.cv360.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.susess.cv360.R
import com.susess.cv360.databinding.ActivitySecondBinding
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.auth.Session
import com.susess.cv360.storage.SessionDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var navController: NavController

    @Inject lateinit var sessionManager: SessionManager
    private lateinit var sessionDataStore: SessionDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sessionDataStore = SessionDataStore(this)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // registrar toolbar como ActionBar
        setSupportActionBar(binding.toolbarHeaderApp)
        val token = intent.getStringExtra("token")
        val username = intent.getStringExtra("username")

        // Guardamos en memoria
        sessionManager.token = token
        sessionManager.username = username
        sessionManager.isLoggedIn = true

        // Guardamos en DataStore
        lifecycleScope.launch {
            sessionDataStore.saveSession(
                Session(
                    token = token,
                    username = username,
                    isLoggedIn = true
                )
            )
        }

        Log.i("data session", sessionManager.toString())

        setupNavigation()
        /*val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_second)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings, R.id.navigation_dashboard, R.id.navigation_about
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/
        setupLogout()
    }
    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment_activity_second)

        // Configurar Toolbar personalizado
        setSupportActionBar(binding.toolbarHeaderApp)

        // Configurar para que el título se muestre en nuestro TextView personalizado
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Observar cambios en el título
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Actualizar el título personalizado
            binding.toolbarTitle.text = destination.label ?: getString(R.string.app_name)
        }

        val navView: BottomNavigationView = binding.navView

        // Configurar AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings, R.id.navigation_dashboard, R.id.navigation_about
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // Esto hace que la flecha Up funcione y navegue en el NavController (hacia el padre)
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupLogout() {
       binding.imageButtonLogout.setOnClickListener {
            lifecycleScope.launch {
                // Limpiar todo
                sessionDataStore.clearSession()
                sessionManager.clear()

                // Ir a login
                startActivity(Intent(this@SecondActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}