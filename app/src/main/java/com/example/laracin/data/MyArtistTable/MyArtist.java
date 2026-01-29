package com.example.laracin.data.MyArtistTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyArtist {

    @PrimaryKey(autoGenerate = true)
    public long Id;


}
