package com.helphub.backend.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DateTimeUtils {

    public static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private DateTimeUtils() {
        // prevent instantiation
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(VN_ZONE);
    }
}
