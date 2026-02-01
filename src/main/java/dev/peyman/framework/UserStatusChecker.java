package dev.peyman.framework;

public interface UserStatusChecker {
    /**
     * @return true اگر کاربر مجاز است، false اگر بن شده یا غیرفعال است
     */
    boolean isUserValid(String username);
}
