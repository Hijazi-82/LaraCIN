package com.example.laracin.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.example.laracin.data.MyArtistTable.MyArtistQuery;
import com.example.laracin.data.MyArtistTable.MyArtist;


@Database(entities = {MyCinemaUser.class, MyArtist.class}, version =2)
    public abstract class AppDatabase extends RoomDatabase {
        private static AppDatabase db;
        public abstract MyCinemaUserQuery myCinemaUserQuery();
        public abstract MyArtistQuery myTaskQuery();
        public static AppDatabase getDb(Context context) {
            if (db == null) {
                db = Room.databaseBuilder(context,AppDatabase.class,"HijaziDatabase")
                .fallbackToDestructiveMigration()
                 .allowMainThreadQueries()
                .build();
            }
            return db;
        }
    }

