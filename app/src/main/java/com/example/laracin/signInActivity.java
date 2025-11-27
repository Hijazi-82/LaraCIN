package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

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
        validateInputs();
        });
    }
    private boolean validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        MyCinemaUser user = AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            etEmail.setError("Invalid email or password");
            etPassword.setError("Invalid email or password");
            isValid = false;
        }
        else {
            etEmail.setError(null);
            etPassword.setError(null);
            isValid = true;
            Intent intent = new Intent(signInActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return isValid;
    }

}