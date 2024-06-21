package com.a2t.myapplication.mediateca.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.ActivityMediatecaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediatecaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatecaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MediatecaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.tab_favorites)
                1 -> tab.text = getString(R.string.tab_playlist)
            }
        }
        tabMediator.attach()

        binding.ivArrow.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}