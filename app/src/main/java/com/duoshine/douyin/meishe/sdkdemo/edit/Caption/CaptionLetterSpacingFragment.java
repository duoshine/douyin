package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duoshine.douyin.R;


public class CaptionLetterSpacingFragment extends Fragment {
    private Button mSmallBtn;
    private Button mStandardBtn;
    private Button mMoreBtn;
    private Button mLargeBtn;

    private TextView tv_letterSpace;
    private TextView tv_lineSpace;

    private LinearLayout mApplyToAll;
    private ImageView mApplyToAllImage;
    private TextView mApplyToAllText;
    private boolean mIsApplyToAll = false;
    private boolean mIsSelectedSmall = false;
    private boolean mIsSelectedStandard = false;
    private boolean mIsSelectedMore = false;
    private boolean mIsSelectedLarge = false;
    private OnCaptionLetterSpacingListener mCaptionLetterSpacingListener;
    public static final int CAPTION_SPACING_LETTER = 1;
    public static final int CAPTION_SPACING_LINE = 2;
    private int spacingMode = CAPTION_SPACING_LINE;
    /**
     * 设置默认的选择类型
     * 0 -- 较小
     * 1 -- 标准
     * 2 -- 较大
     * 3 -- 大
     */
    private int letterSpaceSelectedType =1;
    private int lineSpaceSelectedType =1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.caption_letter_spacing_fragment, container, false);
        mSmallBtn = rootParent.findViewById(R.id.small_btn);
        mStandardBtn = rootParent.findViewById(R.id.standard_btn);
        mMoreBtn = rootParent.findViewById(R.id.more_btn);
        mLargeBtn = rootParent.findViewById(R.id.large_btn);
        mApplyToAll = rootParent.findViewById(R.id.applyToAll);
        mApplyToAllImage = rootParent.findViewById(R.id.applyToAllImage);
        mApplyToAllText = rootParent.findViewById(R.id.applyToAllText);
        tv_lineSpace = rootParent.findViewById(R.id.tv_line_spacing);
        tv_letterSpace = rootParent.findViewById(R.id.tv_letter_spacing);
        return rootParent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mCaptionLetterSpacingListener != null) {
            mCaptionLetterSpacingListener.onFragmentLoadFinished();
        }

        initListener();

    }

    public void setCaptionLetterSpacingListener(OnCaptionLetterSpacingListener listener) {
        mCaptionLetterSpacingListener = listener;
    }

    public void applyToAllCaption(boolean isApplyToAll) {
        mApplyToAllImage.setImageResource(isApplyToAll ? R.mipmap.applytoall : R.mipmap.unapplytoall);
        mApplyToAllText.setTextColor(isApplyToAll ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ff909293"));
        mIsApplyToAll = isApplyToAll;
    }
    public void setSelectedSmall(int spacingMode,boolean isSelectedSmall) {
        this.spacingMode = spacingMode;
        this.mIsSelectedSmall= isSelectedSmall;
        if(spacingMode == CAPTION_SPACING_LINE){
            lineSpaceSelectedType = 0;
        }else{
            letterSpaceSelectedType = 0;
        }
        if (isSelectedSmall) {
            mIsSelectedStandard = false;
            mIsSelectedMore = false;
            mIsSelectedLarge = false;
        }
        updateButtons();
    }

    public void setSelectedStandard(int spacingMode,boolean isSelectedStandard) {
        this.spacingMode = spacingMode;
        this.mIsSelectedStandard = isSelectedStandard;
        if(spacingMode == CAPTION_SPACING_LINE){
            lineSpaceSelectedType = 1;
        }else{
            letterSpaceSelectedType = 1;
        }
        if (isSelectedStandard) {
            mIsSelectedMore = false;
            mIsSelectedLarge = false;
            mIsSelectedSmall = false;
        }
        updateButtons();
    }


    public void setSelectedMore(int spacingMode,boolean isSelectedMore) {
        this.spacingMode = spacingMode;
        this.mIsSelectedMore = isSelectedMore;
        if(spacingMode == CAPTION_SPACING_LINE){
            lineSpaceSelectedType = 2;
        }else{
            letterSpaceSelectedType = 2;
        }
        if (isSelectedMore) {
            mIsSelectedStandard = false;
            mIsSelectedLarge = false;
            mIsSelectedSmall = false;
        }
        updateButtons();
    }

    public void setSelectedLarge(int spacingMode,boolean isSelectedLarge) {
        this.spacingMode = spacingMode;
        this.mIsSelectedLarge = isSelectedLarge;
        if(spacingMode == CAPTION_SPACING_LINE){
            lineSpaceSelectedType = 3;
        }else{
            letterSpaceSelectedType = 3;
        }
        if (isSelectedLarge) {
            mIsSelectedStandard = false;
            mIsSelectedMore = false;
            mIsSelectedSmall = false;
        }
        updateButtons();
    }

    public void updateSmallButton(boolean isSelectedSmall) {
        mSmallBtn.setTextColor(isSelectedSmall ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ffffffff"));
        mIsSelectedSmall = isSelectedSmall;
    }

    public void updateStandardButton(boolean isSelectedStandard) {
        mStandardBtn.setTextColor(isSelectedStandard ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ffffffff"));
        mIsSelectedStandard = isSelectedStandard;
    }

    public void updateMoreButton(boolean isSelectedMore) {
        mMoreBtn.setTextColor(isSelectedMore ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ffffffff"));
        mIsSelectedMore = isSelectedMore;
    }

    public void updateLargeButton(boolean isSelectedLarge) {
        mLargeBtn.setTextColor(isSelectedLarge ? Color.parseColor("#ff4a90e2") : Color.parseColor("#ffffffff"));
        mIsSelectedLarge = isSelectedLarge;
    }

    private void updateButtons() {
        updateSpaceMode(spacingMode);
        updateSmallButton(mIsSelectedSmall);
        updateStandardButton(mIsSelectedStandard);
        updateMoreButton(mIsSelectedMore);
        updateLargeButton(mIsSelectedLarge);
    }

    private void initListener() {
        tv_lineSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spacingMode == CAPTION_SPACING_LETTER){
                    spacingMode = CAPTION_SPACING_LINE;
                    updateSpaceMode(spacingMode);
                    if(spacingMode == CAPTION_SPACING_LINE){
                        mIsSelectedSmall = lineSpaceSelectedType==0;
                        mIsSelectedStandard = lineSpaceSelectedType==1;
                        mIsSelectedMore = lineSpaceSelectedType==2;
                        mIsSelectedLarge = lineSpaceSelectedType==3;
                    }else{
                        mIsSelectedSmall = letterSpaceSelectedType==0;
                        mIsSelectedStandard = letterSpaceSelectedType==1;
                        mIsSelectedMore = letterSpaceSelectedType==2;
                        mIsSelectedLarge = letterSpaceSelectedType==3;
                    }
                    updateButtons();
                }
            }
        });

        tv_letterSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spacingMode == CAPTION_SPACING_LINE){
                    spacingMode = CAPTION_SPACING_LETTER;
                    updateSpaceMode(spacingMode);
                    if(spacingMode == CAPTION_SPACING_LINE){
                        mIsSelectedSmall = lineSpaceSelectedType==0;
                        mIsSelectedStandard = lineSpaceSelectedType==1;
                        mIsSelectedMore = lineSpaceSelectedType==2;
                        mIsSelectedLarge = lineSpaceSelectedType==3;
                    }else{
                        mIsSelectedSmall = letterSpaceSelectedType==0;
                        mIsSelectedStandard = letterSpaceSelectedType==1;
                        mIsSelectedMore = letterSpaceSelectedType==2;
                        mIsSelectedLarge = letterSpaceSelectedType==3;
                    }
                    updateButtons();
                }
            }
        });
        mSmallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionLetterSpacingListener != null) {
                    mCaptionLetterSpacingListener.onSmallBtnClicked(spacingMode);
                    updateButtons();
                    setSelectedSmall(spacingMode,true);
                }
            }
        });
        mStandardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionLetterSpacingListener != null) {
                    mCaptionLetterSpacingListener.onStandardBtnClicked(spacingMode);
                    updateButtons();
                    setSelectedStandard(spacingMode,true);
                }
            }
        });

        mMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionLetterSpacingListener != null) {
                    mCaptionLetterSpacingListener.onMoreBtnClicked(spacingMode);
                    updateButtons();
                    setSelectedMore(spacingMode,true);
                }
            }
        });

        mLargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionLetterSpacingListener != null) {
                    mCaptionLetterSpacingListener.onLargeBtnClicked(spacingMode);
                    updateButtons();
                    setSelectedLarge(spacingMode,true);
                }
            }
        });

        mApplyToAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsApplyToAll = !mIsApplyToAll;
                applyToAllCaption(mIsApplyToAll);
                if (mCaptionLetterSpacingListener != null) {
                    mCaptionLetterSpacingListener.onIsApplyToAll(mIsApplyToAll);
                }
            }
        });
    }

    private void updateSpaceMode(int spacingMode) {
        Drawable letterDrawable;
        Drawable lineDrawable;
        int letterColor;
        int lineColor;
        if(spacingMode == CAPTION_SPACING_LETTER){
            letterDrawable = getResources().getDrawable(R.drawable.shape_caption_spacing_corner_letter_selected);
            lineDrawable = getResources().getDrawable(R.drawable.shape_caption_spacing_corner_line);
            letterColor= getResources().getColor(R.color.white);
            lineColor= getResources().getColor(R.color.menu_selected);
        }else{
            letterDrawable = getResources().getDrawable(R.drawable.shape_caption_spacing_corner_letter);
            lineDrawable = getResources().getDrawable(R.drawable.shape_caption_spacing_corner_line_selected);
            letterColor= getResources().getColor(R.color.menu_selected);
            lineColor= getResources().getColor(R.color.white);
        }
        tv_letterSpace.setTextColor(letterColor);
        tv_lineSpace.setTextColor(lineColor);
        tv_letterSpace.setBackground(letterDrawable);
        tv_lineSpace.setBackground(lineDrawable);
    }

    public interface OnCaptionLetterSpacingListener {
        void onFragmentLoadFinished();

        void onSmallBtnClicked(int mode);

        void onStandardBtnClicked(int mode);

        void onMoreBtnClicked(int mode);

        void onLargeBtnClicked(int mode);

        void onIsApplyToAll(boolean isApplyToAll);
    }

}
