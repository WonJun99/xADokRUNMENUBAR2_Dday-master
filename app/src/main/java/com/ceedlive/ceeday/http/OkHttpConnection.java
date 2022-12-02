package com.ceedlive.ceeday.http;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@SuppressWarnings("unused")
public class OkHttpConnection {

    private OkHttpConnection() { this.mOkHttpClient = new OkHttpClient(); }

    private static OkHttpConnection mHttpConnection;
    private static OkHttpClient mOkHttpClient;

    public static OkHttpConnection getInstance() {
        if (mHttpConnection == null) {
            mHttpConnection = new OkHttpConnection();
        }
        return mHttpConnection;
    }

    /**
     * GET 예제
     * @param callback
     */
    public void requestGet(Callback callback) {

        // 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!

//        String url = "https://jsonplaceholder.typicode.com/todos/1";
//        String url = "https://my-json-server.typicode.com/typicode/demo/posts";
        String url = "http://127.0.0.1:3000/posts";

//        // URL에 포함할 Query문 작성 Name&Value
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//        urlBuilder.addEncodedQueryParameter("searchKey", searchKey);

        // Query문이 들어간 URL을 토대로 Request 생성
        Request request = new Request.Builder()
                .addHeader("Authorization", "TEST AUTH")
                .url(url)
                .build();

        // Http, Https 프로토콜에 따른 이슈

        // callback
        // 우리는 OkHttp를 사용하는 고객
        // 만들어진 Request를 서버로 요청할 Client 생성
        // Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public void requestPost(Callback callback) {

        // Http, Https 프로토콜에 따른 이슈

        String url = "http://127.0.0.1:3000/posts";

        Map paramMap = new HashMap();
        paramMap.put("id", "4");
        paramMap.put("title", "fake-server 4");
        paramMap.put("author", "ceedlive");

        Gson gson = new Gson();

        String json = gson.toJson(paramMap);
//        String json = "{ \"id\": 2, \"title\": \"Post 2\", \"author\": \"ceedlive\" }";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.replaceAll("\\\\", ""));

        Request request = new Request.Builder()
                .url("http://127.0.0.1:3000/posts")
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(callback);
    }

}
