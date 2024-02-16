package com.gtappdevelopers.spotify_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String client_id = "40bef7b7664541cdbb7ef67a0dfa6eb9";
    String client_secret = "043983ea108a4d92831d27fe9d1669f6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // on below line calling method to
        // load our recycler views and search view.
        initializeAlbumsRV();
        initializePopularAlbumsRV();
        initializeTrendingAlbumsRV();
        initializeSearchView();
    }

    // below method is used to initialize search view.
    private void initializeSearchView() {
        // on below line initializing our
        // edit text for search views.
        EditText searchEdt = findViewById(R.id.idEdtSearch);
        searchEdt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // on below line calling method to search tracks.
                    searchTracks(searchEdt.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    // below method is used to open search
    // tracks activity to search tracks.
    private void searchTracks(String searchQuery) {
        // on below line opening search activity to
        // display search results in search activity.
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("searchQuery", searchQuery);
        startActivity(i);
    }

    // below method is used to get the token.
    private String getToken() {
        // on below line getting token from shared prefs.
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sh.getString("token", "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // on below line calling generate
        // token method to generate the token.
        generateToken();
    }

    private void generateToken() {
        // on below line creating a variable for
        // url to generate access token
        String url = "https://accounts.spotify.com/api/token?grant_type=client_credentials";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // on below line making a string request to generate access token.
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line getting access token and
                    // saving it to shared preferences.
                    String tk = jsonObject.getString("access_token");
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("token", "Bearer " + tk);
                    myEdit.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // on below line passing headers.
                // Make sure to add your authorization.
                String client_credentials = client_id + ":" + client_secret;
                String auth = "Basic " + android.util.Base64.encodeToString(client_credentials.getBytes(), android.util.Base64.NO_WRAP);
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        // adding request to queue.
        queue.add(request);
    }

    private void initializeAlbumsRV() {
        // on below line initializing albums rv
        RecyclerView albumsRV = findViewById(R.id.idRVAlbums);
        // on below line creating list, initializing adapter
        // and setting it to recycler view.
        ArrayList<AlbumRVModal> albumRVModalArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModalArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);
        // on below line creating a variable for url
        String url = "https://api.spotify.com/v1/albums?ids=3BGU0BqGwBkYDHpfCWFm7I,1h0Yw7Wm49GGiGpN2gd3o2,2z1B8rt2WK0K92x9XbCV24,3YFTFdg3bhcB8XAg85Rugh,4aawyAB9vmqN3uQ7FjRGTy";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // on below line making json object request to parse json data.
        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        // on below line adding data to array list.
                        albumRVModalArrayList.add(new AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // on below line passing headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // on below line adding request to queue.
        queue.add(albumObjReq);
    }

    private void initializePopularAlbumsRV() {
        // on below line creating list, initializing
        // adapter and setting it to recycler view.
        RecyclerView albumsRV = findViewById(R.id.idRVPopularAlbums);
        ArrayList<AlbumRVModal> albumRVModalArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModalArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);
        // on below line creating a variable for url
        String url = "https://api.spotify.com/v1/albums?ids=3BGU0BqGwBkYDHpfCWFm7I";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // on below line making json object request to parse json data.
        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        // on below line adding data to array list.
                        albumRVModalArrayList.add(new AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("TAG", "onResponse: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // on below line passing headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // on below line adding request to queue.
        queue.add(albumObjReq);
    }

    private void initializeTrendingAlbumsRV() {
        // on below line creating list, initializing adapter
        // and setting it to recycler view.
        RecyclerView albumsRV = findViewById(R.id.idRVTrendingAlbums);
        ArrayList<TrackRVModal> trackRVModals = new ArrayList<>();
        TrackRVAdapter trackRVAdapter = new TrackRVAdapter(trackRVModals, this);
        albumsRV.setAdapter(trackRVAdapter);

        String url = "https://api.spotify.com/v1/playlists/37i9dQZF1DX5baCFRgbF3x/tracks";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray itemsArray = response.getJSONArray("items");

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject trackObj = itemsArray.getJSONObject(i).getJSONObject("track");

                        // Extracting track information
                        String trackName = trackObj.getString("name");

                        // Extracting artist information
                        JSONArray artistsArray = trackObj.getJSONArray("artists");
                        String artistName = artistsArray.getJSONObject(0).getString("name");

                        // Extracting album information
                        JSONObject albumObj = trackObj.getJSONObject("album");
                        String albumId = trackObj.getString("id");
                        String external_urls = trackObj.getJSONObject("external_urls").getString("spotify");
                        // Adding data to array list
                        trackRVModals.add(new TrackRVModal(trackName, artistName, albumId, external_urls));
                    }

                    // Notifying the adapter that data has changed
                    trackRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("TAG", "onResponse: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error fetching data: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Adding headers
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

// Adding the request to the queue
        queue.add(albumObjReq);

    }
}
