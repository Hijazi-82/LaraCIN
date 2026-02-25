package com.example.laracin;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class SplashScreen extends AppCompatActivity {

    // شاشة سبلاش, بتظهر لمدة 3 ثواني وبعدها بتنقل المستخدم لشاشة تسجيل الدخول
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل عرض edge to edge
        EdgeToEdge.enable(this);

        // ربط الواجهة الخاصة بالسبلاش
        setContentView(R.layout.activity_splash_screen);

        /*
          Thread لعمل تأخير 3 ثواني
          بعدها يتم فتح signInActivity
        */
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // مدة عرض السبلاش
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // معالجة حالة انقطاع الثريد
                    e.printStackTrace();
                } finally {
                    // الانتقال للشاشة التالية
                    Intent intent = new Intent(SplashScreen.this, signInActivity.class);
                    startActivity(intent);
                }
            }
        };

        // تشغيل الثريد
        thread.start();
    }
}