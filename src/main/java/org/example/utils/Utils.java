package org.example.utils;

import java.time.Instant;
import java.util.UUID;

public class Utils {
    public static long getCurrentEpochTime(){
        return Instant.now().toEpochMilli();
    }
    public static long getEpochTimeForMonth(int year,int month){
        return Instant.parse(String.format("%04d-%02d-01T00:00:00Z", year, month)).toEpochMilli();
    }
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
