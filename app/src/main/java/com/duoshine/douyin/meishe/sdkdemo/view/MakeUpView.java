package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.BeautyData;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.CustomMakeup;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupArgs;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupData;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupEffect;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupEffectContent;
import com.duoshine.douyin.meishe.sdkdemo.capture.CaptureDataHelper;
import com.duoshine.douyin.meishe.sdkdemo.capture.MakeupAdapter;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.VerticalSeekBar;
import com.duoshine.douyin.meishe.sdkdemo.utils.ColorUtil;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMakeupEffectInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.duoshine.douyin.meishe.sdkdemo.capture.CaptureDataHelper.ASSETS_MAKEUP_PATH;


public class MakeUpView extends RelativeLayout {

    private static final String TAG = "MakeUpView";
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    private ImageView mMakeupChangeBtn;
    private TextView mMakeupChangeBtnText, mTvColor, mTvAlpha;
    private View mMakeupColorHinLayout, mMakeupTopLayout;
    private ColorPickerView mColorPickerView;
    //    private IndicatorSeekBar mMakeupSeekBar;
    private VerticalIndicatorSeekBar mMakeupSeekBar;
    private boolean mIsMakeupMainMenu = true;

    private MakeupAdapter mMakeupAdapter;
    private ArrayList<BeautyData> mMakeupDataList;
    private MakeUpEventListener mMakeUpEventListener;

    // as customCategory--> eyeshadow--> one type of eyeshadow
    private String mCurrentEffectName = null;
    // as customCategory--> eyeshadow
    private String mCurrentEffectId = null;
    private boolean isClearMakeup = false;
    private boolean isSelectCompose = false;
    private boolean isSelectCustom = false;

    public MakeUpView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public MakeUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_make_up, this);
        mMakeupRecyclerView = (RecyclerView) rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeupTopLayout = rootView.findViewById(R.id.makeup_top_layout);
        mMakeupChangeBtn = (ImageView) rootView.findViewById(R.id.change_btn);
        mMakeupChangeBtnText = (TextView) rootView.findViewById(R.id.change_btn_text);
        mTvColor = rootView.findViewById(R.id.tv_color);
        mTvAlpha = rootView.findViewById(R.id.tv_alpha);
        mMakeupColorHinLayout = rootView.findViewById(R.id.makeup_color_hint_layout);
        mColorPickerView = (ColorPickerView) rootView.findViewById(R.id.color_picker_view);
        mMakeupSeekBar = rootView.findViewById(R.id.seek_bar);
        mMakeupAdapter = new MakeupAdapter(mContext, mMakeupDataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
        mMakeupAdapter.setEnable(true);
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MakeupData.getInstacne().setComposeIndex(position);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewComposeDataChanged(position, isClearMakeup);
                    if (isClearMakeup) {
                        isClearMakeup = false;
                    }
                }
            }
        });

        mColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            float lastIntensity = 0f;

            @Override
            public void onColorChanged(MakeupData.ColorData colorData) {
                int color = colorData.color;
                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
                MakeupData.getInstacne().addSelectColor(mCurrentEffectName, colorData);//缓存数据
                NvsMakeupEffectInfo.MakeupEffect makeupEffect = MakeupData.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupEffect == null) {
                    return;
                }
                if (lastIntensity != makeupEffect.intensity) {
                    String alpha = String.format(getResources().getString(R.string.make_up_transparency), (int) (makeupEffect.intensity * 100)) + "%";
                    mTvAlpha.setText(alpha);
                    lastIntensity = makeupEffect.intensity;
                }
                float alphaF = (Color.alpha(color) * 1.0f / 255f);
                float red = (Color.red(color) * 1.0f / 255f);
                float green = (Color.green(color) * 1.0f / 255f);
                float blue = (Color.blue(color) * 1.0f / 255f);
                NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
                if (makeupEffect.color.a != 0 || makeupEffect.color.r != 0 || makeupEffect.color.g != 0 || makeupEffect.color.b != 0) {
                    makeupEffect.color = nvsColor;
                }
                List<NvsMakeupEffectInfo.MakeupEffectLayer> effectLayerArray = makeupEffect.getMakeupEffectLayerArray();
                if (effectLayerArray.isEmpty()) {
                    return;
                }
                for (NvsMakeupEffectInfo.MakeupEffectLayer makeupEffectLayer : effectLayerArray) {
                    if (makeupEffectLayer instanceof NvsMakeupEffectInfo.MakeupEffectLayer3D) {
                        NvsColor texColor = ((NvsMakeupEffectInfo.MakeupEffectLayer3D) makeupEffectLayer).texColor;
                        if (texColor.a != 0 || texColor.r != 0 || texColor.g != 0 || texColor.b != 0) {
                            ((NvsMakeupEffectInfo.MakeupEffectLayer3D) makeupEffectLayer).texColor = nvsColor;
                        }
                    }
                }
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewDataChanged();
                }
            }
        });

        mColorPickerView.setOnColorSeekBarStateChangeListener(new ColorPickerView.OnColorSeekBarStateChangeListener() {
            @Override
            public void onColorSeekBarStateChanged(boolean show) {
                mMakeupColorHinLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        mMakeupSeekBar.setOnSeekBarChangedListener(new VerticalIndicatorSeekBar.OnSeekBarChangedListener() {

            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                NvsMakeupEffectInfo.MakeupEffect makeupEffect = MakeupData.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupEffect == null) {
                    return;
                }
                String alpha = String.format(getResources().getString(R.string.make_up_transparency), progress) + "%";
                mTvAlpha.setText(alpha);
                makeupEffect.intensity = progress * 1.0f / 100;
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewDataChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

            }
        });

        mMakeupChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMakeupMainMenu || !mMakeupChangeBtnText.getText().toString().equals(mContext.getResources().getString(R.string.make_up_custom))) {
                    if (mIsMakeupMainMenu) {
                        Set<String> fxSet = MakeupData.getInstacne().getFxSet();
                        for (String fxName : fxSet) {
                            if (mMakeUpEventListener != null) {
                                mMakeUpEventListener.removeVideoFxByName(fxName);
                            }
                        }
                        fxSet.clear();
                    }
                    mCurrentEffectId = null;
                    // display custom makeup category
                    changeToMakeupSubMenu(new CaptureDataHelper().getCustomMakeupDataList(mContext),
                            mContext.getResources().getString(R.string.make_up_custom), MakeupAdapter.MAKE_UP_WHITE_BG_TYPE);
                } else {
                    // display compose makeup category
                    changeToMakeupMainMenu();
                }
            }
        });
        mMakeupTopLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMakeupTopLayout.getId() == v.getId() && mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeUpViewDismiss();
                }
                return false;
            }
        });
    }

    public void setMakeupArrayList(ArrayList<BeautyData> makeupDataList) {
        mMakeupDataList = makeupDataList;
        if (mMakeupAdapter != null) {
            changeToMakeupMainMenu();
        }
    }

    private void changeToMakeupMainMenu() {
        MakeupData.getInstacne().clearData();
        mMakeupChangeBtnText.setText(mContext.getResources().getString(R.string.make_up_custom));
        mMakeupChangeBtn.setImageResource(R.mipmap.makeup_custom);
        mMakeupAdapter.setDataList(mMakeupDataList, MakeupAdapter.MAKE_UP_RANDOM_BG_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        mMakeupAdapter.setEnable(true);
        final int index = MakeupData.getInstacne().getComposeMakeupIndex();
        //onComposeMakeupDataChanged(index);
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    if ((isSelectCompose && isSelectCustom) || (isSelectCompose && !isSelectCustom)) {
                        isClearMakeup = true;
                        isSelectCompose = false;
                        isSelectCustom = false;
                        MakeupData.getInstacne().clearPositionData();
                    }
                } else {
                    isSelectCompose = true;
                    //这里清除选择的组合美妆
                    MakeupData.getInstacne().clearData();
                    MakeupData.getInstacne().clearPositionData();

                }
                MakeupData.getInstacne().setComposeIndex(position);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewComposeDataChanged(position, isClearMakeup);
                    if (isClearMakeup) {
                        isClearMakeup = false;
                    }
                }
            }
        });
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (index <= 0) {
                    mMakeupRecyclerView.scrollToPosition(0);
                } else {
                    mMakeupRecyclerView.scrollToPosition(index);
                }
            }
        });
        mIsMakeupMainMenu = true;
    }

    private void changeToMakeupSubMenu(final List<BeautyData> data, String backText, int type) {
        MakeupData.getInstacne().clearData();
        mMakeupChangeBtnText.setText(backText);
        mMakeupChangeBtn.setImageResource(R.mipmap.beauty_facetype_back);
        mMakeupAdapter.setDataList(data, type);
        mMakeupAdapter.notifyDataSetChanged();
        // custom makeup inner position
        final int index = MakeupData.getInstacne().getPositionByEffectId(mCurrentEffectId);
        if (index > 0) {
            onSubViewSelect(data.get(index));
        } else {
            setColorPickerVisibility(View.INVISIBLE);
        }
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setEnable(true);
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BeautyData selectItem = mMakeupAdapter.getSelectItem();
                if (selectItem instanceof CustomMakeup) {
                    // custom make up category item
                    CustomMakeup item = (CustomMakeup) selectItem;
                    ArrayList<MakeupEffect<MakeupEffectContent>> effectList = item.getEffectList();
                    try {
                        // get name of custom makeup category
                        MakeupEffectContent makeupEffectContent = effectList.get(0).getEffectContent();
                        mCurrentEffectId = makeupEffectContent.getMakeupArgs().get(0).getMakeupId();
                    } catch (Exception e) {
                        Log.e(TAG, "MakeupAdapter-> onItemClick error:" + e.fillInStackTrace());
                    }
                    MakeupEffect<MakeupEffectContent> nullItem = new MakeupEffect<MakeupEffectContent>() {
                        @Override
                        public String getImageResource() {
                            return ASSETS_MAKEUP_PATH + "/effect_clear.png";
                        }
                    };
                    nullItem.setName(mContext.getResources().getString(R.string.makeup_null));
                    nullItem.setBackgroundColor(ColorUtil.FILTER_BG_NO_SELECTED);
                    effectList.add(0, nullItem);
                    List<BeautyData> list = new ArrayList<>();
                    for (MakeupEffect makeupEffect : effectList) {
                        makeupEffect.setFolderPath(item.getFolderPath());
                        makeupEffect.setIsBuildIn(item.isBuildIn());
                        list.add(makeupEffect);
                    }
                    changeToMakeupSubMenu(list, item.getName(mContext), MakeupAdapter.MAKE_UP_ROUND_ICON_TYPE);
                } else if (selectItem instanceof MakeupEffect) {
                    // custom make up item from a category
                    isSelectCustom = true;
                    MakeupData.getInstacne().addSelectPosition(mCurrentEffectId, position);
                    if (position == 0) { // 把当前选中的特效删除
                        setColorPickerVisibility(View.INVISIBLE);
                        MakeupData.getInstacne().removeMakeupArgs(mCurrentEffectId);
                    } else {
                        if (mMakeupAdapter.getSelectPos() == position) {
                            MakeupData.getInstacne().removeMakeupArgs(mCurrentEffectId);
                        }
                        onSubViewSelect(selectItem);
                    }
                    if (mMakeUpEventListener != null) {
                        mMakeUpEventListener.onMakeupViewDataChanged();
                    }
                }
            }
        });
        mIsMakeupMainMenu = false;
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (index <= 0) {
                    mMakeupRecyclerView.scrollToPosition(0);
                } else {
                    mMakeupRecyclerView.scrollToPosition(index);
                }
            }
        });
    }

    /**
     * select one type of makeup effect from a custom makeup category
     *
     * @param selectItem
     */
    private void onSubViewSelect(BeautyData selectItem) {
        if (!(selectItem instanceof MakeupEffect)) {
            return;
        }

        MakeupEffect item = (MakeupEffect) selectItem;
        mCurrentEffectName = item.getName();
        MakeupEffectContent makeupEffectContent = (MakeupEffectContent) item.getEffectContent();
        MakeupArgs makeupArgs = makeupEffectContent.getMakeupArgs().get(0);

        float intensity = 0f;
        if (!TextUtils.isEmpty(mCurrentEffectId)) {
            NvsMakeupEffectInfo.MakeupEffect makeupEffect = MakeupData.getInstacne().getMakeupEffect(mCurrentEffectId);
            if (makeupEffect != null) {
                intensity = makeupEffect.intensity;
            } else {
                if (makeupEffectContent.getMakeupEffectInfo() != null) {
                    makeupEffectContent.clearMakeupEffectInfo();
                }
                intensity = makeupArgs.getIntensity();
                String color = makeupArgs.getColor();
                if (!TextUtils.isEmpty(color)) {
                    String[] split = color.split(",");
                    if (split.length == 4) {
                        int red = (int) Math.floor(Float.parseFloat(split[0]) * 255 + 0.5D);
                        int green = (int) Math.floor(Float.parseFloat(split[1]) * 255 + 0.5D);
                        int blue = (int) Math.floor(Float.parseFloat(split[2]) * 255 + 0.5D);
                        int alpha = (int) Math.floor(Float.parseFloat(split[3]) * 255 + 0.5D);
                        int argb = Color.argb(alpha, red, green, blue);
                        mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(argb).toUpperCase()));
                    }
                }

                MakeupData.getInstacne().addMakeupArgs(makeupEffectContent);
                MakeupData.getInstacne().removeSelectColor(item.getName());
            }
        }

        mMakeupSeekBar.setProgress((int) (intensity * 100));
        String progress = String.format(getResources().getString(R.string.make_up_transparency), (int) (intensity * 100)) + "%";
        mTvAlpha.setText(progress);
        //  Log.d("lhz","onSubViewSelect intensity is "+intensity);
        List<MakeupArgs.RecommendColor> recommendColors = makeupArgs.getMakeupRecommendColors();
        if (recommendColors != null && !recommendColors.isEmpty()) {
            int[] colors = new int[recommendColors.size()];
            for (int index = 0; index < recommendColors.size(); index++) {
                String[] split = recommendColors.get(index).getMakeupColor().split(",");
                if (split.length == 4) {
                    int red = (int) Math.floor(Float.parseFloat(split[0]) * 255 + 0.5D);
                    int green = (int) Math.floor(Float.parseFloat(split[1]) * 255 + 0.5D);
                    int blue = (int) Math.floor(Float.parseFloat(split[2]) * 255 + 0.5D);
                    int alpha = (int) Math.floor(Float.parseFloat(split[3]) * 255 + 0.5D);
                    int argb = Color.argb(alpha, red, green, blue);
                    colors[index] = argb;
                }
            }
            // get has selected color
            MakeupData.ColorData colorData = MakeupData.getInstacne().getColorByEffectId(item.getName());
            mColorPickerView.setDefaultColor(colors, colorData);
            if (colorData == null) {
                MakeupData.getInstacne().addSelectColor(item.getName(), new MakeupData.ColorData());
            }
            setColorPickerVisibility(View.VISIBLE);
        }
    }

    public BeautyData getSelectItem() {
        if (mMakeupAdapter != null) {
            return mMakeupAdapter.getSelectItem();
        }
        return null;
    }

    public void setColorPickerVisibility(int visibility) {
        mColorPickerView.setVisibility(visibility);
        mMakeupColorHinLayout.setVisibility(visibility);
        mMakeupSeekBar.setVisibility(visibility);
        // mTvColor,mTvAlpha.setVisibility(visibility);
    }

    public void setOnMakeUpEventListener(MakeUpEventListener makeUpEventListener) {
        this.mMakeUpEventListener = makeUpEventListener;
    }

    public interface MakeUpEventListener {
        void onMakeupViewComposeDataChanged(int position, boolean isClearMakeup);

        void onMakeupViewDataChanged();

        void removeVideoFxByName(String name);

        void onMakeUpViewDismiss();
    }
}
