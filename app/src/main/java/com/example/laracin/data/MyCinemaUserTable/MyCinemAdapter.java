package com.example.laracin.data.MyCinemaUserTable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * MyCinemAdapter
 *
 * Adapter مخصص لعرض المستخدمين داخل ListView.
 *
 * الوظائف:
 * 1. عرض صورة المستخدم واسمه ودوره ومهاراته.
 * 2. فتح بروفايل المستخدم عند الضغط على زر القلم.
 * 3. إضافة أو إزالة المستخدم من المفضلة الخاصة بالمستخدم الحالي.
 *
 * ملاحظة مهمة:
 * المفضلة لا تحفظ داخل users/userKey/favorite
 * بل تحفظ داخل:
 * favorites/currentUserUid/userKey
 * حتى تكون المفضلة خاصة بكل مستخدم.
 */
public class MyCinemAdapter extends ArrayAdapter<MyCinemaUser> {

    // ملف تصميم العنصر الواحد داخل القائمة
    private int itemLayout;

    /**
     * Constructor
     *
     * @param context الشاشة التي تعرض القائمة
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
     * تربط بيانات المستخدم مع عناصر actor_item_layout.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(itemLayout, parent, false);
        }

        MyCinemaUser user = getItem(position);

        ImageView imCinemaUser = convertView.findViewById(R.id.imCinemaUser);

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvUserRole = convertView.findViewById(R.id.tvUserRole);
        TextView tvltmNote = convertView.findViewById(R.id.tvltmNote);

        ImageButton imgBtnSend = convertView.findViewById(R.id.imgBtnSend);
        ImageButton imgBtnCall = convertView.findViewById(R.id.imgBtnCall);
        ImageButton imgBtnNote = convertView.findViewById(R.id.imgBtnNote);
        ImageButton imgBtnStar = convertView.findViewById(R.id.imgBtnStar);

        if (user != null) {

            // تعبئة بيانات المستخدم
            tvUserName.setText(user.getFullName() != null ? user.getFullName() : "");
            tvUserRole.setText(user.getRole() != null ? user.getRole() : "");
            tvltmNote.setText(user.getSkills() != null ? user.getSkills() : "");

            // عرض صورة المستخدم بنفس طريقة التخزين عندك: Base64 داخل profileImageUri
            Bitmap bitmap = stringToBitmap(user.getProfileImageUri());

            if (bitmap != null) {
                imCinemaUser.setImageBitmap(bitmap);
            } else {
                imCinemaUser.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            // فتح بروفايل نفس المستخدم عند الضغط على القلم
            imgBtnNote.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("cinmaUser", user);
                getContext().startActivity(intent);
            });

            // شكل النجمة حسب الحالة الحالية داخل القائمة
            if (user.isFavorite()) {
                imgBtnStar.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                imgBtnStar.setImageResource(android.R.drawable.btn_star_big_off);
            }

            // حفظ المفضلة الخاصة بالمستخدم الحالي داخل Firebase
            imgBtnStar.setOnClickListener(v -> {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    return;
                }

                if (user.getKey() == null || user.getKey().isEmpty()) {
                    return;
                }

                String currentUid = FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid();

                user.setFavorite(!user.isFavorite());

                if (user.isFavorite()) {

                    imgBtnStar.setImageResource(android.R.drawable.btn_star_big_on);

                    FirebaseDatabase.getInstance()
                            .getReference("favorites")
                            .child(currentUid)
                            .child(user.getKey())
                            .setValue(true);

                } else {

                    imgBtnStar.setImageResource(android.R.drawable.btn_star_big_off);

                    FirebaseDatabase.getInstance()
                            .getReference("favorites")
                            .child(currentUid)
                            .child(user.getKey())
                            .removeValue();
                }
            });
        }

        return convertView;
    }

    /**
     * stringToBitmap
     *
     * تحول الصورة من String Base64 إلى Bitmap
     * حتى يتم عرضها داخل ImageView.
     */
    private Bitmap stringToBitmap(String imageString) {

        if (imageString == null || imageString.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            return null;
        }
    }
}