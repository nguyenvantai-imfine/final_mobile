package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.ServiceItem;
import com.example.ck_mobile.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/** Danh mục mock cho tiện ích vé phim / khách sạn (luồng trừ tiền và lưu đơn là thật). */
public final class ServiceCatalog {

    private ServiceCatalog() {
    }

    public static List<ServiceItem> movies() {
        List<ServiceItem> l = new ArrayList<>();
        l.add(new ServiceItem("mv1", Constants.TX_MOVIE, "Mai - Suất 19:30", "CGV Vincom Center • Phòng 3", 95_000, "vé"));
        l.add(new ServiceItem("mv2", Constants.TX_MOVIE, "Đào, Phở và Piano - Suất 20:15", "Lotte Cinema Cantavil • Phòng 5", 90_000, "vé"));
        l.add(new ServiceItem("mv3", Constants.TX_MOVIE, "Kung Fu Panda 4 - Suất 17:00", "BHD Star Bitexco • Phòng 2", 85_000, "vé"));
        l.add(new ServiceItem("mv4", Constants.TX_MOVIE, "Dune: Part Two - Suất 21:00", "Galaxy Nguyễn Du • Phòng 1", 110_000, "vé"));
        l.add(new ServiceItem("mv5", Constants.TX_MOVIE, "Godzilla x Kong - Suất 18:45", "CGV Landmark 81 • IMAX", 160_000, "vé"));
        l.add(new ServiceItem("mv6", Constants.TX_MOVIE, "Inside Out 2 - Suất 15:30", "Lotte Cinema Gò Vấp • Phòng 4", 80_000, "vé"));
        return l;
    }

    public static List<ServiceItem> hotels() {
        List<ServiceItem> l = new ArrayList<>();
        l.add(new ServiceItem("ht1", Constants.TX_HOTEL, "Sunrise Central Hotel", "Quận 1 • 4 sao • Gồm ăn sáng", 1_450_000, "đêm"));
        l.add(new ServiceItem("ht2", Constants.TX_HOTEL, "Saigon River View", "Quận 4 • 4 sao • View sông", 1_800_000, "đêm"));
        l.add(new ServiceItem("ht3", Constants.TX_HOTEL, "Lotus Boutique Hotel", "Quận 3 • 3 sao", 850_000, "đêm"));
        l.add(new ServiceItem("ht4", Constants.TX_HOTEL, "Pearl Riverside Resort", "Thủ Đức • 5 sao • Hồ bơi", 2_900_000, "đêm"));
        l.add(new ServiceItem("ht5", Constants.TX_HOTEL, "Airport Comfort Inn", "Tân Bình • 3 sao • Gần sân bay", 720_000, "đêm"));
        l.add(new ServiceItem("ht6", Constants.TX_HOTEL, "District 7 Garden Hotel", "Quận 7 • 4 sao", 1_250_000, "đêm"));
        return l;
    }

    public static List<ServiceItem> forType(String type) {
        return Constants.TX_HOTEL.equals(type) ? hotels() : movies();
    }
}
