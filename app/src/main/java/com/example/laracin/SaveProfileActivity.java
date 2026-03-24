package com.example.laracin;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laracin.data.AppDatabase;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUserQuery;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.core.content.ContextCompat;

/**
 * SaveProfileActivity
 * شاشة استكمال وحفظ ملف المستخدم بعد التسجيل
 *
 * المسؤوليات
 * 1 قراءة بيانات البروفايل من الواجهة, مثل الاسم, الهاتف, الدور, بورتفوليو, خبرة, مهارات
 * 2 التحقق من صحة بعض المدخلات الاساسية
 * 3 إنشاء كائن MyCinemaUser وتجهيز حقول البروفايل
 * 4 حفظ بيانات البروفايل داخل Firebase Realtime Database تحت عقدة CinemaProfiles
 * 5 عرض رسائل نجاح او فشل للمستخدم
 */
public class SaveProfileActivity extends AppCompatActivity {

    // زر حفظ بيانات البروفايل
    private Button btnSignUp;

    // حقول إدخال بيانات البروفايل
    private TextInputEditText etFullName, etPhone, etPortfolio, etExperienceYears, etSkills;

    // اختيار الدور من قائمة
    private AutoCompleteTextView acRole;

    // نص للتنقل للشاشة الرئيسية
    private TextView tvSignIn;

    // زر صورة بالواجهة, موجود بالتصميم (غير مستخدم بالكود الحالي)
    private ImageButton   ivSelectedImage;

    private Uri selectedImageUri;//صفة لحفظ عنوان الصورة بعد اختيارها
    private ActivityResultLauncher<String> pickImage;// ‏كائن لطلب الصورة من الهاتف


    private ActivityResultLauncher<String> requestReadMediaImagesPermission;
    private ActivityResultLauncher<String> requestReadMediaVideoPermission;
    private ActivityResultLauncher<String> requestReadExternalStoragePermission;



    // دالة لفحص وطلب الأذونات
    private void checkAndRequestPermissions() {
        // فحص وطلب إذن READ_MEDIA_IMAGES (للإصدارات الحديثة)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // أندرويد 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadMediaImagesPermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                Log.d(TAG, "READ_MEDIA_IMAGES permission already granted");
                Toast.makeText(this, "إذن قراءة الصور ممنوح بالفعل", Toast.LENGTH_SHORT).show();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // أندرويد 10 و 11 و 12// على هذه الإصدارات، READ_EXTERNAL_STORAGE له سلوك مختلف
            // إذا كنت تستخدم Scoped Storage بشكل صحيح، قد لا تحتاج إلى هذا الإذن
            // ولكن إذا كنت تحتاج إلى الوصول إلى جميع الصور، فقد تحتاج إلى READ_EXTERNAL_STORAGE
            // في هذا المثال، سنفحص READ_EXTERNAL_STORAGE للإصدارات الأقدم من 13
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadExternalStoragePermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                Log.d(TAG, "READ_EXTERNAL_STORAGE permission already granted (for older versions)");
                Toast.makeText(this, "إذن قراءة التخزين ممنوح بالفعل (للإصدارات الأقدم)", Toast.LENGTH_SHORT).show();
            }
        } else { // أندرويد 9 والإصدارات الأقدم
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadExternalStoragePermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                Log.d(TAG, "READ_EXTERNAL_STORAGE permission already granted (for older versions)");
                Toast.makeText(this, "إذن قراءة التخزين ممنوح بالفعل (للإصدارات الأقدم)", Toast.LENGTH_SHORT).show();
            }
        }


        // فحص وطلب إذن READ_MEDIA_VIDEO (للإصدارات الحديثة)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // أندرويد 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadMediaVideoPermission.launch(android.Manifest.permission.READ_MEDIA_VIDEO);
            } else {
                Log.d(TAG, "READ_MEDIA_VIDEO permission already granted");
                Toast.makeText(this, "إذن قراءة الفيديو ممنوح بالفعل", Toast.LENGTH_SHORT).show();
            }
        }// ملاحظة: إذن INTERNET لا يحتاج إلى فحص أو
    }








    // DAO للاستعلام من Room (معلن لكن غير مستخدم فعليا في الكود الحالي)
    /**
     * DAO
     * طبقة الوصول لقاعدة البيانات في Room
     * تحتوي دوال CRUD والاستعلامات الخاصة بجدول معين
     *
     * مثال
     * MyCinemaUserQuery هو DAO لجدول MyCinemaUser
     * - insertUser لاضافة مستخدم
     * - getUserByEmail لجلب مستخدم بالايميل
     * - login للتحقق من ايميل وباسورد
     */
    private MyCinemaUserQuery dao;

    // FirebaseAuth (مهيأ لكن غير مستخدم فعليا في الكود الحالي)
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل edge to edge وتحميل واجهة الشاشة
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_profile);

        requestReadMediaImagesPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d(TAG, "READ_MEDIA_IMAGES permission granted");
                Toast.makeText(this, "تم منح إذن قراءة الصور", Toast.LENGTH_SHORT).show();
                // يمكنك الآن المتابعة بالعملية التي تتطلب هذا الإذن
            } else {
                Log.d(TAG, "READ_MEDIA_IMAGES permission denied");
                Toast.makeText(this, "تم رفض إذن قراءة الصور", Toast.LENGTH_SHORT).show();
                // التعامل مع حالة رفض الإذن
            }
        });
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    selectedImageUri = result;
                    ivSelectedImage.setImageURI(result);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                }
            }
        });

        ivSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage.launch("image/*"); // Launch the image picker
            }
        });





        // تهيئة FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // ربط عناصر الواجهة
        btnSignUp = findViewById(R.id.btSignUp);
        etFullName = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etPortfolio = findViewById(R.id.etPortfolio);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSkills = findViewById(R.id.etSkills);
        acRole = findViewById(R.id.acRole);
        tvSignIn = findViewById(R.id.tvSignIn);
        ivSelectedImage = findViewById(R.id.  ivSelectedImage);

        /**
         * زر الحفظ
         * عند النقر, يتم التحقق من المدخلات, ثم إنشاء كائن المستخدم وحفظه في Firebase Realtime Database
         */
        btnSignUp.setOnClickListener(v -> validateAndInsertRecord());

        /**
         * tvSignIn
         * حسب الكود الحالي, ينقل للشاشة الرئيسية Activity_main1
         */
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SaveProfileActivity.this, Activity_main1.class);
            startActivity(intent);
        });
    }

    /**
     * validateAndInsertRecord
     *
     * الهدف
     * قراءة بيانات البروفايل من الحقول, التحقق من الاساسيات, ثم تجهيز كائن MyCinemaUser وحفظه
     *
     * ملاحظات عن التحقق الحالي
     * - يتحقق من ان الاسم غير فارغ
     * - يتحقق من رقم الهاتف باستخدام Patterns.PHONE
     * - يوجد سطر "التحقق من وجود الإيميل في Room" لكنه فعليا يستعلم بالايميل عبر phone وهذا غير متناسق بالكلام
     *   والنتيجة user لا تستخدم بعدين, يعني التحقق هذا حاليا بلا تأثير
     *
     * @return true اذا التحقق الاساسي نجح, false اذا في اخطاء
     */
    private boolean validateAndInsertRecord() {

        // قراءة المدخلات
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = acRole.getText().toString().trim();
        String portfolio = etPortfolio.getText().toString().trim();
        String experienceYears = etExperienceYears.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();
        String imageButton2 =ivSelectedImage.toString().trim();

        boolean isValid = true;

        // تحقق الاسم
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            isValid = false;
        }

        // تحقق الهاتف
        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }

        /**
         * استعلام من Room
         * حسب اسم الدالة getUserByEmail المفروض يدخل ايميل
         * لكن الكود يرسل phone, فهنا في عدم تطابق بالتسمية والاستخدام
         * النتيجة user لا تستخدم لاحقا
         */
        MyCinemaUser user =
                AppDatabase.getDb(this).myCinemaUserQuery().getUserByEmail(phone);

        // اذا البيانات سليمة, جهز كائن المستخدم واحفظه
        if (isValid) {
            MyCinemaUser user1 = new MyCinemaUser();
            user1.setPhone(phone);
            user1.setFullName(fullName);
            user1.setRole(role);
            user1.setPortfolio(portfolio);

            // تجربة خبرة السنوات موجودة لكن معلقة
            // user1.setExperienceYears(experienceYears);

            user1.setSkills(skills);

            // حفظ البروفايل على Firebase Realtime Database
            saveCinemaUser(user1);
        }

        return isValid;
    }

    /**
     * saveCinemaUser
     *
     * الهدف
     * حفظ كائن MyCinemaUser داخل Firebase Realtime Database
     *
     * خطوات العمل
     * 1 الحصول على مرجع قاعدة البيانات
     * 2 تحديد العقدة CinemaProfiles لتخزين بروفايلات المستخدمين
     * 3 إنشاء سجل جديد بمفتاح فريد باستخدام push
     * 4 تخزين بيانات المستخدم عبر setValue
     * 5 التعامل مع نجاح وفشل العملية عبر listeners
     *
     * ملاحظة مهمة عن الكود الحالي
     * - الكود يحاول تحويل key الناتج من push الى Long عبر Long.parseLong
     *   عادة مفاتيح push ليست ارقام فقط, ممكن تكون حروف وارقام, وهذا ممكن يسبب خطأ
     * - داخل هذه الدالة تم وضع btnSignUp.setOnClickListener مرة ثانية, وهذا تكرار غير منطقي
     *   وبيعمل ان الزر يتغير سلوكه بعد الاستدعاء, وبيعمل recursion محتمل عبر saveCinemaUser
     *
     * @param user كائن المستخدم المراد حفظه
     */
    public void saveCinemaUser(MyCinemaUser user) {

        // مرجع قاعدة البيانات
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // مرجع عقدة البروفايلات
        DatabaseReference usersRef = database.child("CinemaProfiles");

        // إنشاء سجل جديد بمفتاح فريد
        DatabaseReference newUserRef = usersRef.push();

        // تعيين keyId داخل كائن المستخدم
        user.setKeyId(Long.parseLong(newUserRef.getKey()));

        // حفظ بيانات المستخدم
        newUserRef.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(SaveProfileActivity.this,
                                "Succeeded to add User",
                                Toast.LENGTH_SHORT).show();

                        // اغلاق الشاشة بعد النجاح
                        finish();

                        // سجل للنجاح
                        Log.d(TAG, "تم حفظ المستخدم بنجاح: " + user.getKeyId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // سجل للفشل
                        Log.e(TAG, "خطأ في حفظ المستخدم: " + e.getMessage(), e);

                        Toast.makeText(SaveProfileActivity.this,
                                "Failed to add User",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        /**
         * ملاحظة
         * هذا listener موجود داخل saveCinemaUser في الكود الاصلي
         * وهو يعيد تعيين btnSignUp click ويستدعي saveCinemaUser مرة ثانية
         * هذا سلوك غير معتاد وقد يسبب تكرار حفظ او مشاكل منطقية
         */
        btnSignUp.setOnClickListener(v -> {
            String name = etFullName.getText().toString();
            String phone = etPhone.getText().toString();
            String role = acRole.getText().toString();
            String portfolio = etPortfolio.getText().toString();
            String experienceYears = etExperienceYears.getText().toString();
            String skills = etSkills.getText().toString();

            if (!name.isEmpty()) {
                MyCinemaUser newUser = new MyCinemaUser();

                // استدعاء الحفظ مرة ثانية حسب الكود الحالي
                saveCinemaUser(newUser);

                // مسح حقل الاسم فقط حسب الكود الحالي
                etFullName.setText("");
            } else {
                Log.w(TAG, "الرجاء إدخال الاسم والبريد الإلكتروني.");
            }
        });
    }
}