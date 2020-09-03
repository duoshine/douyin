package com.duoshine.douyin.test


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.CommentExpandAdapter
import com.duoshine.douyin.model.CommentReply
import com.duoshine.douyin.model.CommentReplyModel
import com.duoshine.douyin.model.Comments
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_recycler_test.*


class RecyclerActivity : AppCompatActivity() {

    private val reply = ArrayList<CommentReply>().apply {
        val commentReply = CommentReply("","duo_shine","哈哈，你真搞笑","2020-09-03 22:00:00")
        add(commentReply)
    }

    //    private val commentModel: CommentModel = CommentModel("0", 20, 1, true, comments)
    private val comments = ArrayList<Comments>().apply {
        val comment1 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment2 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment3 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment4 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment5 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment6 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment7 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment8 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment9 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        val comment10 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, reply
        )
        add(comment1)
        add(comment2)
        add(comment3)
        add(comment4)
        add(comment5)
        add(comment6)
        add(comment7)
        add(comment8)
        add(comment9)
        add(comment10)
    }

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
        val bottomSheetCommentList =
            view.findViewById<ExpandableListView>(R.id.bottom_sheet_comment_list)
        //取消左侧的箭头指示  自定义在右侧
        bottomSheetCommentList.setGroupIndicator(null)
            //设置取消下划线
        bottomSheetCommentList.divider = null
        val adapter = CommentExpandAdapter(this, comments)
        bottomSheetCommentList.setAdapter(adapter)

    }
}