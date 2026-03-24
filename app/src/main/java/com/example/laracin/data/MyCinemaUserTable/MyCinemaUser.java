package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;

@Entity

/**
 * MyCinemaUser
 * موديل يمثل مستخدم التطبيق, ويستخدم ك entity داخل Room
 *
 * الهدف
 * تخزين معلومات المستخدم الاساسية, ومعلومات البروفايل المتعلقة بالشغل داخل قاعدة بيانات محلية
 *
 * الحقول
 * keyId
 * - مفتاح اساسي داخل Room
 * - autoGenerate يعني Room بتولد رقم تلقائي لكل سجل جديد
 *
 * fullName
 * - الاسم الكامل للمستخدم
 * - مخزن في الجدول باسم full_Name عبر ColumnInfo
 *
 * email و password
 * - بيانات تسجيل الدخول
 *
 * phone
 * - رقم هاتف المستخدم
 *
 * role
 * - دور المستخدم او تخصصه, مثال مصور, مونتير, ممثل, الخ
 *
 * portfolio
 * - رابط او وصف للبورتفوليو
 *
 * experienceYears
 * - عدد سنوات الخبرة, رقم صحيح
 *
 * skills
 * - نص يمثل مهارات المستخدم, مثال مونتاج, اضاءة, Final Cut Pro
 *
 * ملاحظة
 * الكلاس يحتوي getters و setters لكل حقل, و toString لطباعة محتوى الكائن للتشخيص
 */
public class MyCinemaUser implements Serializable {

    // Primary key لقاعدة Room, يتم توليده تلقائيا
    @PrimaryKey(autoGenerate = true)
    public long keyId;

    // الاسم الكامل, اسم العمود في الجدول full_Name
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

    // المهارات كنص
    public String skills;

    // getters و setters

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
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

    /**
     * toString
     * مفيد للتجربة والتشخيص, يعرض كل الحقول كنص واحد
     */
    @Override
    public String toString() {
        return "MyCinemaUser{" +
                "keyId=" + keyId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", experienceYears=" + experienceYears +
                ", skills='" + skills + '\'' +
                '}';
    }
}