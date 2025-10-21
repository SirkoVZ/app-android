package org.volkszaehler.volkszaehlerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity<ViewGroup> extends CustomMenuListActivity {
    private Context myContext;
    // Hashmaps for ListView
    private final ArrayList<HashMap<String, String>> channelValueList = new ArrayList<>();
    private String jsonStr = "";
    private Button refreshButton;
    private ProgressDialog pDialog;
    private SimpleAdapter adapter = null;
    private boolean bAutoReload = false;
    private String channelsToRequest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this;
        addListenerOnButton();
        ListView lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = channelValueList.get(position);

                Intent in = new Intent(getApplicationContext(), ChannelDetails.class);
                in.putExtra("tuplesWert", map.get("tuplesWert"));
                in.putExtra(Tools.TAG_UUID, map.get(Tools.TAG_UUID));
                in.putExtra("tuplesZeit", map.get("tuplesZeit"));

                startActivity(in);
            }
        });
        if (savedInstanceState != null) {
            jsonStr = savedInstanceState.getString("JSONStr");
            channelsToRequest = savedInstanceState.getString("ChannelsToRequest");
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
    }

    private void addListenerOnButton() {
        refreshButton = (Button) findViewById(R.id.buttonRefresh);

        refreshButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                channelValueList.clear();
                // setAdapter(null);

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                jsonStr = "";
                //the channels that are checked
                channelsToRequest = "";
                // adding uuids that are checked in preferences
                for (String preference : PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll().keySet()) {
                    // assume its a UUID of a channel
                    if (preference.contains("-") && preference.length() == 36) {
                        // is preference checked?
                        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(preference, false)) {
                            channelsToRequest = channelsToRequest + "&uuid[]=" + preference;
                        }
                    }
                }
                new GetJSONData().execute(channelsToRequest);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        bAutoReload = sharedPref.getBoolean("autoReload", false);
        if (bAutoReload) {
            refreshButton.performClick();
        } else if (!jsonStr.equals("")) {
            channelValueList.clear();
            new GetJSONData().execute(channelsToRequest);
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    private void addValuesToListView() {
        adapter = new SimpleAdapter(MainActivity.this, channelValueList, R.layout.list_item, new String[]{Tools.TAG_TITLE, Tools.TAG_DESCRIPTION, "tuplesWert"}, new int[]{R.id.channelName,
                R.id.channelDescription, R.id.channelValue}) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                HashMap<String, String> items = (HashMap<String, String>) getListView().getItemAtPosition(position);

                //empty color, default = blue
                String col = "".equals(items.get(Tools.TAG_COLOR)) ? "#0000FF" : items.get(Tools.TAG_COLOR);

                if (col.startsWith("#")) {
                    ((TextView) view.findViewById(R.id.channelName)).setTextColor(Color.parseColor(col.toUpperCase(Locale.getDefault())));
                    ((TextView) view.findViewById(R.id.channelValue)).setTextColor(Color.parseColor(col.toUpperCase(Locale.getDefault())));
                }
                // Workarounds for non existing Colors on S4Mini 4.2.2
                else if (col.equals("teal")) {
                    ((TextView) view.findViewById(R.id.channelName)).setTextColor(getResources().getColor(R.color.teal));
                    ((TextView) view.findViewById(R.id.channelValue)).setTextColor(getResources().getColor(R.color.teal));
                } else if (col.equals("aqua")) {
                    ((TextView) view.findViewById(R.id.channelName)).setTextColor(getResources().getColor(R.color.aqua));
                    ((TextView) view.findViewById(R.id.channelValue)).setTextColor(getResources().getColor(R.color.aqua));
                } else {
                    try {
                        ((TextView) view.findViewById(R.id.channelName)).setTextColor(Color.parseColor(col));
                        ((TextView) view.findViewById(R.id.channelValue)).setTextColor(Color.parseColor(col));
                    } catch (IllegalArgumentException e) {
                        Log.e("MainActivity", "Error setting color " + e.getMessage());
                    }
                }
                return view;
            }
        };
        setListAdapter(adapter);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("JSONStr", jsonStr);
        outState.putString("ChannelsToRequest", channelsToRequest);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    if (key.contains("-") && key.length() == 36) {
                        prefs.edit().putString(Tools.AllCheckedChannels,Tools.getCheckedChannels(myContext, false)).apply();
                    }
                }
            };


    private class GetJSONData extends AsyncTask<String, Void, Void> {

        boolean JSONFehler = false;
        String fehlerAusgabe = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (bAutoReload || jsonStr.equals("")) {
                // Showing progress dialog
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage(getString(R.string.please_wait));
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {

                JSONArray werte;
                SharedPreferences prefs = getSharedPreferences("JSONChannelPrefs", Activity.MODE_PRIVATE);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String JSONChannels = prefs.getString("JSONChannels", "");
                // are there really channels in the Prefs?
                if (JSONChannels.equals("")) {
                    JSONFehler = true;
                    fehlerAusgabe = getString(R.string.no_Channelinformation_found);
                    return null;
                }
                //the channels to request
                String uRLUUIDs = arg0[0];
                Log.d("MainActivity", "uRLUUIDs first: " + uRLUUIDs);

                // workaround removing empty string
                String[] channelsAusParameterMitLeerstring = arg0[0].split("&uuid\\[\\]=");
                ArrayList<String> allUUIDs = new ArrayList<>();
                for (String aChannelsAusParameterMitLeerstring : channelsAusParameterMitLeerstring) {
                    if (aChannelsAusParameterMitLeerstring.equals("")) {
                        // empty element
                        continue;
                    }
                    // add checked channel
                    allUUIDs.add(aChannelsAusParameterMitLeerstring);
                    // check for childs if above is a group
                    String childUUIDs = Tools.getHashMapBasedPropertyOfChannel(myContext, aChannelsAusParameterMitLeerstring, Tools.TAG_CHUILDUUIDS);
                    if (null != childUUIDs && !"".equals(childUUIDs)) {
                        if (childUUIDs.contains("|")) {
                            String[] children = (childUUIDs.split("\\|"));

                            for (String child : children) {
                                // add child
                                if (!allUUIDs.contains(child)) {
                                    allUUIDs.add(child);
                                    uRLUUIDs = uRLUUIDs + "&uuid[]=" + child;
                                    Log.d("MainActivity", " uRLUUIDs in Loop: " + uRLUUIDs);
                                }
                            }
                        }
                        // only one child
                        else {
                            if (!allUUIDs.contains(childUUIDs)) {
                                allUUIDs.add(childUUIDs);
                                uRLUUIDs = uRLUUIDs + "&uuid[]=" + childUUIDs;
                                Log.d("MainActivity", "uRLUUIDs only one Child: " + uRLUUIDs);
                            }
                        }
                        //fix Exception "Getting data is not supported for groups", remove group UUID
                        uRLUUIDs = uRLUUIDs.replace("&uuid[]=" + aChannelsAusParameterMitLeerstring, "");
                    }
                }

                // all channels
                ArrayList<HashMap<String, String>> allChannelsMapList = Tools.getChannelsFromJSONStringEntities(JSONChannels, myContext);

                for (HashMap<String, String> channelMap : allChannelsMapList) {
                    for (String channelAusParameter : allUUIDs) {
                        if (channelAusParameter.equals(channelMap.get(Tools.TAG_UUID))) {
                            if (!channelValueList.contains(channelMap)) {
                                channelValueList.add(channelMap);
                            }
                            break;
                        }
                    }
                }

                if (bAutoReload || jsonStr.equals("")) {
                    // Creating service handler class instance
                    ServiceHandler sh = new ServiceHandler();
                    String url = sharedPref.getString("volkszaehlerURL", "");

                    //long millisNow = System.currentTimeMillis();
                    url = url + "/data.json?from=now" + uRLUUIDs;

                    Log.d("MainActivity: ", "url: " + url);

                    String uname = sharedPref.getString("username", "");
                    String pwd = sharedPref.getString("password", "");

                    // Making a request to url and getting response
                    if (uname.equals("")) {
                        jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
                    } else {
                        jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, null, uname, pwd);
                    }
                    Log.d("MainActivity", "response: " + jsonStr);
                }

                if (jsonStr.startsWith("Error: ")) {
                    JSONFehler = true;
                    fehlerAusgabe = android.text.Html.fromHtml(jsonStr).toString();


                } else {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    if (!jsonObj.has(Tools.TAG_DATA)) {
                        JSONFehler = true;
                        fehlerAusgabe = fehlerAusgabe + "\n" + getString(R.string.no_ChannelsSelected);
                    } else {

                        // Getting JSON Array node
                        werte = jsonObj.getJSONArray(Tools.TAG_DATA);
                        // looping through All channels
                        for (int i = 0; i < werte.length(); i++) {
                            String maxWert = "";
                            String tuplesZeit = "";
                            String tuplesWert = "";
                            String minZeit = "";
                            String maxZeit = "";
                            String minWert = "";

                            JSONObject c = werte.getJSONObject(i);

                            String id = c.has(Tools.TAG_UUID) ? c.getString(Tools.TAG_UUID) : "";
                            // really the correct one?

                            String from = c.has(Tools.TAG_FROM) ? c.getString(Tools.TAG_FROM) : "";
                            String to = c.has(Tools.TAG_TO) ? c.getString(Tools.TAG_TO) : "";
                            if (c.has(Tools.TAG_MIN)) {
                                JSONArray minWerte = c.getJSONArray(Tools.TAG_MIN);
                                minZeit = minWerte.getString(0);
                                minWert = minWerte.getString(1);
                            }

                            if (c.has(Tools.TAG_MAX)) {
                                JSONArray maxWerte = c.getJSONArray(Tools.TAG_MAX);
                                maxZeit = maxWerte.getString(0);
                                maxWert = maxWerte.getString(1);
                            }
                            String average = c.has(Tools.TAG_AVERAGE) ? c.getString(Tools.TAG_AVERAGE) : "";
                            String consumption = c.has(Tools.TAG_CONSUMPTION) ? c.getString(Tools.TAG_CONSUMPTION) : "";
                            String rows = c.has(Tools.TAG_ROWS) ? c.getString(Tools.TAG_ROWS) : "";
                            if (c.has(Tools.TAG_TUPLES)) {
                                JSONArray tuples = c.getJSONArray(Tools.TAG_TUPLES);
                                if (tuples.length() < 1) {
                                    continue;
                                }
                                // only one tuple (in URL), otherwise loop here
                                JSONArray tupleWert = tuples.getJSONArray(0);
                                tuplesZeit = tupleWert.getString(0);
                                tuplesWert = tupleWert.getString(1);
                            }

                            int listSize = channelValueList.size();
                            if(sharedPref.getString("sortChannelMode", "off").equals("plain")) {
                                Collections.sort(channelValueList, new MyHashMapComparator());
                            }
                            // add values to existing Channels in List
                            String unit;
                            for (int j = 0; j < listSize; j++) {
                                HashMap<String, String> currentChannelFromList = channelValueList.get(j);
                                if (currentChannelFromList.containsValue(id)) {
                                    // add unit
                                    unit = Tools.getDefinitionValue(myContext, currentChannelFromList.get(Tools.TAG_TYPE), null, Tools.TAG_UNIT);
                                    // adding each child node to HashMap key =>
                                    // value
                                    currentChannelFromList.put(Tools.TAG_FROM, from);
                                    currentChannelFromList.put(Tools.TAG_TO, to);
                                    currentChannelFromList.put("minZeit", minZeit);
                                    currentChannelFromList.put("minWert", minWert);
                                    currentChannelFromList.put("maxZeit", maxZeit);
                                    currentChannelFromList.put("maxWert", maxWert);
                                    currentChannelFromList.put(Tools.TAG_AVERAGE, average);
                                    currentChannelFromList.put("tuplesZeit", tuplesZeit);
                                    currentChannelFromList.put("tuplesWert", tuplesWert + " " + unit);
                                    currentChannelFromList.put(Tools.TAG_CONSUMPTION, consumption);
                                    currentChannelFromList.put(Tools.TAG_ROWS, rows);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JSONFehler = true;
                fehlerAusgabe = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (JSONFehler) {
                jsonStr = "";
                new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.Error)).setMessage(fehlerAusgabe).setNeutralButton(getString(R.string.Close), null).show();
            } else {
                addValuesToListView();
            }

        }
    }
}
