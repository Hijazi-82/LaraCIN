package com.example.laracin.data.MyCinemaUserTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyUser")
public class MyCinemaUser {

    @PrimaryKey(autoGenerate = true)
    public long keyId;

    @ColumnInfo(name = "full_Name")
    public String fullName;

    public String email;
    public String password;
    public String phone;
    public String role;

    public static void insert(MyCinemaUserQuery myCinemaUserQuery) {
    }

    public static MyCinemaUser findByEmail(String email) {
    }

    // Setters and Getters
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

    @Override
    public String toString() {
        return "MyCinemaUser{" +
                "keyId=" + keyId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                '}';

    }
}
