package com.kankan.androidchatwithh5demo;

/**
 * Created by huangmingming on 2016/12/15.
 * RxBus传递的消息，必须通过此类来包装
 */

public class RxBusEvent {
    
    //事件类型，每添加一种消息，需要在此处添加一种类型
    public enum EventType {
        DEFAULT, MESSAGE
    }

    //事件类型
    public EventType mEventType;
    //事件内容
    public Object mEventContent;

    public RxBusEvent() {
    }

    public RxBusEvent(EventType mEventType, Object mEventContent) {
        this.mEventType = mEventType;
        this.mEventContent = mEventContent;
    }

    public boolean isNull() {
        return (mEventType == null || mEventContent == null);
    }


}
