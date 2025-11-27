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
    private EditText etFullName, etId, etPhone, etEmail2, etPassword2 ;
    private TextView tvRole , tvFullName , tvId , tvPhone , tvEmail2 , tvPassword2;
    private TextView textView5 , textView6 , tvSignIn;
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
            public void insertUser(MyCinemaUser user) {

            }

            @Override
            public void updateUser(MyCinemaUser user) {

            }

            @Override
            public void deleteUser(MyCinemaUser user) {

            }

            @Override
            public List<MyCinemaUser> getAllUsers() {
                return Collections.emptyList();
            }

            @Override
            public MyCinemaUser getUserById(long id) {
                return null;
            }

            @Override
            public MyCinemaUser getUserByEmail(String email) {
                return null;
            }

            @Override
            public MyCinemaUser login(String email, String password) {
                return null;
            }
        });

        // Show success message
        Toast.makeText(this, "Record inserted successfully", Toast.LENGTH_SHORT).show();

        return true;
    }
    public boolean validateAndReadData() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();


        boolean isValid = true;

        if (name.isEmpty()) {
            etFullName.setError("Name is required");
            isValid = false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail2.setError("Enter a valid email");
            isValid = false;
        }
        if (password.length() < 6) {
            etPassword2.setError("Password must be at least 6 characters");
            isValid = false;
        }
        if (!password.equals(etPassword2)) {
            etPassword2.setError("Passwords don't match");
            isValid = false;
        }

        if (!isValid) return false;

        // Check if email already exists
        MyCinemaUser existingUser = dao.getUserByEmail(email);
        if (existingUser != null) {
            Toast.makeText(this, "Email already exists ❌", Toast.LENGTH_SHORT).show();
            return false;
        }

        // إنشاء مستخدم جديد + قيم أولية
        MyCinemaUser user = new MyCinemaUser();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(password);


        // حفظه في قاعدة البيانات
        dao.insertUser(user);

        Toast.makeText(this, "Account Created Successfully ✔", Toast.LENGTH_SHORT).show();

        return true;
    }

}

