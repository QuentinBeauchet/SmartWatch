package com.example.application.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var userId: String,
    private var types: String
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            StartupFragment.newInstance(userId)
        } else {
            TypesFragment.newInstance(types)
        }
    }

}