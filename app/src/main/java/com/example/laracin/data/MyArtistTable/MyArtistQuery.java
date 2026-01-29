package com.example.laracin.data.MyArtistTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyArtistQuery {

    @Insert
    void insertTask(MyArtist task);

    @Update
    void updateTask(MyArtist task);

    @Delete
    void deleteTask(MyArtist task);

    @Query("SELECT * FROM MyArtist")
    List<MyArtist> getAllTasks();

    @Query("SELECT * FROM MyArtist WHERE taskId = :id LIMIT 1")
    MyArtist getTaskById(long id);

    @Query("SELECT * FROM MyArtist WHERE isDone = 1")
    List<MyArtist> getCompletedTasks();

    @Query("SELECT * FROM MyArtist WHERE isDone = 0")
    List<MyArtist> getPendingTasks();
}

