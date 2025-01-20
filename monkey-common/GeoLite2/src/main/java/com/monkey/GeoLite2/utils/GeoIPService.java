//package com.monkey.GeoLite2.utils;
//
//import com.maxmind.geoip2.DatabaseReader;
//import com.maxmind.geoip2.exception.GeoIp2Exception;
//import com.maxmind.geoip2.model.CityResponse;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.InetAddress;
//
//@Service
//public class GeoIPService {
//
//    private final DatabaseReader dbReader;
//
//    public GeoIPService() throws IOException {
//        String path = "D:\\JAVA\\GeoLite2\\GeoLite2-City.mmdb";
//        File database = new File(path);
//        dbReader = new DatabaseReader.Builder(database).build();
//    }
//    public String getProvince(String ipAddress) throws IOException, GeoIp2Exception {
//        InetAddress address = InetAddress.getByName(ipAddress);
//        CityResponse response = dbReader.city(address);
//        return response.getMostSpecificSubdivision().getNames().get("zh-CN");
//    }
//
//    public String getCity(String ipAddress) throws IOException, GeoIp2Exception {
//        InetAddress address = InetAddress.getByName(ipAddress);
//        CityResponse response = dbReader.city(address);
//        return response.getCity().getName();
//    }
//}