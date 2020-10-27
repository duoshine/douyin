package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.AssetItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.*;


public class CaptionAnimationFragment extends Fragment {
    private int[] ANIMATION = new int[]{R.string.march_in_animation, R.string.march_out_animation, R.string.combination_animation};
    private RecyclerView mRvAnimationList;
    private AnimationAdapter mAnimationAdapter;
    private List<AssetItem> mCombinationList;
    private List<AssetItem> mInList;
    private List<AssetItem> mOutList;
    private TabLayout mTabAnimation;
    private ImageView mIvLoadMore;
    private TextView mTvLoadMore;
    private int mSelectedAniPos;
    private int mSelectedInAniPos;
    private int mSelectedOutAniPos;
    private OnCaptionStateListener mCaptionStateListener;

    public interface OnCaptionStateListener {
        void onFragmentLoadFinished();

        void onLoadMore(int type);

        void onItemClick(int pos, int type);
    }

    public void setCaptionStateListener(OnCaptionStateListener stateListener) {
        this.mCaptionStateListener = stateListener;
    }

    /**
     * 检测是动画的tab按照要求显示,仅仅第一次初始化会使用
     * */
    public void checkSelectedTab() {
        int selectedTabPosition = mTabAnimation.getSelectedTabPosition();
        if (mSelectedAniPos > 0 && selectedTabPosition != 2) {
            //如果设置了组合动画，但是组合动画的tab没有被选中，则选中
            TabLayout.Tab animationTab = mTabAnimation.getTabAt(2);
            if (animationTab != null) {
                animationTab.select();
            }
        } else if (mSelectedOutAniPos > 0 && mSelectedInAniPos <= 0 && selectedTabPosition != 1) {
            //如果仅仅设置了出动画动画，但是出动画的tab没有被选中，则选中
            TabLayout.Tab animationTab = mTabAnimation.getTabAt(1);
            if (animationTab != null) {
                animationTab.select();
            }
        }
    }

    /**
     * 设置选中item
     */
    public void setSelectedPos(int aniPos, int inAniPos, int outAniPos) {
        mSelectedAniPos = aniPos;
        mSelectedInAniPos = inAniPos;
        mSelectedOutAniPos = outAniPos;
        if (mAnimationAdapter != null && mTabAnimation != null) {
            if (mTabAnimation.getSelectedTabPosition() == 0) {
                mAnimationAdapter.setSelectedPos(mSelectedInAniPos);
            } else if (mTabAnimation.getSelectedTabPosition() == 1) {
                mAnimationAdapter.setSelectedPos(mSelectedOutAniPos);
            } else {
                mAnimationAdapter.setSelectedPos(mSelectedAniPos);
            }

        }

    }

    public void setAssetList(List<AssetItem> combination, List<AssetItem> in, List<AssetItem> out) {
        mCombinationList = combination;
        mInList = in;
        mOutList = out;
        if (mTabAnimation != null) {
            displayTabAnimation(mTabAnimation.getSelectedTabPosition());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.caption_animation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initListener();
        if (mCaptionStateListener != null) {
            mCaptionStateListener.onFragmentLoadFinished();
        }
    }

    private void initView(View view) {
        mTabAnimation = view.findViewById(R.id.tab_animation);
        mIvLoadMore = view.findViewById(R.id.iv_load_more);
        mTvLoadMore = view.findViewById(R.id.tv_load_more);
        mRvAnimationList = view.findViewById(R.id.rv_list);
    }

    private void initListener() {

        mIvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionStateListener != null) {
                    mCaptionStateListener.onLoadMore(getType(mTabAnimation.getSelectedTabPosition()));
                }
            }
        });
        mTvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionStateListener != null) {
                    mCaptionStateListener.onLoadMore(getType(mTabAnimation.getSelectedTabPosition()));
                }
            }
        });
        mTabAnimation.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                displayTabAnimation(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mAnimationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mCaptionStateListener != null) {
                    mAnimationAdapter.setSelectedPos(pos);
                    int type;
                    int typePos = mTabAnimation.getSelectedTabPosition();
                    if (typePos == 0) {
                        type = ASSET_CAPTION_IN_ANIMATION;
                        mSelectedInAniPos = pos;
                        mSelectedAniPos = 0;
                    } else if (typePos == 1) {
                        type = ASSET_CAPTION_OUT_ANIMATION;
                        mSelectedOutAniPos = pos;
                        mSelectedAniPos = 0;
                    } else {
                        type = ASSET_CAPTION_ANIMATION;
                        mSelectedAniPos = pos;
                        mSelectedOutAniPos = 0;
                        mSelectedInAniPos = 0;
                    }
                    mCaptionStateListener.onItemClick(pos, type);
                }
            }
        });
    }

    private void initData() {
        for (int value : ANIMATION) {
            mTabAnimation.addTab(mTabAnimation.newTab().setText(value));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvAnimationList.setLayoutManager(layoutManager);
        mAnimationAdapter = new AnimationAdapter(getActivity());
        mAnimationAdapter.setAssetList(mInList);
        mRvAnimationList.setAdapter(mAnimationAdapter);
        mRvAnimationList.addItemDecoration(new SpaceItemDecoration(0, 15));

    }

    /***
     * 展示动画Tab对应的内容
     */
    private void displayTabAnimation(int pos) {
        if (mAnimationAdapter == null) {
            return;
        }
        if (pos == 0) {
            mAnimationAdapter.setAssetList(mInList);
            mAnimationAdapter.setSelectedPos(mSelectedInAniPos);
        } else if (pos == 1) {
            mAnimationAdapter.setAssetList(mOutList);
            mAnimationAdapter.setSelectedPos(mSelectedOutAniPos);
        } else {
            mAnimationAdapter.setAssetList(mCombinationList);
            mAnimationAdapter.setSelectedPos(mSelectedAniPos);
        }
        mAnimationAdapter.notifyDataSetChanged();
    }

    /**
     * 获取资源类型
     */
    private int getType(int pos) {
        int type;
        if (pos == 0) {
            type = ASSET_CAPTION_IN_ANIMATION;
        } else if (pos == 1) {
            type = ASSET_CAPTION_OUT_ANIMATION;
        } else {
            type = ASSET_CAPTION_ANIMATION;
        }
        return type;
    }

    static class AnimationAdapter extends RecyclerView.Adapter<AnimationAdapter.ViewHolder> {
        private List<AssetItem> mAssetList = new ArrayList<>();
        private Context mContext;
        private OnItemClickListener mOnItemClickListener = null;
        private int mSelectedPos = 0;

        AnimationAdapter(Context context) {
            mContext = context;
        }

        void setAssetList(List<AssetItem> assetArrayList) {
            this.mAssetList = assetArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_animation_caption, parent, false);
            return new ViewHolder(v);
        }

        public void setSelectedPos(int selectedPos) {
            if (selectedPos == mSelectedPos) {
                return;
            }
            if (selectedPos >= 0 && mAssetList != null && selectedPos < mAssetList.size()) {
                notifyItemChanged(mSelectedPos);
                this.mSelectedPos = selectedPos;
                notifyItemChanged(mSelectedPos);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            AssetItem assetItem = mAssetList.get(position);
            if (assetItem == null)
                return;
            NvAsset asset = assetItem.getAsset();
            if (asset == null)
                return;
            if (assetItem.getAssetMode() == AssetItem.ASSET_NONE) {
                holder.mIvCover.setImageResource(assetItem.getImageRes());
            } else {
                loadWebpImage(holder.mIvCover, asset.coverUrl);
            }

            holder.mTvName.setText(asset.name);
            holder.vSelected.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);
            holder.mTvName.setTextColor(mSelectedPos == position ? mContext.getResources().getColor(R.color.red_ff64)
                    : mContext.getResources().getColor(R.color.white));
        }

        public void loadWebpImage(SimpleDraweeView view, String imageUrl) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(imageUrl)
                    .setAutoPlayAnimations(true)
                    .setOldController(view.getController())
                    .build();
            view.setController(controller);
        }

        @Override
        public int getItemCount() {
            return mAssetList == null ? 0 : mAssetList.size();
        }

        public void setOnItemClickListener(OnItemClickListener itemClickListener) {
            this.mOnItemClickListener = itemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView mIvCover;
            View vSelected;
            TextView mTvName;

            ViewHolder(View itemView) {
                super(itemView);
                mIvCover = itemView.findViewById(R.id.iv_cover);
                vSelected = itemView.findViewById(R.id.v_select);
                mTvName = itemView.findViewById(R.id.tv_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                });
            }
        }
    }
}
