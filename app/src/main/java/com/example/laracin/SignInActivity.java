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

/**
 * SignInActivity
 *
 * شاشة تسجيل الدخول في التطبيق.
 *
 * وظيفة الشاشة:
 * 1. قراءة البريد الإلكتروني وكلمة المرور من المستخدم.
 * 2. التحقق من صحة المدخلات.
 * 3. فحص وجود المستخدم داخل قاعدة البيانات المحلية Room.
 * 4. تسجيل الدخول باستخدام Firebase Authentication.
 * 5. نقل المستخدم إلى شاشة HomeActivity عند نجاح الدخول.
 */
public class SignInActivity extends AppCompatActivity {

    // حقول إدخال البريد الإلكتروني وكلمة المرور
    private EditText etEmail , etPassword;

    // زر تنفيذ تسجيل الدخول
    private Button btnSignIn;

    // نص للانتقال إلى شاشة إنشاء حساب جديد
    private TextView tvAsk;

    // كائن FirebaseAuth المسؤول عن تسجيل الدخول عبر Firebase
    private FirebaseAuth auth;

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة تسجيل الدخول.
     * داخلها يتم ربط عناصر الواجهة، تهيئة Firebase،
     * وتجهيز أزرار التنقل وتسجيل الدخول.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل عرض Edge To Edge
        EdgeToEdge.enable(this);

        // ربط الكلاس بملف التصميم
        setContentView(R.layout.activity_sign_in);

        // تهيئة Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // ضبط أبعاد الشاشة حتى لا تدخل العناصر تحت شريط النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط عناصر الواجهة مع المتغيرات
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvAsk = findViewById(R.id.tvAsk);

        // الانتقال إلى شاشة التسجيل عند الضغط على النص
        tvAsk.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // عند الضغط على زر الدخول يتم فحص المدخلات ومحاولة تسجيل الدخول
        btnSignIn.setOnClickListener(v -> validateInputs());
    }

    /**
     * validateInputs
     *
     * تفحص البريد الإلكتروني وكلمة المرور.
     * إذا كانت المدخلات صحيحة، يتم فحص المستخدم في Room.
     * بعد ذلك يتم تنفيذ تسجيل الدخول عبر Firebase.
     *
     * @return true إذا كانت الفحوصات الأولية صحيحة، false إذا وُجد خطأ
     */
    private boolean validateInputs() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isAllOK = true;

        // فحص البريد الإلكتروني
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isAllOK = false;
        }

        // فحص كلمة المرور
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isAllOK = false;
        }

        // فحص المستخدم داخل Room حسب البريد الإلكتروني
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);

        // التأكد أن المستخدم موجود محليًا وأن كلمة المرور مطابقة
        if (user == null || user.getPassword() == null || !user.getPassword().equals(password)) {
            etEmail.setError("Invalid email or password");
            etPassword.setError("Invalid email or password");
            isAllOK = false;
        }

        // إذا كل الفحوصات صحيحة، يتم تسجيل الدخول عبر Firebase
        if (isAllOK) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        /**
                         * onComplete
                         *
                         * يتم استدعاؤها بعد انتهاء محاولة تسجيل الدخول في Firebase.
                         * إذا نجح الدخول ينتقل المستخدم إلى HomeActivity.
                         * إذا فشل، تظهر رسالة خطأ.
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(SignInActivity.this,
                                        "Signing in Succeeded",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(SignInActivity.this,
                                        "Signing in Failed",
                                        Toast.LENGTH_SHORT).show();

                                if (task.getException() != null) {
                                    etEmail.setError(task.getException().getMessage());
                                }
                            }
                        }
                    });
        }

        return isAllOK;
    }
}