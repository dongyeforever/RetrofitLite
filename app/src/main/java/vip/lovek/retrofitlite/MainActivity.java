package vip.lovek.retrofitlite;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import vip.lovek.retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView tvPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
        tvPost = findViewById(R.id.tvPost);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restapi.amap.com")
                .build();
        WeatherApi api = retrofit.create(WeatherApi.class);

        doGet(api);
        doPost(api);
    }

    private void doGet(WeatherApi api) {
        api.getWeather("110101", "e5c31f97d5677cc4887c5f90a18422a0").enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Log.e("TAG", result);
                runOnUiThread(() -> textView.setText(result));
            }
        });
    }

    private void doPost(WeatherApi api) {
        api.postWeather("110101", "e5c31f97d5677cc4887c5f90a18422a0").enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Log.e("TAG", result);
                runOnUiThread(() -> tvPost.setText(result));
            }
        });
    }
}