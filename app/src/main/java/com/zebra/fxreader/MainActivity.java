package com.zebra.fxreader;

import static com.zebra.fxreader.RFIDController.mReader;
import static com.zebra.fxreader.RFIDController.tagListMap;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.zebra.fxreader.databinding.ActivityMainBinding;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.LoginInfo;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.READER_TYPE;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderManagement;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.SECURE_MODE;
import com.zebra.rfid.api3.TagData;

import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements RfidEventsListener, ReaderConnection {

    private static final String TAG = "FXReader MainActivity";
    private AppBarConfiguration appBarConfiguration;

    ReaderManagement readerManagement;
    LoginInfo loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.zebra.fxreader.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RFIDController.viewModel = new ViewModelProvider(this).get(TagDataViewModel.class);

//        login();
    }

    private void login(){

        mReader = new RFIDReader("10.17.231.218",5084,5000);
        readerManagement = new ReaderManagement();

        loginInfo= new LoginInfo("10.17.231.218", "admin", "Zebra@123", SECURE_MODE.HTTPS, true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                readerManagement.login(loginInfo, READER_TYPE.FX);
            } catch (InvalidUsageException | OperationFailureException e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void eventReadNotify(RfidReadEvents rfidReadEvents) {
        if (mReader != null) {
            Log.d(TAG, "eventReadNotify");
            TagData[] myTag = mReader.Actions.getReadTags(100);
            if (myTag != null) {
                for (TagData tagData : myTag) {
                    final String tag_id = tagData.getTagID();

                    if (tagListMap.containsKey(tag_id)) {
                        InventoryItem inventoryItem = tagListMap.get(tag_id);
                        if (inventoryItem != null) {
                            tagListMap.put(tag_id, new InventoryItem(tag_id, inventoryItem.getCount() + 1, tagData.getPeakRSSI()));
                        }
                    } else {
                        tagListMap.put(tag_id, new InventoryItem(tag_id, tagData.getTagSeenCount(), tagData.getPeakRSSI()));
                    }

                    runOnUiThread(() -> RFIDController.viewModel.setTagItems(tagListMap));
                }
            }
        }
    }

    @Override
    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {

    }

    @Override
    public void onConnected() {
        Log.d(TAG, "ConfigReader");
        ConfigReader();
    }

    public void ConfigReader() {
        new Thread(() -> {
            try {
                mReader.Events.removeEventsListener(MainActivity.this);
                mReader.Events.addEventsListener(MainActivity.this);
            } catch (InvalidUsageException | OperationFailureException e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            mReader.Events.setHandheldEvent(true);
            mReader.Events.setInventoryStartEvent(true);
            mReader.Events.setInventoryStopEvent(true);
            mReader.Events.setTagReadEvent(true);

        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}