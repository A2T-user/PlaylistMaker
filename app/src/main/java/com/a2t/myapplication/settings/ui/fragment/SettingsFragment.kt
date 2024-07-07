package com.a2t.myapplication.settings.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.FragmentSettingsBinding
import com.a2t.myapplication.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем значение Switcher из сохраненного
        var oldDarkTheme = viewModel.getDarkTheme()
        binding.themeSwitcher.setChecked(oldDarkTheme)

        // Темная тема
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            // Сохраняем новое значение Switcher
            viewModel.updateThemeLiveData(checked)

        }
        viewModel.getThemeLiveData().observe(viewLifecycleOwner) { newDarkTheme ->
            if (oldDarkTheme != newDarkTheme) {
                (requireContext().applicationContext as App).switchTheme(newDarkTheme)
                oldDarkTheme = newDarkTheme
            }
        }


        // Поделиться приложением
        binding.tvSend.setOnClickListener {
            viewModel.updateSharingLiveData(Intent.ACTION_SEND)
        }

        // Техподдержка
        binding.tvSupport.setOnClickListener {
            viewModel.updateSharingLiveData(Intent.ACTION_SENDTO)
        }

        // Пользовательское соглашение
        binding.tvUserAgreement.setOnClickListener {
            viewModel.updateSharingLiveData(Intent.ACTION_VIEW)
        }

        viewModel.getSharingLiveData().observe(viewLifecycleOwner) {
            if (it.intentAction != null) {
                when (it.intentAction) {
                    Intent.ACTION_SEND -> shareLink(it.strExtra)        // Поделиться приложением
                    Intent.ACTION_SENDTO-> openSupport(it.strExtra)     // Техподдержка
                    else -> openTerms(it.strExtra)                      // Пользовательское соглашение
                }
            }
        }
    }

    // Поделиться приложением
    private fun shareLink (strExtra: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, strExtra)
        val chooserIntent = Intent.createChooser(intent, getString(R.string.mail))
        startActivity(chooserIntent)
    }

    // Техподдержка
    private fun openSupport(strEmail: String?) {
        val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf(strEmail))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_thank_you))
        val chooserIntent = Intent.createChooser(email, getString(R.string.mail))
        startActivity(chooserIntent)
    }

    // Пользовательское соглашение
    private fun openTerms (uriString: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW , Uri.parse(uriString))
        startActivity(browserIntent)
    }

}