package me.oneqxz.partyoverlay.server.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DatabaseTable(tableName = "friendships")
public class Friendship {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user1;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user2;
}