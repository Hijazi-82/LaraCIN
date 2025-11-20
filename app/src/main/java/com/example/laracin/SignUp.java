package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;

import java.util.Collections;
import java.util.List;

public class SignUp extends AppCompatActivity {
    private Button btnSignUp;
    private EditText etFullName, etId, etPhone, etEmail2, etPassword2;
    private TextView tvRole , tvFullName , tvId , tvPhone , tvEmail2 , tvPassword2;
    private TextView textView5 , textView6 , tvSignIn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        btnSignUp = findViewById(R.id.btSignUp);
        etFullName = findViewById(R.id.etFullname);
        etId = findViewById(R.id.edId);
        etPhone = findViewById(R.id.etPhone);
        etEmail2 = findViewById(R.id.edEmail2);
        etPassword2 = findViewById(R.id.edPassword2);
        tvRole = findViewById(R.id.tvRole);
        tvFullName = findViewById(R.id.tvFullName);
        tvId = findViewById(R.id.tvId);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail2 = findViewById(R.id.tvEmail2);
        tvPassword2 = findViewById(R.id.tvPassword2);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        tvSignIn = findViewById(R.id.tvSignIn);
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);

        });
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, signInActivity.class);
            startActivity(intent);

        });
    }

    // Validates data from EditText fields and inserts a new record in the database
    private boolean validateAndInsertRecord() {
        // Read data from EditText fields
        String fullName = etFullName.getText().toString().trim();
        String id = etId.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();

        // Perform validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            return false;
        }

        if (TextUtils.isEmpty(id)) {
            etId.setError("ID is required");
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail2.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail2.setError("Invalid email");
            return false;
        }

        if (password.length() < 6) {
            etPassword2.setError("Password must be at least 6 characters long");
            return false;
        }

        // Check if the record already exists in the database
        MyCinemaUser user = MyCinemaUser.findByEmail(email);
        if (user != null) {
            etEmail2.setError("Email already exists");
            return false;
        }

        // Insert a new record in the database
        MyCinemaUser.insert(new MyCinemaUserQuery() {
            @Override
            public void insertUser(MyCinemaUserQuery user) {

            }

            @Override
            public void updateUser(MyCinemaUserQuery user) {

            }

            @Override
            public void deleteUser(MyCinemaUserQuery user) {

            }

            @Override
            public List<MyCinemaUserQuery> getAllUsers() {
                return Collections.emptyList();
            }

            @Override
            public MyCinemaUserQuery getUserById(long id) {
                return null;
            }

            @Override
            public MyCinemaUserQuery getUserByEmail(String email) {
                return null;
            }

            @Override
            public MyCinemaUserQuery login(String email, String password) {
                return null;
            }
        });

        // Show success message
        Toast.makeText(this, "Record inserted successfully", Toast.LENGTH_SHORT).show();

        return true;
    }

}

