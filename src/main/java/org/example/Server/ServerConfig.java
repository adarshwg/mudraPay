package org.example.Server;

import org.example.utils.AppConstants;

public class ServerConfig {

    public static int getPort() {
        return AppConstants.ServerConstants.PORT;
    }

    public static int getBacklog() {
        return AppConstants.ServerConstants.BACKLOG;
    }
    public static int getGraceTime(){
        return AppConstants.ServerConstants.GRACE_TIME;
    }

    public static int getThreadPoolSize(){
        return AppConstants.ServerConstants.THREAD_POOL_SIZE;
    }
}
