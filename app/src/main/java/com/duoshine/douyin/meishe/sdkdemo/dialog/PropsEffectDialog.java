package com.duoshine.douyin.meishe.sdkdemo.dialog;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.Props;
import com.duoshine.douyin.meishe.sdkdemo.utils.SystemUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * author : lhz
 * date   : 2020/7/16
 * desc   :道具dialog
 */
public class PropsEffectDialog {
    private AlertDialog mAlertDialog;
    private ImageView mIvLoadMore;
    private ImageView mIvClearProps;
    private TabLayout mTlPropsTab;
    private RecyclerView mRvPropsList;
    private PropsAdapter mPropsAdapter;
    private PropsEventListener mEventListener;
    private int[] TAB_TEXT = new int[]{R.string.props_2d, R.string.props_3d, R.string.props_forword,
            R.string.props_back, R.string.props_eye, R.string.props_mouth, R.string.props_head,
            R.string.props_hand,
    };
    //  private List<FilterItem> mPropsData;

    private PropsEffectDialog() {
    }

    public static PropsEffectDialog create(Context context) {
        PropsEffectDialog propsEffectDialog = new PropsEffectDialog();
        propsEffectDialog.init(context);
        return propsEffectDialog;
    }

    private void init(Context context) {
        mAlertDialog = new AlertDialog.Builder(context).create();
        @SuppressLint("InflateParams")
        View propsView = LayoutInflater.from(context).inflate(R.layout.effects_prop_view, null);
        mAlertDialog.setView(propsView);
        mIvLoadMore = propsView.findViewById(R.id.iv_load_more);
        mIvClearProps = propsView.findViewById(R.id.iv_clear);
        mTlPropsTab = propsView.findViewById(R.id.tl_layout);
        mRvPropsList = propsView.findViewById(R.id.rv_props_list);

        mPropsAdapter = new PropsAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRvPropsList.setLayoutManager(linearLayoutManager);
        mRvPropsList.setAdapter(mPropsAdapter);

        initListener();
    }

    private void initListener() {
        mIvLoadMore.setOnClickListener(mOnClick);
        mIvClearProps.setOnClickListener(mOnClick);
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mEventListener != null){
                    mEventListener.onDismiss();
                }
            }
        });
        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mEventListener != null) {
                    mEventListener.onDialogCancel();
                }
            }
        });
        mTlPropsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //  Log.d("lhz", "onTabSelected ,tag=" + tab.getTag());
                if (tab.getTag() != null) {
                    setSelectTabData((Integer) tab.getTag());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Log.d("lhz", "onTabReselected ,tag=" + tab.getTag());
            }
        });
        mPropsAdapter.setOnItemClickListener(new PropsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Props item, int position) {
                if (mEventListener != null) {
                    mEventListener.onPropsSelected(item);
                }
            }
        });
    }

    private List<Props> propList;

    /**
     * 初始化道具类型tab
     */
    private void initTab(List<Props> propData) {
       // List<Integer> tabTypeList = new ArrayList<>(TAB_TEXT.length);
        int lastTabSelectedPos = mTlPropsTab.getSelectedTabPosition();
        TabLayout.Tab tab = mTlPropsTab.newTab().setText(R.string.props_all);
        // TabLayout.Tab tab = mTlPropsTab.newTab().setCustomView(getTextView(R.string.props_all));
        tab.setTag(-1);
        mTlPropsTab.removeAllTabs();
        mTlPropsTab.addTab(tab);
        boolean hasTab ;
        for (int i = 0; i < TAB_TEXT.length; i++) {
            hasTab = false;
            for (Props props:propData) {
                //如果所给的数据中有对应的类型
                if(i == props.getCategoryId() -1){
                    hasTab = true;
                    break;
                }
            }
            if(hasTab){
                tab = mTlPropsTab.newTab().setText(TAB_TEXT[i]);
                tab.setTag(i);
                mTlPropsTab.addTab(tab);
            }
        }
        //tab = null;
        if (lastTabSelectedPos > 0 && lastTabSelectedPos < mTlPropsTab.getTabCount()) {
            tab = mTlPropsTab.getTabAt(lastTabSelectedPos);
        } else {
            tab = mTlPropsTab.getTabAt(0);
        }
        //  Log.d("lhz", "tab=" + tab + "**lastTabSelectedPos=" + lastTabSelectedPos);
        if (tab != null) {
            tab.select();
        }
    }

    private TextView getTextView(int resId) {
        TextView textView = new TextView(getDialog().getContext());
        textView.setText(resId);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    /**
     * 根据不同的道具类型，显示不同的列表
     */
    private void setSelectTabData(int type) {
        if (propList != null) {
            if (type == -1) {

            } else {

            }
            Props props;
            List<Props> items;
            if (type == -1) {
                items = propList;
            } else {
                items = new ArrayList<>();
                for (int i = 0; i < propList.size(); i++) {
                    props = propList.get(i);
                    if (props != null) {
                        if (type == props.getCategoryId() - 1) {
                            items.add(props);
                        }
                    }
                }
            }
            int selectPos = -1;
            String selectedUid = mPropsAdapter.getSelectUid();
            if (!TextUtils.isEmpty(selectedUid)) {
                for (int i = 0; i < items.size(); i++) {
                    props = items.get(i);
                    if (selectedUid.equals(props.getUuid())) {
                        selectPos = i;
                    }
                }
            }
            mPropsAdapter.setFilterDataList(items);
            mPropsAdapter.notifyDataSetChanged();
            mPropsAdapter.setSelectPos(selectPos);
        }
    }

    /**
     * 设置事件监听
     *
     * @param eventListener PropsEventListener
     */
    public void setPropsEventListener(PropsEventListener eventListener) {
        this.mEventListener = eventListener;
    }

    /**
     * 設置道具数据
     */
    public void setPropsData(List<Props> propData) {
        if (propData != null && mPropsAdapter != null) {
            this.propList = propData;
            initTab(propData);
            // mPropsAdapter.setFilterDataList(propData);
        }
    }

/*
    public void selectPosition(int pos) {
        mPropsAdapter.setSelectPos(pos);
    }*/

    public AlertDialog getDialog() {
        return mAlertDialog;
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_load_more:
                    if (mEventListener != null) {
                        mEventListener.onLoadMore();
                    }
                    break;
                case R.id.iv_clear:
                    mPropsAdapter.setSelectPos(-1);
                    if (mEventListener != null) {
                        mEventListener.onPropsSelected(null);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public interface PropsEventListener {
        void onDismiss();
        void onDialogCancel();

        void onLoadMore();

        void onPropsSelected(Props item);
    }

    static class PropsAdapter extends RecyclerView.Adapter<PropsAdapter.Holder> {

        private Context mContext;
        private OnItemClickListener mClickListener;
        private List<Props> mDataList = new ArrayList<>();
        RequestOptions mOptions = new RequestOptions();
        private int mSelectPos = 0;


        public interface OnItemClickListener {
            void onItemClick(View view, Props item, int position);
        }

        public PropsAdapter(Context context) {
            mContext = context;
            //mOptions.centerCrop();
            mOptions.skipMemoryCache(false);
            mOptions.placeholder(R.mipmap.default_filter);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mClickListener = listener;
        }

        public void setFilterDataList(List<Props> filterDataList) {
            this.mDataList = filterDataList;
        }


        public void setSelectPos(int pos) {
            if (pos >= mDataList.size()) {
                Log.e("PropsEffectDialog", "select pos is error");
                return;
            }
            notifyItemChanged(mSelectPos);
            this.mSelectPos = pos;
            if (pos > 0) {
                notifyItemChanged(mSelectPos);
            }
        }

        public String getSelectUid() {
            if (mDataList != null && mSelectPos >= 0 && mSelectPos < mDataList.size()) {
                return mDataList.get(mSelectPos).getUuid();
            }
            return null;
        }

        public Props getSelectItem() {
            if (mDataList != null && mSelectPos >= 0 && mSelectPos < mDataList.size()) {
                return mDataList.get(mSelectPos);
            }
            return null;
        }

        class Holder extends RecyclerView.ViewHolder {
            private ImageView ivIcon;
            private TextView tvName;
            private View vSelectBg;

            private Holder(View view) {
                super(view);
                tvName = view.findViewById(R.id.tv_name);
                ivIcon = view.findViewById(R.id.iv_icon);
                vSelectBg = view.findViewById(R.id.v_background);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSelectPos == getAdapterPosition()) {
                            return;
                        }
                        notifyItemChanged(mSelectPos);
                        mSelectPos = getAdapterPosition();
                        notifyItemChanged(mSelectPos);
                        if (mClickListener != null) {
                            mClickListener.onItemClick(view, getSelectItem(), getAdapterPosition());
                        }
                    }
                });
            }
        }


        public List<Props> getFilterDataList() {
            return mDataList;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_props, viewGroup, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, final int position) {

            Props itemData = mDataList.get(position);
            if (itemData == null)
                return;
            String name ;
            if(SystemUtils.isZh(mContext)){
                name = itemData.getZh_name();
            }else{
                name = itemData.getName();
            }
            if (!TextUtils.isEmpty(name)) {
                holder.tvName.setText(name);
            } else {
                holder.tvName.setText(mContext.getString(R.string.faceU) + (position + 1));
            }
            /*int filterMode = itemData.getFilterMode();
            if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                int imageId = itemData.getImageId();
                if (imageId != 0)
                    holder.ivIcon.setImageResource(imageId);
            } else {*/
                String imageUrl = itemData.getCover();
                if (imageUrl != null) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(imageUrl)
                            .apply(mOptions)
                            .into(holder.ivIcon);
                }
         //   }
            if (mSelectPos == position) {
                holder.vSelectBg.setBackgroundResource(R.drawable.blue_ring);
                holder.tvName.setTextColor(mContext.getResources().getColor(R.color.blue_63));
            } else {
                holder.vSelectBg.setBackgroundResource(0);
                holder.tvName.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

    }
}
