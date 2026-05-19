package com.example.laracin.data.MyCinemaUserTable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.laracin.HomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * CinemaUserService
 *
 * Service مسؤول عن حفظ بيانات المستخدم في Firebase Realtime Database.
 *
 * وظيفة الـ Service:
 * 1. استقبال كائن MyCinemaUser من خلال Intent.
 * 2. فحص أن المستخدم موجود وليس null.
 * 3. إنشاء key للمستخدم إذا لم يكن موجودًا.
 * 4. حفظ بيانات المستخدم داخل Firebase في المسار users.
 * 5. إيقاف الـ Service بعد انتهاء عملية الحفظ.
 */
public class CinemaUserService extends Service {

    // اسم المفتاح المستخدم لإرسال واستقبال كائن المستخدم داخل Intent
    public static final String EXTRA_USER = "user_extra";

    /**
     * onStartCommand
     *
     * يتم استدعاؤها عند تشغيل الـ Service بواسطة startService.
     * تستقبل Intent وتفحص إذا كان يحتوي على المستخدم.
     *
     * @param intent البيانات المرسلة إلى الـ Service
     * @param flags معلومات إضافية من النظام
     * @param startId رقم تشغيل الـ Service
     * @return START_NOT_STICKY حتى لا يعيد النظام تشغيل الخدمة تلقائيًا بعد إيقافها
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // فحص وجود Intent ووجود المستخدم داخله
        if (intent != null && intent.hasExtra(EXTRA_USER)) {

            MyCinemaUser user =
                    (MyCinemaUser) intent.getSerializableExtra(EXTRA_USER);

            // إذا كان المستخدم موجودًا يتم حفظه في Firebase
            if (user != null) {
                saveUserToFirebase(user);
            } else {
                stopSelf();
            }

        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    /**
     * saveUserToFirebase
     *
     * تحفظ بيانات المستخدم في Firebase Realtime Database.
     * إذا لم يكن للمستخدم key، يتم إنشاء key جديد.
     *
     * @param user المستخدم المراد حفظه
     */
    private void saveUserToFirebase(MyCinemaUser user) {

        // الوصول إلى المسار users داخل Firebase
        DatabaseReference myRef = FirebaseDatabase
                .getInstance()
                .getReference("users");

        String key = "";

        // إذا لم يكن للمستخدم key، يتم إنشاء key جديد من Firebase
        if (user.getKey() == null || user.getKey().isEmpty()) {
            key = myRef.push().getKey();
            user.setKey(key);
        }

        // حفظ المستخدم داخل users حسب المفتاح الخاص به
        myRef.child(user.getKey()).setValue(user).addOnCompleteListener(fbTask -> {

            if (fbTask.isSuccessful()) {

                Toast.makeText(getApplicationContext(),
                        "User Saved Successfully",
                        Toast.LENGTH_SHORT).show();

                // الانتقال إلى شاشة Home بعد نجاح الحفظ
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

            } else {

                Toast.makeText(getApplicationContext(),
                        "Saving Failed",
                        Toast.LENGTH_SHORT).show();
            }

            // إيقاف الـ Service بعد انتهاء العملية
            stopSelf();
        });
    }

    /**
     * onBind
     *
     * هذه الدالة مطلوبة في أي Service.
     * لأن هذا Service يعمل بواسطة startService وليس bindService،
     * لذلك نرجع null.
     *
     * @param intent Intent الخاص بالربط
     * @return null لأن الخدمة غير مرتبطة
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}