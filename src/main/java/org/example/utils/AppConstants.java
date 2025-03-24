package org.example.utils;

import java.net.URI;

public class AppConstants {
    public static class ServerConstants{
        public static final int PORT=8000;
        public static final int BACKLOG = 50;
        public static final int THREAD_POOL_SIZE = 4;
        public static final int GRACE_TIME=5;
    }
    public static class DateTimeConstants {
        public static final String DATE_TIME_FORMAT_STRING= "%04d-%02d-01T00:00:00Z";
    }
    public static class RouteConstants {
        public static final URI LOGIN_URI = URI.create("/login");
        public static final URI SIGNUP_URI = URI.create("/signup");
    }

}
