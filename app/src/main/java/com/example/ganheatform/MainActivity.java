package com.example.ganheatform;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private TextView showResponseTextView;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = MainActivity.this;
        sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);

        showResponseTextView = (TextView) findViewById(R.id.ResponseTextView);
        findViewById(R.id.SubmitButton).setOnClickListener(new HandleClick());

        TextView minTempView = (TextView) findViewById(R.id.minTempEditText);
        TextView maxTempView = (TextView) findViewById(R.id.maxTempEditText);

        minTempView.addTextChangedListener(new MinTextChanger());
        maxTempView.addTextChangedListener(new MaxTextChanger());

        String savedMinTempText = sharedPref.getString(
                getResources().getString(R.string.SavedTempMinName),
                getResources().getString(R.string.TempDefaultMin));
        String savedMaxTempText = sharedPref.getString(
                getResources().getString(R.string.SavedTempMaxName),
                getResources().getString(R.string.TempDefaultMax));

        minTempView.setText(savedMinTempText);
        maxTempView.setText(savedMaxTempText);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String randomTempertureInRange(){
        double randomChildTemp;
        TextView minTempView = (TextView) findViewById(R.id.minTempEditText);
        TextView maxTempView = (TextView) findViewById(R.id.maxTempEditText);

        String minUserTempString = minTempView.getText().toString();
        String maxUserTempString = maxTempView.getText().toString();

        if (!minUserTempString .isEmpty() && !maxUserTempString .isEmpty()) {
            randomChildTemp = ThreadLocalRandom.current().nextDouble(
                    Double.parseDouble(minUserTempString),
                    Double.parseDouble(maxUserTempString));
        }
        else{
            randomChildTemp = ThreadLocalRandom.current().nextDouble(
                    Double.parseDouble(getResources().getString(R.string.TempDefaultMin)),
                    Double.parseDouble(getResources().getString(R.string.TempDefaultMax)));
        }

        DecimalFormat df = new DecimalFormat("##.#");
        return df.format(randomChildTemp);

    }

    private class MinTextChanger implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString( getResources().getString(R.string.SavedTempMinName),s.toString());
            editor.apply();
        }
    }

    private class MaxTextChanger implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString( getResources().getString(R.string.SavedTempMaxName),s.toString());
            editor.apply();
        }
    }

    private class HandleClick implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View arg0) {

            //this is a test form, sed for debugging
//            LocalDate today = LocalDate.now( ZoneId.of( "Asia/Jerusalem" ) );
//
//            OkHttpClient client = new OkHttpClient();
//            FormBody body = new FormBody.Builder()
//                    .add( "entry.1079298264", "blabla1111")
//                    .add("entry.1341503525", "blabla222222")
//                    .add("entry.1654962582_year", String.valueOf(today.getYear()))
//                    .add("entry.1654962582_month",String.valueOf(today.getMonthValue()))
//                    .add("entry.1654962582_day",String.valueOf(today.getDayOfMonth()))
//                    .build();
//            Request request = new Request.Builder()
//                    .url("https://docs.google.com/forms/d/e/1FAIpQLSfiT0JGIscsIn23xjo0jqltkt0_I90JKiFwpixvJ21xqffn1A/formResponse")
//                    .post( body )
//                    .build();
            // end test form

            LocalDate today = LocalDate.now( ZoneId.of( "Asia/Jerusalem" ) );

            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("entry.339401203_year", String.valueOf(today.getYear()))
                    .add("entry.339401203_month", String.valueOf(today.getMonthValue()))
                    .add("entry.339401203_day", String.valueOf(today.getDayOfMonth()))
                    .add("entry.1434417778", getResources().getString(R.string.ChildName))
                    .add("entry.156488808", getResources().getString(R.string.TZ))
                    .add("entry.274906281_year", getResources().getString(R.string.ChildBirthYear))
                    .add("entry.274906281_month", getResources().getString(R.string.ChildBirthMonth))
                    .add("entry.274906281_day", getResources().getString(R.string.ChildBirthDay))
                    .add("entry.456101517", getResources().getString(R.string.ParentName)) //parent name
                    .add("entry.1863663408",getResources().getString(R.string.CellPoneNumber))
                    .add("entry.1714597796", randomTempertureInRange())
                    .add("entry.1360832571",getResources().getString(R.string.ParentName)) // parent signature

                    .build();
            Request request = new Request.Builder()
                    .url("https://docs.google.com/forms/d/1s0dANZOVBaSacfSsTsz_nZVYuJ_m1-1nyuuEpc1TnDE/formResponse")
                    .post( body )
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Do something when request failed
                    e.printStackTrace();
                    System.out.println("Request Failed-------------------------------------------------------");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final boolean Success = response.isSuccessful();

                    if (!Success) {
                        System.out.println("Request Failed-------------------------------------------------------");
                        System.out.println("Error : " + response);

                    } else {
                        System.out.println("Request Succeeded-------------------------------------------------------");
                    }

                    // Read data in the worker thread
//                    final String data = response.body().string();
//                    System.out.println(data);

                    // Display the requested data on UI in main thread
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display requested url data as string into text view
                            if (Success) {
                                showResponseTextView.setText(getString(R.string.Success));
                                showResponseTextView.setBackgroundColor(Color.GREEN);
                            } else {
                                showResponseTextView.setText(getString(R.string.Fail));
                                showResponseTextView.setBackgroundColor(Color.RED);
                            }
                        }
                    });
                }
            });
        }
    }
}
