package com.example.laracin.data.MyCinemaUserTable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laracin.R;

import java.util.List;

/**
 * MyLinksAdapter
 *
 * Adapter مخصص لعرض روابط الأعمال داخل ListView.
 *
 * وظيفة الكلاس:
 * 1. ربط بيانات روابط الأعمال مع تصميم item_work.xml.
 * 2. عرض اسم العمل، نوع العمل، وصف العمل، ورابط العمل.
 * 3. فتح رابط العمل في المتصفح عند الضغط عليه.
 */
public class MyLinksAdapter extends ArrayAdapter<MyCinemaUser> {

    // الشاشة أو الـ Activity التي يعمل داخلها الـ Adapter
    private final Context context;

    // ملف XML الخاص بشكل العنصر الواحد داخل القائمة
    private final int resource;

    /**
     * Constructor
     *
     * يستقبل الشاشة الحالية، تصميم العنصر الواحد،
     * وقائمة المستخدمين الذين لديهم روابط أعمال.
     *
     * @param context الشاشة الحالية
     * @param resource تصميم item الواحد داخل ListView
     * @param objects قائمة المستخدمين الذين سيتم عرض روابط أعمالهم
     */
    public MyLinksAdapter(@NonNull Context context,
                          int resource,
                          @NonNull List<MyCinemaUser> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    /**
     * getView
     *
     * يتم استدعاؤها لكل عنصر داخل ListView.
     * تربط بيانات MyCinemaUser مع عناصر التصميم item_work.xml.
     *
     * @param position موقع العنصر داخل القائمة
     * @param convertView View قديمة يمكن إعادة استخدامها
     * @param parent القائمة الأصلية
     * @return View جاهزة للعرض
     */
    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        // إنشاء View جديدة إذا لم تكن هناك View جاهزة لإعادة الاستخدام
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(context)
                    .inflate(resource, parent, false);
        }

        // ربط عناصر الواجهة داخل item_work.xml
        TextView tvWorkName = convertView.findViewById(R.id.tvWorkName);
        TextView tvWorkType = convertView.findViewById(R.id.tvWorkType);
        TextView tvWorkDescription = convertView.findViewById(R.id.tvWorkDescription);
        TextView tvWorkLink = convertView.findViewById(R.id.tvWorkLink);

        // جلب المستخدم الحالي حسب موقعه في القائمة
        MyCinemaUser user = getItem(position);

        if (user != null) {

            // عرض بيانات العمل داخل العنصر
            tvWorkName.setText(user.getWorkName());
            tvWorkType.setText(user.getWorkType());
            tvWorkDescription.setText(user.getWorkDescription());
            tvWorkLink.setText(user.getWorkLink());

            // فتح الرابط في المتصفح عند الضغط عليه
            tvWorkLink.setOnClickListener(v -> openWorkLink(user.getWorkLink()));
        }

        return convertView;
    }

    /**
     * openWorkLink
     *
     * تفحص رابط العمل، ثم تفتحه في المتصفح.
     * إذا لم يبدأ الرابط بـ http أو https، يتم إضافة https تلقائيًا.
     *
     * @param link رابط العمل المراد فتحه
     */
    private void openWorkLink(String link) {

        if (link == null || link.trim().isEmpty()) {
            Toast.makeText(context, "No link available", Toast.LENGTH_SHORT).show();
            return;
        }

        link = link.trim();

        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "https://" + link;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(intent);
    }
}