package com.a2t.myapplication.сreateplaylist.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.FragmentCreatePlaylistBinding
import com.a2t.myapplication.root.ui.activity.RootActivity
import com.a2t.myapplication.сreateplaylist.ui.view_model.CreatePlaylistViewModel
import com.a2t.myapplication.сreateplaylist.ui.view_model.StatPlaylistAdded
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val URI = "uri"                     // Тег для сохранения URI картинки

open class CreatePlaylistFragment : Fragment() {
    companion object {
        var isCreatePlaylistFragmentFilled = false
    }
    private var strPlayListUri: String = ""

    open val viewModel by viewModel<CreatePlaylistViewModel>()

    open lateinit var binding: FragmentCreatePlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null) {
            val strUri = savedInstanceState.getString(URI, "")
            if (strUri.isNotEmpty()) {
                showCover(strUri.toUri())
            }
        }

        viewModel.getPlaylistAdded().observe(viewLifecycleOwner) { newState ->
            val rootActivity = requireActivity() as RootActivity
            when (newState) {
                StatPlaylistAdded.SUCCESS -> {
                    val str = getString(R.string.playlist_added_success0) + " " + binding.etName.text + " " + getString(R.string.playlist_added_success1)
                    rootActivity.closeFragment()
                    rootActivity.showMessage(str)
                }
                else -> {}
            }
        }

        // Стрелка назад
        binding.backButton.setOnClickListener {
            viewModel.isFilled()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // **********Вставка обложки************

        // Ррегистрируем событие, которое вызывает photopicker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                strPlayListUri = uri.toString()           // Сохраняем URI картинки из общего хранилища
                showCover(strPlayListUri.toUri())      // Выводим картинку из общего хранилища на экран
                viewModel.setUri(strPlayListUri)

            }

        //По клику на playListImage запускаем photopicker
        binding.playListImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Отслеживание изменений в поле 'Название'
        binding.etName.addTextChangedListener(
            afterTextChanged = { s: Editable? ->
                if(!s.isNullOrEmpty()) {
                    binding.etName.setBackgroundResource(R.drawable.edit_text_frame_blue)
                    binding.inscriptionName.isVisible = true
                    binding.createButton.isEnabled = true
                    viewModel.setName(s.toString())
                } else {
                    binding.etName.setBackgroundResource(R.drawable.edit_text_frame)
                    binding.inscriptionName.isVisible = false
                    binding.createButton.isEnabled = false
                    viewModel.setName("")
                }
            }
        )

        // Отслеживание изменений в поле 'Описание'
        binding.etDescription.addTextChangedListener(
            afterTextChanged = { s: Editable? ->
                if(!s.isNullOrEmpty()) {
                    binding.etDescription.setBackgroundResource(R.drawable.edit_text_frame_blue)
                    binding.inscriptionDescription.isVisible = true
                    viewModel.setDescription(s.toString())
                } else {
                    binding.etDescription.setBackgroundResource(R.drawable.edit_text_frame)
                    binding.inscriptionDescription.isVisible = false
                    viewModel.setDescription("")
                }
            }
        )

        // Сохранение плейлиста
        binding.createButton.setOnClickListener {
            savePlaylist(strPlayListUri)
        }

    }


    // Сохранение плейлиста
    open fun savePlaylist (strPlayListUri: String) {
        viewModel.addNewPlaylist(strPlayListUri)
    }

    // Вставка картинки
    fun showCover (uri: Uri?) {
        if(uri != null) {
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_album_big)
                .centerCrop()
                .into(binding.playListImage)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(URI, strPlayListUri)
    }
}