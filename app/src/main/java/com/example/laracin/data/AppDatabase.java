package com.example.laracin.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;

/**
 * AppDatabase
 *
 * قاعدة بيانات Room الرئيسية في التطبيق.
 *
 * وظيفة الكلاس:
 * 1. تعريف الجداول المستخدمة في قاعدة البيانات المحلية.
 * 2. ربط Entity مثل MyCinemaUser مع Room.
 * 3. توفير DAO للتعامل مع بيانات المستخدمين.
 * 4. إنشاء نسخة واحدة فقط من قاعدة البيانات باستخدام Singleton.
 */
@Database(entities = {MyCinemaUser.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    // نسخة واحدة ثابتة من قاعدة البيانات
    private static AppDatabase db;

    /**
     * myCinemaUserQuery
     *
     * ترجع DAO الخاص بجدول MyCinemaUser.
     * من خلاله يمكن تنفيذ عمليات مثل:
     * insert, update, delete, select.
     *
     * @return MyCinemaUserQuery
     */
    public abstract MyCinemaUserQuery myCinemaUserQuery();

    /**
     * getDb
     *
     * ترجع نسخة قاعدة البيانات.
     * إذا لم تكن موجودة، يتم إنشاؤها لأول مرة.
     * إذا كانت موجودة، يتم إرجاع نفس النسخة السابقة.
     *
     * @param context سياق التطبيق أو الشاشة
     * @return نسخة AppDatabase
     */
    public static AppDatabase getDb(Context context) {

        // إنشاء قاعدة البيانات فقط إذا لم تكن موجودة
        if (db == null) {
            db = Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            "HijaziDatabase"
                    )
                    // عند تغيير نسخة قاعدة البيانات بدون Migration، يتم حذف القديم وبناء الجديد
                    .fallbackToDestructiveMigration()

                    // يسمح بتنفيذ أوامر Room على Main Thread، مناسب للتجربة وليس مفضلًا للتطبيقات الكبيرة
                    .allowMainThreadQueries()

                    // بناء قاعدة البيانات
                    .build();
        }

        return db;
    }
}