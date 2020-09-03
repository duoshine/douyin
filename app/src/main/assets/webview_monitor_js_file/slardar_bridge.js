//新修订的版本,增加对象和方法的判断
//去除path、host参数
var slardarBridge = {};

var slardarOutBridge = {};

slardarBridge.checkUsedFull = function (value) {
    return typeof value !== "undefined" && value !== null && value !== "";
};

slardarBridge.isFunction = function (what) {
    return typeof what === 'function';
};

slardarBridge.appendCommonParams = function (dataObj) {
    dataObj.url = location.href;
};

slardarBridge.cover = function (dataObj, serviceStr) {
    if (!slardarBridge.checkUsedFull(dataObj)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.cover)) {
        return
    }
    slardarBridge.appendCommonParams(dataObj);
    dataObj.service = serviceStr;
    var dataJson = JSON.stringify(dataObj);
    window.iesJsBridgeTransferMonitor.cover(dataJson, serviceStr);
};

slardarBridge.accumulate = function (dataObj, serviceStr) {
    if (!slardarBridge.checkUsedFull(dataObj)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.accumulate)) {
        return
    }
    slardarBridge.appendCommonParams(dataObj);
    dataObj.service = serviceStr;
    var dataJson = JSON.stringify(dataObj);
    window.iesJsBridgeTransferMonitor.accumulate(dataJson, serviceStr);
};

slardarBridge.reportDirectly = function (dataObj, serviceStr) {
    if (!slardarBridge.checkUsedFull(dataObj)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.reportDirectly)) {
        return
    }
    slardarBridge.appendCommonParams(dataObj);
    dataObj.service = serviceStr;
    var dataJson = JSON.stringify(dataObj);
    window.iesJsBridgeTransferMonitor.reportDirectly(dataJson, serviceStr);
};

slardarBridge.average = function (dataObj, serviceStr) {
    if (!slardarBridge.checkUsedFull(dataObj)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.average)) {
        return
    }

    slardarBridge.appendCommonParams(dataObj);
    dataObj.service = serviceStr;
    var dataJson = JSON.stringify(dataObj);
    window.iesJsBridgeTransferMonitor.average(dataJson, serviceStr);
};

slardarBridge.diff = function (dataObj, serviceStr) {
    if (!slardarBridge.checkUsedFull(dataObj)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.diff)) {
        return
    }
    dataObj.service = serviceStr;
    var dataJson = JSON.stringify(dataObj);
    window.iesJsBridgeTransferMonitor.diff(dataJson, serviceStr);
};

slardarOutBridge.customReport = function (event_name,msg) {

    if (!slardarBridge.checkUsedFull(msg)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.customReport)) {
        return
    }

    if (!slardarBridge.checkUsedFull(msg)) {
        return;
    }
    var metricJson = JSON.stringify(msg.metric);
    if (!slardarBridge.checkUsedFull(metricJson)) {
        metricJson = "";
    }

    var categoryJson = JSON.stringify(msg.category);
    if (!slardarBridge.checkUsedFull(categoryJson)) {
        categoryJson = "";
    }

    var extraJson = JSON.stringify(msg.extra);
    if (!slardarBridge.checkUsedFull(extraJson)) {
        extraData={}
        extraData['event_name']=event_name
        extraJson = JSON.stringify(extraData);
    }else{
         extraData=msg.extra
         extraData['event_name']=event_name
         extraJson = JSON.stringify(extraData);
    }

    var typeJson = msg.type+"";
    if (!slardarBridge.checkUsedFull(typeJson)) {
        typeJson = "0";//default
    }
    window.iesJsBridgeTransferMonitor.customReport(metricJson, categoryJson, extraJson, typeJson);
};

slardarBridge.sendInitTimeInfo = function (dataJson) {
    if (!slardarBridge.checkUsedFull(dataJson)) {
        return
    }
    if (!slardarBridge.checkUsedFull(window.iesJsBridgeTransferMonitor)) {
        return
    }
    if (!slardarBridge.isFunction(window.iesJsBridgeTransferMonitor.sendInitTimeInfo)) {
        return
    }
    window.iesJsBridgeTransferMonitor.sendInitTimeInfo(dataJson);
};

window.jsIESLiveTimingMonitor = slardarBridge;
window.jsIESWebViewMonitor = slardarOutBridge;

//add 回调SlardarHybrid的初始化方法
window._initSlardarHybrid instanceof Function && window._initSlardarHybrid();

//add listener to notify init time
window.addEventListener("DOMContentLoaded", function () {
    window.setTimeout(function () {
        if (window.history.length <= 1) {
            var time = new Date().getTime() - performance.timing.navigationStart;
            time = time > 0 ? parseInt(time) : 0;
            slardarBridge.sendInitTimeInfo(time + "");
        }
    }, 0);
});