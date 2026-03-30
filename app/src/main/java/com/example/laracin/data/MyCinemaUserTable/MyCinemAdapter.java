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

import com.example.laracin.FavoriteActivity;
import com.example.laracin.R;
import com.example.laracin.data.AppDatabase;

/**
 * MyCinemUserAdapter
 * Adapter مخصص لعرض عناصر MyCinemaUser داخل ListView
 *
 * الفكرة
 * - الكلاس بيمد ArrayAdapter عشان يقدر يعرض قائمة من MyCinemaUser
 * - getView مسؤولة عن بناء او إعادة استخدام view لكل صف داخل القائمة
 * - يتم تعبئة عناصر الصف مثل الاسم والايميل والدور داخل TextViews
 *
 * itemLayout
 * - رقم ال layout resource للعنصر الواحد داخل القائمة, مثال actor_item_layout
 */

public class MyCinemAdapter extends ArrayAdapter<MyCinemaUser> {

    // layout الخاص بكل item داخل القائمة
    private int itemLayout;

    /**
     * constructor
     *
     * @param context سياق الشاشة اللي بتعرض القائمة
     * @param resource layout resource لكل عنصر داخل القائمة
     */
    public MyCinemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;
    }

    /**
     * getView
     * يتم استدعاؤها لكل عنصر في القائمة لتجهيزه وعرضه داخل ListView
     *
     * @param position موقع العنصر بالقائمة
     * @param convertView view جاهزة لاعادة الاستخدام, اذا null يتم عمل inflate جديدة
     * @param parent الـ ListView نفسه
     * @return view جاهزة للعرض
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // اذا ما في view جاهزة, بنبني واحدة جديدة من itemLayout
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
        }

        // جلب المستخدم الحالي حسب position
        MyCinemaUser user = getItem(position);

        // ربط عناصر الواجهة داخل item layout
        ImageView imgUser = convertView.findViewById(R.id.imCinemaUser);

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvUserRole = convertView.findViewById(R.id.tvUserRole);
        TextView tvltmNote = convertView.findViewById(R.id.tvltmNote);

        // ازرار داخل كل item, موجودة بالlayout لكنها غير مستخدمة حاليا بالكود
        ImageButton imgBtnSend = convertView.findViewById(R.id.imgBtnSend);
        ImageButton imgBtnCall = convertView.findViewById(R.id.imgBtnCall);
        ImageButton imgBtnNote = convertView.findViewById(R.id.imgBtnNote);
        ImageButton imgBtnStar = convertView.findViewById(R.id.imgBtnStar);


        /**
         * تعبئة البيانات
         * - العنوان: الاسم الكامل
         * - النص: الايميل
         * - الملاحظة: الدور
         */
        if (user != null) {
            tvUserName.setText(user.getFullName());
            tvUserRole.setText(user.getRole());
            tvltmNote.setText(user.getSkills());
// FOR ITEM STAR
            imgBtnStar.setOnClickListener(v -> {
                user.setFavorite(!user.isFavorite());
                AppDatabase.getDb(getContext()).myCinemaUserQuery().updateUser(user);

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