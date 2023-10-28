package com.zebra.fxreader;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ConcurrentHashMap;

public class TagDataViewModel extends ViewModel {

    private final MutableLiveData<ConcurrentHashMap<String, InventoryItem>> inventoryItem =
            new MutableLiveData<>();

    public LiveData<ConcurrentHashMap<String, InventoryItem>> getInventoryItem() {
        return inventoryItem;
    }

    public void setTagItems(ConcurrentHashMap<String, InventoryItem> item) {
        inventoryItem.setValue(item);
    }
}


