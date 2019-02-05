package com.example.adeeb.ParkingToronto;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class AboutActivity  extends AppCompatActivity
{

    private String TAG = AboutActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private static String url = "https://www.toronto.ca/ext/open_data/catalog/data_set_files/greenPParking2015.json";
    ArrayList<HashMap<String, String>> parkingList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        parkingList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetLocations().execute();

    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetLocations extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AboutActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray lots = jsonObj.getJSONArray("carparks");

                    // looping through All lots
                    for (int i = 0; i < lots.length(); i++) {
                        JSONObject c = lots.getJSONObject(i);

                        String id = c.getString("id");
                        String address = c.getString("address");
                        String lat = c.getString("lat");
                        String lng = c.getString("lng");
                        String capacity = c.getString("capacity");
                        String rate = c.getString("rate");
                        String carpark_type = c.getString("carpark_type");

                        // tmp hash map for single lots
                        HashMap<String, String> lot = new HashMap<>();

                        // adding each child node to HashMap key => value
                        lot.put("id", id);
                        lot.put("address", address);
                        lot.put("lat", lat);
                        lot.put("lng", lng);
                        lot.put("capacity", capacity);
                        lot.put("rate", rate);
                        lot.put("carpark_type", carpark_type);

                        // adding lot to lots list
                        parkingList.add(lot);

                    }
                } catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter
                    (
                            AboutActivity.this, parkingList,

                            R.layout.list_item,

                            new String[]{"address", "lat", "lng","capacity","rate","carpark_type","title"},

                            new int[]{R.id.name, R.id.email, R.id.mobile, R.id.capacity, R.id.rate, R.id.carpark_type });

            lv.setAdapter(adapter);
        }

    }
}