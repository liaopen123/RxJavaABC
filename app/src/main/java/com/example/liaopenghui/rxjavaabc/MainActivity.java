package com.example.liaopenghui.rxjavaabc;

import android.database.Observable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.Observer;
import java.util.logging.Logger;

import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //    howToUseABC();
        //getName();
        //setDrawable();
    }

    private void setDrawable() {
        /******************************************************************实战演练:显示图片******************************************/
        final int drawableRes = R.mipmap.ic_launcher;
        final ImageView iv_src = (ImageView) findViewById(R.id.iv_src);
        rx.Observable.create(new rx.Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(drawableRes*2);
                subscriber.onNext(drawable);
            }
        }).subscribe(new rx.Observer<Drawable>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,"出错了");
                e.printStackTrace();
            }

            @Override
            public void onNext(Drawable drawable) {
                iv_src.setImageDrawable(drawable);
            }
        });
    }

    private void getName() {
        /******************************************************************实战演练:打印字符串数组******************************************/
        String[] names = {"zyy","小公主","廖鹏辉","jay"};
        rx.Observable.from(names).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG,"得到的名字为:"+s);
            }
        });
    }


    private void howToUseABC() {
        /******************************************************************第一步******************************************/
        rx.Observer<String> observer = new rx.Observer<String>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "Completed!observer");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error!observer");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,"ITEMobserver:"+s);
            }
        };

        //除了Observer接口之外,RxJava还内置了一个实现Observer的类:Subscriber
        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "Completed!subscriber");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error!subscriber");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,"ITEM:"+s);
            }
        };
        /******************************************************************第二步******************************************/
        rx.Observable<String> observable = rx.Observable.create(new rx.Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello");
                subscriber.onNext("hi");
                subscriber.onNext("aloha");
                subscriber.onCompleted();
            }
        });


        rx.Observable<String> observable1 = rx.Observable.just("hello1", "hi1", "aloha");
        String[] arrys = {"hello2","hi2","aloha"};
        rx.Observable<String> observable2 = rx.Observable.from(arrys);


        /******************************************************************第三步******************************************/
        observable.subscribe(subscriber);
        observable.subscribe(observer);
        observable1.subscribe(subscriber);
        observable1.subscribe(observer);
        observable2.subscribe(subscriber);
        observable2.subscribe(observer);


        /******************************************************************算是结束了******************************************/


        //除了 subscribe(Observer) 和 subscribe(Subscriber) ，subscribe() 还支持不完整定义的回调，RxJava 会自动根据定义创建出 Subscriber 。形式如下：

        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        };

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
               //错误回调
            }
        };

        Action0 onCompletedAction = new Action0() {
            @Override
            public void call() {
                Log.e(TAG, "completed");
            }

        };


        observable.subscribe(onNextAction);
        observable.subscribe(onNextAction,onErrorAction);
        observable.subscribe(onNextAction,onErrorAction,onCompletedAction);
    }
}
