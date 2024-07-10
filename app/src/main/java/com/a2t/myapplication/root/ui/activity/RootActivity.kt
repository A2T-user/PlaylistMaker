package com.a2t.myapplication.root.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.ActivityRootBinding
import com.a2t.myapplication.root.ui.view_model.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModel<RootViewModel>()

        // Устанавливаем значение темы из сохраненного
        (applicationContext as App).switchTheme(viewModel.getAppTheme())

        // Привязываем вёрстку к экрану
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

}