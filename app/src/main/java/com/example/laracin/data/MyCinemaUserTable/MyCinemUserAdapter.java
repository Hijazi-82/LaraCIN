package com.example.laracin.data.MyCinemaUserTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laracin.R;

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
public class MyCinemUserAdapter extends ArrayAdapter<MyCinemaUser> {

    // layout الخاص بكل item داخل القائمة
    private int itemLayout;

    /**
     * constructor
     *
     * @param context سياق الشاشة اللي بتعرض القائمة
     * @param resource layout resource لكل عنصر داخل القائمة
     */
    public MyCinemUserAdapter(@NonNull Context context, int resource) {
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

        TextView tvltmTitle = convertView.findViewById(R.id.tvltmTitle);
        TextView tvltmText = convertView.findViewById(R.id.tvltmText);
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
            tvltmTitle.setText(user.getFullName());
            tvltmText.setText(user.getEmail());
            tvltmNote.setText(user.getRole());
        }

        return convertView;
    }
}