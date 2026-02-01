package dev.peyman.framework;

public interface RequestSessionTracker {
    void trackSession(String username, String userAgent, String remoteIp, String token);
}
