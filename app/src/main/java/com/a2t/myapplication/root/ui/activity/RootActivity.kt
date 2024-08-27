package com.a2t.myapplication.root.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.ActivityRootBinding
import com.a2t.myapplication.root.ui.view_model.RootViewModel
import com.a2t.myapplication.сreateplaylist.ui.fragment.CreatePlaylistFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

const val MESSAGE_DURATION = 3000L

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var backPressedCallback: OnBackPressedCallback
    lateinit var navController: NavController
    lateinit var bottomNavigationView: BottomNavigationView
    private var bottomNavigationViewVisibility = View.VISIBLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModel<RootViewModel>()

        // Устанавливаем значение темы из сохраненного
        (applicationContext as App).switchTheme(viewModel.getAppTheme())

        // Привязываем вёрстку к экрану
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        backPressedCallback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                if (CreatePlaylistFragment.isCreatePlaylistFragmentFilled) {
                    MaterialAlertDialogBuilder(this@RootActivity)
                        .setTitle("Завершить создание плейлиста?")                  // Заголовок диалога
                        .setMessage("Все несохраненные данные будут потеряны")      // Описание диалога
                        .setNeutralButton("Отмена") { dialog, which ->          // Добавляет кнопку «Отмена»
                        }
                        .setPositiveButton("Завершить") { dialog, which ->      // Добавляет кнопку «Завершить»
                            navController.popBackStack()
                            CreatePlaylistFragment.isCreatePlaylistFragmentFilled = false
                        }
                        .show()
                } else {
                    navController.popBackStack()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.e ("МОЁ","destination = " + destination.id.toString())
            when (destination.id) {
                R.id.createPlaylistFragment, R.id.playerFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    bottomNavigationViewVisibility = View.GONE
                    backPressedCallback.isEnabled = true
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationViewVisibility = View.VISIBLE
                    backPressedCallback.isEnabled = false
                }
            }
            stopShowMessage()
        }
    }



    fun closeFragment () {
        navController.popBackStack()
    }

    fun showMessage (str: String) {
        binding.tvMessage.text = str
        bottomNavigationView.visibility = View.GONE
        binding.tvMessage.isVisible = true
        lifecycleScope.launch {
            delay(MESSAGE_DURATION)
            stopShowMessage()
        }
    }

    fun stopShowMessage () {
        binding.tvMessage.isVisible = false
        bottomNavigationView.visibility = bottomNavigationViewVisibility
    }

}