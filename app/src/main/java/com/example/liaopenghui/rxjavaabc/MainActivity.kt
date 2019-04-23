package com.example.liaopenghui.rxjavaabc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), AnkoLogger {
    //mCompositeDisposable作用：如果一个页面有多个observer需要观察  可以通用放在里面
    //onDestory的时候 执行clear(),统一清理。
    val TAG = "MainActivity"
    val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    lateinit var disposable: Disposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        baseFunc()
//        baseFunc1()
//        threadChange()
//        mapOprationSymble()
//        concatOperationSymble()
//        flatMapOprationSymble()
//        zipOprationSymble()
//        intervalOprationSymble()
//        debounceSymble()
//        takeSymble()
//        skipSymble()
//        throttleWithTimeoutSymble()
//        bufferSymble()
//        distinctSymble()
//        lastSymble()
//        reduceSymble()
        scanSymble()
    }


    private fun baseFunc() {
        Observable.create(ObservableOnSubscribe<Int> { emitter ->
            //第一步创建被观察者 Observable
            //emitter 发射器
            emitter.onNext(1)
            emitter.onNext(3)
            emitter.onNext(1)
            emitter.onNext(4)
            emitter.onNext(5)
            emitter.onNext(2)
            emitter.onNext(1)
        }).subscribe(object : Observer<Int> {
            //第二步创建观察者 Observer
            //第三步 订阅 subscribe
            override fun onComplete() {
                info { "onComplete" }
            }

            override fun onSubscribe(d: Disposable) {
                //当开始订阅的时候  提供了Disposable对象
                info { "onSubscribe" }
                disposable = d
            }

            override fun onNext(t: Int) {
                info { "onNext:$t" }
                toast("onNext:$t")
                if (t == 4) {
                    disposable.dispose()
                }
            }

            override fun onError(e: Throwable) {
            }

        })
    }

    private fun baseFunc1() {
        val subscribe = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            //第一步创建被观察者 Observable
            //emitter 发射器
            emitter.onNext(1)
            emitter.onNext(3)
            emitter.onNext(1)
            emitter.onNext(4)
            emitter.onNext(5)
            emitter.onNext(2)
            emitter.onNext(1)
        }).subscribe { t -> info { "onNext:$t" } }
        mCompositeDisposable.add(subscribe)
    }

    //线程变换
    fun threadChange() {
        val subscribe = Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("lph~~~")
            emitter.onNext("jay~~~")
            emitter.onNext("mayday~~~")
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t -> toast(t.toString()) }
        mCompositeDisposable.add(subscribe)
    }

    //map操作符
    fun mapOprationSymble() {
        var total: Int = 0
        val subscribe = Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("5")
            emitter.onNext("5")
        }).map { t -> t.toInt() }.subscribe { t ->
            total += t!!
            info { "total:$total" }
        }

        mCompositeDisposable.add(subscribe)
    }

    //concat操作符
    fun concatOperationSymble() {
//        Observable.concat(getObservableA(null), getObservableB(null), getObservableA(null), getObservableB(null))
//            .subscribe(object : Consumer<String> {
//                override fun accept(t: String?) {
//                    info{t}
//                }
//
//            })


        var disposableObserver = object : DisposableObserver<String>() {
            override fun onNext(t: String) {
                info { "disposableObserver:$t" }
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }

        }
        val subscribeWith =
            Observable.concat(getObservableA(null), getObservableB(null), getObservableA(null), getObservableB(null))
                .subscribeWith(disposableObserver)
        mCompositeDisposable.add(subscribeWith)
    }

    //concat操作符
    fun getObservableA(o: Any?): Observable<String> {
        return Observable.fromCallable {
            Thread.sleep(1000)//假设为耗时操作
            "liaopenghui"
        }
    }

    //concat操作符
    fun getObservableB(o: Any?): Observable<String> {
        return Observable.fromCallable {
            Thread.sleep(1000)//假设为耗时操作
            "jay jay jay"
        }
    }

    //flatMap 拿着上个函数的结果 作为下一个函数的参数
    fun flatMapOprationSymble() {
        val flatMapOprationSymbleSubscribe =
            Observable.create(ObservableOnSubscribe<String> { emitter -> emitter.onNext("123456789111") })
                .flatMap { t ->
                    if (t == "123456789") {
                        ObservableSource<String> { observer -> observer.onNext("密码正确") }
                    } else {
                        ObservableSource<String> { observer -> observer.onNext("密码错误") }
                    }
                }.subscribeOn(AndroidSchedulers.mainThread()).subscribe { t -> toast(t.toString()) }
        mCompositeDisposable.add(flatMapOprationSymbleSubscribe)
    }

    fun zipOprationSymble() {
        val zipSubscribe = Observable.zip(getObservableA(null), getObservableB(null),
            BiFunction<String, String, String> { t1, t2 -> t1 + t2 }).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { t -> toast(t.toString()) }
        mCompositeDisposable.add(zipSubscribe)
    }


    fun intervalOprationSymble() {
        disposable = Flowable.interval(1, TimeUnit.SECONDS).doOnNext { t -> error("accept: doOnNext : $t") }
            .subscribe { t -> error("accept: accept : $t") }
    }

    override fun onDestroy() {
        // 如果退出程序，就清除后台任务
        mCompositeDisposable.clear()
        disposable.dispose()
        super.onDestroy()
    }


    //debounce延迟操作符
    fun debounceSymble() {
        var debounceSymbleSubscribe = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            emitter.onNext(1); // skip
            Thread.sleep(400);
            emitter.onNext(2); // deliver
            Thread.sleep(505);
            emitter.onNext(3); // skip
            Thread.sleep(100);
            emitter.onNext(4); // deliver
            Thread.sleep(605);
            emitter.onNext(5); // deliver
            Thread.sleep(510);
            emitter.onComplete()
        }).debounce(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t -> toast(t.toString()) }
        mCompositeDisposable.add(debounceSymbleSubscribe)
    }

    fun takeSymble() {
        var subscribe = Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("lph~~~")
            emitter.onNext("jay~~~")
            emitter.onNext("mayday~~~")
            emitter.onNext("mayday~1~~")
            emitter.onNext("mayday2~~~")
            emitter.onNext("mayday3~~~")
            emitter.onNext("mayday~4~~")
            emitter.onComplete()
        }).take(3).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t -> Log.e(TAG, "" + t) }
        mCompositeDisposable.add(subscribe)


    }


    private fun skipSymble() {
        val subscribe =
            Observable.just(1, 2, 3, 4, 5, 6, 7).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .skip(3).subscribe {
                    Log.e(TAG, "" + it)
                }
        mCompositeDisposable.add(subscribe)
        //output   4  5  6  7
    }


    //throttleWithTimeout操作符
    fun throttleWithTimeoutSymble() {
        var subscribe = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            emitter.onNext(1); // skip
            Thread.sleep(400);
            emitter.onNext(2); // deliver
            Thread.sleep(505);
            emitter.onNext(3); // skip
            Thread.sleep(100);
            emitter.onNext(4); // deliver
            Thread.sleep(605);
            emitter.onNext(5); // deliver
            Thread.sleep(510);
            emitter.onComplete()
        }).throttleWithTimeout(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t -> Log.e(TAG, "" + t) }
        mCompositeDisposable.add(subscribe)
    }


    private fun bufferSymble() {
        val subscribe = Observable.just("one", "two", "three", "four", "five")
            .buffer(3, 1) //每三个一缓冲,每次步进1
            .subscribe {
                //返回了一个List
                Log.e(TAG, "list:$it")
                //output:
//                 list:[one, two, three]
//                 list:[three, four, five]
//                 list:[two, three, four]
//                 list:[four, five]
//                 list:[five]
            }
        mCompositeDisposable.add(subscribe)
    }


    private fun distinctSymble() { //主要用于去重
        val subscribe = Observable.just(1, 2, 1, 1, 2, 3, 4, 6, 2, 1)
            .subscribeOn(Schedulers.io())
            .distinct()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e(TAG, "item:$it")
            }

        mCompositeDisposable.add(subscribe)
    }

    private fun lastSymble() {
        val subscribe = Observable.just("liao", "peng", "hui")
            .last("没人发送，就发送我自己")
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                {
                    Log.e(TAG, "item:$t")
                }
            }
        mCompositeDisposable.add(subscribe)
    }


    private fun reduceSymble() { //字面意思是减少  其实可以理解为聚合  把多个事件压缩 最后发射压缩后的事件
        val subscribe = Observable.just(1, 2, 1, 1, 2, 3, 4, 6, 2, 1)
            .subscribeOn(Schedulers.io())
            .reduce { t1: Int, t2: Int -> t1 + t2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e(TAG, "item:$it")  //最后对所有的数求和
            }

        mCompositeDisposable.add(subscribe)
    }

    private fun scanSymble() { //和reduce的用法差不多  但是每次处理的结果 它都会发射
        val subscribe = Observable.just(1, 2, 1, 1, 2, 3, 4, 6, 2, 1)
            .subscribeOn(Schedulers.io())
            .scan { t1: Int, t2: Int -> t1 + t2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e(TAG, "item:$it")  //最后对所有的数求和
            }

        mCompositeDisposable.add(subscribe)
    }

}