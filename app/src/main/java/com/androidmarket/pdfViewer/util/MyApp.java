package com.androidmarket.pdfViewer.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;



import org.json.JSONException;
import org.json.JSONObject;

public class MyApp extends MultiDexApplication {

    public interface AdFinished {
        void onAdFinished();
    }

    // Interstitial
    private     InterstitialAd  mInterstitialAd;
    public static final int     INTERVAL        = 2;
    public static int           AttemptToShowInterstitial = 0;

    // Load Json from Serv
    public static RequestQueue  requestQueue;
    public static String        BannerAdmob         = "ca-app-pub-3940256099942544/6300978111";
    public static String        InterstitialAdmob   = "ca-app-pub-3940256099942544/1033173712";
    public static String        NativeAdmob         = "ca-app-pub-3940256099942544/224769611";
    public static String        JSON_LINK           = "https://raw.githubusercontent.com/BigDaddyReskin/Json/main/pdfViewer";
    public static int           isJsonRetrieved     = 0;

    // Read JSON
    public static String        JSON_GUIDE_DATA     = "UpadeInfo";
    public static String        JSON_ADS            = "AdsController";
    public static String        JSON_BRADMOB        = "BannerAdmob";
    public static String        JSON_INTADMOB       = "InterstitialAdmob";
    public static String        JSON_NATADMOB       = "NativeAdmob";

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * HOW TO USE :
     *
     * /// To Show A banner ad ///
     *
     * STEP 1
     * Add the line below in the end of the onCreate method in any activity class related to a layout that has a banner container
     * MyApp.displayBannerAd(getApplicationContext(),findViewById(R.id.adMobView));
     *
     *
     * STEP 2
     * A banner container is something like this :
     * 	<RelativeLayout
     * 	                android:id="@+id/adMobView"
     * 	                android:layout_width="match_parent"
     * 	                android:layout_height="wrap_content"
     * 	                android:layout_alignParentBottom="true"/>
     *
     * /// To Show An Interstitial ///
     *
     * OPTION 1:
     *
     * Use the line below
     * MyApp myApp = new MyApp(); myApp.showInterstitial(activity);
     *
     * OPTION 2:
     * To show an interstitial in intervals use this instead:
     * MyApp myApp = new MyApp(); myApp.switcherLoadInter(activity);
     * Don't forget to change the constant INTERVAL
     *
     *
     * TO OPTIMIZE THE INTERSTITIAL, and to show it more often
     * findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
     *     @Override
     *     public void onClick(View v) {
     *         MyApp myApp = new MyApp(); myApp.showInterstitial(activity);
     *     }
     * }
     *
     */

    /** //////////////////////////////////
     *  //////// RETRIEVE JSON ///////////
     *  //////////////////////////////////

     /**
     * This method loads an ad by sending a GET request to the specified JSON link using Volley library.
     * The response is then parsed and the ad unit ID is extracted from it and set to the ad.
     * If the request is successful, the ad is loaded. If the request fails, an error message is displayed.
     *
     * @param context The context of the current activity.
     */
    public static void retrieveJSON(Context context)
    {
        Log.d("TAG", "Server: callAdsFromServer Started ");
        requestQueue = Volley.newRequestQueue(context);
        MobileAds.initialize(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                JSON_LINK,
                null,
                response -> {
                    try {
                        JSONObject GuideData = ((JSONObject) response).getJSONObject(JSON_GUIDE_DATA);
                        JSONObject AdsController = GuideData.getJSONObject(JSON_ADS);
                        MyApp.BannerAdmob = AdsController.getString(JSON_BRADMOB);
                        MyApp.InterstitialAdmob = AdsController.getString(JSON_INTADMOB);
                        MyApp.isJsonRetrieved = 1;
                        Log.d("TAG", "Server: We received the JSON.");

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
        requestQueue.add(jsonObjectRequest);
    }

    /** ///////////////////////////////////
     *  //////// BANNER METHODS ///////////
     *  ///////////////////////////////////


     /**
     * This method displays an ad in the specified ad container.
     * If the JSON for the ad has not been loaded yet, the method will call callAdsFromServer to load the ad.
     * Once the JSON is loaded, the ad is displayed by calling {@link #showBannerAd(Context, View)}
     *
     * @param context The context of the current activity.
     * @param adContainer The container that the ad will be displayed in.
     */
    public static void displayBannerAd(Context context, View adContainer)
    {
        if(isJsonRetrieved == 1)
        {
            showBannerAd(context,adContainer);
        }
        else
        {
            // We will not show the banner but we will just try to retrieve again the json
            retrieveJSON(context);
        }
    }

    /**
     * Add the banner to the layout
     */
    private static void showBannerAd(Context context, View adContainer)
    {
        // Ad View
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(MyApp.BannerAdmob);

        // Container
        ((RelativeLayout)adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    /** /////////////////////////////////////////
     *  //////// Interstitial METHODS ///////////
     *  /////////////////////////////////////////

     /**
     * Method that will be called in other classes by using :
     * MyApp myApp = new MyApp(); myApp.showInterstitial(activity);
     * @param activity the activity where the ad will be displayed
     */
    public void showInterstitial(Activity activity){
        displayFullScreenInterstitial(new MyApp.AdFinished() {
            @Override
            public void onAdFinished() {
                // continue with the rest of your code
                Log.d("TAG", "Ad Finished ");
            }
        },activity);
    }

    /**
     * Used in above to show inter
     * @param adFinished an instance of MyApp.AdFinished interface
     * @param activity the activity where the ad will be displayed
     */
    private void displayFullScreenInterstitial(final MyApp.AdFinished adFinished, Activity activity) {
        Log.d("TAG", "Start of showInter: ");
        if (mInterstitialAd != null) {
            Log.d("TAG", "mInterstitialAd Not null: ");
            mInterstitialAd.show(activity);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    adFinished.onAdFinished();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    adFinished.onAdFinished();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    AttemptToShowInterstitial++;
                    Log.d("TAG", "interLaunchCount is now:  " + AttemptToShowInterstitial);
                }
            });
        } else {
            loadInterstitial(activity);
        }
    }

    /**
     * Will load then show interstitial
     * @param activity the activity where the ad will be displayed
     */
    private void loadInterstitial(Activity activity)
    {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, MyApp.InterstitialAdmob, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                Log.d("TAG", "Interstitial loaded successfully ");
                mInterstitialAd = interstitialAd;
                showInterstitial(activity);
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("TAG", "Failed to load Interstitial");
                // Handle the error
                mInterstitialAd = null;
            }
        });
    }

    /**
     * You cann this method instead of showInterstitial to only display the Interstitial in intervals
     *
     * @param activity Current activity
     */
    public void switcherLoadInter(Activity activity)
    {
        if(AttemptToShowInterstitial % INTERVAL == 0)
        {
            showInterstitial(activity);
        }
        else
        {
            AttemptToShowInterstitial++;
        }
    }

}
