package com.example.laracin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity2
 * شاشة رئيسية او شاشة قائمة خيارات, فيها عناصر للبحث عن افلام, اضافة فيلم, وعرض قائمتي
 *
 * اللي بيصير حاليا
 * 1 تحميل layout الخاص بالشاشة activity_main2
 * 2 تفعيل edge to edge وضبط padding حسب system bars
 * 3 ربط عناصر الواجهة بالمتغيرات عبر findViewById
 *
 * ملاحظة
 * الكلاس حاليا ما فيه اي منطق او listeners للازرار, بس تجهيز وربط عناصر
 */
public class MainActivity2 extends AppCompatActivity {

    // نصوص بالواجهة
    private TextView tvSearchMovies, header_title, tvMyList;

    // ازرار بالواجهة
    private Button btnSearchMovies, btnAddMovie, btnMyList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل edge to edge
        EdgeToEdge.enable(this);

        // تحميل واجهة الشاشة
        setContentView(R.layout.activity_main2);

        // ضبط padding حسب شريط الحالة والتنقل
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط TextViews
        tvSearchMovies = findViewById(R.id.tvSearchMovies);
        header_title = findViewById(R.id.header_title);
        tvMyList = findViewById(R.id.tvMyList);

        // ربط Buttons
        btnSearchMovies = findViewById(R.id.btnSearchMovies);
        btnAddMovie = findViewById(R.id.btnAddMovie);
        btnMyList = findViewById(R.id.btnMyList);
    }
}