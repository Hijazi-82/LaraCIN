package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashScreen
 *
 * هذه الشاشة هي أول شاشة تظهر عند تشغيل التطبيق.
 *
 * وظيفة الشاشة:
 * 1. عرض واجهة البداية الخاصة بالتطبيق.
 * 2. إبقاء الشاشة ظاهرة لمدة 3 ثواني.
 * 3. نقل المستخدم تلقائيًا إلى شاشة تسجيل الدخول SignInActivity.
 *
 * ملاحظة:
 * لا تحتوي هذه الشاشة على أزرار أو إدخال من المستخدم،
 * وظيفتها فقط إعطاء بداية منظمة للتطبيق قبل الانتقال للشاشة التالية.
 */
public class SplashScreen extends AppCompatActivity {

    /**
     * onCreate
     *
     * يتم استدعاء هذه الدالة عند إنشاء الشاشة لأول مرة.
     * داخلها يتم ربط التصميم، تشغيل الانتظار، ثم الانتقال إلى شاشة تسجيل الدخول.
     *
     * @param savedInstanceState يحفظ حالة الشاشة إذا تم إعادة إنشائها
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل عرض الشاشة بطريقة Edge To Edge
        EdgeToEdge.enable(this);

        // ربط الكلاس بملف تصميم شاشة السبلاش
        setContentView(R.layout.activity_splash_screen);

        /*
         * Thread
         *
         * نستخدمه هنا لعمل تأخير زمني قبل الانتقال.
         * مدة التأخير هي 3000 milliseconds، أي 3 ثواني.
         */
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // إبقاء شاشة البداية ظاهرة لمدة 3 ثواني
                    Thread.sleep(3000);

                } catch (InterruptedException e) {
                    // طباعة الخطأ في حال توقف الـ Thread بشكل غير متوقع
                    e.printStackTrace();

                } finally {
                    // بعد انتهاء مدة الانتظار، يتم فتح شاشة تسجيل الدخول
                    Intent intent = new Intent(SplashScreen.this, SignInActivity.class);
                    startActivity(intent);

                    // إنهاء شاشة السبلاش حتى لا يرجع إليها المستخدم بزر الرجوع
                    finish();
                }
            }
        };

        // تشغيل الـ Thread
        thread.start();
    }
}