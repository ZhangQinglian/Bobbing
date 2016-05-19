package com.zqlite.android.bobbing;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class BobbingActivity extends AppCompatActivity {

    XiaoHua xiaoHua;
    List<String> xiaohuaList;

    final int maxPages = 149;

    Handler handler = new Handler();

    BobbingView bobbingView;

    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bobbing);

        bobbingView = (BobbingView) findViewById(R.id.bobbing);
        bobbingView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        bobbingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xiaohuaList.size()>0){
                    String xiaohua = xiaohuaList.remove(0);
                    bobbingView.setBobbingText(xiaohua.split("</p><p>"));
                }
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.baidu.com/")
                .build();
        xiaoHua = retrofit.create(XiaoHua.class);
        xiaohuaList = new ArrayList<>(100);
        handler.post(new Runnable() {
            @Override
            public void run() {
                cacheXiaohua();
                handler.postDelayed(this, 10000);
            }
        });
        bobbingView.setBobbingText("loading ...");
    }

    private void cacheXiaohua() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<ResponseBody> response = xiaoHua.getXiaohua(new Random().nextInt(maxPages + 1)).execute();
                    String string = new String(response.body().bytes());
                    JSONObject jsonObjectXiaohua = new JSONObject(string);
                    JSONArray jsonArrayXiaohua = jsonObjectXiaohua.getJSONObject("showapi_res_body").getJSONArray("contentlist");
                    for (int i = 0; i < jsonArrayXiaohua.length(); i++) {
                        final String xiaohua = jsonArrayXiaohua.getJSONObject(i).getString("text");
                        addXiaohua(xiaohua);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private synchronized void addXiaohua(String xiaohua) {
        if(!xiaohua.contains("</p><p>")){
            xiaohua = xiaohua.replace("。”", ".\"</p><p>");
            xiaohua = xiaohua.replace("。“", ".\"</p><p>");
            xiaohua = xiaohua.replace("。", ".</p><p>");
            xiaohua = xiaohua.replace("；", ";</p><p>");
            xiaohua = xiaohua.replace("？”", "?\"</p><p>");
            xiaohua = xiaohua.replace("？“", "?\"</p><p>");
            xiaohua = xiaohua.replace("？", "?</p><p>");
            xiaohua = xiaohua.replace("！“", "!\"</p><p>");
            xiaohua = xiaohua.replace("！”", "!\"</p><p>");
            xiaohua = xiaohua.replace("！", "!</p><p>");
            xiaohua = xiaohua.replace(";", ";</p><p>");
            xiaohua = xiaohua.replace("</p> <p>", "</p><p>");
            xiaohua = xiaohua.replace("<\br>", "</p><p>");
        }
        xiaohuaList.add(xiaohua);
        if(first){
            final String tmp = xiaohuaList.remove(0);
            first = false ;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bobbingView.setBobbingText(tmp.split("</p><p>"));
                }
            });
        }
    }

    private interface XiaoHua {
        @Headers("apikey: cab696dddf89186ab642b05968253bf5")
        @GET("showapi_open_bus/showapi_joke/joke_text")
        Call<ResponseBody> getXiaohua(@Query("page") int page);
    }
}
