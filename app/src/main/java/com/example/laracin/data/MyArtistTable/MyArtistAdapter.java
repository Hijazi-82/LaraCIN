package com.example.laracin.data.MyArtistTable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

public abstract class MyArtistAdapter extends ArrayAdapter<MyArtist>
{
private final int itemLayout;

    /**
     * פעולה בונה מתאם
     * @param context קישור להקשר (מסך- אקטיביטי)
     * @param resource עיצוב של פריט שיציג הנתונים של העצם
     */


    public MyArtistAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;
    }


    /**
     * בונה פריט גרפי אחד בהתאם לעיצוב והצגת נתוני העצם עליו
     * @param position מיקום הפריט החל מ 0
     * @param convertView
     * @param parent רכיב האוסף שיכיל את הפריטים כמו listview
     * @return  . פריט גרפי שמציג נתוני עצם אחד
     */

    public  View getView(int position, View convertView, ViewGroup parent)
    {
        return super.getView(position,convertView,parent);
    }
}
