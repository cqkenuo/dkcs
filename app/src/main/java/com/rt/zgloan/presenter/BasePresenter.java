package com.rt.zgloan.presenter;


import com.rt.zgloan.base.BaseView;
import com.rt.zgloan.bean.BaseResponse;
import com.rt.zgloan.http.ActivityLifeCycleEvent;
import com.rt.zgloan.http.HttpSubscriber;
import com.rt.zgloan.http.RxHelper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * des:基类presenter
 */
public class BasePresenter {
    private static BasePresenter basePresenter;
    public BaseView mView;
    //销毁时退出异步任务
    protected CompositeSubscription mSubscriptions;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    public void init(BaseView v) {
        this.mView = v;
        this.onStart();
    }

    public void onStart() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
    }

    ;

    public void onDestroy() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
        if (mSubscriptions != null && mSubscriptions.hasSubscriptions()) {
            mSubscriptions.unsubscribe();
        }
    }

    /**
     * 添加线程管理并订阅
     *
     * @param ob
     * @param subscriber
     */
    public void toSubscribe(Observable ob, HttpSubscriber subscriber) {
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }
        //数据预处理
        Observable.Transformer<BaseResponse<Object>, Object> result = RxHelper.handleResult(ActivityLifeCycleEvent.DESTROY, lifecycleSubject);
        Subscription subscription = ob.compose(result).subscribe(subscriber);
        mSubscriptions.add(subscription);
    }

    /**
     * 上传图片封装
     *
     * @param key
     * @param fileName
     * @return
     */
    public MultipartBody.Part putFile(String key, String fileName) {
        //构建要上传的文件
        File file = new File(fileName);
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(key, file.getName(), requestFile);
        return body;
    }

    public static BasePresenter getInstence() {
        if (basePresenter == null) {
            basePresenter = new BasePresenter();
        }
        return basePresenter;
    }
}
