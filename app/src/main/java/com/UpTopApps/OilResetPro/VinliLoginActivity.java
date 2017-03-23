package com.UpTopApps.OilResetPro;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import li.vin.net.Coordinate;
import li.vin.net.Device;
import li.vin.net.Location;
import li.vin.net.Page;
import li.vin.net.StreamMessage;
import li.vin.net.User;
import li.vin.net.Vehicle;
import li.vin.net.Vinli;
import li.vin.net.VinliApp;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class VinliLoginActivity extends AppCompatActivity {

    private final int DEFAULT_VALUE = -10101;
    private final String TAG = this.getClass().getSimpleName();

    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView phone;
    private LinearLayout deviceContainer;
    RequestQueue queue;

    private boolean contentBound;
    private boolean signInRequested;
    private VinliApp vinliApp;
    private CompositeSubscription subscription;
    private Subscription streamSubscription;
    private ListView list_device;
    private Adapter adp;
    private ArrayList<String> devices = new ArrayList<String>();
    private ArrayList<String> device_ids = new ArrayList<String>();
    @Override
    protected void onResume() {
        super.onResume();

        loadApp(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        loadApp(intent);
    }

    @Override
    protected void onPause(){
        super.onPause();

        // We don't want to continue the stream in the background.
        stopStream();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cleanupSubscription();
    }

    void onSignOutClick() {
        if (vinliApp != null && !isFinishing()) {
            stopStream();
          //  signIn();
            Vinli.clearApp(this);
            vinliApp = null;
            killAllCookies();
            finish();
        }
    }

    /**
     * Load the VinliApp instance if possible - if not, proceed by either signing in or finishing the
     * Activity.
     */
    private void loadApp(Intent intent) {
        if (vinliApp == null) {
            vinliApp = (intent == null)
                    ? Vinli.loadApp(this)
                    : Vinli.initApp(this, intent);
            if (vinliApp == null) {
                if (signInRequested) {
                    // If a sign in was already requested, it failed or was canceled - finish.
                    finish();
                } else {
                    // Otherwise, sign in.
                    signIn();
                }
            } else {
                // Succesfully loaded VinliApp - proceed.
                setupContent();
                subscribeAll();
            }
        }
    }

    /** Clear existing session state and sign in. */
    private void signIn() {
        signInRequested = true;
        setIntent(new Intent());
        Vinli.clearApp(this);
        vinliApp = null;
        killAllCookies();
        Vinli.signIn(this,
                getString(R.string.app_client_id), // Get your app id from dev.vin.li
                getString(R.string.app_redirect_uri), // Set your app redirect uri at dev.vin.li
                PendingIntent.getActivity(this, 0, new Intent(this, VinliLoginActivity.class), 0));
    }

    /** Set content view and bind views. Only do this once. */
    private void setupContent() {
        if (contentBound) return;
        setContentView(R.layout.activity_vinli_login);

       // firstName = (TextView) findViewById(R.id.first_name);
       // lastName = (TextView) findViewById(R.id.last_name);
       // email = (TextView) findViewById(R.id.email);
      //  phone = (TextView) findViewById(R.id.phone);
        deviceContainer = (LinearLayout) findViewById(R.id.device_container);
        ImageView btn_signout = (ImageView) findViewById(R.id.btn_singout);
        list_device = (ListView) findViewById(R.id.list_device);
        queue = Volley.newRequestQueue(this);

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("logout:", "clicked");
                onSignOutClick();
            }
        });

        list_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                // TODO Auto-generated method stub
                   Intent redirect = new Intent(VinliLoginActivity.this, MakeActivity.class);
                   // redirect.putExtra("vehicle", device_ids.get(position));


               // showProgressDialog();
                String url = "http://api.devfj.com/get-vinli-vehicles?device_id="+device_ids.get(position);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // mTxtDisplay.setText("Response: " + response.toString());
                              //  hideProgressDialog();
                                String res = null;

                                try {
                                    Log.d("Response: ",  response.toString());
                                    res = response.getString("vehicles");
                                    JSONArray jsonArray = new JSONArray(res);


                                    if(jsonArray.length() > 0){
                                        Intent intent = new Intent(VinliLoginActivity.this, VinliVehiclesDetail.class);
                                        ArrayList<CarsData> carsDatas = new ArrayList<CarsData>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("year",jsonArray.getJSONObject(i).getString("year") );
                                            jsonObject.put("make",jsonArray.getJSONObject(i).getString("make") );
                                            jsonObject.put("model",jsonArray.getJSONObject(i).getString("model") );
                                            jsonObject.put("video","none" );
                                            jsonObject.put("reset","reset" );
                                            CarsData obj = new CarsData(jsonObject);
                                            carsDatas.add(obj);
                                        }
                                        intent.putExtra("cars_datas", carsDatas);
                                        startActivity(intent);

                                    }else{

                                    }
                                  //  Log.d("Response: ",  res);
                                    //  if(res.equals("success")){



                                    // }else {
                                    //  Toast.makeText(EmailLoginActivity.this,res,Toast.LENGTH_LONG).show();
                                    // }
                                }catch (JSONException ex){
                                    Log.d("Response: ",  ex.getMessage());
                                }catch (NumberFormatException ex2){
                                    //Toast.makeText(EmailLoginActivity.this,res,Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                              //  hideProgressDialog();

                            }
                        })/*{
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            try {
                                String text = getString(R.string.app_client_id)+":T26eg_uZpC3P1n9Vv5";
                                Log.d("String",text);
                                byte[] data = text.getBytes("UTF-8");
                                Log.d("Byte",data+"");
                                Log.d("Encoded String",Base64.encodeToString(data, Base64.DEFAULT));
                                params.put("Authorization: Basic ", Base64.encodeToString(data, Base64.DEFAULT) );
                            }catch (java.io.UnsupportedEncodingException ex){
                                ex.printStackTrace();
                            }
                            return params;
                        }
                    }*/;

                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                        180000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsObjRequest);

            }
        });

        contentBound = true;
    }

    /** Permissively subscribe to all data. Clean up beforehand if necessary. */
    private void subscribeAll() {
        // Clean up an existing subscriptions that are ongoing.
        cleanupSubscription();

        // Sanity check.
        if (vinliApp == null || !contentBound) return;

        // Generic composite subscription to hold all individual subscriptions to data.
        subscription = new CompositeSubscription();

        // Remove all views from device container - best practice would be to use an AdapterView for
        // this, such as ListView or RecyclerView, but for this simple example it's less verbose to
        // do it this way.
        deviceContainer.removeAllViews();

        subscription.add(vinliApp.currentUser() // Fetch the current signed in user
                .observeOn(AndroidSchedulers.mainThread()) // Call onCompleted/onError/onNext on the main/UI thread
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error fetching user: " + e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                      //  setStyledText(getString(R.string.first_name), user.firstName(), firstName);
                      //  setStyledText(getString(R.string.last_name), user.lastName(), lastName);
                      //  setStyledText(getString(R.string.email), user.email(), email);
                      //  setStyledText(getString(R.string.phone), user.phone(), phone);
                    }
                }));

        // Loop through each of the user's devices...
        subscription.add(vinliApp.devices()
                .flatMap(Page.<Device>allItems()) // Get all devices from all pages
                .observeOn(AndroidSchedulers.mainThread()) // Run the onCompleted/onError/onNext on Android's main/UI thread
                .subscribe(new Subscriber<Device>() {
                    @Override
                    public void onCompleted() { // Called after we have finished fetching all devices.
                        adp = new Adapter(VinliLoginActivity.this, devices);
                        list_device.setAdapter(adp);
                    }

                    @Override
                    public void onError(Throwable e) { // Called if something goes wrong fetching devices.
                        Log.e(TAG, "Error fetching devices: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Device device) { // Called for each device that we fetch.
                        // Inflate device layout into device container. See above note about how using an
                        // AdapterView would be better if this weren't just a naive example.
                      //  View v = LayoutInflater.from(VinliLoginActivity.this).inflate(R.layout.device_layout, deviceContainer, false);
                      //  TextView deviceName = (TextView) v.findViewById(R.id.device_name);
                       // final TextView latestVehicle = (TextView) v.findViewById(R.id.latest_vehicle);
                       // final TextView latestLocation = (TextView) v.findViewById(R.id.latest_location);
                       // Button streamButton = (Button) v.findViewById(R.id.stream_button);
                       // deviceContainer.addView(v);

                      //  streamButton.setTag(device);

                       /* streamButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VinliLoginActivity.this.streamButtonPressed((Button) v);
                            }
                        });*/

                       // setStyledText(getString(R.string.device_name), (device.name() != null) ? device.name() : getString(R.string.unnamed_device), deviceName);
                        devices.add( device.name() );
                        device_ids.add(device.id());
                      /*  subscription.add(device.latestVehicle() // Get the latest vehicle for this device
                                .observeOn(AndroidSchedulers.mainThread()) // Run the onCompleted/onError/onNext on Android's main/UI thread
                                .subscribe(new Subscriber<Vehicle>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "Error fetching latest vehicle: " + e.getMessage());
                                    }

                                    @Override
                                    public void onNext(Vehicle vehicle) {
                                        String vehicleStr = (vehicle != null) ? vehicle.vin() : getString(R.string.none);
                                        setStyledText(getString(R.string.latest_vehicle), vehicleStr, latestVehicle);
                                    }
                                }));*/

                       /* subscription.add(device.latestlocation() // Get the latest location for this device.
                                .observeOn(AndroidSchedulers.mainThread()) // Run the onCompleted/onError/onNext on Android's main/UI thread.
                                .subscribe(new Subscriber<Location>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "Error fetching latest location: " + e.getMessage());
                                    }

                                    @Override
                                    public void onNext(Location location) {
                                        String locStr = (location != null) ? (location.coordinate().lat() + ", " + location.coordinate().lon()) : getString(R.string.none);
                                        setStyledText(getString(R.string.latest_location), locStr, latestLocation);
                                    }
                                }));*/
                    }
                }));
    }

    /** Unsubscribe all. Need to call this to clean up rx resources. */
    private void cleanupSubscription() {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) subscription.unsubscribe();
            subscription = null;
        }

        if(streamSubscription != null){
            if(!streamSubscription.isUnsubscribed()) streamSubscription.unsubscribe();
            streamSubscription = null;
        }
    }

    /**
     * Kill all WebView cookies. Need this to sign out & in properly, so WebView doesn't cache the
     * last session.
     */
    @SuppressWarnings("deprecation")
    private void killAllCookies() {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT >= 21) cookieManager.removeAllCookies(null);
        cookieSyncManager.sync();
        if (Build.VERSION.SDK_INT >= 21) cookieManager.flush();
    }

    private void setStyledText(String label, String value, TextView textView){
       // textView.setText(Html.fromHtml(String.format(getString(R.string.success_fmt), label, value)));
    }

    private void streamButtonPressed(Button button){
        Device device = (Device) button.getTag();

        // Lets only be streaming one device at a time, so kill the other stream off if it already exists.
        stopStream();

        // Create an array of ParametricFilter.Seeds to setup the stream with.
        ArrayList<StreamMessage.ParametricFilter.Seed> filterList = new ArrayList<>();
        // We only want to see StreamMessages that contain `rpm` in its data.
        filterList.add(StreamMessage.ParametricFilter.create().deviceId(device.id()).parameter("rpm").build());

        // Start the stream from the device object
        Log.i(TAG, "Starting stream for device: " + device.id());
        streamSubscription = device.stream(filterList, null) // Create the stream for this device.
                .observeOn(AndroidSchedulers.mainThread()) // Call onCompleted/onError/onNext on Android's main/UI thread.
                .subscribe(new Subscriber<StreamMessage>() { // To stop the stream, call streamSubscription.unsubscribe();
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error getting messages from stream: " + e.getMessage());
                    }

                    @Override
                    public void onNext(StreamMessage streamMessage) {
                        // We only want to look at publish messages from the stream. We don't want to see acks, filters, heartbeats, etc
                        if(streamMessage.getType() != null && streamMessage.getType().equals("pub")){
                            // Grab the RPM value from the StreamMessage, if RPM is not in the message it return DEFAULT_VALUE
                            int rpm = streamMessage.intVal(StreamMessage.DataType.RPM, DEFAULT_VALUE);
                            if (rpm != DEFAULT_VALUE) {
                                Log.i(TAG, "Rpm: " + rpm);
                            }

                            // Get the current location of the device from the stream. It defaults to null if its not in the message.
                            Coordinate coord = streamMessage.coord();
                            if (coord != null) {
                                Log.i(TAG, String.format("Latitude: %f Longitude %f", coord.lat(), coord.lon()));
                            }
                        }
                    }
                });
    }

    private void stopStream(){
        // Unsubscribe to stop the stream.
        if(streamSubscription != null && !streamSubscription.isUnsubscribed()){
            streamSubscription.unsubscribe();
        }
    }
}
