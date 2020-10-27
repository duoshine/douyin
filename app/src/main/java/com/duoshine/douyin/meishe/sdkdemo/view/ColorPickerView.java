package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupData;


public class ColorPickerView extends RelativeLayout {
    private RoundColorView mCustomBtn;
    private ColorSeekBar mColorSeekBar;
    private int[] mDefaultColor = new int[]{Color.RED, Color.RED, Color.BLUE};
    private ListView mColorList;
    private ListAdapter mListAdapter;

    private OnColorChangedListener mOnColorChangedListener;
    private OnColorSeekBarStateChangeListener mOnColorSeekBarStateChangeListener;


    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.mOnColorChangedListener = onColorChangedListener;
    }

    public void setOnColorSeekBarStateChangeListener(OnColorSeekBarStateChangeListener onColorSeekBarStateChangeListener) {
        this.mOnColorSeekBarStateChangeListener = onColorSeekBarStateChangeListener;
    }

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setDefaultColor(int[] defaultColor, MakeupData.ColorData colorData) {
        mDefaultColor = defaultColor;
        initData(colorData);
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_makeup_color, this);
        mColorSeekBar = rootView.findViewById(R.id.seekBar);
        mColorList = (ListView) rootView.findViewById(R.id.color_list);
        mColorSeekBar.setOnColorChangedListener(new ColorSeekBar.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mCustomBtn.setColor(color);
                if (mOnColorChangedListener != null) {
                    mOnColorChangedListener.onColorChanged(new MakeupData.ColorData(mColorSeekBar.rawX, -1, color));
                }
            }
        });
        mCustomBtn = rootView.findViewById(R.id.custom_btn);
        mCustomBtn.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
        mCustomBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorSeekBar.getVisibility() == View.VISIBLE) {
                    mColorSeekBar.setVisibility(GONE);
                    mCustomBtn.setSelected(false);
                    mCustomBtn.setText("");
                    mCustomBtn.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
                } else {
                    mColorSeekBar.setVisibility(VISIBLE);
                    mCustomBtn.setSelected(true);
                    mCustomBtn.setBitmap(null);
                }
                if (mOnColorSeekBarStateChangeListener != null) {
                    mOnColorSeekBarStateChangeListener.onColorSeekBarStateChanged(mColorSeekBar.getVisibility() == View.VISIBLE);
                }

                if (mListAdapter.getSelectPosition() >= 0) {
                    mListAdapter.setSelectPosition(-1);
                }
            }
        });

    }

    private void initData(MakeupData.ColorData colorData) {
        initListView(colorData);
    }

    private void initListView(MakeupData.ColorData colorData) {
        mListAdapter = new ListAdapter();
        float progress = -1f;
        int colorIndex = -1;
        int color = mDefaultColor[mDefaultColor.length - 1];
        if (colorData != null) {
            colorIndex = colorData.colorIndex;
            progress = colorData.colorsProgress;
            if (colorData.color > 0) {
                color = colorData.color;
            }
        }
        mListAdapter.setSelectPosition(colorIndex);
        mColorSeekBar.setColors(mDefaultColor, progress);
        mListAdapter.setData(mDefaultColor);
        mColorList.setAdapter(mListAdapter);
        if (mDefaultColor != null) {
            mCustomBtn.setColor(color);
        }
    }

    class ListAdapter extends BaseAdapter {

        private int[] mData = new int[]{Color.RED, Color.RED, Color.BLUE};
        private int mSelectPosition = -1;

        public void setSelectPosition(int selectPosition) {
            this.mSelectPosition = selectPosition;
            notifyDataSetChanged();
        }

        public int getSelectPosition() {
            return mSelectPosition;
        }

        public void setData(int[] data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.length;
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_makeup_color, null);
                viewHolder = new ViewHolder();
                viewHolder.colorView = convertView.findViewById(R.id.color_btn);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (mSelectPosition == position) {
                viewHolder.colorView.setSelected(true);
            } else {
                viewHolder.colorView.setSelected(false);
            }
            viewHolder.colorView.setTextAndColor("BO3", mData[position]);

            viewHolder.colorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnColorChangedListener != null) {
                        mOnColorChangedListener.onColorChanged(new MakeupData.ColorData(-1f, position, mData[position]));
                    }
                    if (mCustomBtn.isSelected()) {
                        mCustomBtn.setSelected(false);
                        mCustomBtn.setText("");
                        mCustomBtn.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
                        mColorSeekBar.setVisibility(GONE);
                    }
                    mSelectPosition = position;
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        class ViewHolder {
            public RoundColorView colorView;
        }
    }

    public interface OnColorChangedListener {
        void onColorChanged(MakeupData.ColorData colorData);
    }

    public interface OnColorSeekBarStateChangeListener {
        void onColorSeekBarStateChanged(boolean show);
    }
}
