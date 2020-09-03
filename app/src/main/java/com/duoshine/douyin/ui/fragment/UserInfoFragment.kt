package com.duoshine.douyin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duoshine.douyin.R
import kotlinx.android.synthetic.main.fragment_userinfo.*

/**
 *Created by chen on 2020 个人信息
 */
class UserInfoFragment : BaseFragment() {
    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {

        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val count = arguments?.getString("count","1")
        tvName.text = "当前页面：$count"
    }


    fun newInstanse(count: String): UserInfoFragment {
        val userInfoFragment = UserInfoFragment()
        val bun = Bundle()
        bun.putString("count",count)
        userInfoFragment.arguments =bun
        return userInfoFragment
    }
}