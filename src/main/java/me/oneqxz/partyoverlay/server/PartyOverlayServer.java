package me.oneqxz.partyoverlay.server;

import me.oneqxz.partyoverlay.server.database.DatabaseConnection;
import me.oneqxz.partyoverlay.server.network.ServerConnection;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
public class PartyOverlayServer {

    public static void main(String[] args) {
        DatabaseConnection.getInstance().init();
        ServerConnection.getInstance().init();
    }

}