package com.duoshine.douyin.meishe.sdkdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.MSApplication;
import com.duoshine.douyin.meishe.sdkdemo.utils.SystemUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
* 隐私及声明对话框
* Privacy and declaration dialog
* */
public class PrivacyPolicyDialog extends Dialog {
    private static final String TAG = "PrivacyPolicyDialog";
    /*
    * 《服务协议》及《隐私政策》中文匹配，?表示尽量少匹配
    * "Service Agreement" and "Privacy Policy" are matched in Chinese,? Means as few matches as possible
    * */
    private static final String PRIVACY_MATCH_RULE_CH = "《.*?》";
    /*
     * 《服务协议》及《隐私政策》英文匹配，?表示尽量少匹配
     * "Service Agreement" and "Privacy Policy" are matched in English,? Means as few matches as possible
     * */
    private static final String PRIVACY_MATCH_RULE_EN = "\".*?\"";
    private TextView mNotUsedButton;
    private TextView mAgreeButton;
    private TextView mStatementContent;
    private Context mContext = MSApplication.getmContext();
    private OnPrivacyClickListener mPrivacyListener;

    public interface OnPrivacyClickListener {
        void onButtonClick(boolean isAgree);
        void pageJumpToWeb(String clickTextContent);
    }

    public void setOnButtonClickListener(OnPrivacyClickListener listener) {
        this.mPrivacyListener = listener;
    }

    public PrivacyPolicyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_dialog_layout);
        setCanceledOnTouchOutside(false);
        initViews();
        initData();
        initViewsListener();
    }

    private void initData(){
        String statementContent = mContext.getString(R.string.statement_content);
        final SpannableStringBuilder contentStyle = new SpannableStringBuilder();
        contentStyle.append(statementContent);
        String regex =  SystemUtils.isZh(mContext) ? PRIVACY_MATCH_RULE_CH : PRIVACY_MATCH_RULE_EN;
        Pattern p = Pattern.compile(regex);//正则表达式，匹配搜索关键字
        Matcher matcher = p.matcher(statementContent);
        while (matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            final String groupContent = matcher.group();
            //设置部分文字点击事件
            final ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (mPrivacyListener != null){
                        mPrivacyListener.pageJumpToWeb(groupContent);
                    }
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);//去掉下划线
                }
            };
            contentStyle.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置部分文字颜色
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#ff407df8"));
            contentStyle.setSpan(foregroundColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        }
        mStatementContent.setMovementMethod(LinkMovementMethod.getInstance());//实现点击事件
        mStatementContent.setText(contentStyle);
        mStatementContent.setHighlightColor(mContext.getResources().getColor(R.color.colorTranslucent));//去掉点击背景色
    }

    private void initViews(){
        mStatementContent = (TextView)findViewById(R.id.statementContent);
        mNotUsedButton = (TextView)findViewById(R.id.notUsedButton);
        mAgreeButton = (TextView)findViewById(R.id.agreeButton);
    }

    private void initViewsListener() {
        mNotUsedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrivacyListener != null){
                    mPrivacyListener.onButtonClick(false);
                }
                dismiss();
            }
        });
        mAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrivacyListener != null){
                    mPrivacyListener.onButtonClick(true);
                }
                dismiss();
            }
        });
    }
}
