package com.UpTopApps.OilResetPro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.UpTopApps.OilResetPro.util.IabHelper;
import com.UpTopApps.OilResetPro.util.IabResult;
import com.UpTopApps.OilResetPro.util.Inventory;
import com.UpTopApps.OilResetPro.util.Purchase;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    IabHelper mHelper;
    String TAG = "SettingsActivity";
    static final String ITEM_SKU = "com_uptopapps_oilresetpro_removeads";// New One 2.99 / updated
//	static final String ITEM_SKU = "com_uptopapps_oilresetpro_removeads"; // Old one 0.99
//	static final String ITEM_Un_Block_Cars = "com_uptopapps_oilresetpro_dummyunlockcar1";
private ProgressDialog dialog;
    protected boolean in_App_Billing;
    protected boolean in_App_Billing_disable = true;
    ImageView remove_aids, restore_purchase, btn_intro;
    private String ITEM_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyFE57xU65ismtmOxbpZc4h/CnesHpIOvjVF0+CpeVeVRRrOYuK6gDxqNJ0OQLlfqn/DRrx7qS12AFm3Rv4BKpO04d5CLuOqwTQVSx+6ZF/3spv2UE0zLUguKbTA0BsxV7dy0WMT4nNS3Ft+q977fWVf1cgDiZVMKXEBogyFxNZHO4ShNcSv5UHRrt09sH+lEbyRYezoLqXWLgQlzlW98RMPUhzLzAkS3CzWGP60eChnAzKWmDyLUKIFNCwDXkwxy0pVSFoRhFe4RTQsUgmr9p48Xl4PBRwzwe5xQhogFaPkmDyMHa06owfuMhuLRcQDiRVjE31mSUFDK7d0esp7ONQIDAQAB";

        restore_purchase = (ImageView) findViewById(R.id.restore_purchase);
        remove_aids = (ImageView) findViewById(R.id.remove_aids);
        btn_intro = (ImageView) findViewById(R.id.btn_intro);
        restore_purchase.setOnClickListener(this);
        remove_aids.setOnClickListener(this);
        btn_intro.setOnClickListener(this);
        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    in_App_Billing_disable = true;
                }else {
                    in_App_Billing_disable = false;
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.remove_aids:
                ITEM_id = ITEM_SKU;
                try {
                    mHelper.launchPurchaseFlow(this, ITEM_id, 10001,
                            mPurchaseFinishedListener, "");
                } catch (IabHelper.IabAsyncInProgressException e) {
                   // complai("Error launching purchase flow. Another async operation in progress.");
                  //  setWaitScree(false);
                }
                break;
            case R.id.btn_intro:
                startActivity(new Intent(SettingsActivity.this, IntroActivity.class));
                break;
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                // consume the gas and update the UI
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            mHelper.disposeWhenFinished();
        mHelper = null;
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result.isFailure()) {
                // handle error here
//				Log.e("Error", "error");
            } else {
                if (ITEM_id.length() == 0 || ITEM_id.equalsIgnoreCase(ITEM_SKU)) {
                    Purchase ttu = inventory.getPurchase(ITEM_SKU);
                    if (ttu != null) {
                        // mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        // mConsumeFinishedListener);
                        ttu.getPackageName();
                        ttu.getSku();

                        remove_aids.setImageDrawable(getResources().getDrawable(R.mipmap.in_app_purchase_1_done));

                        Create_Alert(0, "You have successfully restore the item");

                    }
                    else{
                        Create_Alert(0,
                                "We are unable to restore this item");
                    }
                }
                else{


                }
            }
        }
    };

    void Create_Alert(final int response, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SettingsActivity.this);
        String OK;
        String Cancel = "Cancel";
        if (response == 7) {
            OK = "Restore";

        } else {
            OK = "OK";
        }
        alertDialogBuilder.setMessage(Message).setCancelable(false)
                .setPositiveButton(OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        if (response == 7) {
                            dialog = ProgressDialog.show(SettingsActivity.this,
                                    "", "Loading", true);
                            try {
                                mHelper.queryInventoryAsync(mGotInventoryListener);
                            }catch (IabHelper.IabAsyncInProgressException e){

                            }

                        }
                    }
                });
        if (response == 7) {
            alertDialogBuilder.setNegativeButton(Cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
        }
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}