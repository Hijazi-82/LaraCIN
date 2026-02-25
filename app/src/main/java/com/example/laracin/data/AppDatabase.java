package com.example.laracin.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;



/**
 * AppDatabase
 * قاعدة بيانات Room الرئيسية للتطبيق
 *
 * الفكرة
 * - تعريف ال entities اللي بدها تنخزن محليا, هون MyCinemaUser
 * - تعريف ال DAO اللي بنستخدمه للاستعلام والادخال, هون MyCinemaUserQuery
 * - توفير Singleton instance من قاعدة البيانات عبر getDb عشان ما تنبني اكثر من مرة
 *
 * ملاحظات مهمة عن الاعدادات الحالية
 * - version = 3 رقم نسخة قاعدة البيانات, لازم يزيد لما يتغير ال schema
 * - fallbackToDestructiveMigration يعني اذا صار تغيير بالنسخة بدون migration
 *   بيحذف البيانات القديمة وبعيد بناء الجداول
 * - allowMainThreadQueries يسمح باستعلامات على ال main thread, سهل للتجربة
 *   بس مش مفضل لتطبيق فعلي لانه ممكن يعلق الواجهة
 */
@Database(entities = {MyCinemaUser.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    // Singleton instance
    private static AppDatabase db;

    /**
     * myCinemaUserQuery
     * DAO للوصول لعمليات المستخدمين, insert, select, update, delete حسب تعريفك داخل MyCinemaUserQuery
     */
    public abstract MyCinemaUserQuery myCinemaUserQuery();

    /**
     * getDb
     * بترجع نفس نسخة قاعدة البيانات كل مرة
     *
     * @param context سياق التطبيق او الاكتفتي
     * @return AppDatabase instance
     */
    public static AppDatabase getDb(Context context) {

        // بناء قاعدة البيانات اول مرة فقط
        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, "HijaziDatabase")
                    // اذا ما في migration, احذف وابني من جديد
                    .fallbackToDestructiveMigration()
                    // السماح بالاستعلام على ال main thread (غير مفضل للانتاج)
                    .allowMainThreadQueries()
                    .build();
        }

        return db;
    }
}