package com.xb.saykat.recyclerviewadmob;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final int ITEMS_PER_AD = 9;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    private RecyclerView recyclerView;
    private List<Object> recyclerViewItems = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position %9==0 ? 2 : 1);
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        addMenuItemsFromJson();
        addBannerAds();
        loadBannerAds();
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerViewAdapter(this,
            recyclerViewItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }
    private void addBannerAds() {
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(MainActivity.this);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(AD_UNIT_ID);
            recyclerViewItems.add(i, adView);
        }
    }
    private void loadBannerAds() {
        loadBannerAd(0);
    }
    private void loadBannerAd(final int index) {

        if (index >= recyclerViewItems.size()) {
            return;
        }

        Object item = recyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to" + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }
    private void addMenuItemsFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();
            JSONArray menuItemsJsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {

                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);

                String menuItemName = menuItemObject.getString("name");
                String menuItemDescription = menuItemObject.getString("description");
                String menuItemPrice = menuItemObject.getString("price");
                String menuItemCategory = menuItemObject.getString("category");
                String menuItemImageName = menuItemObject.getString("photo");

                MenuItem menuItem = new MenuItem(menuItemName, menuItemDescription, menuItemPrice,
                        menuItemCategory, menuItemImageName);
                recyclerViewItems.add(menuItem);
            }
        } catch (IOException | JSONException exception) {
            Log.e(MainActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
    }
    private String readJsonDataFromFile() throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = getResources().openRawResource(R.raw.menu_items_json);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }

}
