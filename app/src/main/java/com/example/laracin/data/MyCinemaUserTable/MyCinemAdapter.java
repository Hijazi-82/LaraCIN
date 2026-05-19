package com.example.laracin.data.MyCinemaUserTable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laracin.ProfileActivity;
import com.example.laracin.R;
import com.example.laracin.data.AppDatabase;

/**
 * MyCinemAdapter
 *
 * Adapter مخصص لعرض مستخدمي التطبيق داخل ListView.
 *
 * وظيفة الكلاس:
 * 1. عرض بيانات كل مستخدم داخل item خاص.
 * 2. تعبئة الاسم، الدور، والمهارات داخل عناصر الواجهة.
 * 3. فتح صفحة ProfileActivity عند الضغط على زر الملاحظة/القلم.
 * 4. إضافة أو إزالة المستخدم من المفضلة عند الضغط على النجمة.
 */
public class MyCinemAdapter extends ArrayAdapter<MyCinemaUser> {

    // ملف تصميم العنصر الواحد داخل القائمة
    private int itemLayout;

    /**
     * Constructor
     *
     * يستقبل الشاشة الحالية وملف تصميم العنصر الواحد.
     *
     * @param context الشاشة التي يتم عرض القائمة داخلها
     * @param resource ملف XML الخاص بشكل كل item
     */
    public MyCinemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;
    }

    /**
     * getView
     *
     * يتم استدعاؤها لكل عنصر داخل ListView.
     * تربط بيانات المستخدم مع عناصر التصميم actor_item_layout.
     *
     * @param position مكان المستخدم داخل القائمة
     * @param convertView View قديمة يمكن إعادة استخدامها
     * @param parent القائمة الأصلية ListView
     * @return View جاهزة للعرض داخل القائمة
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // إذا لم تكن هناك View جاهزة، يتم إنشاء واحدة جديدة من ملف التصميم
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(itemLayout, parent, false);
        }

        // جلب المستخدم حسب موقعه في القائمة
        MyCinemaUser user = getItem(position);

        // ربط عناصر الواجهة داخل item
        ImageView imgUser = convertView.findViewById(R.id.imCinemaUser);

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvUserRole = convertView.findViewById(R.id.tvUserRole);
        TextView tvltmNote = convertView.findViewById(R.id.tvltmNote);

        ImageButton imgBtnSend = convertView.findViewById(R.id.imgBtnSend);
        ImageButton imgBtnCall = convertView.findViewById(R.id.imgBtnCall);
        ImageButton imgBtnNote = convertView.findViewById(R.id.imgBtnNote);
        ImageButton imgBtnStar = convertView.findViewById(R.id.imgBtnStar);

        if (user != null) {

            // تعبئة بيانات المستخدم في الواجهة
            tvUserName.setText(user.getFullName());
            tvUserRole.setText(user.getRole());
            tvltmNote.setText(user.getSkills());

            // فتح صفحة البروفايل الخاصة بالمستخدم
            imgBtnNote.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("cinmaUser", user);
                getContext().startActivity(intent);
            });

            // ضبط شكل النجمة حسب حالة المفضلة
            if (user.isFavorite()) {
                imgBtnStar.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                imgBtnStar.setImageResource(android.R.drawable.btn_star_big_off);
            }

            // تغيير حالة المفضلة عند الضغط على النجمة
            imgBtnStar.setOnClickListener(v -> {
                user.setFavorite(!user.isFavorite());

                AppDatabase.getDb(getContext())
                        .myCinemaUserQuery()
                        .updateUser(user);

                if (user.isFavorite()) {
                    imgBtnStar.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    imgBtnStar.setImageResource(android.R.drawable.btn_star_big_off);
                }
            });
        }

        return convertView;
    }
}