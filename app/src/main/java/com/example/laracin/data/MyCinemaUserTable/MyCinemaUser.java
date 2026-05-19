package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * MyCinemaUser
 *
 * Entity يمثل مستخدم داخل التطبيق.
 *
 * وظيفة الكلاس:
 * 1. تخزين بيانات تسجيل الدخول.
 * 2. تخزين بيانات البروفايل مثل الاسم، الهاتف، الدور والخبرة.
 * 3. تخزين بيانات روابط الأعمال.
 * 4. حفظ حالة المستخدم إذا كان مضافًا للمفضلة.
 * 5. استخدامه داخل Room Database و Firebase.
 */
@Entity
public class MyCinemaUser implements Serializable {

    // المفتاح الأساسي داخل Room، ويتم توليده تلقائيًا
    @PrimaryKey(autoGenerate = true)
    public long keyId;

    // مفتاح خارجي يستخدم غالبًا مع Firebase
    public String key;

    // الاسم الكامل للمستخدم، ويتم تخزينه في Room باسم full_Name
    @ColumnInfo(name = "full_Name")
    public String fullName;

    // بيانات الحساب
    public String email;
    public String password;

    // بيانات التواصل والبروفايل
    public String phone;
    public String role;
    public String portfolio;

    // عدد سنوات الخبرة
    public int experienceYears;

    // مهارات المستخدم
    public String skills;

    // بيانات رابط العمل
    public String workName;
    public String workType;
    public String workDescription;
    public String workLink;

    // هل المستخدم مضاف إلى المفضلة أم لا
    @ColumnInfo(name = "is_favorite")
    private boolean favorite;

    // Getter و Setter لحالة المفضلة
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Getters و Setters الأساسية

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    // Getters و Setters الخاصة بروابط الأعمال

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public String getWorkLink() {
        return workLink;
    }

    public void setWorkLink(String workLink) {
        this.workLink = workLink;
    }

    /**
     * toString
     *
     * تعرض محتوى الكائن كنص.
     * مفيدة أثناء الفحص والتجربة لمعرفة القيم المخزنة داخل المستخدم.
     */
    @Override
    public String toString() {
        return "MyCinemaUser{" +
                "keyId=" + keyId +
                ", key='" + key + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", experienceYears=" + experienceYears +
                ", skills='" + skills + '\'' +
                ", workName='" + workName + '\'' +
                ", workType='" + workType + '\'' +
                ", workDescription='" + workDescription + '\'' +
                ", workLink='" + workLink + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}