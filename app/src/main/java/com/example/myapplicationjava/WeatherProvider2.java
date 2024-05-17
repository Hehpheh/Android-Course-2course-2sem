package com.example.myapplicationjava;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class WeatherProvider2 extends AppWidgetProvider {

    private static final String API_KEY = "29d184acbe17692c252278dac81ed9d8";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWeatherData(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWeatherData(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        new FetchWeatherTask(context, appWidgetManager, appWidgetId).execute();
    }

    private static PendingIntent getPendingSelfIntent(Context context, int appWidgetId, String action) {
        Intent intent = new Intent(context, WeatherProvider2.class);
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void downloadWeatherIcon(Context context, String url, RemoteViews views, int appWidgetId) {
        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.weather_icon, views, appWidgetId);
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(url)
                .into(appWidgetTarget);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("update".equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                updateWeatherData(context, appWidgetManager, appWidgetId);
            }
        }
    }


    private static class FetchWeatherTask extends AsyncTask<Void, Void, WeatherData> {
        private Context mContext;
        private AppWidgetManager mAppWidgetManager;
        private int mAppWidgetId;

        FetchWeatherTask(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
            mContext = context;
            mAppWidgetManager = appWidgetManager;
            mAppWidgetId = appWidgetId;
        }

        @Override
        protected WeatherData doInBackground(Void... voids) {

            String location = "Moscow";


            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + API_KEY + "&units=metric&lang=ru";


            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();


                return parseWeatherData(response.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeatherData weatherData) {
            if (weatherData != null) {

                updateWidget(mContext, mAppWidgetManager, mAppWidgetId, weatherData);
            }
        }

        private static WeatherData parseWeatherData(String response) throws JSONException {
            JSONObject jsonObject = new JSONObject(response);

            String location = jsonObject.getString("name");
            JSONObject main = jsonObject.getJSONObject("main");
            String temperature = main.getString("temp");
            String feelsLike = main.getString("feels_like");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObj = weatherArray.getJSONObject(0);
            String iconCode = weatherObj.getString("icon");
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

            return new WeatherData(location, temperature + "°C", feelsLike + "°C", iconUrl);
        }
    }


    private static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, WeatherData weatherData) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_provider2);

        views.setTextViewText(R.id.location_text, weatherData.getLocation());
        views.setTextViewText(R.id.temperature_text, weatherData.getTemperature());
        views.setTextViewText(R.id.feels_like_text, "Ощущается как: " + weatherData.getFeelsLike());


        downloadWeatherIcon(context, weatherData.getIconUrl(), views, appWidgetId);


        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        }
        String lastUpdate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lastUpdate = "Последнее обновление: " + sdf.format(new Date());
        }
        views.setTextViewText(R.id.last_update_text, lastUpdate);


        views.setOnClickPendingIntent(R.id.update_button, getPendingSelfIntent(context, appWidgetId, "update"));


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    private static class WeatherData {
        private String location;
        private String temperature;
        private String feelsLike;
        private String iconUrl;

        WeatherData(String location, String temperature, String feelsLike, String iconUrl) {
            this.location = location;
            this.temperature = temperature;
            this.feelsLike = feelsLike;
            this.iconUrl = iconUrl;
        }

        public String getLocation() {
            return location;
        }

        public String getTemperature() {
            return temperature;
        }

        public String getFeelsLike() {
            return feelsLike;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }
}