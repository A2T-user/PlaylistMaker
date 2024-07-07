package com.a2t.myapplication.mediateca.ui.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a2t.myapplication.mediateca.ui.view_model.PlaylistViewModel
import com.a2t.myapplication.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistFragment()
    }
    private lateinit var binding: FragmentPlaylistBinding

    private val viewModel: PlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
}