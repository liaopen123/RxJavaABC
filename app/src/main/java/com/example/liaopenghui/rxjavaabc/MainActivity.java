package com.example.liaopenghui.rxjavaabc;

import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.liaopenghui.rxjavaabc.bean.Course;
import com.example.liaopenghui.rxjavaabc.bean.Student;

import java.util.ArrayList;
import java.util.Observer;
import java.util.logging.Logger;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*********************基本用法和流程*******************/
        //    howToUseABC();
        //getName();
        //setDrawable();
        /********************scheduler的用法******************/
        //  practiceforSchedular();
        // practiceforSchedular1();
        /***********************最牛逼的变换********************/
        //  practiceForMap();
        practiceForFlatMap();
    }

    /**
     * 应用实例:
     * 现在要答应Students对象里的Course对象,并且一个Student有很多Course
     * 相当于发送(Observable)的是Student对象,得到的是Course对象  那么需要一个中转(中转的作用是发送Course对象)
     */
    private void practiceForFlatMap() {
        ArrayList<Student> students = getStudents();
        rx.Observable.from(students)
                .flatMap(new Func1<Student, rx.Observable<Course>>() {

                    @Override
                    public rx.Observable<Course> call(Student student) {
                        return rx.Observable.from(student.courses);
                    }
                }).subscribe(new Action1<Course>() {
            @Override
            public void call(Course course) {
                Log.e(TAG,"得到的课程有:"+course.courseName);
            }
        });
    }

    /**
     * 对map的联系  map 中  new Func1的参数:
     * String 发送的数据类型   和just()中的参数一致
     * bitMap   转换的数据类型   用户直接写  new Action1 中的泛型和它保持一致
     *
     */
//    private void practiceForMap() {
//        rx.Observable.just("images/logo.png")  //输入类型:String
//                .map(new Func1<String, Bitmap>() {
//                    @Override
//                    public Bitmap call(String filePath) {
//
//                     return getBitmapFromPath(filePath); // 返回类型 Bitmap
//                    }
//                }).subscribe(new Action1<Bitmap>() {
//            @Override
//            public void call(Bitmap bitmap) {
//                //接收到的类型 bitmap
//            }
//        });
////        final int drawableRes = R.mipmap.ic_launcher;
////        final ImageView iv_src = (ImageView) findViewById(R.id.iv_src);
////        rx.Observable.just(drawableRes)
////                .map(new Func1<Integer, Drawable>() {
////                    @Override
////                    public Drawable call(Integer drawable) {
////
////                        return getResources().getDrawable(drawable);
////                    }
////                }).subscribe(new Action1<Drawable>() {
////            @Override
////            public void call(Drawable bitmap) {
////                iv_src.setImageDrawable(bitmap);
////            }
////        });
//    }


    /**
     * 显示图片的就适合用scheduler进行  大图片的加载放在后台   线上放在mainThread上面
     */
    private void practiceforSchedular1() {
        final int drawableRes = R.mipmap.ic_launcher;
        final ImageView iv_src = (ImageView) findViewById(R.id.iv_src);
        rx.Observable.create(new rx.Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(drawableRes);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())   //io线程中加载图片
                .observeOn(AndroidSchedulers.mainThread())  //主线程中显示图片
                .subscribe(new Action1<Drawable>() {
                    @Override
                    public void call(Drawable drawable) {
                        iv_src.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 用来练习schedular
     */
    private void practiceforSchedular() {
        rx.Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())   //指定subscribe()发送在IO线程
                .observeOn(AndroidSchedulers.mainThread())//指定subscriber发送在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, "得到的number:" + integer);
                        Toast.makeText(MainActivity.this, "number" + integer, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setDrawable() {
        /******************************************************************实战演练:显示图片******************************************/
        final int drawableRes = R.mipmap.ic_launcher;
        final ImageView iv_src = (ImageView) findViewById(R.id.iv_src);
        rx.Observable.create(new rx.Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(drawableRes * 2);
                subscriber.onNext(drawable);
            }
        }).subscribe(new rx.Observer<Drawable>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "出错了");
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
        String[] names = {"zyy", "小公主", "廖鹏辉", "jay"};
        rx.Observable.from(names).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "得到的名字为:" + s);
            }
        });
    }


    private void howToUseABC() {
        /******************************************************************第一步:接受数据的 ******************************************/
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
                Log.e(TAG, "ITEMobserver:" + s);
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
                Log.e(TAG, "ITEM:" + s);
            }
        };
        /******************************************************************第二步:发送数据的******************************************/
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
        String[] arrys = {"hello2", "hi2", "aloha"};
        rx.Observable<String> observable2 = rx.Observable.from(arrys);


        /******************************************************************第三步订阅******************************************/
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
        observable.subscribe(onNextAction, onErrorAction);
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    public ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < 5; i++){
    Student student = new Student();
            ArrayList<Course> courseArrayList = new ArrayList<>();
    for(int p = 0;p<6;p++){
        Course course = new Course();
        course.courseName = "课程"+i+p;
        courseArrayList.add(course);
    }
            student.courses =courseArrayList;
            students.add(student);
        }

    return students;
}

}
