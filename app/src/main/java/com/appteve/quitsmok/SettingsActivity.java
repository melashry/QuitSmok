package com.appteve.quitsmok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class SettingsActivity extends AppCompatActivity implements AppData{

    SharedPreferences prefs;


    TextView changeUserData;
    TextView userPointText;
    TextView adSettingText;
    TextView disclamer;
    Button changrBtn;
    Button pointBtn;
    Button adBtn;
    Button disclamerBtn;
    BillingProcessor bp;
    private boolean readyToPurchase = false;
    private static final String LOG_TAG = "iabv3";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

       makeUI();

    }

    private  void  makeUI(){

        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        float point = prefs.getFloat(USER_POINT,0);


        changeUserData = (TextView) findViewById(R.id.changeUserDataText);
        changeUserData.setText(R.string.setting_change_user_data);

        userPointText = (TextView) findViewById(R.id.userPointText);
        userPointText.setText(getResources().getString(R.string.user_point_text) + " " + point);

        adSettingText = (TextView) findViewById(R.id.appAdText);


        disclamer = (TextView) findViewById(R.id.disclamer);
        disclamer.setText(R.string.disclamer);

        changrBtn = (Button) findViewById(R.id.changeBtn);
        changrBtn.setText(R.string.change_data);

        pointBtn = (Button) findViewById(R.id.pointBtn);
        pointBtn.setText(R.string.buy_point);



        adBtn = (Button) findViewById(R.id.adButton);
        adBtn.setText(R.string.remove_ads);

        disclamerBtn = (Button) findViewById(R.id.disclamerBtn);
        disclamerBtn.setText(R.string.disclamer_btn);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTabPosition(3);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_outsmocking) {

                    Intent startIntent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(startIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

                if (tabId == R.id.tab_awards) {

                    Intent startIntent = new Intent(SettingsActivity.this, AwardsActivity.class);
                    startActivity(startIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

                if (tabId == R.id.tab_health) {

                    Intent startIntent = new Intent(SettingsActivity.this, HealthActivity.class);
                    startActivity(startIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }



            }
        });


        if(!BillingProcessor.isIabServiceAvailable(this)) {
        }

        bp = new BillingProcessor(this, GOOGLE_BILLING_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {


                if (productId.equals(REMOVE_AD_ID)){


                    prefs.edit().putBoolean(AD_REMOVE, true).apply();
                    adSettingText.setText(R.string.ad_off);

                }

            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {


            }
            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;

            }
            @Override
            public void onPurchaseHistoryRestored() {
                for(String sku : bp.listOwnedProducts())
                    System.out.println("Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    System.out.println("Owned Subscription: " + sku);
            }
        });

        Boolean ad = prefs.getBoolean(AD_REMOVE,false);
        System.out.println("RECORD  AD  = = = = = = = = = = = = =  " + ad);

        if(ad){
            adSettingText.setText(R.string.ad_off);
            adBtn.setText(R.string.removed);

        } else {

            adSettingText.setText(R.string.ad_onn);
        }

    }

    public void changeUserDatas(View view){

        Intent startIntent = new Intent(SettingsActivity.this, UserDataActivity.class);
        startActivity(startIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }
    public void  buyPoints(View view){

        Intent startIntent = new Intent(SettingsActivity.this, BuyPointsActivity.class);
        startActivity(startIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void  removeAds(View view){

        bp.purchase(SettingsActivity.this,REMOVE_AD_ID);


    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }





    public void restoreAds(View view){

        bp.loadOwnedPurchasesFromGoogle();

    }

    public void disclamerText(View view){

        Intent startIntent = new Intent(SettingsActivity.this, DisclamerActivity.class);
        startActivity(startIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    @Override
    protected void onResume() {
        super.onResume();
        makeUI();
    }

}
