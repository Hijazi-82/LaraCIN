package com.example.laracin.data.AppDataBase;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// --- تصحيح مسارات الاستيراد ---
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.example.laracin.data.MyTaskTable.MyTask;
import com.example.laracin.data.MyTaskTable.MyTaskQuery;

@Database(entities = {MyTask.class, MyCinemaUser.class}, version = 1, exportSchema = false)
// تم تغيير اسم الكلاس ليطابق اسم الملف المفترض (AppDataBase.java)
public abstract class AppDataBase extends RoomDatabase {

    // volatile تضمن أن التغييرات على المتغير تكون مرئية لجميع الـ threads
    private static volatile AppDataBase INSTANCE;

    // دوال مجردة للحصول على كائنات الوصول للبيانات (DAO)
    public abstract MyTaskQuery myTaskQuery();
    public abstract MyCinemaUserQuery myCinemaUserQuery();

    // دالة لإنشاء أو الحصول على نسخة وحيدة من قاعدة البيانات (Singleton Pattern)
    public static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) { // لمنع الإنشاء المتعدد في بيئة متعددة الـ threads
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDataBase.class,
                                    "laracin_database" // اسم ملف قاعدة البيانات
                            )
                            .fallbackToDestructiveMigration() // يسمح لـ Room بإعادة إنشاء الجداول عند تغيير الإصدار
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
