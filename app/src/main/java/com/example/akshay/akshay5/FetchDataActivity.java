package com.example.akshay.akshay5;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by AKSHAY on 29-01-2018.
 */

public class FetchDataActivity extends AsyncTask<Void , Void , Void> {

    String data = "";
    String dataParsed = "";
    String singleParsed = "";


    @Override
    protected Void doInBackground(Void... voids) {



        try {
            URL url = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json");

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){

                line = bufferedReader.readLine();
                data = data+ line;

            }
            JSONArray JA = new JSONArray(data);
            for(int i = 0; i <JA.length(); i++){
                JSONObject JO =(JSONObject) JA.get(i);
                singleParsed = "User ID : " +JO.get("user_id") + "\n"+
                            "No of Tweets : " +JO.get("count") + "\n";

                dataParsed = dataParsed + singleParsed;

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //UI THREAD


        DashboardActivity.tweetText.setText(this.dataParsed);
    }
}
