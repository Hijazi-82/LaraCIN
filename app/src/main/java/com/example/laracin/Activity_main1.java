package com.example.laracin;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemUserAdapter;

public class Activity_main1 extends AppCompatActivity {

    private ListView listusers ;
    private MyCinemUserAdapter adapteruser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main1);

        listusers = findViewById(R.id.listusers);
        adapteruser = new MyCinemUserAdapter(this, R.layout.actor_item_layout);
        listusers.setAdapter(adapteruser);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    protected void onResume() {
        super.onResume();

        adapteruser.clear();
       // adapteruser.addAll(AppDatabase.getDb(this).myCinemaUserQuery().getAllUsers());
         adapteruser.notifyDataSetChanged();
    }

}