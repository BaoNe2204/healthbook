package com.example.healthbook.network;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Dùng IP 192.168.1.4 thay vì 10.0.2.2 để có thể chạy được trên cả Máy Ảo và Máy Thật (cùng WiFi)
    private static final String BASE_URL = "http://192.168.1.4:3000/";
    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                
                if (user != null) {
                    try {
                        Task<GetTokenResult> task = user.getIdToken(false);
                        GetTokenResult result = Tasks.await(task);
                        String token = result.getToken();
                        
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + token);
                        return chain.proceed(requestBuilder.build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                return chain.proceed(original);
            }
        }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
