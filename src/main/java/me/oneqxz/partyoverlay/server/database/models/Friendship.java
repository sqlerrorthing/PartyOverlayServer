package me.oneqxz.partyoverlay.server.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DatabaseTable(tableName = "friendships")
public class Friendship {
    @DatabaseField(generatedId = true)
    private int id;

    @NonNull
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user1;

    @NonNull
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user2;
}