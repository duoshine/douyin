package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * 字幕背景色
 */
public class CaptionBackgroundFragment extends Fragment {
    private RecyclerView mCaptionBackgroundRecyclerView;
    private CaptionBackgroundRecyclerAdaper mCaptionBackgroundRecycleAdapter;
    private SeekBar mCaptonOpacitySeekBar;
    private SeekBar mCaptonCornerSeekBar;
    private TextView mSeekBarOpacityValue;
    private TextView mSeekBarCornerValue;
    private OnCaptionBackgroundListener captionBackgroundListener;
    private ArrayList<CaptionColorInfo> mCaptionColorInfolist = new ArrayList<>();
    private LinearLayout mApplyToAll;
    private ImageView mApplyToAllImage;
    private TextView mApplyToAllText;
    private boolean mIsApplyToAll = false;
    public interface OnCaptionBackgroundListener{
        void onFragmentLoadFinished();
        void onCaptionColor(int pos);
        void onCaptionOpacity(int progress);
        void onCaptionCorner(int progress);
        void onIsApplyToAll(boolean isApplyToAll);
    }

    public void setCaptionBackgroundListener(OnCaptionBackgroundListener captionBackgroundListener) {
        this.captionBackgroundListener = captionBackgroundListener;
    }
    public void updateCaptionOpacityValue(int progress){
        mSeekBarOpacityValue.setText(String.valueOf(progress));
        mCaptonOpacitySeekBar.setProgress(progress);
    }

    /**
     * 更新设置圆角的进度
     * @param progress 获取圆角的值
     *
     */
    public void updateCaptionCornerValue(int progress){

        mSeekBarCornerValue.setText(String.valueOf(progress));
        mCaptonCornerSeekBar.setProgress(progress);
    }

    /**
     * 设置最大进度
     *  @param max 最大值(原则上是高度的一半)
     */
    public void initCaptionMaxCorner(int max){
        mCaptonCornerSeekBar.setMax(max);
    }

    public void setCaptionBackgroundInfolist(ArrayList<CaptionColorInfo> captionColorInfolist) {
        this.mCaptionColorInfolist = captionColorInfolist;
        if(mCaptionBackgroundRecycleAdapter != null)
            mCaptionBackgroundRecycleAdapter.setCaptionBackgroundColorList(captionColorInfolist);
    }

    public void applyToAllCaption(boolean isApplyToAll){
        mApplyToAllImage.setImageResource(isApplyToAll ? R.mipmap.applytoall : R.mipmap.unapplytoall);
        mApplyToAllText.setTextColor(isApplyToAll ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ff909293"));
        mIsApplyToAll = isApplyToAll;
    }

    public void notifyDataSetChanged(){
        mCaptionBackgroundRecycleAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.caption_background_fragment, container, false);
        mCaptionBackgroundRecyclerView = (RecyclerView)rootParent.findViewById(R.id.captionColorRecyerView);
        mCaptonOpacitySeekBar = (SeekBar)rootParent.findViewById(R.id.captonOpacitySeekBar);
        mCaptonOpacitySeekBar.setMax(100);
        mCaptonCornerSeekBar = (SeekBar)rootParent.findViewById(R.id.captonCornerSeekBar);
        mCaptonCornerSeekBar.setMax(100);
        mSeekBarOpacityValue = (TextView) rootParent.findViewById(R.id.seekBarOpacityValue);
        mSeekBarCornerValue = (TextView) rootParent.findViewById(R.id.seekBarCornerValue);
        mApplyToAll = (LinearLayout)rootParent.findViewById(R.id.applyToAll);
        mApplyToAllImage = (ImageView)rootParent.findViewById(R.id.applyToAllImage);
        mApplyToAllText = (TextView)rootParent.findViewById(R.id.applyToAllText);
        return rootParent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCaptionBackgroundRecycleAdapter();
        initCaptionBackgroundSeekBar();
        if(captionBackgroundListener != null){
            captionBackgroundListener.onFragmentLoadFinished();
        }
    }

    private void initCaptionBackgroundSeekBar() {
        mCaptonOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionOpacityValue(progress);
                    if(captionBackgroundListener != null){
                        captionBackgroundListener.onCaptionOpacity(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mCaptonCornerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateCaptionCornerValue(progress);
                    if(captionBackgroundListener != null){
                        captionBackgroundListener.onCaptionCorner(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initCaptionBackgroundRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCaptionBackgroundRecyclerView.setLayoutManager(layoutManager);
        mCaptionBackgroundRecycleAdapter = new CaptionBackgroundRecyclerAdaper(getActivity());
        mCaptionBackgroundRecycleAdapter.setCaptionBackgroundColorList(mCaptionColorInfolist);
        mCaptionBackgroundRecyclerView.setAdapter(mCaptionBackgroundRecycleAdapter);
        mCaptionBackgroundRecyclerView.addItemDecoration(new SpaceItemDecoration(0, ScreenUtils.dip2px(getActivity(),16)));
        mCaptionBackgroundRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if(captionBackgroundListener != null){
                    captionBackgroundListener.onCaptionColor(pos);
                }
            }
        });

        mApplyToAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsApplyToAll = !mIsApplyToAll;
                applyToAllCaption(mIsApplyToAll);
                if(captionBackgroundListener != null){
                    captionBackgroundListener.onIsApplyToAll(mIsApplyToAll);
                }
            }
        });
    }
}
