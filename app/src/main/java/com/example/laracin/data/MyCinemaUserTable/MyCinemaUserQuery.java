package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyCinemaUserQuery {

    @Insert
    void insertUser(MyCinemaUser user);

    @Update
    void updateUser(MyCinemaUser user);

    @Delete
    void deleteUser(MyCinemaUser user);

    // استعلام للحصول على جميع المستخدمين
    @Query("SELECT * FROM MyCinemaUser")
    List<MyCinemaUser> getAllUsers();

    // استعلام للحصول على مستخدم حسب الـ keyId
    @Query("SELECT * FROM MyCinemaUser WHERE keyId = :id LIMIT 1")
    MyCinemaUser getUserById(long id);

    // استعلام للحصول على مستخدم حسب البريد الإلكتروني
    @Query("SELECT * FROM MyCinemaUser WHERE email = :email LIMIT 1")
    MyCinemaUser getUserByEmail(String email);

    // استعلام لتسجيل الدخول باستخدام البريد الإلكتروني وكلمة المرور
    @Query("SELECT * FROM MyCinemaUser WHERE email = :email AND password = :password LIMIT 1")
    MyCinemaUser login(String email, String password);
}
