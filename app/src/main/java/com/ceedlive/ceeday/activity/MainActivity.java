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
     * ????????? ???????????? ?????? ????????? Dday ???????????? README.md ??? ??????
     */
    private PackageInfo mPackageInfo;// ???????????? ?????? ???????????? ??????

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

        initialize();// ?????? ?????????

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

        setEvent();// ????????? ??????
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
        String[] menuArray = {"??????"};

        mNavBodyListView = findViewById(R.id.nav_body_listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                switch (position) {
                    case 0:
                        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dday_info, 0, 0, 0);
                        view.setText(String.format("%s(%s)", "?????? ", getAppVersionName()));
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

                // 2019-04-19 ????????? ????????? ?????? ?????? ?????? ??????

                // ?????? ?????? ????????????
                // MainActivity -> MergeActivity
                // ????????? ????????? ?????? ??????, ?????? ????????? ????????? ?????? ??????
                // ??????
                // ?????? ????????? ?????????
                // ?????? ?????? ????????? ?????? == ????????????????????? ????????? ?????? ??????
                // MergeActivity ??? ??????
                // ?????? ??????
                // ??????
                // MainActivity??? ?????? ??? ???
                // ???????????? ??????
                // MainActivity onCreate ????????? ?????? ??????
                // NotificationService ???????????? onStartCommand ??????
                // InnerNotificationServiceHandler > handleMessage: ????????? ??????????????? ?????? ????????? ?????? ( ?????? ?????? ?????????)

                // ?????????
                // ????????? ??? ?????? ?????? ????????? ?????????????????? ????????? ??????, onReceive ????????? ?????? ?????? ?????? ??????
                // ?????? ??????

                setSQLiteData();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver( mBroadcastReceiver,
                new IntentFilter(Constant.ACTION_INTENT_FILTER_NOTIFICATION_ON_START_COMMAND) );
    }

    /**
     * ????????? ??????
     * @return
     */
    public int getAppVersionCode(){
        // PackageInfo ?????????
        try{
            mPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return mPackageInfo.versionCode;
    }
    /**
     * ????????? ??????
     * @return
     */
    public String getAppVersionName(){
        // PackageInfo ?????????
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
     * ???????????? ???????????? ??????????????? ??????
     * ??????????????? ???????????? ?????? ?????? ??? ???????????? ????????? ????????? ?????????????????? ??????,
     * ?????? ????????? ?????? ???????????? ???????????? ????????????.
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
        // ????????? ????????? ????????? ?????? ??????
        mLayoutNoContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goMergeActivity(Constant.DDAY.NEW);
            }
        });
    }

    /**
     * ???????????? ??????????????? ??????
     * @param _id
     */
    private void goDetailActivity(int _id) {
        // ???????????? ?????? ??????
        // ????????? ?????? -> ?????? ????????????, ????????? ????????????
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        // ??????/??????
        if (_id > 0) {
//            intent.putExtra(Constant.INTENT_DATA_NAME_SHARED_PREFERENCES, _id);
            DdayItem ddayItem = mDatabaseHelper.getDday(_id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, _id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem);
        }

        // ????????? ??????
        startActivityForResult(intent, Constant.INTENT.REQUEST_CODE.MAIN_ACTIVITY);
    }

    /**
     * ???????????? ??????????????? ??????
     * @param _id
     */
    private void goMergeActivity(int _id) {
        // ???????????? ?????? ??????
        // ????????? ?????? -> ?????? ????????????, ????????? ????????????
        Intent intent = new Intent(MainActivity.this, MergeActivity.class);

        // ??????/??????
        if (_id > 0) {
//            intent.putExtra(Constant.INTENT_DATA_NAME_SHARED_PREFERENCES, _id);
            DdayItem ddayItem = mDatabaseHelper.getDday(_id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, _id);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem);
        }

        // ????????? ??????
        startActivityForResult(intent, Constant.INTENT.REQUEST_CODE.MAIN_ACTIVITY);
    }

    /**
     * ????????? ?????? ?????? ?????? ?????????
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode: ????????? Activity ???????????? ?????? ???
        // resultCode: ????????? Activity ?????? ????????? Activity ??? ????????? ??????????????? ??????????????? ?????????
        // Intent data: ????????? Activity ?????? ????????? Activity ??? ?????? ?????? ?????????
        if (Constant.REQUEST_CODE_MAIN_ACTIVITY == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
//                setSharedPreferencesData();

                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_add_dday), Snackbar.LENGTH_SHORT).show();
                setSQLiteData();
            }
            if (Activity.RESULT_CANCELED == resultCode) {
                // ?????? ???????????? ?????? ????????? ????????? ????????? ???????????????.
                Toast.makeText(this, "?????? ???????????? ?????? ????????? ????????? ????????? ???????????????.", Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * SharedPreferences ??? ????????? key/value pair ????????? ???????????? ????????????
     */
    private void setSharedPreferencesData() {
        // ??????: https://itpangpang.xyz/143 [ITPangPang]
        // ??????: https://bitnori.tistory.com/entry/Android-??????-????????????Layout???-??????-????????????-LayoutInflater-?????? [Bitnori's Blog]
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
        // ???????????????
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        // ??????????????? ???/?????? ??????
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
                                // ?????????????????? ????????????.
                                dialog.cancel();
                            }
                        });

        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private void handleClickCheckedItemNotify() {
        // ?????? ??????
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
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, rowId); //????????? ???
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem); //????????? ???
                        startService(intent);
                        // ?????? ?????? ?????? ?????? ??????, NullPointerException

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
        // ?????? ??????
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
        // ???????????????
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        // ??????????????? ???/?????? ??????
        mAlertDialogBuilder
                .setTitle(R.string.alert_title_delete_dday_checked)
                .setMessage(R.string.alert_message_delete_dday_checked)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // ?????? ??????
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
                                // ?????????????????? ????????????.
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
     * onLongClick ?????? ?????? ?????? ??? ????????? ?????????
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
     * ?????? ?????? ?????? ??? ????????? ?????????
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
     * ?????? ?????? ?????? ??? ????????? ?????????
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
//                // TODO SharedPreferences ??? ????????????
//                // ????????? ?????? name ??? ?????? SharedPreferences ??? ???????????????.
//                // ?????? ???????????? ???????????? ?????? ?????? ???????????? xml ????????? ??????????????? ??????????????? ?????????.
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
//                intent.putExtra(Constant.INTENT_DATA_NAME_SHARED_PREFERENCES, ddayItem.getUniqueKey()); //????????? ???
                    ddayItem.setNotification(Constant.NOTIFICATION.REGISTERED);
                    if (mDatabaseHelper.updateDday(ddayItem) > 0) {
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, ddayItem.get_id()); //????????? ???
                        intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem); //????????? ???
                        startService(intent);
                    }

                    // ????????? ?????? ????????? ????????? Toast??? ???????????? ?????? ???????????? ?????????.
                    // ????????? ??????????????? ??????????????? ????????? ???????????? Toast??? ????????? ????????????.
                    // ????????? ???????????? ????????? ????????? ???????????? ????????????.
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_add_notification), Snackbar.LENGTH_SHORT).show();
                }

                handleFabVisibility(false);
                setSQLiteData();

                // intent ??????
                // ???????????? ????????? ?????? ??????
//                mIsService = bindService(intent, mServiceConnection, Context.BIND_DEBUG_UNBIND);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????? ?????? ?????? ??? ????????? ?????????
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
     * ?????? ?????? ?????? ??? ????????? ?????????
     * @param _id
     */
    public void onClickDelete(final int _id) {
        try {
            if (_id > 0) {
                // ???????????????
                mAlertDialogBuilder = new AlertDialog.Builder(this);

                // ??????????????? ???/?????? ??????
                mAlertDialogBuilder
                        .setTitle(R.string.alert_title_delete_dday)
                        .setMessage(R.string.alert_message_delete_dday)
                        .setCancelable(false)
                        .setPositiveButton(R.string.btn_delete,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // ?????? ??????
                                        doDeleteItem(_id);
                                        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.snackbar_msg_delete_dday), Snackbar.LENGTH_SHORT).show();
                                        setSQLiteData();
                                    }
                                })
                        .setNegativeButton(R.string.btn_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // ?????????????????? ????????????.
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
     * ?????? ??????
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
     * ?????? ??????
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
     * ??????????????? ??????
     */
    @SuppressWarnings("unused")
    private void showDialog() {
        // ???????????????
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        // ??????????????? ???/?????? ??????
        mAlertDialogBuilder
                .setTitle(R.string.alert_title_delete_dday)
                .setMessage(R.string.alert_message_delete_dday)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // ?????? ??????
                                doDeleteItemSharedPreferences(mSharedPreferencesDataKey);
                            }
                        })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // ?????????????????? ????????????.
                                dialog.cancel();
                            }
                        });

        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        // reference: https://mainia.tistory.com/2017
        // reference: https://m.blog.naver.com/PostView.nhn?blogId=sgepyh2916&logNo=221176134263&proxyReferer=https%3A%2F%2Fwww.google.com%2F
    }

}
