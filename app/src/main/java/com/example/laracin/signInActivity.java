package com.example.laracin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signInActivity extends AppCompatActivity {

    private TextView tvFilmconnect;
    private TextView tvSignin;
    private TextView tvEmail;
    private EditText etEmail;
    private TextView tvPassword;
    private EditText etPassword;
    private Button btnSignIn;
    private TextView tvAsk;

    // 🔥 Firebase
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // 🔥 تهيئة Firebase
        auth = FirebaseAuth.getInstance();

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

        boolean isAllOK = true;

        // التحقق من صحة الإيميل
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isAllOK = false;
        }

        // التحقق من كلمة المرور
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isAllOK = false;
        }

        // التحقق من Room
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            etEmail.setError("Invalid email or password");
            etPassword.setError("Invalid email or password");
            isAllOK = false;
        }

        // ✅ Firebase Login (مثل الكود اللي بعثته)
        if (isAllOK) {

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(signInActivity.this,
                                        "Signing in Succeeded",
                                        Toast.LENGTH_SHORT).show();

                                // الانتقال للشاشة الرئيسية
                                Intent i = new Intent(signInActivity.this,
                                        Activity_main1.class);
                                startActivity(i);

                            } else {

                                Toast.makeText(signInActivity.this,
                                        "Signing in Failed",
                                        Toast.LENGTH_SHORT).show();

                                etEmail.setError(task.getException().getMessage());
                            }
                        }
                    });
        }

        return isAllOK;
    }
}

