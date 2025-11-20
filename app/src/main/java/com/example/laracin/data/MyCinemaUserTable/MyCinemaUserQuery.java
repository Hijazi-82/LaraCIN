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
    void insertUser(MyCinemaUserQuery user);

    @Update
    void updateUser(MyCinemaUserQuery user);

    @Delete
    void deleteUser(MyCinemaUserQuery user);


    @Query("SELECT * FROM MyUser")
    List<MyCinemaUserQuery> getAllUsers();

    @Query("SELECT * FROM MyUser WHERE keyId = :id LIMIT 1")
    MyCinemaUserQuery getUserById(long id);

    @Query("SELECT * FROM MyUser WHERE email = :email LIMIT 1")
    MyCinemaUserQuery getUserByEmail(String email);

    @Query("SELECT * FROM MyUser WHERE email = :email AND password = :password LIMIT 1")
    MyCinemaUserQuery login(String email, String password);




}
