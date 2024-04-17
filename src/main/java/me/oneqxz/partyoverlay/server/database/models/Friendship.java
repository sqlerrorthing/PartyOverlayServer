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
 * @since 17.04.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DatabaseTable(tableName = "users")
public class Friendship {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = "user_id")
    private User user;

    @DatabaseField(foreign = true, columnName = "friend_id")
    private User friend;

    @DatabaseField
    private long since;

    @DatabaseField
    private State state;

    public enum State {
        REQUESTED,
        FRIENDS;
    }
}
