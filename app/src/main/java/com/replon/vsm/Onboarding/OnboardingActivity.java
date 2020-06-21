package com.replon.vsm.Onboarding;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.replon.vsm.R;
import com.replon.vsm.Utility.DefaultTextConfig;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotLayout;
    private TextView[] dotstv;
    private int[] layouts;
    private TextView skip,next;

    private PagerAdapterOnboarding pagerAdapterOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), OnboardingActivity.this);

        setStatusBarTransparent();
        setContentView(R.layout.activity_onboarding);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
        skip = (TextView)findViewById(R.id.skip);
        next = (TextView)findViewById(R.id.next);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
//               overridePendingTransition(0,0);

            }
        });

        layouts = new int[]{R.layout.slider1,R.layout.slider2,R.layout.slider3};

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem()+1;
                if (currentPage <layouts.length){
                    viewPager.setCurrentItem(currentPage);
                }else {
                    finish();
//                    overridePendingTransition(0,0);
                }

            }
        });
        pagerAdapterOnboarding = new PagerAdapterOnboarding(layouts, getApplicationContext());
        viewPager.setAdapter(pagerAdapterOnboarding);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == layouts.length-1){
                    next.setText("START");
                    skip.setVisibility(View.GONE);
                }else {
                    next.setText("NEXT");
                    skip.setVisibility(View.VISIBLE);
                }
                setDotStatus(i);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setDotStatus(0);



    }


    private void setDotStatus(int page){

        dotLayout.removeAllViews();
        dotstv = new TextView[layouts.length];
        for (int i = 0;i<dotstv.length;i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(getColor(R.color.lightGrey));
            dotLayout.addView(dotstv[i]);

        }

        if (dotstv.length > 0){
            dotstv[page].setTextColor(getColor(R.color.black));
        }


    }

    private void setStatusBarTransparent(){
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }
}
