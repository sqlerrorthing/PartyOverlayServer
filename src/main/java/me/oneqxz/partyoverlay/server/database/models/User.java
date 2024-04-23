package me.oneqxz.partyoverlay.server.database.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
public class User {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String username;

    @DatabaseField
    private String password;

    @DatabaseField
    private String ip;

    @DatabaseField
    private long lastOnline;

    @ForeignCollectionField(eager = true, maxEagerLevel = -1, foreignFieldName = "user1")
    private ForeignCollection<Friendship> friendships1;

    @ForeignCollectionField(eager = true, maxEagerLevel = -1, foreignFieldName = "user2")
    private ForeignCollection<Friendship> friendships2;

    public User[] getFriends() {
        List<User> friendsList = new ArrayList<>();

        for (Friendship friendship : friendships1) {
            friendsList.add(friendship.getUser2());
        }

        for (Friendship friendship : friendships2) {
            friendsList.add(friendship.getUser1());
        }

        return friendsList.toArray(new User[0]);
    }
}