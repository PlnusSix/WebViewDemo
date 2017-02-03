package com.kankan.androidchatwithh5demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by huangmingming on 2016/12/15.
 * 通过RxJava实现的消息总线，作为组件以及线程间通信的一种补充手段
 * 注：订阅者需自行在onNext()方法中捕获异常，如果订阅者onNext()中的发生异常没有捕获会导致onError()方法被回调，订阅关系终止，后续消息会接收不到
 */

public class RxBus {

    private static volatile RxBus mRxBus;
    private Subject<RxBusEvent, RxBusEvent> mBus;
    private Map<RxBusEvent.EventType, RxBusEvent> mStickyEventMap;
    private CompositeSubscription mSubscriptions;


    private RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.<RxBusEvent>create());
        mStickyEventMap = new ConcurrentHashMap<>();
        mSubscriptions = new CompositeSubscription();
    }


    /**
     * 获取消息总线
     *
     * @return
     */
    public static RxBus get() {
        if (mRxBus == null) {
            synchronized (RxBus.class) {
                if (mRxBus == null) {
                    mRxBus = new RxBus();
                }
            }
        }
        return mRxBus;
    }


    /**
     * 传递一个普通消息
     *
     * @param event
     */
    public void post(RxBusEvent event) {
        if (event != null && !event.isNull()) {
            mBus.onNext(event);
        }
    }

    /**
     * 传递一个粘性消息，订阅者订阅粘性消息后会首先接收到最近已发出的一个消息（如果有的话）
     *
     * @param event
     */
    public void postSticky(RxBusEvent event) {
        if (event != null && !event.isNull()) {
            synchronized (mStickyEventMap) {
                mStickyEventMap.put(event.mEventType, event);
                mStickyEventMap.put(RxBusEvent.EventType.DEFAULT, event);
            }
            mBus.onNext(event);
        }
    }

    /**
     * 转换为被订阅者
     *
     * @return
     */
    public Observable<RxBusEvent> toObservable() {
        return mBus;
    }

    /**
     * 转换为相应类型的被订阅者，用于订阅相应类型的消息
     *
     * @param type 时间类型
     * @return
     */
    public Observable<RxBusEvent> toObservable(final RxBusEvent.EventType type) {
        return mBus.filter(new Func1<RxBusEvent, Boolean>() {
            @Override
            public Boolean call(RxBusEvent event) {
                return type == event.mEventType;
            }
        });
    }


    /**
     * 转换为粘性被订阅者，用于订阅相粘性消息
     *
     * @return
     */
    public Observable<RxBusEvent> toObservableSticky() {
        return toObservableSticky(RxBusEvent.EventType.DEFAULT);
    }


    /**
     * 转换为相应类型的粘性订阅者
     *
     * @param type 事件类型
     * @return
     */
    public Observable<RxBusEvent> toObservableSticky(RxBusEvent.EventType type) {
        RxBusEvent event;
        synchronized (mStickyEventMap) {
            event = mStickyEventMap.get(type);
        }
        Observable<RxBusEvent> observable = toObservable(type);
        if (event != null) {
            observable = observable.mergeWith(Observable.just(event));
        }
        return observable;
    }

    /**
     * 是否有订阅者
     *
     * @return
     */
    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    /**
     * 根据事件类型获取粘性时间
     *
     * @param type
     * @return
     */
    public RxBusEvent getRxBusEvent(RxBusEvent.EventType type) {
        if (type == null) {
            return null;
        }
        synchronized (mStickyEventMap) {
            return mStickyEventMap.get(type);
        }
    }

    /**
     * 根据事件类型移除粘性事件
     *
     * @param type
     * @return
     */
    public RxBusEvent removeRxBusEvent(RxBusEvent.EventType type) {
        if (type == null) {
            return null;
        }
        synchronized (mStickyEventMap) {
            return mStickyEventMap.remove(type);
        }
    }

    /**
     * 清除所有粘性事件
     */
    public void clearRxBusEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

    /**
     * 添加订阅关系，用于后续统一解除订阅
     *
     * @param subscriptions
     */
    public void addSubscriptions(Subscription... subscriptions) {
        mSubscriptions.addAll(subscriptions);
    }

    /**
     * 清除所有订阅关系
     */
    public void clearSubscriptions() {
        if (mSubscriptions.hasSubscriptions()) {
            mSubscriptions.clear();
        }
    }

    /**
     * 释放资源，在应用退出前需要进行释放
     */
    public void release() {
        if (mStickyEventMap != null) {
            clearRxBusEvents();
            mStickyEventMap = null;
        }
        if (mSubscriptions != null) {
            clearSubscriptions();
            if (!mSubscriptions.isUnsubscribed()) {
                mSubscriptions.unsubscribe();
            }
            mSubscriptions = null;
        }
        mBus = null;
        mRxBus = null;
    }
}
