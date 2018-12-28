package com.example.liaopenghui.rxjavaabc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), AnkoLogger {
    //mCompositeDisposable作用：如果一个页面有多个observer需要观察  可以通用放在里面
    //onDestory的时候 执行clear(),统一清理。
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
        intervalOprationSymble()
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
        }).subscribe(object : Consumer<Int> {
            override fun accept(t: Int?) {
                info { "onNext:$t" }
            }
        })
    }

    //线程变换
    fun threadChange() {
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("lph~~~")
                emitter.onNext("jay~~~")
                emitter.onNext("mayday~~~")
            }

        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Consumer<String> {
                override fun accept(t: String?) {
                    toast(t.toString())
                }

            })
    }

    //map操作符
    fun mapOprationSymble() {
        var total: Int = 0
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("5")
                emitter.onNext("5")
            }

        }).map(object : Function<String, Int> {
            override fun apply(t: String): Int {
                return t.toInt()
            }

        }).subscribe(object : Consumer<Int> {
            override fun accept(t: Int?) {
                total += t!!
                info { "total:$total" }
            }

        })
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
        return Observable.fromCallable(object : Callable<String> {
            override fun call(): String {
                Thread.sleep(1000)//假设为耗时操作
                return "liaopenghui"
            }

        })
    }

    //concat操作符
    fun getObservableB(o: Any?): Observable<String> {
        return Observable.fromCallable(object : Callable<String> {
            override fun call(): String {
                Thread.sleep(1000)//假设为耗时操作
                return "jay jay jay"
            }

        })
    }

    //flatMap 拿着上个函数的结果 作为下一个函数的参数
    fun flatMapOprationSymble() {
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("123456789111")
            }
        }).flatMap(object : Function<String,ObservableSource<String>>{
            override fun apply(t: String): ObservableSource<String> {
                if(t == "123456789"){
                    return object :ObservableSource<String>{
                        override fun subscribe(observer: Observer<in String>) {
                            observer.onNext("密码正确")
                        }
                    }
                }else{
                    return object :ObservableSource<String>{
                        override fun subscribe(observer: Observer<in String>) {
                            observer.onNext("密码错误")
                        }
                    }
                }
            }

        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(object :Consumer<String>{
            override fun accept(t: String?) {
                toast(t.toString())
            }


        })
    }

    fun zipOprationSymble(){
        Observable.zip(getObservableA(null),getObservableB(null),object :BiFunction<String,String,String>{
            override fun apply(t1: String, t2: String): String {
                return t1+t2

            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(object :Consumer<String>{
            override fun accept(t: String?) {
                toast(t.toString())
            }
        })
    }


    fun intervalOprationSymble(){
         disposable = Flowable.interval(1, TimeUnit.SECONDS).doOnNext { t -> error("accept: doOnNext : $t") }
            .subscribe { t -> error("accept: accept : $t") }
    }

    override fun onDestroy() {
        // 如果退出程序，就清除后台任务
        mCompositeDisposable.clear()
        disposable.dispose()
        super.onDestroy()
    }


}