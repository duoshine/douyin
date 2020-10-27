package com.duoshine.douyin.meishe.sdkdemo.bean;

import java.util.List;

public class RecordInfo {
    private List<JsonInfo> jsonList;

    public class JsonInfo {
        public String jsonPath;
    }

    public List<JsonInfo> getJsonList() {
        return jsonList;
    }

    public void setJsonList(List<JsonInfo> jsonList) {
        this.jsonList = jsonList;
    }
}
