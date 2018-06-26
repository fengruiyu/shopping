package com.example.dell.shopping;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkUtils {
    private final OkHttpClient okHttpClient;
    private static OkUtils mokUtils;
    private final Handler mhandler;

    private OkUtils() {
        //创建handler
        mhandler = new Handler(Looper.getMainLooper());
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000,TimeUnit.MILLISECONDS)
                .writeTimeout(5000,TimeUnit.MILLISECONDS)
                .build();
    }
    public static OkUtils getokClient(){
        if (mokUtils == null){
            synchronized (OkUtils.class){
                if (mokUtils==null){
                    return mokUtils = new OkUtils();
                }
            }
        }
        return mokUtils;
    }
    public void doGet(String url, final IOkutils iOkutils){
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iOkutils.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null && response.isSuccessful()){
                    final String json = response.body().string();
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            iOkutils.onResponse(json);
                        }
                    });
                }
            }
        });
    }
    public void doPost(String url, Map<String,String> map, final IOkutils iOkutils){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key :map.keySet()){
            builder.add(key,map.get(key));
        }
        FormBody build = builder.build();
        Request request = new Request.Builder()
                .post(build)
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iOkutils.onFailure(e);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response !=null && response.isSuccessful()){
                    final String json = response.body().string();
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            iOkutils.onResponse(json);
                        }
                    });

                }
            }
        });
    }
    public interface IOkutils{
        void onFailure(IOException e);
        void onResponse(String json);
    }

}
