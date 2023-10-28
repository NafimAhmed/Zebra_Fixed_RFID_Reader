package com.zebra.fxreader;

import androidx.navigation.NavController;

import com.zebra.rfid.api3.RFIDReader;

import java.util.concurrent.ConcurrentHashMap;

public class RFIDController {
    public static String hostName = "";
    public static int port = 5084;
    public static int timeoutMilliSeconds = 5000;

    public static RFIDReader mReader;
    public static boolean isInventoryRunning = false;
    public static ConcurrentHashMap<String, InventoryItem> tagListMap = new ConcurrentHashMap<>();

    public static TagDataViewModel viewModel;


}
