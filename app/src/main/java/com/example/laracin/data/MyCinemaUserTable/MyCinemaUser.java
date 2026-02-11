package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.material.textfield.TextInputEditText;

@Entity
public class MyCinemaUser {

    @PrimaryKey(autoGenerate = true)
    public long keyId;

    @ColumnInfo(name = "full_Name")
    public String fullName;

    public String email;
    public String password;
    public String phone;
    public String role;
    public String portfolio;
    public int experienceYears; // عدد سنوات الخبرة
    public String skills; // المهارات (مثال: "مونتاج فيديو، إضاءة، Final Cut Pro")




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
