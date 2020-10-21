package com.duoshine.douyin

import com.hyphenate.chat.EMClient

/**
 *Created by chen on 2020
 */
class AppHelper {
    companion object {
        private var userName:String? = null

        /**
         * if ever logged in
         * @return
         */
        fun isLoggedIn(): Boolean {
            return EMClient.getInstance().isLoggedInBefore
        }

        /**
         * 设置用户名
         */
        fun setCurrentUserName(userName:String){
            this.userName = userName
        }

        /**
         * 获取用户名
         */
        fun getCurrentUserName():String{
           return EMClient.getInstance().currentUser
        }
    }
}