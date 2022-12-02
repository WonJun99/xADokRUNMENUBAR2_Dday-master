package com.ceedlive.ceeday.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ceedlive.ceeday.BaseActivity;
import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.R;

public class HelpActivity extends BaseActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Button mButtonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initialize();

//        Intent intent = new Intent(
//                getApplicationContext(), // 현재 화면의 제어권자
//                MainActivity.class); // 다음 넘어갈 클래스 지정
//        startActivity(intent); // 다음 화면으로 넘어간다.

        // 액티비티를 넘어간 후 이전 액티비티를 삭제하고 싶다면 다음의 명령어를 사용한다.
//        finish();

//        start();
    }

    @Override
    protected void initialize() {
        mViewPager = findViewById(R.id.help_pager);
        mViewPager.setAdapter( new CustomPagerAdapter( getApplicationContext() ) );

        mTabLayout = findViewById(R.id.help_tab);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }

    private void start() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                finish();

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        MainActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다.

                // 액티비티를 넘어간 후 이전 액티비티를 삭제하고 싶다면 다음의 명령어를 사용한다.
                finish();
            }
        }, Constant.LOADING_DELAY_MILLIS);
    }

    private class CustomPagerAdapter extends PagerAdapter {
        private LayoutInflater mLayoutInflater;
        public CustomPagerAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        /**
         * 사용 가능한 뷰의 갯수를 리턴.
         * @return
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * position에 해당하는 페이지 생성.
         * @param container
         * @param position
         * @return
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View view = null;
            ImageView imageView;

            if ( 0 == position ) {
                view = mLayoutInflater.inflate(R.layout.helpview1, null);
                imageView = view.findViewById(R.id.help_iv1);
            } else if ( 1 == position ) {
                view = mLayoutInflater.inflate(R.layout.helpview2, null);
                imageView = view.findViewById(R.id.help_iv2);
            } else {
                view = mLayoutInflater.inflate(R.layout.helpview3, null);
                imageView = view.findViewById(R.id.help_iv3);

                mButtonStart = view.findViewById(R.id.help_btn_start);
                mButtonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(
                                getApplicationContext(), // 현재 화면의 제어권자
                                MainActivity.class); // 다음 넘어갈 클래스 지정
                        startActivity(intent); // 다음 화면으로 넘어간다

                        // 액티비티를 넘어간 후 이전 액티비티를 삭제하고 싶다면 다음의 명령어를 사용한다.
                        finish();
                    }
                });
            }

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop();

            Glide.with(getApplicationContext())
                    .load("https://placeimg.com/1024/768/any")
                    .apply(requestOptions)
                    .into(imageView);

            mViewPager.addView(view, 0);
            return view;
//            return super.instantiateItem(container, position);
        }

        /**
         * position 위치의 페이지 제거.
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            mViewPager.removeView( (View) object);
        }

        /**
         * 페이지뷰가 특정 키 객체(key object)와 연관되는지 여부.
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return ( view == (View) object );
        }
    }
}
