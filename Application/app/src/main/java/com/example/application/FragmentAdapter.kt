package com.example.application

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private lateinit var userId : String
    private lateinit var types : String

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, userId: String, types : String) : this(fragmentManager,lifecycle) {
        this.userId = userId
        this.types = types
    }

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