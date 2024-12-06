package com.esther.perfect;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class home extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Menu1 fragmentHome = new Menu1();
    private Menu2 fragmentEvent = new Menu2();
    private Menu3 fragmentFavorite = new Menu3();
    private Menu4 fragmentShop = new Menu4();
    private Menu5 fragmentChat = new Menu5();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 툴바 초기화
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home"); // 초기 툴바 제목 설정
        }

        initializeUI();
        setupBottomNavigationView();
    }

    private void initializeUI() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    // 바텀 네비게이션 아이템 선택 리스너
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            int itemId = menuItem.getItemId();
            if (itemId == R.id.home) {
                transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                getSupportActionBar().setTitle("Home"); // 툴바 제목 변경
            } else if (itemId == R.id.event) {
                transaction.replace(R.id.menu_frame_layout, fragmentEvent).commitAllowingStateLoss();
                getSupportActionBar().setTitle("Ticket"); // 툴바 제목 변경
            } else if (itemId == R.id.favorite) {
                transaction.replace(R.id.menu_frame_layout, fragmentFavorite).commitAllowingStateLoss();
                getSupportActionBar().setTitle("My"); // 툴바 제목 변경
            } else if (itemId == R.id.shop) {
                transaction.replace(R.id.menu_frame_layout, fragmentShop).commitAllowingStateLoss();
                getSupportActionBar().setTitle("Shop"); // 툴바 제목 변경
            } else if (itemId == R.id.member) {
                transaction.replace(R.id.menu_frame_layout, fragmentChat).commitAllowingStateLoss();
                getSupportActionBar().setTitle("Member"); // 툴바 제목 변경
            }

            return true;
        }
    }
}
