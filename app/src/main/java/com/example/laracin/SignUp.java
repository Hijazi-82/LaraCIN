package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.google.android.material.textfield.TextInputEditText;

public class SignUp extends AppCompatActivity {
    private Button btnSignUp;
    private TextInputEditText etFullName, etEmail2, etPhone, eiEmail2, etPassword2 , etPorfolio ,etExperienceYears, etSkills ;

    private AutoCompleteTextView acRole ;
    private TextView  tvSignIn;
    private MyCinemaUserQuery dao;

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
        etPhone = findViewById(R.id.etPhone);
        etEmail2 = findViewById(R.id.eiEmail2);
        etPassword2 = findViewById(R.id.edPassword2);

        tvSignIn = findViewById(R.id.tvSignIn);
        btnSignUp.setOnClickListener(v -> {
     validateAndInsertRecord();

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

        String phone = etPhone.getText().toString().trim();
        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            isValid = false;
        }


        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail2.setError("Email is required");
            isValid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail2.setError("Invalid email");
            isValid = false;
        }

        if (password.length() < 6) {
            etPassword2.setError("Password must be at least 6 characters long");
            isValid = false;
        }

        // Check if the record already exists in the database
        MyCinemaUser user = AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);
        if (user != null) {
            etEmail2.setError("Email already exists");
            isValid = false;
        }

        if (isValid) {
            // Create a new MyCinemaUser object
            MyCinemaUser myuser = new MyCinemaUser();
            myuser.setFullName(fullName);
            myuser.setEmail(email);
            myuser.setPassword(password);
            myuser.setPhone(phone);
            myuser.setRole("myuser");

            // Insert the record into the database
            AppDatabase.getDb(this).myCinemaUserQuery().insertUser(myuser);
            // Show success message
            Toast.makeText(this, "Record inserted successfully", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }


}

