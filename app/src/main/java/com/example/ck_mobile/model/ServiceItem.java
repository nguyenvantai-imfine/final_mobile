package com.example.ck_mobile.model;

/** Mục trong danh mục mock của tiện ích vé phim / khách sạn. */
public class ServiceItem {
    public final String id;
    public final String type;      // Constants.TX_MOVIE hoặc TX_HOTEL
    public final String title;
    public final String subtitle;
    public final long unitPrice;
    public final String unitLabel; // "vé" hoặc "đêm"

    public ServiceItem(String id, String type, String title, String subtitle, long unitPrice, String unitLabel) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.unitPrice = unitPrice;
        this.unitLabel = unitLabel;
    }
}
