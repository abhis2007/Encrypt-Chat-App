package com.example.routineapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.material.tabs.TabLayout;

public class wholeweekroutine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholeweekroutine);
        Toolbar toolbar=findViewById(R.id.fragmentToolbar);
        setSupportActionBar(toolbar);
        ViewPager routinePager=findViewById(R.id.routinePager);
        routinePager.setAdapter(new fragmentpageadapter(this,getSupportFragmentManager()));
        final PagerSlidingTabStrip tabSlidingStrip=findViewById(R.id.routineTabs);
        tabSlidingStrip.setViewPager(routinePager);
        tabSlidingStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageSelected(int position) { }
            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

}