package com.duoshine.douyin.test


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.CommentAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_recycler_test.*


class RecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_test)
        //设置加载的json文件所用到的资源目录
        //animation_view.imageAssetsFolder = "digg_heart_bubbles/images"
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels
        val heightPixels = outMetrics.heightPixels

        val bottomSheetDialog = BottomSheetDialog(this)
        val behavior = bottomSheetDialog.behavior

        val view =
            layoutInflater.inflate(R.layout.player_comment_dialog, null, false)
        val bottomSheetHeight = (heightPixels * 0.8).toInt()
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            bottomSheetHeight //设置自定义的view的高度为屏幕高度的百分之80，不设置的话其父默认是wrap_content
        )
        //设置底部bottomSheet的高度 如果不设置默认为 16:9  这里设置为屏幕高度的百分之80
        behavior.peekHeight = bottomSheetHeight

        bottomSheetDialog.setContentView(view, params)

        btn_show_dialog.setOnClickListener {
            bottomSheetDialog.show()
        }

        //添加适配器
        val bottomSheetCommentList = view.findViewById<RecyclerView>(R.id.bottom_sheet_comment_list)
        bottomSheetCommentList.layoutManager = LinearLayoutManager(this)
        bottomSheetCommentList.adapter = CommentAdapter(this)
    }
}