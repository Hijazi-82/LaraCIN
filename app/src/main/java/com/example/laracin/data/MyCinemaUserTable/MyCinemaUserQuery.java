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

    @Query("SELECT * FROM MyUser")
    List<MyCinemaUser> getAllUsers();

    @Query("SELECT * FROM MyUser WHERE keyId = :id LIMIT 1")
    MyCinemaUser getUserById(long id);

    @Query("SELECT * FROM MyUser WHERE email = :email LIMIT 1")
    MyCinemaUser getUserByEmail(String email);

    @Query("SELECT * FROM MyUser WHERE email = :email AND password = :password LIMIT 1")
    MyCinemaUser login(String email, String password);
}
