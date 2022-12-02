package com.ceedlive.ceeday.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.ceedlive.ceeday.BaseActivity;
import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.R;
import com.ceedlive.ceeday.http.RetrofitApiService;
import com.ceedlive.ceeday.http.RetrofitConnection;
import com.ceedlive.ceeday.receiver.PackageEventReceiver;
import com.ceedlive.ceeday.sqlite.DatabaseHelper;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoadingActivity extends BaseActivity {

    private static final String TAG = "LoadingActivity";
    private DatabaseHelper mDatabaseHelper;

    private String[] mArrWiseSaying;
    private TextView mTvWiseSaying;

    private Thread mThread;

    private Retrofit mRetrofit;
    private RetrofitApiService mRetrofitApiService;

    private RetrofitConnection mRetrofitConnection;

    private PackageEventReceiver mPackageEventReceiver;

    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

//        registerPackageEventReceiver();

        initialize();

        createTable();
        loadWiseSaying();
        startLoading();

        // MobileAds 클래스의 정적 메소드 initialize()에 제공된 두 번째 인수는 Ad Mob에 가입할 때 얻은 Ad Mob 애플리케이션 ID 여야 합니다.
        // 이 경우 데모 용도로 Google에서 제공하는 공개 애플리케이션 ID를 사용하고 있습니다.
        // This method should be called as early as possible, and only once per application launch
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        // reference: https://code.tutsplus.com/ko/tutorials/how-to-monetize-your-android-apps-with-admob--cms-29255
        // reference: https://stackoverflow.com/questions/37418663/what-is-the-proper-way-to-call-mobileads-initialize

//        mPackageEventReceiver.callback(new PackageEventReceiver.ReceiveListener() {
//            @Override
//            public void onReceive(String action) {
//                Log.e(TAG, "onCreate onReceive action: " + action);
//                try {
//
//
//
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        retrofitGet();
//        retrofitCreate();
//        retrofitUpdate();
//        retrofitDelete();
    }

    @Override
    protected void initialize() {
        mRetrofit = RetrofitConnection.getInstance(RetrofitApiService.API_URL);
        mRetrofitApiService = mRetrofit.create(RetrofitApiService.class);
    }

    private void registerPackageEventReceiver() {
        mPackageEventReceiver = new PackageEventReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);

        intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);

        intentFilter.addDataScheme("package");
        registerReceiver(mPackageEventReceiver, intentFilter);

        // When the user uninstalls the app, at first the process is killed,
        // then your apk file and data directory are deleted, along with the records
        // in Package Manager that tell other apps which intent filters you've registered for.

    }

    /**
     * android.app.IntentReceiverLeaked: Activity com.example.ceedlive.dday.activity.LoadingActivity has leaked IntentReceiver com.example.ceedlive.dday.receiver.PackageEventReceiver@eac196 that was originally registered here. Are you missing a call to unregisterReceiver()?
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPackageEventReceiver != null) {
            unregisterReceiver(mPackageEventReceiver);
        }
    }

    private void createTable() {
        mDatabaseHelper = DatabaseHelper.getInstance(LoadingActivity.this);
    }

    private void loadWiseSaying() {
        mTvWiseSaying = findViewById(R.id.loading_tv_wise_saying);
        mArrWiseSaying = new String[]{
                "효도르 부탁해 \n Made By Hyodor Bros.",
        };

        int randomIndex = (int) (Math.random() * mArrWiseSaying.length);
        String randomWiseSaying = mArrWiseSaying[randomIndex];
        mTvWiseSaying.setText(randomWiseSaying);
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        MainActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다

                // 액티비티를 넘어간 후 이전 액티비티를 삭제하고 싶다면 다음의 명령어를 사용한다.
                finish();
            }
        }, Constant.LOADING_DELAY_MILLIS);
    }

    private void retrofitGet() {
//        Call<ResponseBody> comment = mRetrofitApiService.getDummyEmployee(24494);
        Call<ResponseBody> comment = mRetrofitApiService.getDummyBackend();

        // retrofit의 통신은 background Thread에서 돌아갑니다.
        // 이렇게 실행된 결과값을 UI Thread에서 사용하기 위해서는 CallBack 함수가 필요합니다.

        // enqueue()는 비동기로 Request를 보내고 Response가 돌아 왔을 때 콜백으로 앱에게 알립니다.
        // 이 Request는 비동기식이므로 Retrofit에서 Main UI 스레드가 차단되거나 간섭받지 않도록 Background 스레드에서 Request를 처리합니다.
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                int responseCode = response.code();
                ResponseBody responseBody = (ResponseBody) response.body();

                try {
                    String token = response.headers().get("Content-Type");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String json = "";
                try {
                    json = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                // onResponse()를 호출하였다 하더라도, 무조건적인 성공이 보장되지는 않는다는 것 입니다.
                // 기획 또는 서버 개발자의 의도에 따라 서버에서 40x등의 응답코드를 전송할 경우라 할지라도, 실질적으로는 onResponse()를 호출합니다.
                // 따라서, onResponse() method 에서는 두번째 매개변수로 전달되는 response를 잘 활용하여,
                // 해당 응답이 20x의 응답코드를 가진 정상적인 응답인지, 40x등의 응답코드를 가진 요청은 정상적이였으나 실패된 응답인지 판단하는 추가적인 코드들은 필요할 수 있습니다.

                if ( response.isSuccessful() ) {
                    // 상태 코드가 성공을 나타내는 200~300 범위에 있는지 확인
                    // Do awesome stuff
                    mTvWiseSaying.setText(json);
                    Toast.makeText(getApplicationContext(), "Do awesome stuff", Toast.LENGTH_SHORT).show();

                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code

                    switch (statusCode) {
                        case HttpURLConnection.HTTP_NOT_FOUND:
                            // Handle not found
                            Toast.makeText(getApplicationContext(), "Handle not found", Toast.LENGTH_SHORT).show();
                            break;
                        case HttpURLConnection.HTTP_UNAUTHORIZED:
                            // Handle unauthorized
                            Toast.makeText(getApplicationContext(), "Handle unauthorized", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // onFailure()의 경우 정말 요청이 실패된 경우에 한해서만 호출이 됩니다.
                // 즉, 여기서 말하는 정말 요청이 실패된 경우라 하면,
                // 요청한 API 서버의 다운 / DB Query 중 오류 등와 같은 서버의 비정상적인 동작으로 인해 요청에 대한 응답을 받지 못하는 경우를 말합니다.
            }
        });
        // reference:
    }

    private void retrofitCreate() {

//        {"name":"test","salary":"123","age":"23"}

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "ceed1");
        jsonObject.addProperty("salary", "300");
        jsonObject.addProperty("age", "35");

        Call<ResponseBody> comment = mRetrofitApiService.createDummyEmployee(jsonObject);
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                int responseCode = response.code();
                ResponseBody responseBody = (ResponseBody) response.body();

                String json = "";
                try {
                    json = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    private void retrofitUpdate() {

//        {"name":"test","salary":"123","age":"23"}

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "ceed10");
        jsonObject.addProperty("salary", "3000");
        jsonObject.addProperty("age", "350");

        Call<ResponseBody> comment = mRetrofitApiService.updateDummyEmployee(24494, jsonObject);
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                int responseCode = response.code();
                ResponseBody responseBody = (ResponseBody) response.body();

                String json = "";
                try {
                    json = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    private void retrofitDelete() {

//        {"name":"test","salary":"123","age":"23"}

        Call<ResponseBody> comment = mRetrofitApiService.deleteDummyEmployee(24494);
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                int responseCode = response.code();
                ResponseBody responseBody = (ResponseBody) response.body();

                String json = "";
                try {
                    json = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

//    private void requestGet() {
//        httpConnection = OkHttpConnection.getInstance();
//        mThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                httpConnection.requestGet(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.e(TAG, "onFailure: " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) {
//                        try {
//                            String result = response.body().string();
//                            Log.e(TAG, "onResponse: " + result);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//        mThread.start();
//    }

//    private void requestPost() {
//        httpConnection = OkHttpConnection.getInstance();
//        mThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                httpConnection.requestPost(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.e(TAG, "onFailure: " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) {
//                        try {
//                            String result = response.body().string();
//                            Log.e(TAG, "onResponse: " + result);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//        mThread.start();
//    }
}
