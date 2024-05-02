package com.example.myapplicationjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new Fragment1(), "Вход");
        vpAdapter.addFragment(new Fragment2(), "Регистрация");
        viewPager.setAdapter(vpAdapter);

        db = new DatabaseHelper(this);
    }

    public void registerUser(String login, String password, String repeatPassword) {
        if (db.checkUserExists(login)) {
            Toast.makeText(this, "Вы уже зарегистрированы", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }
        db.addUser(login, password);
        Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
        viewPager.setCurrentItem(0);
    }

    public boolean loginUser(String login, String password) {
        if (db.checkUserExists(login)) {
            if (db.checkUserPassword(login, password)) {
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
                return true;
            } else {
                Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void logoutUser() {
        db.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}