package com.example.huilee.okhttpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String mBaseUrl = "http://192.168.1.105:8080/webtest/";
    OkHttpClient client = new OkHttpClient();

    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
    }

    public void doPost(View view) {
        //1.拿到okhttpclient对象

        FormBody.Builder build = new FormBody.Builder();
        //2.构造request
        //2.1 构造requestBody
        RequestBody body = build.add("username", "huilee").add("password", "123").build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "login").post(body).build();

        //3.将request封装为call
        executeRequest(request);
    }

    public void doPostFile(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "banner2.jpg");
        if (file.exists()) {
            LogUtil.e(file.getAbsolutePath() + "not exist!");
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-steam"), file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postString").post(requestBody).build();
        executeRequest(request);
    }

    public void doUpload(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "banner2.jpg");
        if (file.exists()) {
            LogUtil.e(file.getAbsolutePath() + "not exist!");
            return;
        }

        MultipartBody.Builder builder1 = new MultipartBody.Builder();
        MultipartBody body = builder1.setType(MultipartBody.FORM)//
                .addFormDataPart("username", "huilee")//
                .addFormDataPart("password", "123")//
                .addFormDataPart("upload", "banner.jpg", RequestBody.create(MediaType.parse("application/octet-steam"), file))
                .build();

        CountingRequestBody countingRequestBody = new CountingRequestBody(body, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long byteWrited, long contentLength) {
                LogUtil.e(byteWrited+"/"+contentLength);
            }
        });

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "uploadInfo").post(countingRequestBody).build();
        executeRequest(request);
    }


    public void doPostString(View view) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"), "{username:huilee,password:123}");
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postString").post(requestBody).build();
        executeRequest(request);
    }

    public void doGet(View view) throws IOException {
        //1.拿到okhttpclient对象
//        OkHttpClient client = new OkHttpClient();

        //2.构造request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(mBaseUrl + "login?username=huilee&password=123").build();
        executeRequest(request);
    }


    private void executeRequest(Request request) {
        //3.将request封装为call
        Call call = client.newCall(request);

        //4.执行call
//        Response response = call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("onFailure:" + e.getMessage());
                e.getStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.e("onResponse:");
                final String res = response.body().string();
                LogUtil.e(res);

                InputStream inputStream = response.body().byteStream();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //处理ui线程
                        text.setText(res);
                    }
                });
            }
        });
    }


}
