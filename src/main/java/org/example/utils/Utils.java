package org.example.utils;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public class Utils {
    public static long getCurrentEpochTime(){
        return Instant.now().toEpochMilli();
    }
    public static int getCurrentMonth(){
        return LocalDate.now().getMonthValue();
    }
    public static int getCurrentYear(){
        return LocalDate.now().getYear();
    }
    public static long getEpochTimeForMonth(int year,int month){
        return Instant.parse(String.format(AppConstants.DateTimeConstants.DATE_TIME_FORMAT_STRING, year, month)).toEpochMilli();
    }
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
    public static boolean isPublicURI(URI requestURI){
        return requestURI.equals(AppConstants.RouteConstants.LOGIN_URI) || requestURI.equals(AppConstants.RouteConstants.SIGNUP_URI);
    }
}
