package com.lilsort.easyauthextension.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import com.lilsort.easyauthextension.config.ExtensionConfig;

public class FailedLoginIpManager {
    // Stores the number of failed attempts per IP
    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    // Stores the ban expiration time (in milliseconds) per IP
    private static final Map<String, Long> bannedIps = new ConcurrentHashMap<>();

    public static boolean isBanned(String ip) {
        if (ip == null) return false;
        
        Long expiration = bannedIps.get(ip);
        if (expiration != null) {
            if (System.currentTimeMillis() < expiration) {
                return true; // Still banned
            } else {
                bannedIps.remove(ip); // Ban expired
                failedAttempts.remove(ip);
                return false;
            }
        }
        return false;
    }

    public static boolean recordFailure(String ip) {
        if (ip == null) return false;

        // If already banned, don't increment, just return true
        if (isBanned(ip)) {
            return true;
        }

        int attempts = failedAttempts.getOrDefault(ip, 0) + 1;
        failedAttempts.put(ip, attempts);

        if (attempts >= ExtensionConfig.INSTANCE.maxFailures) {
            long banDurationMs = ExtensionConfig.INSTANCE.banDurationMinutes * 60 * 1000L;
            bannedIps.put(ip, System.currentTimeMillis() + banDurationMs);
            return true; // Now banned
        }

        return false; // Not banned yet
    }

    public static void clearFailures(String ip) {
        if (ip == null) return;
        failedAttempts.remove(ip);
        bannedIps.remove(ip);
    }
    
    public static boolean unbanIp(String ip) {
        if (ip == null || !bannedIps.containsKey(ip)) return false;
        bannedIps.remove(ip);
        failedAttempts.remove(ip);
        return true;
    }
    
    public static Set<String> getBannedIps() {
        return bannedIps.keySet();
    }
}
