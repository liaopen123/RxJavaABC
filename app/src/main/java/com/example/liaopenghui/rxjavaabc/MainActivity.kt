package com.example.liaopenghui.rxjavaabc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() ,AnkoLogger{

    lateinit var disposable : Disposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        baseFunc()
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
                disposable =   d
            }

            override fun onNext(t: Int) {
                info { "onNext:$t" }
                toast( "onNext:$t" )
                if(t==4){
                    disposable.dispose()
                }
            }

            override fun onError(e: Throwable) {
            }

        })
    }
}