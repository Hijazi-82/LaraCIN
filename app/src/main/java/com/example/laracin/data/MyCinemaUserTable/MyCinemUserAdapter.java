package com.example.laracin.data.MyCinemaUserTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laracin.R;

public class MyCinemUserAdapter extends ArrayAdapter<MyCinemaUser> {
    private  int itemLayout;

    public MyCinemUserAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout =resource;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       MyCinemaUser user = getItem(position);
       if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
       }
       ImageView imgUser = convertView.findViewById(R.id.imgVitm);

       return convertView;
    }

}

