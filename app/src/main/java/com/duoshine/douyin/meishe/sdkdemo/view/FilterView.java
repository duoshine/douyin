package com.duoshine.douyin.meishe.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.MSApplication;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.filter.FilterAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 2018/11/15.
 */

public class FilterView extends RelativeLayout {
    private SeekBar mIntensitySeekBar;
    private TextView mIntensityText;
    private LinearLayout mIntensityLayout;
    private LinearLayout mFilterFxList;
    private RecyclerView mFilterRecyclerList;
    private LinearLayout mMoreFilterButton;
    private ImageButton mMoreFilerImage;
    private TextView mMoreFilerText;
    private RelativeLayout mFilterTabLayout;
    private TextView mCartoonFilterTab, mCommonFilterTab;
    private View mVCartoonLine, mVFilterLine;
    private FilterAdapter mFilterAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private OnFilterListener mFilterListener;
    private OnSeekBarTouchListener mSeekBarTouchListener;
    private OnRecyclerViewScrollListener mRecyclerScrollListener;
    private boolean isBlackTheme = false;

    public interface OnFilterListener {
        void onItmeClick(View v, int position);

        void onMoreFilter();

        void onIntensity(int value);
    }

    public interface OnSeekBarTouchListener {
        void onStartTrackingTouch();

        void onStopTrackingTouch();
    }

    public interface OnRecyclerViewScrollListener {
        void onRecyclerViewScroll(RecyclerView recyclerView, int dx, int dy);
    }

    public void setSeekBarTouchListener(OnSeekBarTouchListener seekBarTouchListener) {
        mSeekBarTouchListener = seekBarTouchListener;
    }

    public void setRecyclerScrollListener(OnRecyclerViewScrollListener recyclerScrollListener) {
        mRecyclerScrollListener = recyclerScrollListener;
    }

    public void setFilterFxListBackgroud(String strColor) {
        mFilterTabLayout.setBackgroundColor(Color.parseColor(strColor));
        mFilterFxList.setBackgroundColor(Color.parseColor(strColor));
    }

    public void setIntensityTextVisible(int visible) {
        mIntensityText.setVisibility(visible);
    }

    public void setIntensityLayoutVisible(int visible) {
        if(mIntensityLayout.getVisibility() != visible){
            mIntensityLayout.setVisibility(visible);
        }
    }

    public void setFilterListener(OnFilterListener faceUPropListener) {
        this.mFilterListener = faceUPropListener;
    }

    public void setMoreFilterClickable(boolean clickable) {
        mMoreFilterButton.setClickable(clickable);
    }

    public void setIntensitySeekBarVisibility(int visibility) {
        mIntensitySeekBar.setVisibility(visibility);
    }

    public void setIntensitySeekBarProgress(int progress) {
        mIntensitySeekBar.setProgress(progress);
    }
    public int getIntensitySeekBarProgress() {
       return mIntensitySeekBar.getProgress();
    }
    public void setIntensitySeekBarMaxValue(int maxValue) {
        mIntensitySeekBar.setMax(maxValue);
    }

    public FilterView(Context context) {
        this(context, null);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setBlackTheme(boolean is) {
        this.isBlackTheme = true;
        if (mCommonFilterTab != null) {
            mCommonFilterTab.setTextColor(ContextCompat.getColor(MSApplication.getmContext(), R.color.ccffffff));
        }
        if (mMoreFilerText != null) {
            mMoreFilerText.setTextColor(ContextCompat.getColor(MSApplication.getmContext(), R.color.ccffffff));
        }
    }

    public int getSelectedPos() {
        if (mFilterAdapter != null) {
            return mFilterAdapter.getSelectPos();
        }
        return 0;
    }

    public void setSelectedPos(int selectedPos) {
        if (mFilterAdapter != null) {
            mFilterAdapter.setSelectPos(selectedPos);
            mFilterAdapter.notifyDataSetChanged();
        }
    }

    public void scrollToPosition(final int pos){
        if(mFilterRecyclerList != null){
            mFilterRecyclerList.post(new Runnable() {
                @Override
                public void run() {
                    mFilterRecyclerList.scrollToPosition(pos);
                }
            });
        }
    }
    public void setFilterArrayList(ArrayList<FilterItem> filterDataList) {
        if (mFilterAdapter != null) {
            mFilterAdapter.setFilterDataList(filterDataList);
            if (mFilterAdapter.getSpecialFilterCount() > 0) {
                mFilterTabLayout.setVisibility(VISIBLE);
            } else {
                mFilterTabLayout.setVisibility(GONE);
            }

        }

    }

    public void notifyDataSetChanged() {
        if (mFilterAdapter != null) {
            mFilterAdapter.notifyDataSetChanged();
        }
    }

    public void initFilterRecyclerView(Context context) {
        mLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mFilterRecyclerList.setLayoutManager(mLinearLayoutManager);
        mFilterRecyclerList.setAdapter(mFilterAdapter);

        mFilterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFilterListener != null) {
                    mFilterListener.onItmeClick(view, position);
                }
            }
        });

        mFilterRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRecyclerScrollListener != null) {
                    mRecyclerScrollListener.onRecyclerViewScroll(recyclerView, dx, dy);
                }

                int position = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (position >= mFilterAdapter.getSpecialFilterCount()) {
                    selectFilterTab(false);
                } else {
                    selectFilterTab(true);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        mFilterAdapter = new FilterAdapter(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.filter_list_view, this);
        mIntensityLayout = (LinearLayout) rootView.findViewById(R.id.intensityLayout);
        mIntensityText = (TextView) rootView.findViewById(R.id.intensityText);
        mIntensitySeekBar = (SeekBar) rootView.findViewById(R.id.intensitySeekBar);
        mCartoonFilterTab = (TextView) rootView.findViewById(R.id.cartoonFilterTab);
        mVCartoonLine = rootView.findViewById(R.id.v_cartoon_line);
        mCommonFilterTab = (TextView) rootView.findViewById(R.id.commonFilterTab);
        mVFilterLine = rootView.findViewById(R.id.v_filter_line);
        mFilterTabLayout = (RelativeLayout) rootView.findViewById(R.id.filterTabLayout);
        mIntensityLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect seekRect = new Rect();
                mIntensitySeekBar.getHitRect(seekRect);
                if ((event.getY() >= (seekRect.top - 10)) && (event.getY() <= (seekRect.bottom + 10))) {
                    float y = seekRect.top + seekRect.height() / 2;
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y, event.getMetaState());
                    return mIntensitySeekBar.onTouchEvent(me);
                }
                return false;
            }
        });
        mIntensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mFilterListener != null) {
                        mFilterListener.onIntensity(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mSeekBarTouchListener != null) {
                    mSeekBarTouchListener.onStartTrackingTouch();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mSeekBarTouchListener != null) {
                    mSeekBarTouchListener.onStopTrackingTouch();
                }
            }
        });
        mFilterFxList = (LinearLayout) rootView.findViewById(R.id.filterFxList);
        mFilterRecyclerList = (RecyclerView) rootView.findViewById(R.id.filterRecyclerList);
        mMoreFilerImage = (ImageButton) rootView.findViewById(R.id.moreFilerImage);
        mMoreFilerImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoreFilterButton.callOnClick();
            }
        });
        mMoreFilerText = (TextView) rootView.findViewById(R.id.moreFilerText);
        mMoreFilerText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoreFilterButton.callOnClick();
            }
        });
        mMoreFilterButton = (LinearLayout) rootView.findViewById(R.id.moreFilterButton);
        mMoreFilterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilterListener != null) {
                    mFilterListener.onMoreFilter();
                }
            }
        });

        mCartoonFilterTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFilterTab(true);
                mLinearLayoutManager.scrollToPositionWithOffset(1, 0);
            }
        });

        mCommonFilterTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFilterTab(false);
                mLinearLayoutManager.scrollToPositionWithOffset(mFilterAdapter.getSpecialFilterCount() + 1, 0);
            }
        });
    }

    private void selectFilterTab(boolean isCartoon) {
        if (isCartoon) {
            mCartoonFilterTab.setTextColor(ContextCompat.getColor(MSApplication.getmContext(), R.color.ms_blue));
            mCommonFilterTab.setTextColor(ContextCompat.getColor(MSApplication.getmContext(), isBlackTheme ? R.color.ccffffff : R.color.black));
            mVCartoonLine.setBackgroundColor(getResources().getColor(R.color.ms_blue));
            mVFilterLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
        } else {
            mCartoonFilterTab.setTextColor(ContextCompat.getColor(MSApplication.getmContext(), isBlackTheme ? R.color.ccffffff : R.color.black));
            mCommonFilterTab.setTextColor(ContextCompat.getColor(MSApplication.getmContext(), R.color.ms_blue));
            mVCartoonLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
            mVFilterLine.setBackgroundColor(getResources().getColor(R.color.ms_blue));
        }
    }
}
