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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * SignInActivity
 *
 * شاشة تسجيل الدخول.
 *
 * تعتمد على Firebase Authentication لتسجيل الدخول,
 * ثم تفحص وجود المستخدم في Room.
 * إذا لم يكن موجودًا محليًا, يتم جلبه من Firebase وحفظه في Room.
 */
public class SignInActivity extends AppCompatActivity {

    // حقول إدخال البريد وكلمة المرور
    private EditText etEmail;
    private EditText etPassword;

    // زر تسجيل الدخول
    private Button btnSignIn;

    // نص للانتقال إلى شاشة التسجيل
    private TextView tvAsk;

    // كائن FirebaseAuth لتسجيل الدخول
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvAsk = findViewById(R.id.tvAsk);

        tvAsk.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> {
            validateInputs();
        });
    }

    /**
     * validateInputs
     *
     * تفحص صحة البريد وكلمة المرور,
     * ثم تسجل الدخول من Firebase,
     * وبعد النجاح تفحص وجود المستخدم في Room.
     */
    private boolean validateInputs() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isAllOK = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isAllOK = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isAllOK = false;
        }

        if (!isAllOK) {
            return false;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        if (firebaseUser == null) {
                            Toast.makeText(SignInActivity.this,
                                    "User not found",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = firebaseUser.getUid();

                        MyCinemaUser localUser =
                                AppDatabase.getDb(SignInActivity.this)
                                        .myCinemaUserQuery()
                                        .getUserByEmail(email);

                        if (localUser != null) {
                            goToHome();
                        } else {
                            saveUserFromFirebaseToRoom(uid, email, password);
                        }

                    } else {

                        Toast.makeText(SignInActivity.this,
                                "Signing in Failed",
                                Toast.LENGTH_SHORT).show();

                        if (task.getException() != null) {
                            etEmail.setError(task.getException().getMessage());
                        }
                    }
                });

        return true;
    }

    /**
     * saveUserFromFirebaseToRoom
     *
     * تجلب بيانات المستخدم من Firebase Realtime Database
     * وتحفظها داخل Room إذا لم تكن موجودة محليًا.
     */
    private void saveUserFromFirebaseToRoom(String uid, String email, String password) {

        DatabaseReference userRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(uid);

        userRef.get().addOnSuccessListener(snapshot -> {

            MyCinemaUser user = snapshot.getValue(MyCinemaUser.class);

            if (user == null) {
                user = new MyCinemaUser();
                user.setKey(uid);
                user.setEmail(email);
                user.setPassword(password);
            }

            if (user.getKey() == null || user.getKey().isEmpty()) {
                user.setKey(uid);
            }

            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(email);
            }

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(password);
            }

            AppDatabase.getDb(SignInActivity.this)
                    .myCinemaUserQuery()
                    .insertUser(user);

            goToHome();

        }).addOnFailureListener(e -> {
            Toast.makeText(SignInActivity.this,
                    "Failed to load user data",
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * goToHome
     *
     * ينقل المستخدم إلى شاشة Home بعد نجاح الدخول.
     */
    private void goToHome() {
        Toast.makeText(SignInActivity.this,
                "Signing in Succeeded",
                Toast.LENGTH_SHORT).show();

        Intent i = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}