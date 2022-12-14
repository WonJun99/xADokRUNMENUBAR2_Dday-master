package com.ceedlive.ceeday.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ceedlive.ceeday.BaseActivity;
import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.R;
import com.ceedlive.ceeday.data.DdayItem;
import com.ceedlive.ceeday.util.CalendarUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ListView mListView;

    private int mId;
    private Intent mIntent;

    private DdayItem mDdayItem;
    private SimpleAdapter mSimpleAdapter;

    private Calendar mTargetCalendar, mBaseCalendar, mCalendar;
    private Date mDdayDate, mTargetDate;

    private SimpleDateFormat mSimpleDateFormat;

    private List<Map<String, String>> mList = new ArrayList<>();
    private Map<String, String> mItem;

    private TextView mTvDiffDays;
    private TextView mTvTargetDay;

    private int mCalcWeight;

    //private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initialize();

        //mAdView = findViewById(R.id.detail_adView);
        //AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("8A807912B473B630ADD61488024D05EB") // This request is sent from a test device.
//                .addTestDevice("5E52A824C274C8491B1CA21E1FD6E82F") // This request is sent from a test device.
//                .addTestDevice("02BAA7172204A562C207F49284761F2A") // This request is sent from a test device.
        //        .build();
        //mAdView.loadAd(adRequest);
    }

    @Override
    protected void initialize() {
        mToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title_detail); // it works.

        mList = new ArrayList();
        mListView = findViewById(R.id.detail_lv_detail);
        mTvDiffDays = findViewById(R.id.detail_tv_diff_days);
        mTvTargetDay = findViewById(R.id.detail_tv_target_day);

        // ????????? ????????? ?????????????????? Calendar ???????????? ??????
        mTargetCalendar = new GregorianCalendar();
        mBaseCalendar = new GregorianCalendar();

        mIntent = getIntent();

        mId = mIntent.getIntExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, 0);

        if ( mIntent.getExtras() != null
                && mIntent.getExtras().containsKey(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID)
                && mIntent.getExtras().containsKey(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM) ) {

            mDdayItem = mIntent.getParcelableExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM);

            setSimpleListItem2Adapter();
        }
//        setSimpleListItem1Adapter();// TEST
    }

    private void setSimpleListItem1Adapter() {
        // [1] ????????? ?????? : ????????? ????????? ?????????
        String[] fruits = {"??????","???","??????","??????","??????","????????????","??????","?????????","??????","???","??????"};

        // [2] ????????? ??????
        // public ArrayAdapter (Context context, int textViewResourceId, T[] objects)
        // ????????????(???????????????????????? ????????????), ???????????????(?????????????????????), ?????????(??????)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fruits);
        // android.R.layout.simple_list_item_checked : ????????? ????????????

        // [3] ??????????????? ListView??? ????????? ???????????? ??????.

        // ???????????? ???????????? ????????? ListView????????? ??????????????? R.id.listview1 ????????? ID??? ????????? findViewByID()??? ????????????.
//        ListView listView = (ListView) findViewById(R.id.lv_detail);
        mListView.setAdapter(adapter); //????????? ?????? ????????? ??????.
    }

    private void setSimpleListItem2Adapter() {

        mSimpleDateFormat = new SimpleDateFormat(
                Constant.SIMPLE_DATE_FORMAT.PATTERN.YYYY???_MM???_DD???_E??????,
                Locale.KOREA);

        mCalendar = Calendar.getInstance();

        String selectedDate = mDdayItem.getDate();
        String[] arrDate = selectedDate.split(Constant.REGEX.SLASH);
        String yearStr = arrDate[0];
        String monthStr = arrDate[1];
        String dayStr = arrDate[2];

        monthStr = Integer.parseInt(monthStr) < 10 ? "0" + monthStr : monthStr;
        String yyyyMMddStr = String.format("%s%s%s", yearStr, monthStr, dayStr); // ????????? ??????

        String diffDays = CalendarUtil.getDiffDays(this,
                mTargetCalendar,
                mBaseCalendar,
                Integer.parseInt(yearStr),
                Integer.parseInt(monthStr),
                Integer.parseInt(dayStr),
                Constant.DIRECTION.FORWARD);

        if ( diffDays.equals( getString(R.string.dday_today) ) ) {
            // D-DAY
            mCalcWeight = 0;

        } else if ( diffDays.startsWith( getString(R.string.dday_valid_prefix) ) ) {
            // D-
            String minusStr = diffDays.substring( getString(R.string.dday_valid_prefix).length() );
            mCalcWeight = -Integer.parseInt(minusStr);

        } else if ( diffDays.startsWith( getString(R.string.dday_invalid_prefix) ) ) {
            // D+
            String plusStr = diffDays.substring( getString(R.string.dday_invalid_prefix).length() );
            mCalcWeight = Integer.parseInt(plusStr);
        }

        mSimpleDateFormat = new SimpleDateFormat(
                Constant.SIMPLE_DATE_FORMAT.PATTERN.YYYY???_MM???_DD???_E??????,
                Locale.KOREA);

        mTargetDate = CalendarUtil.getNextDate(0, yyyyMMddStr);

        mTvDiffDays.setText(diffDays);
        mTvTargetDay.setText( mSimpleDateFormat.format(mTargetDate) );

        int interval = 100;
        int nextDateInt = 0;
        for (int i=-10; i<11; i++) {
            nextDateInt = (i * interval);
            Date nextDate = CalendarUtil.getNextDate(nextDateInt, yyyyMMddStr);

//            mSimpleDateFormat = new SimpleDateFormat(
//                    Constant.SIMPLE_DATE_FORMAT.PATTERN.YYYY???_MM???_DD???_E??????,
//                    Locale.KOREA);

            mCalendar.setTime(nextDate);
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH) + 1;
            int day = mCalendar.get(Calendar.DAY_OF_MONTH) + mCalcWeight;

            String targetDiffDays = CalendarUtil.getDiffDays(this,
                    mTargetCalendar,
                    mBaseCalendar,
                    year,
                    month,
                    day,
                    Constant.DIRECTION.REVERSE);

            mItem = new HashMap<>();
            mItem.put("item 1", targetDiffDays);
            mItem.put("item 2", mSimpleDateFormat.format(nextDate));
            mList.add(mItem);
        }

        mSimpleAdapter = new SimpleAdapter(this, mList,
                android.R.layout.simple_list_item_2,
                new String[] {"item 1","item 2"},
                new int[] {android.R.id.text1, android.R.id.text2});

        mListView.setAdapter(mSimpleAdapter);
    }

}
