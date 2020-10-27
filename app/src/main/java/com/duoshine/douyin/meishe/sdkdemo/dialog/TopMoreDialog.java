package com.duoshine.douyin.meishe.sdkdemo.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.VerticalSeekBar;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.meicam.sdk.NvsStreamingContext;


/**
 * author : lhz
 * date   : 2020/7/27
 * desc   :拍摄页面顶部的更多dialog，包含变焦、曝光、补光灯
 */
public class TopMoreDialog {
    private String TAG = "TopMoreDialog";
    private AlertDialog mAlertDialog;
    private View mRootView;
    private NvsStreamingContext mStreamingContext;
    private LinearLayout mLlZoom;
    private LinearLayout mLlExpose;
    private LinearLayout mLlFlash;
    private LinearLayout mLlZoomBarParent;
    private VerticalSeekBar mZoomSeekBar;
    private LinearLayout mLlExposeBarParent;
    private VerticalSeekBar mExposeSeekBar;
    private TextView mTvZoomNum;
    private TextView mTvExposureNum;
    private ImageView mIvFlash;
    private EventListener mListener;
    private int mMinExpose;
    private int mCaptureType;
    private Handler mHandler;
    private final int DELAY_DISMISS = 1;
    private final int DELAY_TIME = 3000;

    private TopMoreDialog() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == DELAY_DISMISS) {
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                    } else {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                }
            }
        };
    }

    public static TopMoreDialog create(Context context, NvsStreamingContext streamingContext) {
        TopMoreDialog topMoreDialog = new TopMoreDialog();
        topMoreDialog.mStreamingContext = streamingContext;
        topMoreDialog.init(context);
        return topMoreDialog;
    }

    private void init(Context context) {
        mAlertDialog = new AlertDialog.Builder(context).create();

        mRootView = LayoutInflater.from(context).inflate(R.layout.capture_more_view, null);
        mAlertDialog.setView(mRootView);
        mAlertDialog.setCanceledOnTouchOutside(true);

        mLlZoom = mRootView.findViewById(R.id.ll_zoom);
        mLlExpose = mRootView.findViewById(R.id.ll_exposure);
        mLlFlash = mRootView.findViewById(R.id.ll_flash);
        mLlZoomBarParent = mRootView.findViewById(R.id.ll_zoom_bar_container);
        mZoomSeekBar = mRootView.findViewById(R.id.sb_zoom_bar);
        mZoomSeekBar.setThumb(R.mipmap.zoom_enlarge);
        mZoomSeekBar.setThumbSizeDp(18, 18);
        mZoomSeekBar.setmInnerProgressWidthDp(3);
        mTvZoomNum = mRootView.findViewById(R.id.tv_zoom_num);
        mLlExposeBarParent = mRootView.findViewById(R.id.ll_exposure_bar_container);
        mExposeSeekBar = mRootView.findViewById(R.id.sb_exposure_bar);
        mExposeSeekBar.setThumb(R.mipmap.capture_exposure);
        mExposeSeekBar.setThumbSizeDp(30, 30);
        mExposeSeekBar.setmInnerProgressWidthDp(3);
        mTvExposureNum = mRootView.findViewById(R.id.tv_exposure_num);
        mIvFlash = mRootView.findViewById(R.id.iv_flash);

        initListener();
    }

    private void initListener() {
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mLlZoomBarParent.getVisibility() == View.VISIBLE) {
                    mLlZoomBarParent.setVisibility(View.INVISIBLE);
                }
                if (mLlExposeBarParent.getVisibility() == View.VISIBLE) {
                    mLlExposeBarParent.setVisibility(View.INVISIBLE);
                }
                mHandler.removeCallbacksAndMessages(null);
                if (mListener != null) {
                    mListener.onDismiss();
                }
            }
        });
        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mHandler.removeCallbacksAndMessages(null);
                if (mListener != null) {
                    mListener.onDialogCancel();
                }
            }
        });
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                delayDismiss();
                mZoomSeekBar.setThumb(R.mipmap.zoom_enlarge);
                mExposeSeekBar.setThumb(R.mipmap.capture_exposure);
            }
        });
        mLlZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlZoomBarParent.setVisibility(View.VISIBLE);
                mLlExposeBarParent.setVisibility(View.INVISIBLE);
                mCaptureType = Constants.CAPTURE_TYPE_ZOOM;
                delayDismiss();
            }
        });
        mLlExpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlZoomBarParent.setVisibility(View.INVISIBLE);
                mLlExposeBarParent.setVisibility(View.VISIBLE);
                mCaptureType = Constants.CAPTURE_TYPE_EXPOSE;
                delayDismiss();
            }
        });
        mLlFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
       /*         if (mCurrentDeviceIndex == 1) {
                    mIvFlash.setImageResource(R.mipmap.capture_flash_off);
                    return;
                }*/
                delayDismiss();
                if (mLlZoomBarParent.getVisibility() == View.VISIBLE) {
                    mLlZoomBarParent.setVisibility(View.INVISIBLE);
                }
                if (mLlExposeBarParent.getVisibility() == View.VISIBLE) {
                    mLlExposeBarParent.setVisibility(View.INVISIBLE);
                }
                if (mStreamingContext.isFlashOn()) {
                    mStreamingContext.toggleFlash(false);
                    mIvFlash.setImageResource(R.mipmap.capture_flash_off);
                    mIvFlash.setImageAlpha(255);
                } else {
                    mStreamingContext.toggleFlash(true);
                    mIvFlash.setImageResource(R.mipmap.capture_flash_on);
                    mIvFlash.setImageAlpha(255);
                }
            }
        });
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == -1 && mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                return false;
            }
        });
        /*
         * 变焦调节
         * Zoom adjustment
         */
        mZoomSeekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            private boolean startTracking = false;

            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {
                startTracking = true;
            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                // Log.d("lhz","progress="+progress+"**startTracking="+startTracking+"**mCaptureType="+mCaptureType);
                delayDismiss();
                if (startTracking) {
                    if (mCaptureType == Constants.CAPTURE_TYPE_ZOOM) {
                        mStreamingContext.setZoom(progress);
                        float text = 1 + progress / 10f;
                        mTvZoomNum.setText(text + "X");
                    }
                }
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {
                startTracking = false;
            }
        });

        /*
         * 曝光补偿调节
         * Exposure compensation adjustment
         */
        mExposeSeekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {

            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                delayDismiss();
                //Log.d("lhz","mCaptureType="+mCaptureType+"**progress="+progress);
                if (mCaptureType == Constants.CAPTURE_TYPE_EXPOSE) {
                    mStreamingContext.setExposureCompensation(progress + mMinExpose);
                    mTvExposureNum.setText(progress + mMinExpose + ".0");
                }
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {

            }
        });
    }

    private void delayDismiss() {
        mHandler.removeMessages(DELAY_DISMISS);
        mHandler.sendEmptyMessageDelayed(DELAY_DISMISS, DELAY_TIME);
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    public void setFlashEnable(boolean enable) {
        if (!enable) {
            mIvFlash.setImageResource(R.mipmap.capture_flash_off);
        }
        mLlFlash.setEnabled(enable);
    }

    public boolean isShowing() {
        return mAlertDialog != null && mAlertDialog.isShowing();
    }

    public void dismiss() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    public void show() {
        if (mAlertDialog != null && mAlertDialog.getWindow() != null) {
            WindowManager.LayoutParams params = mAlertDialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.dimAmount = 0.0f;
            mAlertDialog.getWindow().setAttributes(params);
            mAlertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mAlertDialog.getContext(), R.color.colorTranslucent));
            mAlertDialog.show();
        }
    }

    public AlertDialog getDialog() {
        return mAlertDialog;
    }

    public void checkCapability(NvsStreamingContext.CaptureDeviceCapability capability) {
        /*
         * 是否支持闪光灯
         * Whether to support flash
         * */
        if (capability.supportFlash) {
            mLlFlash.setEnabled(true);
        }
        /*
         * 是否支持缩放
         * Whether to support scaling
         * */
        if (capability.supportZoom) {
            mZoomSeekBar.setMaxProgress(capability.maxZoom);
            mZoomSeekBar.setProgress(mStreamingContext.getZoom());
            //Log.d("lhz", "zoom,progress=" + mZoomSeekBar.getProgress() + "**mZoomValue=" + mZoomValue);
            mTvZoomNum.setText("1.0X");
            mZoomSeekBar.setEnabled(true);
        } else {
            Log.e(TAG, "该设备不支持缩放");
        }
        /*
         * 是否支持曝光补偿
         * Whether to support exposure compensation
         * */
        if (capability.supportExposureCompensation) {
            mMinExpose = capability.minExposureCompensation;
            mExposeSeekBar.setMaxProgress(capability.maxExposureCompensation - mMinExpose);
            mExposeSeekBar.setProgress(mStreamingContext.getExposureCompensation() - mMinExpose);
            mExposeSeekBar.setEnabled(true);
            mTvExposureNum.setText(mExposeSeekBar.getProgress() + mMinExpose + ".0");
            //  Log.d("lhz", "expose,progress=" + mExposeSeekBar.getProgress());
        }
    }

    public interface EventListener {
        void onDismiss();

        void onDialogCancel();
    }
}
