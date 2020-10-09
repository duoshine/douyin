package com.duoshine.douyin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 *Created by chen on 2020
 */
class UserInfoTabAdapter(fragment: FragmentManager, private val fragments: MutableList<Fragment>,private val titles:MutableList<String>) :
    FragmentStatePagerAdapter(fragment,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}