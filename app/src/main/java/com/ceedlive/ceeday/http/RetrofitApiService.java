package com.ceedlive.ceeday.http;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitApiService {

    // FIXME localhost or 127.0.0.1 호스트로 접근 시 onFailure 떨어짐
    public static final String API_URL = "http://e08ec768.ngrok.io";

//    public static final String API_URL = "http://dummy.restapiexample.com/api/v1/";
//    public static final String API_URL = "http://localhost:9090";
//    public static final String API_URL = "http://192.0.0.5:9090";

//    public static final String API_URL = "https://jsonplaceholder.typicode.com";

//    @GET("posts/{id}")
//    Call<ResponseBody> getComment(@Path("id") int id);

    // reference: http://dummy.restapiexample.com
    // https://interconnection.tistory.com/73
    // https://devuryu.tistory.com/44
    // https://falinrush.tistory.com/5
    // http://chuumong.github.io/android/2017/01/13/Get-Started-With-Retrofit-2-HTTP-Client
    // https://www.vogella.com/tutorials/Retrofit/article.html
    // https://futurestud.io/tutorials/retrofit-2-how-to-update-objects-on-the-server-put-vs-patch
    // https://stackoverflow.com/questions/36251080/retrofit-2-0-how-to-delete

    /**
     * OK
     * @return
     */
    @GET("posts")
    Call<ResponseBody> getDummyBackend();

    /**
     * OK
     * @param id
     * @return
     */
    @GET("employee/{id}")
    Call<ResponseBody> getDummyEmployee(@Path("id") int id);

    /**
     * OK
     * @param jsonObject
     * @return
     */
    @POST("create")
    Call<ResponseBody> createDummyEmployee(@Body JsonObject jsonObject);

    /**
     * OK
     * @param id
     * @param jsonObject
     * @return
     */
    @PUT("update/{id}")
    Call<ResponseBody> updateDummyEmployee(@Path("id") int id, @Body JsonObject jsonObject);

    /**
     * OK
     * @param id
     * @return
     */
    @DELETE("delete/{id}")
    Call<ResponseBody> deleteDummyEmployee(@Path("id") int id);
}
