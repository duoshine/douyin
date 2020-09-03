package com.duoshine.douyin.util

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.util.Util
import java.util.*

class Util {
    companion object {

        /**
         * 根据毫秒数获取秒数字符串  1000  = 00:01
         */
        fun getStringForTime(timeMs: Long, formatBuilder: StringBuilder): String {
            return Util.getStringForTime(
                formatBuilder,
                Formatter(formatBuilder, Locale.getDefault()),
                timeMs
            )
        }

        /**
         * 根据毫秒数获取秒数  1000 = 1
         */
        fun getStringForTime(timeMs: Long): Int {
            var timeMs = timeMs
            if (timeMs == C.TIME_UNSET) {
                timeMs = 0
            }
            val totalSeconds = (timeMs + 500) / 1000
            return (totalSeconds % 60).toInt()
        }
    }
}