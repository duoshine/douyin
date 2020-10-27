package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


public class RoundColorView extends View {
    private int mTextSize;
    private int mSelectRoundColor;
    private int mStrokeWidth;
    private Paint mColorPaint = new Paint();
    private Paint mColorLinePaint = new Paint();
    private Paint mTextPaint = new Paint();
    private int mColor;
    private boolean mSeleced = false;
    private String mText;
    private Bitmap mBitmap;

    public RoundColorView(Context context) {
        super(context);
    }

    public RoundColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundColorView);
        mStrokeWidth = mTypedArray.getDimensionPixelSize(R.styleable.RoundColorView_selectStrokeWidth,
                ScreenUtils.dip2px(getContext(), 2));
        mSelectRoundColor = mTypedArray.getColor(R.styleable.RoundColorView_selectRoundStrokeColor,
                Color.BLUE);
        mTextSize = mTypedArray.getDimensionPixelOffset(R.styleable.RoundColorView_roundTextSize, 0);
        initData();
    }

    public void setColor(int color) {
        mColor = color;
        mColorPaint.setColor(mColor);
        invalidate();
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

    public void setTextAndColor(String text, int color) {
        mText = text;
        mColor = color;
        mColorPaint.setColor(mColor);
        invalidate();
    }

    @Override
    public void setSelected(boolean selected) {
        mSeleced = selected;
        invalidate();
    }

    public boolean isSelected() {
        return mSeleced;
    }

    private void initData() {
        mColorPaint.setAntiAlias(true);
        if (mColor == 0) {
            mColor = Color.RED;
        }
        mColorPaint.setColor(mColor);
        mColorPaint.setStyle(Paint.Style.FILL);

        mColorLinePaint.setAntiAlias(true);
        mColorLinePaint.setColor(mSelectRoundColor);
        mColorLinePaint.setStrokeWidth(mStrokeWidth);
        mColorLinePaint.setStyle(Paint.Style.STROKE);


        mTextPaint.setAntiAlias(true);
        //mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSeleced) {
            mColorPaint.setColor(mColor);
            canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredHeight() / 2.0f, mColorPaint);
            canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredHeight() / 2.0f - mStrokeWidth / 2.0f, mColorLinePaint);
        } else {
            if(mBitmap != null) {
                mColorPaint.setColor(getContext().getResources().getColor(R.color.caption_edit_edit_text_hint_color));
                canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredHeight() / 2.0f, mColorPaint);
                int dstValue= ScreenUtils.dip2px(getContext(), 30);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, dstValue, dstValue, true);
                canvas.drawBitmap(scaledBitmap, 0, 0, mColorPaint);
            } else {
                canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredHeight() / 2.0f, mColorPaint);
            }
        }

        if (!TextUtils.isEmpty(mText)) {
            float txtWidth = mTextPaint.measureText(mText, 0, mText.length());
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            float txtHeight = fm.bottom - fm.top;
            //计算长宽
            int x = (int) (getMeasuredWidth() / 2.0f - txtWidth/ 2.0f);
            int y = (int) (getMeasuredHeight() / 2.0f + txtHeight / 4.0f);
            canvas.drawText(mText, x, y, mTextPaint);
        }
    }

}
