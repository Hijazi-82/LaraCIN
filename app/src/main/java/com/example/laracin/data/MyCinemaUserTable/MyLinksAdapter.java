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
 * الهدف:
 * هذا الكلاس يربط بين بيانات روابط الأعمال الموجودة داخل MyCinemaUser
 * وبين ملف التصميم item_work.xml.
 *
 * كل item يعرض:
 * - اسم العمل
 * - نوع العمل
 * - وصف العمل
 * - رابط العمل
 */
public class MyLinksAdapter extends ArrayAdapter<MyCinemaUser> {

    /**
     * context
     * يمثل الشاشة التي يعمل داخلها الـ Adapter.
     */
    private final Context context;

    /**
     * resource
     * يمثل ملف XML الخاص بشكل العنصر الواحد.
     * غالبًا يكون R.layout.item_work.
     */
    private final int resource;

    /**
     * constructor
     *
     * @param context الشاشة الحالية
     * @param resource تصميم item الواحد داخل ListView
     * @param objects قائمة المستخدمين الذين لديهم روابط أعمال
     */
    public MyLinksAdapter(@NonNull Context context, int resource, @NonNull List<MyCinemaUser> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    /**
     * getView
     *
     * يتم استدعاؤها لكل عنصر داخل ListView.
     * تربط بيانات MyCinemaUser مع item_work.xml.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvWorkName = convertView.findViewById(R.id.tvWorkName);
        TextView tvWorkType = convertView.findViewById(R.id.tvWorkType);
        TextView tvWorkDescription = convertView.findViewById(R.id.tvWorkDescription);
        TextView tvWorkLink = convertView.findViewById(R.id.tvWorkLink);

        MyCinemaUser user = getItem(position);

        if (user != null) {

            tvWorkName.setText(user.getWorkName());
            tvWorkType.setText(user.getWorkType());
            tvWorkDescription.setText(user.getWorkDescription());
            tvWorkLink.setText(user.getWorkLink());

            tvWorkLink.setOnClickListener(v -> {
                String link = user.getWorkLink();

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
            });
        }

        return convertView;
    }
}