package com.ceedlive.ceeday;

import android.util.Log;

import com.ceedlive.ceeday.http.OkHttpConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OkHttpUnitTest {

    String TAG = "OkHttpUnitTest";
    OkHttpConnection mHttpConnection;
    Thread mThread;

    @Before
    public void setUp() {
        mHttpConnection = OkHttpConnection.getInstance();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void requestGet_isOk() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mHttpConnection.requestGet(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            String result = response.body().string();

                            JSONObject jsonObject = new JSONObject(result);
                            System.out.println(jsonObject.toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mThread.start();
    }

    @Test
    public void requestPost_isOk() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mHttpConnection.requestPost(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            String result = response.body().string();

                            System.out.println(result);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mThread.start();
    }

}
