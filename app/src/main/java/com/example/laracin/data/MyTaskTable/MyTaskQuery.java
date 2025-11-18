package com.example.laracin.data.MyTaskTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyTaskQuery {

    @Insert
    void insertTask(MyTask task);

    @Update
    void updateTask(MyTask task);

    @Delete
    void deleteTask(MyTask task);

    @Query("SELECT * FROM MyTask")
    List<MyTask> getAllTasks();

    @Query("SELECT * FROM MyTask WHERE taskId = :id LIMIT 1")
    MyTask getTaskById(long id);

    @Query("SELECT * FROM MyTask WHERE isDone = 1")
    List<MyTask> getCompletedTasks();

    @Query("SELECT * FROM MyTask WHERE isDone = 0")
    List<MyTask> getPendingTasks();
}

