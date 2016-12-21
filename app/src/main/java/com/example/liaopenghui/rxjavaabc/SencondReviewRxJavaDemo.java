package com.example.liaopenghui.rxjavaabc;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.liaopenghui.rxjavaabc.bean.Course;
import com.example.liaopenghui.rxjavaabc.bean.Student;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 第二次系统性 过Rxjava
 */
public class SencondReviewRxJavaDemo extends AppCompatActivity {

    private static final String TAG = "SencondReviewRxJavaDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sencond_review_rx_java_demo);


       // RxJavaAbc();
    //    RxJavaABCPro();
     //   RxJavaScheduler();
        /**变换*/
//        RxJavaMap();
//        RxJavaFlatMap();
        RxJavaFilter();

    }




    /**
     * 入门使用
     */
    private void RxJavaAbc() {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("liao");
                subscriber.onNext("peng");
                subscriber.onNext("hui");
                subscriber.onNext("hao");
                subscriber.onNext("shuai");
                subscriber.onCompleted();
            }
        });
        Observable<String> observable1 = Observable.just("liao1", "peng1", "hui1", "hao1", "shuai1");
        String liao[] = {"liao2", "peng2", "hui2", "hao2", "shuai2"};
        Observable<String> observable2 = Observable.from(liao);

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                //队列执行完毕的时候调用
            }

            @Override
            public void onError(Throwable e) {
                //发生错误的时候 调用
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG,s);
            }
        };

        observable2.subscribe(subscriber);
    }

    /**
     * 入门进阶
     */
    private void RxJavaABCPro() {

        Action1<String> action1 = new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(SencondReviewRxJavaDemo.this, s, Toast.LENGTH_SHORT).show();
            }
        };
        /**错误的回调  需要Throwable的泛型*/
        Action1<Throwable> action2 = new Action1<Throwable>() {

            @Override
            public void call(Throwable throwable) {

            }
        };

        Action0 action0 = new Action0() {

            @Override
            public void call() {
                Toast.makeText(SencondReviewRxJavaDemo.this, "完成", Toast.LENGTH_SHORT).show();
            }
        };
        Observable<String> observable1 = Observable.just("liao1", "peng1", "hui1", "hao1", "shuai1");
//        observable1.subscribe(action1);
//        observable1.subscribe(action1,action2);
//        observable1.subscribe(action1,action2,action0);
    }

    /**
     * 对schedule的运用
     */
    private void RxJavaScheduler() {
        Observable<String> observable1 = Observable.just("liao1", "peng1", "hui1", "hao1", "shuai1");
        Action1<String> action1 = new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(SencondReviewRxJavaDemo.this, s, Toast.LENGTH_SHORT).show();
            }
        };
        observable1.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(action1);
    }

    /**
     * map()变换：
     * 把int id 变化为drawable
     * map(new Func1<Integer,Drawable>(){})
     */
    private void RxJavaMap() {
        final ImageView imageView = (ImageView) findViewById(R.id.iv_src);


        Observable<Integer> objectObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(R.mipmap.ic_launcher);
            }
        });
      objectObservable.map(new Func1<Integer, Drawable>() {
          @Override
          public Drawable call(Integer integer) {
              return getResources().getDrawable(integer);
          }
      }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Drawable>() {
          @Override
          public void call(Drawable drawable) {
              imageView.setImageDrawable(drawable);
          }
      });

    }


    private void RxJavaFlatMap() {
        Observable<Student> observable = Observable.from(getStudents());

        observable.flatMap(new Func1<Student, Observable<Course>>() {
            @Override
            public Observable<Course> call(Student student) {
                return Observable.from(student.courses);
            }
        }).subscribe(new Action1<Course>() {
            @Override
            public void call(Course course) {
                Toast.makeText(SencondReviewRxJavaDemo.this, course.courseName, Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 过滤符  通过filter拦截无用的信息
     */
    private void RxJavaFilter() {
        Observable<Student> observable = Observable.from(getStudents());
        observable.flatMap(new Func1<Student, Observable<Course>>() {
            @Override
            public Observable<Course> call(Student student) {
                return Observable.from(student.courses);
            }
        })
                .filter(new Func1<Course, Boolean>() {
            @Override
            public Boolean call(Course course) {
                //返回包含0的值
                return course.courseName.contains("0");


            }
        })
                .take(3)
                .doOnNext(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        Log.e(TAG,"在输出前 先保存course"+course.courseName);
                    }
                })
                .subscribe(new Action1<Course>() {
            @Override
            public void call(Course course) {
                Log.e(TAG,course.courseName);
            }
        });
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
