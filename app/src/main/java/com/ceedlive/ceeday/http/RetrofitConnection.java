package com.ceedlive.ceeday.http;


import com.ceedlive.ceeday.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/***
 * 기본적으로 REST API 통신을 위해 구현된 라이브러리입니다.
 * AsyncTask 없이 background Thread에서 실행되며 callBack을 통해 main thread에서의 UI 업데이트를 간단하게 할수 있도록 제공합니다.
 */
public class RetrofitConnection {

    private static Retrofit mRetrofit;
    private static HttpLoggingInterceptor mHttpLoggingInterceptor;
    private static OkHttpClient mOkHttpClient;
    private static Retrofit.Builder mRetrofitBuilder;

    public static Retrofit getInstance(String baseUrl) {
        if (mRetrofit == null) {
            mHttpLoggingInterceptor = new HttpLoggingInterceptor();
            mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request originalRequest = chain.request();
//                    Response originalResponse = chain.proceed(originalRequest);

                    // if 'x-auth-token' is available into the response header
                    // save the new token into session.The header key can be
                    // different upon implementation of backend.

                    String newToken = "";
//                    try {
//                        newToken = originalResponse.header("X-auth-token");
//                    } catch (NullPointerException e) {
//                        e.printStackTrace();
//                        newToken = "";
//                    }

                    if ( "".equals(newToken) ) {
                        newToken = "ceedlive";
                    }

                    Request.Builder builder = originalRequest.newBuilder()
                            .header("X-authentication", newToken);

                    Request newRequest = builder.build();
                    Response newResponse = chain.proceed(newRequest);

                    return newResponse;
                }
            };
            // https://www.vogella.com/tutorials/Retrofit/article.html


            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(mHttpLoggingInterceptor)
                    .addInterceptor(interceptor)
                    .build();

            mRetrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl);

            if (BuildConfig.DEBUG) {
                mRetrofitBuilder.client(mOkHttpClient);
            }

            mRetrofit = mRetrofitBuilder
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

}
