package com.duoshine.douyin.meishe.sdkdemo.edit.filter;

import android.content.Intent;
import android.view.View;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meicam.sdk.NvsTimeline;

import java.util.ArrayList;

/**
 * @author yyj
 * @date 2018/5/30 0030
 */

public class FilterActivity extends BaseFilterActivity {

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.filterAssetFinish) {
            // save data
            TimelineData.instance().setVideoClipFxData(mVideoClipFxInfo);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            quitActivity();
        }
    }

    @Override
    protected VideoClipFxInfo initClipFxInfo() {
        VideoClipFxInfo videoClipFxData = TimelineData.instance().getVideoClipFxData();
        if (videoClipFxData == null) {
            videoClipFxData = new VideoClipFxInfo();
        }
        return videoClipFxData;
    }

    @Override
    protected NvsTimeline initTimeLine() {
        NvsTimeline timeline = TimelineUtil.createTimeline();
        if (timeline == null) {
            return null;
        }

        TimelineUtil.applyTheme(timeline, null);
        /*
         * 移除主题，则需要删除字幕，然后重新添加，防止带片头主题删掉字幕
         * To remove a topic, you need to delete the subtitle and then add it again to prevent the title from deleting the subtitle
         * */
        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        TimelineUtil.setCaption(timeline, captionArray);
        return timeline;
    }

    @Override
    protected void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter) {
        TimelineUtil.buildTimelineFilter(timeline, changedClipFilter);
    }
}
