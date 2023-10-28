package com.zebra.fxreader;

public class InventoryItem {

    private String tagID;
    private int count;
    private short RSSI;

    public InventoryItem(String tagID, int count, short RSSI) {
        this.tagID = tagID;
        this.count = count;
        this.RSSI = RSSI;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public short getRSSI() {
        return RSSI;
    }

    public void setRSSI(short RSSI) {
        this.RSSI = RSSI;
    }

}
