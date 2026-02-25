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
 * signInActivity
 * شاشة تسجيل الدخول
 *
 * المسؤوليات
 * 1 قراءة الايميل والباسورد من الواجهة
 * 2 التحقق من صحة المدخلات
 * 3 التحقق من بيانات المستخدم محليا من Room
 * 4 تسجيل الدخول عبر Firebase Authentication
 * 5 عند النجاح, نقل المستخدم للشاشة الرئيسية
 */
public class signInActivity extends AppCompatActivity {

    // حقول الادخال
    private EditText etEmail;
    private EditText etPassword;

    // زر تسجيل الدخول
    private Button btnSignIn;

    // نص ينقل المستخدم لشاشة التسجيل
    private TextView tvAsk;

    // FirebaseAuth لعملية تسجيل الدخول
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل edge to edge وتحديد واجهة الشاشة
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // تهيئة Firebase
        auth = FirebaseAuth.getInstance();

        // ضبط padding حسب system bars لتحسين عرض الواجهة
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط عناصر الواجهة
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvAsk = findViewById(R.id.tvAsk);

        /**
         * tvAsk
         * عند النقر, ينقل المستخدم لشاشة إنشاء حساب جديد
         */
        tvAsk.setOnClickListener(v -> {
            Intent intent = new Intent(signInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        /**
         * btnSignIn
         * عند النقر, يبدأ التحقق من المدخلات ومحاولة تسجيل الدخول
         */
        btnSignIn.setOnClickListener(v -> {
            validateInputs();
        });
    }

    /**
     * validateInputs
     *
     * الهدف
     * التحقق من صحة الايميل والباسورد, ثم التأكد من وجود المستخدم في Room,
     * ثم تسجيل الدخول على Firebase
     *
     * خطوات العمل
     * 1 قراءة الايميل والباسورد من الحقول
     * 2 فحص صيغة الايميل وطول الباسورد
     * 3 جلب المستخدم من Room بواسطة الايميل
     * 4 مقارنة كلمة المرور المدخلة مع المخزنة في Room
     * 5 اذا كل شيء صحيح, تنفيذ signInWithEmailAndPassword على Firebase
     * 6 داخل onComplete
     *    - اذا نجاح: Toast نجاح ثم الانتقال للشاشة الرئيسية Activity_main1
     *    - اذا فشل: Toast فشل ثم عرض رسالة الخطأ على حقل الايميل
     *
     * ملاحظة
     * isAllOK يعبر عن صحة المدخلات + نجاح تحقق Room قبل محاولة Firebase
     *
     * @return true اذا كل فحوصات التحقق نجحت قبل Firebase, false اذا في اخطاء
     */
    private boolean validateInputs() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isAllOK = true;

        // التحقق من صحة الايميل
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isAllOK = false;
        }

        // التحقق من كلمة المرور
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isAllOK = false;
        }

        /**
         * التحقق من Room
         * يتم جلب المستخدم عبر الايميل, اذا غير موجود او كلمة المرور غير مطابقة
         * يتم اعتبار تسجيل الدخول غير صالح
         */
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            etEmail.setError("Invalid email or password");
            etPassword.setError("Invalid email or password");
            isAllOK = false;
        }

        /**
         * Firebase Login
         * يتم تنفيذ تسجيل الدخول فقط اذا كل الفحوصات السابقة سليمة
         * النتيجة النهائية تأتي داخل onComplete
         */
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

                                // عرض سبب الفشل على حقل الايميل
                                etEmail.setError(task.getException().getMessage());
                            }
                        }
                    });
        }

        return isAllOK;
    }
}
