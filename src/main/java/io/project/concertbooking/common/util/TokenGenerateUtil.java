package io.project.concertbooking.common.util;

import java.util.UUID;

public class TokenGenerateUtil {

    public static String generateUUIDToken() {
        return UUID.randomUUID().toString();
    }
}
