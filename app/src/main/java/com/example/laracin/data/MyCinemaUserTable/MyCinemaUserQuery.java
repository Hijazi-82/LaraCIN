package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


/**
 * MyCinemaUserQuery
 * DAO خاص بجدول MyCinemaUser داخل Room
 *
 * الهدف
 * توفير عمليات CRUD واستعلامات جاهزة للتعامل مع بيانات المستخدمين محليا
 *
 * CRUD
 * - Insert اضافة مستخدم جديد
 * - Update تعديل بيانات مستخدم موجود
 * - Delete حذف مستخدم
 *
 * Queries
 * - getAllUsers جلب كل المستخدمين
 * - getUserById جلب مستخدم حسب المفتاح keyId
 * - getUserByEmail جلب مستخدم حسب الايميل
 * - login جلب مستخدم مطابق لايميل وباسورد, لاستخدام تسجيل الدخول محليا
 */
.public interface MyCinemaUserQuery {

    /**
     * insertUser
     * اضافة سجل مستخدم جديد داخل قاعدة البيانات
     *
     * @param user كائن MyCinemaUser المراد ادخاله
     */
    @Insert
    void insertUser(MyCinemaUser user);

    /**
     * updateUser
     * تحديث بيانات مستخدم موجود, لازم keyId يكون موجود عشان Room يعرف اي سجل يحدث
     *
     * @param user كائن المستخدم بعد التعديل
     */
    @Update
    void updateUser(MyCinemaUser user);

    /**
     * deleteUser
     * حذف مستخدم من قاعدة البيانات
     *
     * @param user كائن المستخدم المراد حذفه
     */
    @Delete
    void deleteUser(MyCinemaUser user);

    /**
     * getAllUsers
     * جلب كل المستخدمين المخزنين في جدول MyCinemaUser
     *
     * @return قائمة بجميع المستخدمين
     */
    @Query("SELECT * FROM MyCinemaUser")
    List<MyCinemaUser> getAllUsers();

    /**
     * getUserById
     * جلب مستخدم واحد حسب keyId
     *
     * @param id المفتاح الاساسي للمستخدم
     * @return المستخدم اذا موجود, او null اذا مش موجود
     */
    @Query("SELECT * FROM MyCinemaUser WHERE keyId = :id LIMIT 1")
    MyCinemaUser getUserById(long id);

    /**
     * getUserByEmail
     * جلب مستخدم واحد حسب البريد الالكتروني
     *
     * @param email ايميل المستخدم
     * @return المستخدم اذا موجود, او null اذا مش موجود
     */
    @Query("SELECT * FROM MyCinemaUser WHERE email = :email LIMIT 1")
    MyCinemaUser getUserByEmail(String email);

    /**
     * login
     * استعلام تسجيل دخول محلي عبر مطابقة الايميل والباسورد
     *
     * ملاحظة
     * هذا استعلام محلي فقط على Room, مش Firebase
     *
     * @param email ايميل المستخدم
     * @param password كلمة المرور
     * @return المستخدم اذا كانت البيانات مطابقة, او null اذا فشلت المطابقة
     */
    @Query("SELECT * FROM MyCinemaUser WHERE email = :email AND password = :password LIMIT 1")
    MyCinemaUser login(String email, String password);
}
