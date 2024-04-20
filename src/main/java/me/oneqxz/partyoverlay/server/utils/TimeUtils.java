package me.oneqxz.partyoverlay.server.utils;

import java.time.Instant;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
public class TimeUtils {

    public static long getUTCMillis()
    {
        return Instant.now().toEpochMilli();
    }

}
