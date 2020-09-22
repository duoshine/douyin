package com.duoshine.douyin.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duoshine.douyin.R;
import com.duoshine.douyin.util.AnimUtils;

import java.util.Random;

/**
 * description 点赞动画view
 */
public class LikeView extends RelativeLayout {
    private GestureDetector gestureDetector;
    /** 图片大小 */
    private int likeViewSize = 200;
    private int[] angles = new int[]{-30, 0, 30};
    /** 单击是否有点赞效果 */
    private boolean canSingleTabShow = false;
    private OnLikeListener onLikeListener;

    private String TAG = "LikeView";

    public LikeView(Context context) {
        super(context);
        init();
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap: ");
                addLikeView(e);
                if (onLikeListener != null) {
                    onLikeListener.onLikeListener();
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onLikeListener != null) {
                    onLikeListener.onPlayOrPause();
                }
                return true;
            }
        });

        setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void addLikeView(MotionEvent e) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.c2x);

        addView(imageView);

        LayoutParams layoutParams = new LayoutParams(likeViewSize, likeViewSize);
        layoutParams.leftMargin = (int) e.getX() - likeViewSize / 2;
        layoutParams.topMargin = (int) e.getY() - likeViewSize;
        imageView.setLayoutParams(layoutParams);

        playAnim(imageView);
    }

    private void playAnim(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        int degrees = angles[new Random().nextInt(3)];
        animationSet.addAnimation(AnimUtils.rotateAnim(0, 0, degrees));
        animationSet.addAnimation(AnimUtils.scaleAnim(100, 2f, 1f, 0));
        animationSet.addAnimation(AnimUtils.alphaAnim(0, 1, 100, 0));
        animationSet.addAnimation(AnimUtils.scaleAnim(500, 1f, 1.8f, 300));
        animationSet.addAnimation(AnimUtils.alphaAnim(1f, 0, 500, 300));
        animationSet.addAnimation(AnimUtils.translationAnim(500, 0, 0, 0, -400, 300));

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(() -> removeView(view));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animationSet);
    }

    public interface OnLikeListener {
        void onLikeListener();
        void onPlayOrPause();
    }

    /**
     * 设置事件
     * @param onLikeListener
     */
    public void setOnLikeListener(OnLikeListener onLikeListener) {
        this.onLikeListener = onLikeListener;
    }
}