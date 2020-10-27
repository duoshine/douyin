package com.duoshine.douyin.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;


/**
 * Created by CaoZhiChao on 2018/5/28 15:10
 */
public class CustomTitleBar extends LinearLayout implements View.OnClickListener {
    TextView textCenter, textRight;
    private RelativeLayout backLayout;
    private RelativeLayout forwardLayout;
    OnTitleBarClickListener onTitleBarClickListener;
    private boolean finishActivity = true;
    private ImageView backLayoutImageView;
    private RelativeLayout mainLayout;

    public CustomTitleBar(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View viewParent = mInflater.inflate(R.layout.view_titlebar, null);
        addView(viewParent);
        mainLayout = (RelativeLayout) viewParent.findViewById(R.id.main_layout);
        backLayout = (RelativeLayout) viewParent.findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        backLayoutImageView = (ImageView) viewParent.findViewById(R.id.back_layout_imageView);
        textCenter = (TextView) viewParent.findViewById(R.id.text_center);
        textCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources( ).getDimension(R.dimen.title_textSize));
        textCenter.getPaint( ).setFakeBoldText(true);
        textCenter.setOnClickListener(this);
        textRight = (TextView) viewParent.findViewById(R.id.text_right);
        textRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources( ).getDimension(R.dimen.title_textSize));
        textRight.getPaint( ).setFakeBoldText(true);
        forwardLayout = (RelativeLayout) viewParent.findViewById(R.id.forwardLayout);
        forwardLayout.setOnClickListener(this);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setFinishActivity(boolean finishActivity) {
        this.finishActivity = finishActivity;
    }

    public void setTextCenterColor(int color) {
        textCenter.setTextColor(color);
    }

    public void setTextCenter(String msg) {
        textCenter.setText(msg);
    }

    public void setTextCenter(@StringRes int resid) {
        textCenter.setText(getContext( ).getResources( ).getText(resid));
    }

    public void setTextRight(String msg) {
        textRight.setText(msg);
    }

    public void setTextRight(@StringRes int resid) {
        textRight.setText(getContext( ).getResources( ).getText(resid));
    }

    public void setTextRightVisible(int visible) {
        textRight.setVisibility(visible);
        if (visible == VISIBLE) {
            forwardLayout.setClickable(true);
        } else {
            forwardLayout.setClickable(false);
        }
    }

    public void setBackImageVisible(int visible) {
        backLayout.setVisibility(visible);
    }

    public void setBackImageIcon(int resid) {
        backLayoutImageView.setImageResource(resid);
    }

    public void setMainLayoutColor(int color) {
        mainLayout.setBackgroundColor(color);
    }

    public void setMainLayoutResource(int resid) {
        mainLayout.setBackgroundResource(resid);
    }

    public OnTitleBarClickListener getOnTitleBarClickListener() {
        return onTitleBarClickListener;
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
        this.onTitleBarClickListener = onTitleBarClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId( )) {
            case R.id.backLayout:
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.OnBackImageClick( );
                }
                if (finishActivity) {
                    AppManager.getInstance( ).finishActivity( );
                }
                break;
            case R.id.text_center:
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.OnCenterTextClick( );
                }
                break;
            case R.id.forwardLayout:
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.OnRightTextClick( );
                }
                break;
            default:
                break;
        }
    }
}
