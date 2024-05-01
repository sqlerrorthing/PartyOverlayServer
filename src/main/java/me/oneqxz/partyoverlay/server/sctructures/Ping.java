package me.oneqxz.partyoverlay.server.sctructures;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 01.05.2024
 */
@Getter
@AllArgsConstructor
public class Ping {

    private UUID pingUUID;
    private long created;
    private PartyMember from;

    private double x, y, z;
}
