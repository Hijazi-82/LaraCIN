package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signInActivity extends AppCompatActivity {
    private TextView tvFilmconnect;
    private TextView tvSignin;
    private TextView tvEmail;
    private EditText etEmail;
    private TextView tvPassword;
    private EditText etPassword;
    private Button btnSignIn;
    private TextView tvAsk;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvFilmconnect = findViewById(R.id.tvFilmconnect);
        tvSignin = findViewById(R.id.tvSignin);
        tvEmail = findViewById(R.id.tvEmail);
        etEmail = findViewById(R.id.etEmail);
        tvPassword = findViewById(R.id.tvPassword);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvAsk = findViewById(R.id.tvAsk);
        btnSignIn.setOnClickListener(v -> {

        });
        tvAsk.setOnClickListener(v -> {

            Intent intent = new Intent(signInActivity.this, SignUp.class);
            startActivity(intent);
        });
        btnSignIn.setOnClickListener(v -> {

            Intent intent = new Intent(signInActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}