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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * SignUpActivity
 * شاشة تسجيل حساب جديد
 *
         * المسؤوليات
 * 1 قراءة الايميل والباسورد من الواجهة
 * 2 التحقق من صحة المدخلات
 * 3 إنشاء مستخدم جديد على Firebase Authentication
 * 4 عند النجاح, تخزين سجل المستخدم محليا داخل Room Database
 * 5 عرض رسائل نجاح او فشل للمستخدم
 */
    public class SignUpActivity extends AppCompatActivity {

        /**
         * etEmail2 و etPassword2
         * حقول ادخال الايميل والباسورد في واجهة التسجيل
         */
        private TextInputEditText etEmail2, etPassword2;

        /**
         * tvSignIn
         * TextView قابل للنقر, ينقل المستخدم لشاشة تسجيل الدخول بدل التسجيل
         */
        private TextView tvSignIn;


        /**
         * auth
         * كائن FirebaseAuth المسؤول عن عمليات التسجيل على Firebase
         */
        private FirebaseAuth auth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // تفعيل edge to edge عشان الواجهة تتعامل صح مع system bars
            EdgeToEdge.enable(this);

            // تحميل واجهة شاشة التسجيل
            setContentView(R.layout.activity_sign_up);

            // ضبط padding حسب شريط الحالة والتنقل, عشان العناصر تضل واضحة ومش ملزوقة بالحواف
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // تهيئة FirebaseAuth
            auth = FirebaseAuth.getInstance();

            // ربط عناصر الواجهة بالمتغيرات
            etEmail2 = findViewById(R.id.eiEmail2);
            etPassword2 = findViewById(R.id.edPassword2);
            tvSignIn = findViewById(R.id.tvSignIn);

            /**
             * زر التسجيل
             * عند النقر:
             * 1 يتحقق من صحة المدخلات
             * 2 يبدأ عملية التسجيل على Firebase
             * 3 ينتقل لشاشة SaveProfileActivity حسب الكود الحالي
             */
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateAndInsertRecord();

                }
            });

            /**
             * نص تسجيل الدخول
             * عند النقر ينقل المستخدم لشاشة signInActivity
             * * onClick
             *  * يتم استدعاؤها لما المستخدم ينقر على View مرتبط بالـ listener
             *  *
             *  * @param v الـ View اللي انكبس فعليا, ممكن يكون زر او TextView او اي عنصر واجهة
             *  *
             *  * الوظيفة داخل هذا الحدث
             *  * - فتح شاشة تسجيل الدخول signInActivity
             *  * - اغلاق شاشة SignUpActivity عشان زر الرجوع ما يرجع المستخدم للتسجيل
             *  */

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
        }

        /**
         * validateAndInsertRecord
         *
         * الهدف
         * التحقق من صحة المدخلات ثم تسجيل المستخدم في Firebase, وبعدها تخزينه في Room عند النجاح
         *
         * خطوات العمل
         * 1 قراءة الايميل والباسورد من TextInputEditText
         * 2 فحص الايميل فاضي ولا لا
         * 3 فحص صيغة الايميل عبر Patterns.EMAIL_ADDRESS
         * 4 فحص طول الباسورد (اقل من 6 يعتبر غير صالح)
         * 5 اذا المدخلات سليمة, يبدأ createUserWithEmailAndPassword
         * 6 داخل onComplete
         *    - اذا نجاح: ينشئ MyCinemaUser ويحفظه في Room, ويعرض Toast نجاح
         *    - اذا فشل: يعرض Toast فشل, ويحط رسالة الخطأ على حقل الايميل
         *
         * ملاحظة
         * القيمة الراجعة من الدالة ترجع صحة المدخلات فقط
         * نجاح التسجيل على Firebase يتحدد لاحقا داخل onComplete
         *
         * @return true اذا المدخلات سليمة, false اذا في اخطاء تحقق
         */
        private boolean validateAndInsertRecord() {

            // قراءة المدخلات مع trim لتفادي مسافات بالبداية او النهاية
            String email = etEmail2.getText().toString().trim();
            String password = etPassword2.getText().toString().trim();

            boolean isValid = true;

            // فحص ان الايميل مش فاضي
            if (TextUtils.isEmpty(email)) {
                etEmail2.setError("Email is required");
                isValid = false;
            }

            // فحص صيغة الايميل
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail2.setError("Invalid email");
                isValid = false;
            }

            // فحص طول الباسورد
            if (password.length() < 6) {
                etPassword2.setError("Password must be at least 6 characters long");
                isValid = false;
            }

            // اذا البيانات سليمة, يبدأ تسجيل المستخدم على Firebase
            if (isValid) {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            /**
                             * onComplete
                             * يتم استدعاؤها بعد انتهاء طلب Firebase
                             * task يحمل نتيجة العملية
                             */
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // نجاح عملية التسجيل على Firebase
                                if (task.isSuccessful()) {

                                    // إنشاء كائن المستخدم لتخزينه محليا في Room
                                    MyCinemaUser myuser = new MyCinemaUser();
                                    myuser.setEmail(email);
                                    myuser.setPassword(password);
                                    myuser.setRole("myuser");
                                   myuser.setKey( task.getResult().getUser().getUid());
                                    saveOrUpdateUserToFirebase(myuser);

                                    // إدخال المستخدم في قاعدة البيانات المحلية
                                    AppDatabase.getDb(SignUpActivity.this)
                                            .myCinemaUserQuery()
                                            .insertUser(myuser);

                                    Toast.makeText(SignUpActivity.this,
                                            "Signing up Succeeded",
                                            Toast.LENGTH_SHORT).show();

                                    // اغلاق الشاشة بعد نجاح العملية
                                    Intent intent = new Intent(SignUpActivity.this, SaveProfileActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    // فشل عملية التسجيل
                                    Toast.makeText(SignUpActivity.this,
                                            "Signing up Failed",
                                            Toast.LENGTH_SHORT).show();

                                    // عرض سبب الفشل على حقل الايميل
                                    etEmail2.setError(task.getException().getMessage());
                                }
                            }
                        });
            }

            return isValid;
        }
    private void saveOrUpdateUserToFirebase(MyCinemaUser user) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        String key="";
        if(user.getKey()==null || user.getKey().isEmpty())
        {
            key = myRef.push().getKey();
            user.setKey(key);
        }



        myRef.child(user.getKey()).setValue(user).addOnCompleteListener(fbTask -> {
            if (fbTask.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "User Saved Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Saving Failed", Toast.LENGTH_SHORT).show();
            }


        });
    }

    }