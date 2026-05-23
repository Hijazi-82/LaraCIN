package com.example.laracin;

// لإخفاء تحذير لو Android Studio قال إن في id مش واضح
import android.annotation.SuppressLint;

// Intent للتنقل بين الشاشات أو تشغيل Service
import android.content.Intent;

// Bitmap للتعامل مع الصور بعد تحويلها من Uri
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// Uri يمثل مسار الصورة المختارة من الهاتف
import android.net.Uri;

// Bundle لتشغيل الشاشة واستقبال البيانات
import android.os.Bundle;

// TextUtils لفحص إذا النص فارغ
import android.text.TextUtils;

// Base64 لتحويل الصورة إلى نص وحفظها
import android.util.Base64;

// Patterns لفحص رقم الهاتف
import android.util.Patterns;

// View عشان نتحكم بظهور الصورة
import android.view.View;

// عناصر الواجهة
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// لتفعيل تصميم EdgeToEdge
import androidx.activity.EdgeToEdge;

// لاختيار صورة من الجهاز بطريقة حديثة
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

// نوع الشاشة الأساسي
import androidx.appcompat.app.AppCompatActivity;

// قاعدة البيانات المحلية Room
import com.example.laracin.data.AppDatabase;

// Service مسؤولة عن حفظ المستخدم في Firebase
import com.example.laracin.data.MyCinemaUserTable.CinemaUserService;

// موديل المستخدم
import com.example.laracin.data.MyCinemaUserTable.MyCinemaUser;

// TextInputEditText من Material Design
import com.google.android.material.textfield.TextInputEditText;

// FirebaseAuth لمعرفة المستخدم المسجل دخوله
import com.google.firebase.auth.FirebaseAuth;

// لتحويل الصورة إلى bytes
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * SaveProfileActivity
 *
 * هذه الشاشة مسؤولة عن إنشاء أو تعديل بروفايل المستخدم.
 *
 * ماذا تعمل؟
 * 1. تعرض بيانات المستخدم القديمة إذا كان داخل من Profile.
 * 2. تسمح للمستخدم بتعديل الاسم، الهاتف، الدور، الخبرة، المهارات.
 * 3. تسمح باختيار صورة من الهاتف.
 * 4. تحول الصورة إلى Base64 حتى تنحفظ كنص.
 * 5. تحدث بيانات المستخدم في Room.
 * 6. ترسل المستخدم إلى CinemaUserService حتى يتم تحديثه في Firebase بدون حذف works.
 */
public class SaveProfileActivity extends AppCompatActivity {

    // زر الحفظ أو التحديث
    private Button btnSignUp;

    // حقول إدخال بيانات البروفايل
    private TextInputEditText  etFullName ,etPhone,etPortfolio,etExperienceYears, etSkills;

    // قائمة لاختيار دور المستخدم مثل Actor أو Editor
    private AutoCompleteTextView acRole;

    // نص ينقل المستخدم إلى شاشة تسجيل الدخول
    private TextView tvSignIn;

    // ImageView لعرض صورة البروفايل
    private ImageView ivSelectedImage;

    // يخزن مسار الصورة التي اختارها المستخدم
    private Uri selectedImageUri;

    // أداة اختيار صورة من الهاتف
    private ActivityResultLauncher<String> pickImage;

    // FirebaseAuth لمعرفة الحساب الحالي
    private FirebaseAuth auth;

    // المستخدم الذي وصل من ProfileActivity عند التعديل
    private MyCinemaUser cinmaUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // يجعل الشاشة تمتد خلف شريط الحالة والتنقل
        EdgeToEdge.enable(this);

        // ربط الشاشة بملف XML
        setContentView(R.layout.activity_save_profile);

        // تجهيز FirebaseAuth
        auth = FirebaseAuth.getInstance();

        /*
         * نفحص إذا وصلنا مستخدم من الشاشة السابقة.
         * إذا وصل cinmaUser، معناها المستخدم داخل يعدل بروفايله القديم.
         */
        if (getIntent() != null
                && getIntent().getExtras() != null
                && getIntent().getExtras().get("cinmaUser") != null) {

            cinmaUser = (MyCinemaUser) getIntent().getExtras().get("cinmaUser");
        }

        // ربط عناصر XML مع Java
        btnSignUp = findViewById(R.id.btCreatePro);
        etFullName = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etPortfolio = findViewById(R.id.etPortfolio);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSkills = findViewById(R.id.etSkills);
        acRole = findViewById(R.id.acRole);
        tvSignIn = findViewById(R.id.tvIfSignIn);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        // تجهيز قائمة الأدوار
        setupRoleDropDown();
        // تجهيز اختيار الصورة
        setupImagePicker();
        // عند الضغط على زر الحفظ، افحص البيانات واحفظها
        btnSignUp.setOnClickListener(v -> validateAndSaveProfile());
        // إذا المستخدم داخل تعديل، اعرض بياناته القديمة
        loadOldUserData();

        // الانتقال إلى SignInActivity عند الضغط على النص
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SaveProfileActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    /**
     * setupRoleDropDown
     *
     * هذه الدالة تجهز قائمة الأدوار داخل acRole.
     * المستخدم يضغط على الحقل فتظهر له قائمة أدوار جاهزة.
     */
    private void setupRoleDropDown() {
        // مصفوفة فيها الأدوار التي يستطيع المستخدم اختيارها
        String[] roles = {
                "Director",
                "Actor",
                "Producer",
                "Editor",
                "Cinematographer",
                "Screenwriter",
                "Sound Designer"
        };
        /*
         * ArrayAdapter يربط مصفوفة roles مع AutoCompleteTextView.
         * simple_dropdown_item_1line هو شكل جاهز من Android لعناصر القائمة.
         */
        ArrayAdapter<String> roleAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        roles
                );

        // وضع الـ adapter داخل acRole
        acRole.setAdapter(roleAdapter);

        // عند الضغط على الحقل، افتح القائمة
        acRole.setOnClickListener(v -> acRole.showDropDown());
    }

    /**
     * setupImagePicker
     *
     * هذه الدالة تجهز اختيار صورة من الهاتف.
     * بعد اختيار الصورة، يتم عرضها داخل ivSelectedImage.
     */
    private void setupImagePicker() {
        /*
         * registerForActivityResult هي الطريقة الحديثة لاختيار ملف من الجهاز.
         * GetContent يعني اختار محتوى من الهاتف مثل صورة.
         */
        pickImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),

                // result هو Uri الصورة التي اختارها المستخدم
                result -> {
                    if (result != null) {

                        // نخزن مسار الصورة في selectedImageUri
                        selectedImageUri = result;

                        // نعرض الصورة في ImageView
                        ivSelectedImage.setImageURI(result);

                        // نتأكد أن الصورة ظاهرة
                        ivSelectedImage.setVisibility(View.VISIBLE);
                    }
                }
        );

        // عند الضغط على الصورة، افتح معرض الصور
        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));
    }

    /**
     * loadOldUserData
     *
     * إذا المستخدم دخل من ProfileActivity للتعديل،
     * هذه الدالة تعرض بياناته القديمة داخل الحقول.
     */
    private void loadOldUserData() {

        // إذا ما وصل مستخدم، لا تعمل شيء
        if (cinmaUser == null) {
            return;
        }

        // وضع بيانات المستخدم القديمة داخل الحقول
        etFullName.setText(cinmaUser.getFullName());
        etPhone.setText(cinmaUser.getPhone());
        acRole.setText(cinmaUser.getRole(), false);
        etPortfolio.setText(cinmaUser.getPortfolio());
        etExperienceYears.setText(String.valueOf(cinmaUser.getExperienceYears()));
        etSkills.setText(cinmaUser.getSkills());

        // تغيير نص الزر من إنشاء إلى تحديث
        btnSignUp.setText("Update");

        // تحويل صورة Base64 القديمة إلى Bitmap
        Bitmap bitmap = stringToBitmap(cinmaUser.getProfileImageUri());

        // إذا الصورة موجودة، اعرضها
        if (bitmap != null) {
            ivSelectedImage.setImageBitmap(bitmap);
            ivSelectedImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * validateAndSaveProfile
     *
     * هذه أهم دالة في الشاشة.
     *
     * تعمل:
     * 1. تقرأ القيم من الحقول.
     * 2. تفحص إذا البيانات صحيحة.
     * 3. تجيب المستخدم الحالي حسب الإيميل من Room.
     * 4. تحدث بياناته.
     * 5. تحفظ الصورة إذا تم اختيار صورة جديدة.
     * 6. تحدث المستخدم في Room.
     * 7. ترسله إلى Service للحفظ في Firebase.
     */
    private boolean validateAndSaveProfile() {

        // قراءة النصوص من الحقول
        String fullName = getText(etFullName);
        String phone = getText(etPhone);
        String role = acRole.getText().toString().trim();
        String portfolio = getText(etPortfolio);
        String experienceYearsText = getText(etExperienceYears);
        String skills = getText(etSkills);

        // متغير يحدد إذا البيانات صحيحة أو لا
        boolean isValid = true;

        // فحص الاسم
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            isValid = false;
        }

        // فحص الهاتف: ممنوع يكون فاضي ولازم يكون شكله رقم هاتف
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone is required");
            isValid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }

        // فحص الدور
        if (TextUtils.isEmpty(role)) {
            acRole.setError("Role is required");
            isValid = false;
        }

        // إذا في خطأ، وقف الحفظ
        if (!isValid) {
            return false;
        }

        // نخزن إيميل المستخدم الحالي
        String currentEmail = null;

        // إذا في مستخدم مسجل دخوله، نأخذ الإيميل
        if (auth.getCurrentUser() != null) {
            currentEmail = auth.getCurrentUser().getEmail();
        }

        // إذا الإيميل فاضي، لا يمكن نعرف مين المستخدم
        if (TextUtils.isEmpty(currentEmail)) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*
         * نبحث عن المستخدم داخل Room حسب الإيميل.
         * هذا مهم حتى نعدل نفس المستخدم وليس ننشئ مستخدم جديد.
         */
        MyCinemaUser user =
                AppDatabase.getDb(this)
                        .myCinemaUserQuery()
                        .getUserByEmail(currentEmail);

        // إذا لم نجده، نوقف العملية
        if (user == null) {
            Toast.makeText(this, "User not found in local database", Toast.LENGTH_SHORT).show();
            return false;
        }

        // تحويل سنوات الخبرة من String إلى int
        int experienceYears = 0;

        if (!TextUtils.isEmpty(experienceYearsText)) {
            try {
                experienceYears = Integer.parseInt(experienceYearsText);
            } catch (NumberFormatException e) {
                etExperienceYears.setError("Invalid experience years");
                return false;
            }
        }

        // تحديث بيانات المستخدم داخل object
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(role);
        user.setPortfolio(portfolio);
        user.setExperienceYears(experienceYears);
        user.setSkills(skills);

        /*
         * إذا المستخدم اختار صورة جديدة:
         * نحولها إلى Base64 ونحفظها داخل profileImageUri.
         *
         * إذا لم يختر صورة:
         * لا نلمس الصورة القديمة.
         */
        if (selectedImageUri != null) {

            String imageString = convertImageToString(selectedImageUri);

            if (imageString != null) {
                user.setProfileImageUri(imageString);
            }
        }

        // تحديث المستخدم في قاعدة البيانات المحلية Room
        AppDatabase.getDb(this)
                .myCinemaUserQuery()
                .updateUser(user);

        /*
         * تشغيل CinemaUserService.
         *
         * السبب:
         * لا نحفظ Firebase مباشرة من هذه الشاشة.
         * نرسل المستخدم إلى Service حتى تحفظه في Firebase بطريقة updateChildren.
         * هذه الطريقة تحافظ على works ولا تحذف أعمال المستخدم.
         */
        Intent serviceIntent = new Intent(this, CinemaUserService.class);
        serviceIntent.putExtra(CinemaUserService.EXTRA_USER, user);
        startService(serviceIntent);

        // رسالة نجاح
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

        // إغلاق الشاشة والرجوع للشاشة السابقة
        finish();

        return true;
    }

    /**
     * getText
     *
     * دالة صغيرة تساعدنا نقرأ النص بأمان من TextInputEditText.
     * بدل ما نكرر getText().toString().trim() كل مرة.
     */
    private String getText(TextInputEditText editText) {

        // إذا الحقل فارغ أو getText رجع null، رجع نص فاضي
        if (editText.getText() == null) {
            return "";
        }

        // رجع النص بعد حذف الفراغات من البداية والنهاية
        return editText.getText().toString().trim();
    }

    /**
     * convertImageToString
     *
     * تحول الصورة من Uri إلى Base64 String.
     *
     * ليش؟
     * لأن Firebase و Room يقدروا يخزنوا النص بسهولة،
     * فبنحول الصورة إلى نص ونحفظها داخل profileImageUri.
     */
    public String convertImageToString(Uri uri) {

        try {
            // فتح الصورة من الهاتف كـ InputStream
            InputStream inputStream = getContentResolver().openInputStream(uri);

            // إذا فشل فتح الصورة
            if (inputStream == null) {
                Toast.makeText(this, "Failed to open image", Toast.LENGTH_SHORT).show();
                return null;
            }

            // تحويل InputStream إلى Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // إذا فشل تحويل الصورة
            if (bitmap == null) {
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                return null;
            }

            // ByteArrayOutputStream يخزن الصورة على شكل bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            /*
             * ضغط الصورة بصيغة JPEG وبجودة 40.
             * الهدف أن لا تكون الصورة كبيرة جدًا في Firebase.
             */
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);

            // تحويل الصورة المضغوطة إلى byte array
            byte[] imageBytes = outputStream.toByteArray();

            // تحويل bytes إلى Base64 String وإرجاعه
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {

            // إذا صار أي خطأ أثناء التحويل
            Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * stringToBitmap
     *
     * تحول Base64 String إلى Bitmap.
     *
     * نستخدمها عندما نريد عرض الصورة القديمة داخل ImageView.
     */
    private Bitmap stringToBitmap(String imageString) {

        // إذا النص فاضي، لا يوجد صورة
        if (imageString == null || imageString.isEmpty()) {
            return null;
        }

        try {
            // تحويل Base64 String إلى bytes
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);

            // تحويل bytes إلى Bitmap وعرضها لاحقًا
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        } catch (Exception e) {

            // إذا فشل التحويل، نرجع null بدل ما ينهار التطبيق
            return null;
        }
    }
}