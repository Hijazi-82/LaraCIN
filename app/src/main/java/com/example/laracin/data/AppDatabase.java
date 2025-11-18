package com.example.laracin.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.example.laracin.data.MyTaskTable.MyTask;
import com.example.laracin.data.MyTaskTable.MyTaskQuery;


    @Database(entities = {MyCinemaUser.class, MyTask.class}, version = 1)
    public abstract class AppDatabase extends RoomDatabase {
        private static AppDatabase dp;
        public abstract MyCinemaUserQuery myCinemaUserQuery();
        public abstract MyTaskQuery myTaskQuery();
        public static AppDatabase getDp(Context context) {
            if (dp == null) {
                dp = Room.databaseBuilder(context,AppDatabase.class,"HijaziDatabase")
                .fallbackToDestructiveMigration()
                 .allowMainThreadQueries()
                .build();
            }
            return dp;
        }
    }

