package com.example.laracin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView tvSearchMovies, header_title, tvMyList;
    private Button btnSearchMovies, btnAddMovie, btnMyList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvSearchMovies = findViewById(R.id.tvSearchMovies);
        header_title = findViewById(R.id.header_title);
        tvMyList = findViewById(R.id.tvMyList);
        btnSearchMovies = findViewById(R.id.btnSearchMovies);
        btnAddMovie = findViewById(R.id.btnAddMovie);
        btnMyList = findViewById(R.id.btnMyList);

    }
}

