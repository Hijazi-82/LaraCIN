package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * MyCinemaUserQuery
 *
 * DAO خاص بجدول MyCinemaUser داخل Room Database.
 *
 * وظيفة الواجهة:
 * 1. إضافة مستخدم جديد.
 * 2. تعديل بيانات مستخدم موجود.
 * 3. حذف مستخدم.
 * 4. جلب المستخدمين من قاعدة البيانات.
 * 5. البحث عن مستخدم حسب id أو email.
 * 6. فحص تسجيل الدخول محليًا.
 * 7. جلب المستخدمين المفضلين.
 */
@Dao
public interface MyCinemaUserQuery {

    /**
     * insertUser
     *
     * تضيف مستخدم جديد إلى جدول MyCinemaUser.
     *
     * @param user المستخدم المراد إضافته
     */
    @Insert
    void insertUser(MyCinemaUser user);

    /**
     * updateUser
     *
     * تعدّل بيانات مستخدم موجود في الجدول.
     * يعتمد التعديل على المفتاح الأساسي keyId.
     *
     * @param user المستخدم بعد التعديل
     */
    @Update
    void updateUser(MyCinemaUser user);

    /**
     * deleteUser
     *
     * تحذف مستخدم من قاعدة البيانات.
     *
     * @param user المستخدم المراد حذفه
     */
    @Delete
    void deleteUser(MyCinemaUser user);

    /**
     * getAllUsers
     *
     * تجلب جميع المستخدمين المخزنين في جدول MyCinemaUser.
     *
     * @return قائمة بجميع المستخدمين
     */
    @Query("SELECT * FROM MyCinemaUser")
    List<MyCinemaUser> getAllUsers();

    /**
     * getUserById
     *
     * تجلب مستخدم واحد حسب المفتاح الأساسي keyId.
     *
     * @param id رقم المستخدم داخل Room
     * @return المستخدم إذا كان موجودًا، أو null إذا لم يوجد
     */
    @Query("SELECT * FROM MyCinemaUser WHERE keyId = :id LIMIT 1")
    MyCinemaUser getUserById(long id);

    /**
     * getUserByEmail
     *
     * تجلب مستخدم واحد حسب البريد الإلكتروني.
     *
     * @param email البريد الإلكتروني للمستخدم
     * @return المستخدم إذا كان موجودًا، أو null إذا لم يوجد
     */
    @Query("SELECT * FROM MyCinemaUser WHERE email = :email LIMIT 1")
    MyCinemaUser getUserByEmail(String email);

    /**
     * login
     *
     * تفحص تسجيل الدخول محليًا داخل Room.
     * تبحث عن مستخدم يملك نفس البريد الإلكتروني وكلمة المرور.
     *
     * @param email البريد الإلكتروني
     * @param password كلمة المرور
     * @return المستخدم إذا كانت البيانات صحيحة، أو null إذا كانت غير صحيحة
     */
    @Query("SELECT * FROM MyCinemaUser WHERE email = :email AND password = :password LIMIT 1")
    MyCinemaUser login(String email, String password);

    /**
     * getFavoriteUsers
     *
     * تجلب جميع المستخدمين الذين تم وضعهم في المفضلة.
     *
     * @return قائمة المستخدمين المفضلين
     */
    @Query("SELECT * FROM MyCinemaUser WHERE is_favorite = 1")
    List<MyCinemaUser> getFavoriteUsers();
}