package com.ceedlive.ceeday.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ceedlive.ceeday.BaseActivity;
import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.R;
import com.ceedlive.ceeday.adapter.DdayListAdapter;
import com.ceedlive.ceeday.data.DdayItem;
import com.ceedlive.ceeday.service.NotificationService;
import com.ceedlive.ceeday.sqlite.DatabaseHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*
     * 디데이 프로젝트 관련 내용은 Dday 프로젝트 README.md 에 정리
     */
    private PackageInfo mPackageInfo;// 패키지에 대한 전반적인 정보

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private ListView mNavBodyListView;
    private TextView mNavBodyTv;

    private DatabaseHelper mDatabaseHelper;

    private LinearLayout mLayoutNoContent;
    private ListView mListViewContent;

    private FloatingActionButton mFabToBeCreated;
    private FloatingActionButton mFabToBeDeleted;
    private FloatingActionButton mFabToBeCancelled;
    private FloatingActionButton mFabToBeNotified;

    private List<DdayItem> mDdayItemList;
    private String mAnniversaryInfoKey;
    private String mAnniversaryInfoJsonString;

    private String mSharedPreferencesDataKey;
    private int _id;

    private SharedPreferences mSharedPreferences;

    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    private List<Integer> mDynamicCheckedItemIdList;

    private NotificationManager mNotificationManager;

    private BroadcastReceiver mBroadcastReceiver;

    //private AdView mBottomAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();// 변수 초기화

        //mBottomAdView = findViewById(R.id.main_adView);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("8A807912B473B630ADD61488024D05EB") // This request is sent from a test device.
//                .addTestDevice("5E52A824C274C8491B1CA21E1FD6E82F") // This request is sent from a test device.
//                .addTestDevice("02BAA7172204A562C207F49284761F2A") // This request is sent from a test device.
                .build();
       // mBottomAdView.loadAd(adRequest);

       /* mBottomAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);

            }
        });*/

        setEvent();// 이벤트 설정
        setSQLiteData(); // (SQLite)
    }

    /** Called when leaving the activity */
   /* @Override
    public void onPause() {
        if (mBottomAdView != null) {
            mBottomAdView.pause();
        }
        super.onPause();
    }*/

    /** Called when returning to the activity */
    /*@Override
    public void onResume() {
        super.onResume();
        if (mBottomAdView != null) {
            mBottomAdView.resume();
        }
    }*/

    /** Called before the activity is destroyed */
    /*@Override
   *//* public void onDestroy() {
        if (mBottomAdView != null) {
            mBottomAdView.destroy();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }*/

    @Override
    protected void initialize() {
        mLayoutNoContent = findViewById(R.id.main_ll_no_content);
        mListViewContent = findViewById(R.id.expandableListView);

        mFabToBeCreated = findViewById(R.id.fabToBeCreated);
        mFabToBeDeleted = findViewById(R.id.fabToBeDeleted);
        mFabToBeCancelled = findViewById(R.id.fabToBeCancelled);
        mFabToBeNotified = findViewById(R.id.fabToBeNotified);

        // https://stackoverflow.com/questions/30969455/android-changing-floating-action-button-color
//        mFabBtn.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorWhite) ));
        mFabToBeDeleted.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorWhite) ));
        mFabToBeCancelled.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorWhite) ));
        mFabToBeNotified.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorWhite) ));

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Array of strings...
        String[] menuArray = {"버전"};

        mNavBodyListView = findViewById(R.id.nav_body_listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                switch (position) {
                    case 0:
                        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dday_info, 0, 0, 0);
                        view.setText(String.format("%s(%s)", "버전 ", getAppVersionName()));
                        break;
                }

                return view;
            }
        };
        mNavBodyListView.setAdapter(adapter);

        mNavBodyTv = findViewById(R.id.nav_body_text);
        mNavBodyTv.setMovementMethod(new ScrollingMovementMethod());

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDdayItemList = new ArrayList<>();
        mDynamicCheckedItemIdList = new CopyOnWriteArrayList<>();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // intent ..

                // 2019-04-19 금요일 발견한 버그 해결 내용 정리

                // 버그 발생 시나리오
                // MainActivity -> MergeActivity
                // 신규로 디데이 일정 등록, 이때 상단바 디데이 고정 선택
                // 저장
                // 알림 메시지 내려옴
                // 해당 알림 메시지 터치 == 노티피케이션에 등록된 알림 터치
                // MergeActivity 로 이동
                // 날짜 변경
                // 저장
                // MainActivity에 반영 안 됨
                // 로그캣을 보면
                // MainActivity onCreate 메소드 호출 안됨
                // NotificationService 클래스의 onStartCommand 호출
                // InnerNotificationServiceHandler > handleMessage: 핸들러 메시지큐에 있는 작업을 처리 ( 실제 처리 메소드)

                // 그래서
                // 서비스 내 해당 코드 말미에 브로드캐스트 리시버 추가, onReceive 메소드 안에 다음 코드 추가
                // 버그 해결

                setSQLiteData();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver( mBroadcastReceiver,
                new IntentFilter(Constant.ACTION_INTENT_FILTER_NOTIFICATION_ON_START_COMMAND) );
    }

    /**
     * 앱버전 코드
     * @return
     */
    public int getAppVersionCode(){
        // PackageInfo 초기화
        try{
            mPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return mPackageInfo.versionCode;
    }
    /**
     * 앱버전 코드
     * @return
     */
    public String getAppVersionName(){
        // PackageInfo 초기화
        try{
            mPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return mPackageInfo.versionName;
    }

    private void setEvent() {
        onClickFabButtonCreate();
        onClickLayoutNoContent();

        mFabToBeDeleted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFabDelete();
            }
        });

        mFabToBeCancelled.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFabNotification(Constant.NOTIFICATION.TO_BE_CANCELLED);
            }
        });

        mFabToBeNotified.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFabNotification(Constant.NOTIFICATION.TO_BE_NOTIFIED);
            }
        });
    }

    //

    /**
     * 뒤로가기 버튼으로 내비게이션 닫기
     * 내비게이션 드로어가 열려 있을 때 뒤로가기 버튼을 누르면 내비게이션을 닫고,
     * 닫혀 있다면 기존 뒤로가기 버튼으로 작동한다.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if ( drawer.isDrawerOpen(GravityCompat.START) ) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.community:
                return true;
            case R.id.gift:
                return true;
            case R.id.mypage:
                return true;
            case R.id.home:
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Add Custom
     */



    /**
     * Floating Action Button Click Event
     */
    private void onClickFabButtonCreate() {
        mFabToBeCreated.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goMergeActivity(Constant.DDAY.NEW);
            }
        });
    }

    private void onClickLayoutNoContent() {
        // 등록된 일정이 하나도 없는 경우
        mLayoutNoContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goMergeActivity(Constant.DDAY.NEW);
            }
        });
    }

    /**
     * 상세화면 액티비티로 이동
     * @param _id
     */
    private void goDetailActivity(int _id) {
        // 액티비티 전환 코드
        // 인텐트 선언 -> 현재 액티비티, 넘어갈 액티비티
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        // 수정/삭제
        if (_id > 0) {
//            intent.putExtra(Constant.INTENT_DATA_NAME_SHARED_PREFERENCES, _id);
            DdayItem ddayItem = mDatabaseHelper.getDday(_id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, _id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem);
        }

        // 인텐트 실행
        startActivityForResult(intent, Constant.INTENT.REQUEST_CODE.MAIN_ACTIVITY);
    }

    /**
     * 상세화면 액티비티로 이동
     * @param _id
     */
    private void goMergeActivity(int _id) {
        // 액티비티 전환 코드
        // 인텐트 선언 -> 현재 액티비티, 넘어갈 액티비티
        Intent intent = new Intent(MainActivity.this, MergeActivity.class);

        // 수정/삭제
        if (_id > 0) {
//            intent.putExtra(Constant.INTENT_DATA_NAME_SHARED_PREFERENCES, _id);
            DdayItem ddayItem = mDatabaseHelper.getDday(_id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, _id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem);
        }

        // 인텐트 실행
        startActivityForResult(intent, Constant.INTENT.REQUEST_CODE.MAIN_ACTIVITY);
    }

    /**
     * 인텐트 실행 결과 처리 메소드
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode: 송신자 Activity 구별하기 위한 값
        // resultCode: 수신자 Activity 에서 송신자 Activity 로 어떠한 결과코드를 주었는지를 나타냄
        // Intent data: 수신자 Activity 에서 송신자 Activity 로 보낸 결과 데이터
        if (Constant.REQUEST_CODE_MAIN_ACTIVITY == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
//                setSharedPreferencesData();

                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_add_dday), Snackbar.LENGTH_SHORT).show();
                setSQLiteData();
            }
            if (Activity.RESULT_CANCELED == resultCode) {
                // 만약 반환값이 없을 경우의 코드를 여기에 작성하세요.
                Toast.makeText(this, "만약 반환값이 없을 경우의 코드를 여기에 작성하세요.", Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * SharedPreferences 에 저장된 key/value pair 데이터 세팅하고 출력하기
     */
    private void setSharedPreferencesData() {
        // 출처: https://itpangpang.xyz/143 [ITPangPang]
        // 출처: https://bitnori.tistory.com/entry/Android-다른-레이아웃Layout의-위젯-제어하기-LayoutInflater-사용 [Bitnori's Blog]
        // https://stackoverflow.com/questions/28193552/null-pointer-exception-on-setonclicklistener
        mDdayItemList.clear();

        mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        // How to get all keys of SharedPreferences programmatically in Android?
        Map<String, ?> allEntries = mSharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("main map values", entry.getKey() + ": " + entry.getValue().toString());
            mAnniversaryInfoKey = entry.getKey();
            mAnniversaryInfoJsonString = entry.getValue().toString();
            DdayItem ddayItem = gson.fromJson(mAnniversaryInfoJsonString, DdayItem.class);
            ddayItem.setUniqueKey(mAnniversaryInfoKey);
            mDdayItemList.add(ddayItem);
        }

        SortDescending sortDescending = new SortDescending();
        Collections.sort(mDdayItemList, sortDescending);

        if (mDdayItemList.isEmpty()) {
            mLayoutNoContent.setVisibility(View.VISIBLE);
            mListViewContent.setVisibility(View.INVISIBLE);
        } else {
            mListViewContent.setVisibility(View.VISIBLE);
            mLayoutNoContent.setVisibility(View.INVISIBLE);
        }

        mListViewContent.setAdapter(new DdayListAdapter(mDdayItemList, this));
    }

    private void setSQLiteData() {
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        mDdayItemList.clear();

        List<DdayItem> ddayItemList = mDatabaseHelper.getDdayList();
        for (DdayItem ddayItem : ddayItemList) {
            mDdayItemList.add(ddayItem);
        }

        SortDescending sortDescending = new SortDescending();
        Collections.sort(mDdayItemList, sortDescending);

        if ( mDdayItemList.isEmpty() ) {
            mLayoutNoContent.setVisibility(View.VISIBLE);
            mListViewContent.setVisibility(View.INVISIBLE);
            mFabToBeDeleted.setVisibility(View.GONE);
            mFabToBeCancelled.setVisibility(View.GONE);
            mFabToBeNotified.setVisibility(View.GONE);
        } else {
            mListViewContent.setVisibility(View.VISIBLE);
            mLayoutNoContent.setVisibility(View.INVISIBLE);
        }

        mListViewContent.setAdapter(new DdayListAdapter(mDdayItemList, this));
    }

    class SortDescending implements Comparator<DdayItem> {
        @Override
        public int compare(DdayItem ddayItem1, DdayItem ddayItem2) {
            // SharedPreferences
//            int first = Integer.parseInt( ddayItem1.getUniqueKey().replace(Constant.SHARED_PREFERENCES_KEY_PREFIX, "") );
//            int second = Integer.parseInt( ddayItem2.getUniqueKey().replace(Constant.SHARED_PREFERENCES_KEY_PREFIX, "") );

            // SQLite
            int first = ddayItem1.get_id();
            int second = ddayItem2.get_id();

//            int compareValue = 0;
//            compareValue = second > first ? 1 : second == first ? 0 : -1;
//            if (second > first) {
//                compareValue = 1;
//            }
//            if (second == first) {
//                compareValue = 0;
//            }
//            if (second < first) {
//                compareValue = -1;
//            }

            return Integer.compare(second, first);
        }
    }

    public void onClickFabNotification(boolean isToBeNotified) {
        // 다이얼로그
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        // 다이얼로그 값/옵션 세팅
        mAlertDialogBuilder
                .setTitle(isToBeNotified ?
                        R.string.alert_title_activate_dday_checked :
                        R.string.alert_title_deactivate_dday_checked)
                .setMessage(isToBeNotified ?
                        R.string.alert_message_activate_dday_checked :
                        R.string.alert_message_deactivate_dday_checked)
                .setCancelable(false)
                .setPositiveButton(isToBeNotified ? R.string.btn_activate : R.string.btn_deactivate,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (isToBeNotified) {
                                    handleClickCheckedItemNotify();
                                } else {
                                    handleClickCheckedItemCancel();
                                }
                            }
                        })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다.
                                dialog.cancel();
                            }
                        });

        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private void handleClickCheckedItemNotify() {
        // 일정 수정
        if ( !mDynamicCheckedItemIdList.isEmpty() ) {
            int count = 0;
            int updated = 0;
            int total = mDynamicCheckedItemIdList.size();

            for (Integer rowId : mDynamicCheckedItemIdList) {
                count++;

                DdayItem ddayItem = mDatabaseHelper.getDday(rowId);

                if (ddayItem != null) {
                    if ( Constant.NOTIFICATION.REGISTERED == ddayItem.getNotification() ) {
                        continue;
                    }

                    ddayItem.setNotification(Constant.NOTIFICATION.REGISTERED);

                    if (mDatabaseHelper.updateDday(ddayItem) > 0) {
                        Intent intent = new Intent(this, NotificationService.class);
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, rowId); //전달할 값
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem); //전달할 값
                        startService(intent);
                        // 간혹 앱이 죽는 현상 발생, NullPointerException

                        updated++;
                    }
                }

                if (count == total) { // last item
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_activate_dday), Snackbar.LENGTH_SHORT).show();
                }
            }

            if (updated == 0) {
                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_no_activate_dday), Snackbar.LENGTH_SHORT).show();
            }

        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_no_activate_dday), Snackbar.LENGTH_SHORT).show();
        }

        handleFabVisibility(false);
        setSQLiteData();
    }

    private void handleClickCheckedItemCancel() {
        // 일정 수정
        if ( !mDynamicCheckedItemIdList.isEmpty() ) {
            int count = 0;
            int updated = 0;
            int total = mDynamicCheckedItemIdList.size();

            for (Integer rowId : mDynamicCheckedItemIdList) {
                count++;

                DdayItem ddayItem = mDatabaseHelper.getDday(rowId);

                if ( Constant.NOTIFICATION.UNREGISTERED == ddayItem.getNotification() ) {
                    continue;
                }

                ddayItem.setNotification(Constant.NOTIFICATION.UNREGISTERED);

                if (mDatabaseHelper.updateDday(ddayItem) > 0) {
                    try {
                        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancel(rowId);
                        updated++;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                if (count == total) { // last item
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_deactivate_dday), Snackbar.LENGTH_SHORT).show();
                }
            }

            if (updated == 0) {
                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_no_deactivate_dday), Snackbar.LENGTH_SHORT).show();
            }

        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_no_deactivate_dday), Snackbar.LENGTH_SHORT).show();
        }

        handleFabVisibility(false);
        setSQLiteData();
    }

    public void onClickFabDelete() {
        // 다이얼로그
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        // 다이얼로그 값/옵션 세팅
        mAlertDialogBuilder
                .setTitle(R.string.alert_title_delete_dday_checked)
                .setMessage(R.string.alert_message_delete_dday_checked)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 일정 삭제
                                if ( !mDynamicCheckedItemIdList.isEmpty() ) {
                                    for (Integer rowId : mDynamicCheckedItemIdList) {
                                        doDeleteItem(rowId);
                                    }
                                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_delete_dday), Snackbar.LENGTH_SHORT).show();
                                    handleFabVisibility(false);
                                    setSQLiteData();
                                }
                            }
                        })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다.
                                dialog.cancel();
                            }
                        });

        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void addChecked(int targetId) {
        if ( mDynamicCheckedItemIdList.isEmpty() || !mDynamicCheckedItemIdList.contains(targetId) ) {
            mDynamicCheckedItemIdList.add(targetId);
        }
    }

    public void removeChecked(int targetId) {
        // FIXME ConcurrentModificationException
        for (Integer id : mDynamicCheckedItemIdList) {
            if (id == targetId) {
                int index = mDynamicCheckedItemIdList.indexOf(id);
                mDynamicCheckedItemIdList.remove(index);
            }
        }
    }

    /**
     * onLongClick 전부 체크 해제 시 이벤트 핸들러
     * @param visible
     */
    public void handleFabVisibility(boolean visible) {
        if (visible) {
            mFabToBeDeleted.setVisibility(View.VISIBLE);
            mFabToBeCancelled.setVisibility(View.VISIBLE);
            mFabToBeNotified.setVisibility(View.VISIBLE);

            mFabToBeCreated.setEnabled(false);
            mFabToBeCreated.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorWhite) ));
        } else {
            mFabToBeDeleted.setVisibility(View.GONE);
            mFabToBeCancelled.setVisibility(View.GONE);
            mFabToBeNotified.setVisibility(View.GONE);

            mFabToBeCreated.setEnabled(true);
            mFabToBeCreated.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorBrigntRed) ));

            mDynamicCheckedItemIdList.clear();
        }
    }

    /**
     * 수정 버튼 클릭 시 이벤트 핸들러
     * @param _id
     */
    public void onClickDetail(int _id) {
        try {
            if (_id > 0) {
                goDetailActivity(_id);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 노티 버튼 클릭 시 이벤트 핸들러
     * @param isNotification
     * @param _id
     */
    public void onClickNoti(boolean isNotification, int _id) {
        try {
//            mSharedPreferencesDataKey = uniqueKey;
//            if (null != mSharedPreferencesDataKey) {
            if (_id > 0) {

                // SharedPreferences
//                SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
//                // TODO SharedPreferences 더 알아보기
//                // 첫번째 인자 name 은 해당 SharedPreferences 의 이름입니다.
//                // 특정 이름으로 생성할수 있고 해당 이름으로 xml 파일이 생성된다고 생각하시면 됩니다.
//
//                String jsonStringValue = sharedPreferences.getString(mSharedPreferencesDataKey, "");
//                DdayItem ddayItem = gson.fromJson(jsonStringValue, DdayItem.class);

                // SQLite
                DdayItem ddayItem = mDatabaseHelper.getDday(_id);

                if (isNotification) {
                    ddayItem.setNotification(Constant.NOTIFICATION.UNREGISTERED);
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(_id);
                    mDatabaseHelper.updateDday(ddayItem);
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_remove_notification), Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, NotificationService.class);
//                intent.putExtra(Constant.INTENT_DATA_NAME_SHARED_PREFERENCES, ddayItem.getUniqueKey()); //전달할 값
                    ddayItem.setNotification(Constant.NOTIFICATION.REGISTERED);
                    if (mDatabaseHelper.updateDday(ddayItem) > 0) {
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, ddayItem.get_id()); //전달할 값
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem); //전달할 값
                        startService(intent);
                    }

                    // 우리는 보통 알림을 띄울때 Toast를 이용해서 많이 이용했을 겁니다.
                    // 하지만 안드로이드 오레오부터 알림을 끄게되면 Toast가 보이지 않습니다.
                    // 따라서 스낵바로 넘어갈 상황이 필요한것 같습니다.
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_add_notification), Snackbar.LENGTH_SHORT).show();
                }

                handleFabVisibility(false);
                setSQLiteData();

                // intent 객체
                // 서비스와 연결에 대한 정의
//                mIsService = bindService(intent, mServiceConnection, Context.BIND_DEBUG_UNBIND);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 수정 버튼 클릭 시 이벤트 핸들러
     * @param _id
     */
    public void onClickEdit(int _id) {
        try {
//            mSharedPreferencesDataKey = uniqueKey;
            if (_id > 0) {
                goMergeActivity(_id);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 삭제 버튼 클릭 시 이벤트 핸들러
     * @param _id
     */
    public void onClickDelete(final int _id) {
        try {
            if (_id > 0) {
                // 다이얼로그
                mAlertDialogBuilder = new AlertDialog.Builder(this);

                // 다이얼로그 값/옵션 세팅
                mAlertDialogBuilder
                        .setTitle(R.string.alert_title_delete_dday)
                        .setMessage(R.string.alert_message_delete_dday)
                        .setCancelable(false)
                        .setPositiveButton(R.string.btn_delete,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 일정 삭제
                                        doDeleteItem(_id);
                                        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_delete_dday), Snackbar.LENGTH_SHORT).show();
                                        setSQLiteData();
                                    }
                                })
                        .setNegativeButton(R.string.btn_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다.
                                        dialog.cancel();
                                    }
                                });

                mAlertDialog = mAlertDialogBuilder.create();
                mAlertDialog.show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * SQLite
     * 일정 삭제
     * @param _id
     */
    private void doDeleteItem(int _id) {
        int result = mDatabaseHelper.deleteDday(_id);
        if (result > 0) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(_id);
        }
    }

    /**
     * SharedPreferences
     * 일정 삭제
     * @param sharedPreferencesDataKey
     */
    private void doDeleteItemSharedPreferences(String sharedPreferencesDataKey) {
        // How to remove some key/value pair from SharedPreferences?
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(sharedPreferencesDataKey);
        editor.apply();

        // ? editor.apply VS editor.commit

        setSharedPreferencesData();
    }

    /**
     * 다이얼로그 출력
     */
    @SuppressWarnings("unused")
    private void showDialog() {
        // 다이얼로그
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        // 다이얼로그 값/옵션 세팅
        mAlertDialogBuilder
                .setTitle(R.string.alert_title_delete_dday)
                .setMessage(R.string.alert_message_delete_dday)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 일정 삭제
                                doDeleteItemSharedPreferences(mSharedPreferencesDataKey);
                            }
                        })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다.
                                dialog.cancel();
                            }
                        });

        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        // reference: https://mainia.tistory.com/2017
        // reference: https://m.blog.naver.com/PostView.nhn?blogId=sgepyh2916&logNo=221176134263&proxyReferer=https%3A%2F%2Fwww.google.com%2F
    }

}
