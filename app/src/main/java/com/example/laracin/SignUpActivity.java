package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity
{
    private TextInputEditText  etEmail2, etPassword2;
    private TextView tvSignIn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        etEmail2 = findViewById(R.id.eiEmail2);
        etPassword2 = findViewById(R.id.edPassword2);
        tvSignIn = findViewById(R.id.tvSignIn);

        // Set up button click listener
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndInsertRecord();
                Intent intent = new Intent(SignUpActivity.this, SaveProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up sign-in text click listener
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to sign-in activity
                Intent intent = new Intent(SignUpActivity.this, signInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean validateAndInsertRecord() {

        String email = etEmail2.getText().toString().trim();
        String password = etPassword2.getText().toString().trim();

        boolean isValid = true;



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



        // ✅ إذا البيانات صحيحة
        if (isValid) {

            // 🔥 تسجيل المستخدم في Firebase
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                // إنشاء المستخدم داخل Room
                                MyCinemaUser myuser = new MyCinemaUser();
                                myuser.setEmail(email);
                                myuser.setPassword(password);
                                myuser.setRole("myuser");

                                AppDatabase.getDb(SignUpActivity.this)
                                       .myCinemaUserQuery()
                                        .insertUser(myuser);

                                Toast.makeText(SignUpActivity.this,
                                        "Signing up Succeeded",
                                        Toast.LENGTH_SHORT).show();

                                finish();

                            } else {

                                Toast.makeText(SignUpActivity.this,
                                        "Signing up Failed",
                                        Toast.LENGTH_SHORT).show();

                                etEmail2.setError(task.getException().getMessage());
                            }
                        }
                    });
        }

        return isValid;
    }
}