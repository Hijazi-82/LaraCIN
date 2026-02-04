package com.example.laracin.data.MyCinemaUserTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laracin.R;

public class MyCinemUserAdapter extends ArrayAdapter<MyCinemaUser> {
    private int itemLayout;

    public MyCinemUserAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
        }

        MyCinemaUser user = getItem(position);

        ImageView imgUser = convertView.findViewById(R.id.imCinemaUser);
        TextView tvltmTitle = convertView.findViewById(R.id.tvltmTitle);
        TextView tvltmText = convertView.findViewById(R.id.tvltmText);
        TextView tvltmNote = convertView.findViewById(R.id.tvltmNote);

        ImageButton imgBtnSend = convertView.findViewById(R.id.imgBtnSend);
        ImageButton imgBtnCall = convertView.findViewById(R.id.imgBtnCall);
        ImageButton imgBtnNote = convertView.findViewById(R.id.imgBtnNote);
        ImageButton imgBtnStar = convertView.findViewById(R.id.imgBtnStar);

        if (user != null) {
            tvltmTitle.setText(user.getFullName());
            tvltmText.setText(user.getEmail());
            tvltmNote.setText(user.getRole());
        }

        return convertView;
    }
}

