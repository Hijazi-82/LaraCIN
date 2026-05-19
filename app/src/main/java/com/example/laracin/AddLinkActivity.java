package com.example.laracin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AddLinkActivity
 *
 * شاشة إضافة رابط عمل جديد.
 *
 * وظيفة الشاشة:
 * 1. إدخال اسم العمل.
 * 2. إدخال نوع العمل.
 * 3. إدخال وصف قصير للعمل.
 * 4. إدخال رابط خارجي للعمل.
 * 5. فحص أن الحقول المطلوبة غير فارغة.
 * 6. إرجاع البيانات إلى الشاشة السابقة WorkActivity.
 */
public class AddLinkActivity extends AppCompatActivity {

    // زر الرجوع إلى الشاشة السابقة
    private ImageView btnBackToLinks;

    // حقول إدخال بيانات العمل
    private EditText etWorkName;
    private EditText etWorkType;
    private EditText etWorkDescription;
    private EditText etWorkLink;

    // زر حفظ الرابط
    private Button btnSaveLink;

    /**
     * onCreate
     *
     * يتم استدعاؤها عند فتح شاشة إضافة الرابط.
     * داخلها يتم ربط عناصر الواجهة، وتجهيز زر الرجوع وزر الحفظ.
     *
     * @param savedInstanceState يحفظ حالة الشاشة عند إعادة إنشائها
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ربط الكلاس بملف تصميم الشاشة
        setContentView(R.layout.activity_add_link);

        // ربط عناصر الواجهة مع المتغيرات
        btnBackToLinks = findViewById(R.id.btnBackToLinks);
        etWorkName = findViewById(R.id.etWorkName);
        etWorkType = findViewById(R.id.etWorkType);
        etWorkDescription = findViewById(R.id.etWorkDescription);
        etWorkLink = findViewById(R.id.etWorkLink);
        btnSaveLink = findViewById(R.id.btnSaveLink);

        // الرجوع إلى الشاشة السابقة بدون حفظ
        btnBackToLinks.setOnClickListener(v -> finish());

        // حفظ بيانات الرابط وإرجاعها إلى WorkActivity
        btnSaveLink.setOnClickListener(v -> saveLinkData());
    }

    /**
     * saveLinkData
     *
     * تقرأ بيانات الرابط من الحقول.
     * إذا كان أحد الحقول فارغًا، تعرض رسالة خطأ.
     * إذا كانت البيانات صحيحة، ترجعها إلى الشاشة السابقة باستخدام Intent.
     */
    private void saveLinkData() {

        String workName = etWorkName.getText().toString().trim();
        String workType = etWorkType.getText().toString().trim();
        String workDescription = etWorkDescription.getText().toString().trim();
        String workLink = etWorkLink.getText().toString().trim();

        // فحص اسم العمل
        if (TextUtils.isEmpty(workName)) {
            etWorkName.setError("Enter work name");
            etWorkName.requestFocus();
            return;
        }

        // فحص نوع العمل
        if (TextUtils.isEmpty(workType)) {
            etWorkType.setError("Enter work type");
            etWorkType.requestFocus();
            return;
        }

        // فحص وصف العمل
        if (TextUtils.isEmpty(workDescription)) {
            etWorkDescription.setError("Enter description");
            etWorkDescription.requestFocus();
            return;
        }

        // فحص رابط العمل
        if (TextUtils.isEmpty(workLink)) {
            etWorkLink.setError("Paste link here");
            etWorkLink.requestFocus();
            return;
        }

        // تجهيز Intent لإرجاع البيانات إلى WorkActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("workName", workName);
        resultIntent.putExtra("workType", workType);
        resultIntent.putExtra("workDescription", workDescription);
        resultIntent.putExtra("workLink", workLink);

        // إرسال النتيجة وإغلاق الشاشة
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Link saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}