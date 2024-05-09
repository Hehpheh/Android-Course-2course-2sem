package com.example.myapplicationjava;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends Activity {
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;
    private ImageView weatherIconImageView;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);
        weatherIconImageView = findViewById(R.id.image_view_weather_icon);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Определение locationListener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                String key = "29d184acbe17692c252278dac81ed9d8";
                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + key + "&units=metric&lang=ru";
                new GetURLData().execute(url);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Запрос обновления местоположения
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        // Обработчик нажатия кнопки
        main_btn.setOnClickListener(view -> {
            String city = user_field.getText().toString().trim();
            if (city.equals("")) {
                Toast.makeText(WeatherActivity.this, "Введите город!", Toast.LENGTH_SHORT).show();
            } else {
                String key = "29d184acbe17692c252278dac81ed9d8";
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
                new GetURLData().execute(url);
            }
        });
    }

    // AsyncTask для получения данных о погоде
    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append('\n');
                }
                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String icon = weather.getString("icon");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String humidity = jsonObject.getJSONObject("main").getString("humidity");
                String pressure = jsonObject.getJSONObject("main").getString("pressure");

                result_info.setText("Температура: " + temp + "\n" +
                        "Влажность: " + humidity + "%" + "\n" +
                        "Давление: " + pressure + " hPa");

                String imageUrl = "https://openweathermap.org/img/wn/" + icon + ".png";
                Glide.with(WeatherActivity.this).load(imageUrl).into(weatherIconImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
