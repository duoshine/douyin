package com.duoshine.douyin.constants

import com.duoshine.douyin.R

class EmojiConstants {

    companion object {
        private val emojis_1 = mutableListOf(
            R.mipmap.d__,
            R.mipmap.d_0,
            R.mipmap.d_1,
            R.mipmap.d_2,
            R.mipmap.d_3,
            R.mipmap.d_4,
            R.mipmap.d_5,
            R.mipmap.d_6,
            R.mipmap.d_7,
            R.mipmap.d_8,
            R.mipmap.d_9,
            R.mipmap.d_a,
            R.mipmap.d_b,
            R.mipmap.d_c,
            R.mipmap.d_d,
            R.mipmap.d_e,
            R.mipmap.d_f,
            R.mipmap.d_g,
            R.mipmap.d_h,
            R.mipmap.d_i,
            R.mipmap.d_j
        )

        private val emojis_2 = mutableListOf(
            R.mipmap.d_k,
            R.mipmap.d_l,
            R.mipmap.d_m,
            R.mipmap.d_n,
            R.mipmap.d_o,
            R.mipmap.d_p,
            R.mipmap.d_q,
            R.mipmap.d_r,
            R.mipmap.d_s,
            R.mipmap.d_t,
            R.mipmap.d_u,
            R.mipmap.d_v,
            R.mipmap.d_w,
            R.mipmap.d_x,
            R.mipmap.d_y,
            R.mipmap.d_z,
            R.mipmap.d8n,
            R.mipmap.d8o,
            R.mipmap.d8p,
            R.mipmap.d8r,
            R.mipmap.d8s
        )

        private val emojis_3 = mutableListOf(
            R.mipmap.d8t,
            R.mipmap.d8u,
            R.mipmap.d8v,
            R.mipmap.d8w,
            R.mipmap.d8x,
            R.mipmap.d8y,
            R.mipmap.d8z,
            R.mipmap.d_r,
            R.mipmap.d90,
            R.mipmap.d91,
            R.mipmap.d92,
            R.mipmap.d93,
            R.mipmap.d94,
            R.mipmap.d95,
            R.mipmap.d96,
            R.mipmap.d97,
            R.mipmap.d98,
            R.mipmap.d99,
            R.mipmap.d9a,
            R.mipmap.d9b,
            R.mipmap.d9c
        )
        val emojiList = mutableListOf<MutableList<Int>>().apply {
            add(emojis_1)
            add(emojis_2)
            add(emojis_3)
            add(emojis_1)
            add(emojis_2)
            add(emojis_3)
            add(emojis_1)
            add(emojis_2)
        }

        val defaultEmojiList = mutableListOf<Int>(
            R.mipmap.db_,
            R.mipmap.db0,
            R.mipmap.db1,
            R.mipmap.db2,
            R.mipmap.db3,
            R.mipmap.db4,
            R.mipmap.db5,
            R.mipmap.db6
        )

        val codeMap = HashMap<String, Int>().apply {
            put("\uE415", R.mipmap.d__)
            put("\uE056", R.mipmap.d_0)
            put("\uE057", R.mipmap.d_1)
            put("\uE414", R.mipmap.d_2)
            put("\uE405", R.mipmap.d_3)
            put("\uE106", R.mipmap.d_4)
            put("\uE418", R.mipmap.d_5)
            put("\uE417", R.mipmap.d_6)
            put("\uE40d", R.mipmap.d_7)
            put("\uE40a", R.mipmap.d_8)
            put("\uE404", R.mipmap.d_9)
            put("\uE105", R.mipmap.d_a)
            put("\uE409", R.mipmap.d_b)
            put("\uE40e", R.mipmap.d_c)
            put("\uE402", R.mipmap.d_d)
            put("\uE108", R.mipmap.d_e)
            put("\uE403", R.mipmap.d_f)
            put("\uE058", R.mipmap.d_g)
            put("\uE407", R.mipmap.d_h)
            put("\uE401", R.mipmap.d_i)
            put("\uE40f", R.mipmap.d_j)
            put("\uE40b", R.mipmap.d_k)
            put("\uE406", R.mipmap.d_l)
            put("\uE413", R.mipmap.d_m)
            put("\uE411", R.mipmap.d_n)
            put("\uE412", R.mipmap.d_o)
            put("\uE410", R.mipmap.d_p)
            put("\uE107", R.mipmap.d_q)
            put("\uE059", R.mipmap.d_r)
            put("\uE416", R.mipmap.d_s)
            put("\uE408", R.mipmap.d_t)
            put("\uE40c", R.mipmap.d_u)
            put("\uE11a", R.mipmap.d_v)
            put("\uE10c", R.mipmap.d_w)
            put("\uE32c", R.mipmap.d_x)
            put("\uE32a", R.mipmap.d_y)
            put("\uE32d", R.mipmap.d_z)
            put("\uE328", R.mipmap.d8n)
            put("\uE32b", R.mipmap.d8o)
            put("\uE022", R.mipmap.d8p)
            put("\uE023", R.mipmap.d8r)
            put("\uE327", R.mipmap.d8s)
            put("\uE329", R.mipmap.d8t)
            put("\uE32e", R.mipmap.d8u)
            put("\uE32f", R.mipmap.d8v)
            put("\uE335", R.mipmap.d8w)
            put("\uE334", R.mipmap.d8x)
            put("\uE021", R.mipmap.d8y)
            put("\uE336", R.mipmap.d8z)
            put("\uE13c", R.mipmap.d_r)
            put("\uE337", R.mipmap.d90)
            put("\uE020", R.mipmap.d91)
            put("\uE330", R.mipmap.d92)
            put("\uE331", R.mipmap.d93)
            put("\uE326", R.mipmap.d94)
            put("\uE03e", R.mipmap.d95)
            put("\uE11d", R.mipmap.d96)
            put("\uE05a", R.mipmap.d97)
            put("\uE00e", R.mipmap.d98)
            put("\uE421", R.mipmap.d99)
            put("\uE420", R.mipmap.d9a)
            put("\uE00d", R.mipmap.d9b)
            put("\uE010", R.mipmap.d9c)
            put("\uE011", R.mipmap.db_)
            put("\uE41e", R.mipmap.db0)
            put("\uE012", R.mipmap.db1)
            put("\uE422", R.mipmap.db2)
            put("\uE22e", R.mipmap.db3)
            put("\uE22f", R.mipmap.db4)
            put("\uE231", R.mipmap.db5)
            put("\uE230", R.mipmap.db6)
        }

        val resIdMap = HashMap<Int, String>().apply {
            put(R.mipmap.d__, "\uE415")
            put(R.mipmap.d_0, "\uE056")
            put(R.mipmap.d_1, "\uE057")
            put(R.mipmap.d_2, "\uE414")
            put(R.mipmap.d_3, "\uE405")
            put(R.mipmap.d_4, "\uE106")
            put(R.mipmap.d_5, "\uE418")
            put(R.mipmap.d_6, "\uE417")
            put(R.mipmap.d_7, "\uE40d")
            put(R.mipmap.d_8, "\uE40a")
            put(R.mipmap.d_9, "\uE404")
            put(R.mipmap.d_a, "\uE105")
            put(R.mipmap.d_b, "\uE409")
            put(R.mipmap.d_c, "\uE40e")
            put(R.mipmap.d_d, "\uE402")
            put(R.mipmap.d_e, "\uE108")
            put(R.mipmap.d_f, "\uE403")
            put(R.mipmap.d_g, "\uE058")
            put(R.mipmap.d_h, "\uE407")
            put(R.mipmap.d_i, "\uE401")
            put(R.mipmap.d_j, "\uE40f")
            put(R.mipmap.d_k, "\uE40b")
            put(R.mipmap.d_l, "\uE406")
            put(R.mipmap.d_m, "\uE413")
            put(R.mipmap.d_n, "\uE411")
            put(R.mipmap.d_o, "\uE412")
            put(R.mipmap.d_p, "\uE410")
            put(R.mipmap.d_q, "\uE107")
            put(R.mipmap.d_r, "\uE059")
            put(R.mipmap.d_s, "\uE416")
            put(R.mipmap.d_t, "\uE408")
            put(R.mipmap.d_u, "\uE40c")
            put(R.mipmap.d_v, "\uE11a")
            put(R.mipmap.d_w, "\uE10c")
            put(R.mipmap.d_x, "\uE32c")
            put(R.mipmap.d_y, "\uE32a")
            put(R.mipmap.d_z, "\uE32d")
            put(R.mipmap.d8n, "\uE328")
            put(R.mipmap.d8o, "\uE32b")
            put(R.mipmap.d8p, "\uE022")
            put(R.mipmap.d8r, "\uE023")
            put(R.mipmap.d8s, "\uE327")
            put(R.mipmap.d8t, "\uE329")
            put(R.mipmap.d8u, "\uE32e")
            put(R.mipmap.d8v, "\uE32f")
            put(R.mipmap.d8w, "\uE335")
            put(R.mipmap.d8x, "\uE334")
            put(R.mipmap.d8y, "\uE021")
            put(R.mipmap.d8z, "\uE336")
            put(R.mipmap.d_r, "\uE13c")
            put(R.mipmap.d90, "\uE337")
            put(R.mipmap.d91, "\uE020")
            put(R.mipmap.d92, "\uE330")
            put(R.mipmap.d93, "\uE331")
            put(R.mipmap.d94, "\uE326")
            put(R.mipmap.d95, "\uE03e")
            put(R.mipmap.d96, "\uE11d")
            put(R.mipmap.d97, "\uE05a")
            put(R.mipmap.d98, "\uE00e")
            put(R.mipmap.d99, "\uE421")
            put(R.mipmap.d9a, "\uE420")
            put(R.mipmap.d9b, "\uE00d")
            put(R.mipmap.d9c, "\uE010")
            put(R.mipmap.db_, "\uE011")
            put(R.mipmap.db0, "\uE41e")
            put(R.mipmap.db1, "\uE012")
            put(R.mipmap.db2, "\uE422")
            put(R.mipmap.db3, "\uE22e")
            put(R.mipmap.db4, "\uE22f")
            put(R.mipmap.db5, "\uE231")
            put(R.mipmap.db6, "\uE230")
        }
    }
}