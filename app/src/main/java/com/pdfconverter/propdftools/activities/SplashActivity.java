package com.pdfconverter.propdftools.activities;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pdfconverter.propdftools.util.AdsUtility;
import com.pdfconverter.propdftools.util.MyApp;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import pdfconverterpro.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MyApp.requestQueue = Volley.newRequestQueue(getApplicationContext());
        MobileAds.initialize(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                MyApp.JSON_LINK,
                null,
                response -> {
                    try {
                        JSONObject GuideData = ((JSONObject) response).getJSONObject(MyApp.JSON_GUIDE_DATA);
                        JSONObject AdsController = GuideData.getJSONObject(MyApp.JSON_ADS);
                        MyApp.BannerAdmob = AdsController.getString(MyApp.JSON_BRADMOB);
                        MyApp.InterstitialAdmob = AdsController.getString(MyApp.JSON_INTADMOB);
                        MyApp.NativeAdmob = AdsController.getString(MyApp.JSON_NATADMOB);
                        MyApp.isJsonRetrieved = 1;
                        Log.d("TAGG", MyApp.BannerAdmob);

                        //AdsUtility.loadInterstitialAd(this, MyApp.InterstitialAdmob);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                /*if (AdsUtility.mInterstitialAd.isLoaded()){
                                    AdsUtility.mInterstitialAd.show();
                                    AdsUtility.mInterstitialAd.setAdListener(new AdListener(){
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    });
                                } else {
                                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                    //finish();

                                }*/
                            }
                        }, 3000);

                    } catch (JSONException e) {

                        Log.d("TAG", "Server: callAdsFromServer FAILED:" + e);
                        MyApp.isJsonRetrieved = 2;
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("TAG", "Server: callAdsFromServer Server Call FAILED:" + error);
                    MyApp.isJsonRetrieved = 2;
                });
        jsonObjectRequest.setShouldCache(false);
        MyApp.requestQueue.add(jsonObjectRequest);

    }
}