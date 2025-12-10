package com.example.laracin.data.MyArtistTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyTask")
public class MyArtist {

    @PrimaryKey(autoGenerate = true)
    public long taskId;

    public String taskTitle;

    public String taskDescription;

    public String taskDate;


    public boolean isDone;

    // Setters and Getters
    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
