package me.oneqxz.partyoverlay.server.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.database.models.Friendship;
import me.oneqxz.partyoverlay.server.database.models.User;

import java.sql.SQLException;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
@Log4j2
public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:mysql://fin-1.honte-hosting.com:25578/piski?user=party&password=QxooL7OVn6W51r0r";
    private static DatabaseConnection INSTANCE;

    private ConnectionSource connectionSource;

    @SneakyThrows
    public void init()
    {
        log.info("Connecting to database...");
        ConnectionSource connectionSource = getConnectionSource();

        for (Class<?> clazz : new Class<?>[]{User.class, Friendship.class}) {
            TableUtils.createTableIfNotExists(connectionSource, clazz);
        }

        log.info("Created tables");

        closeConnection();
    }

    public ConnectionSource getConnectionSource() throws SQLException {
        if (connectionSource == null) {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
        }
        return connectionSource;
    }

    public void closeConnection() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static DatabaseConnection getInstance()
    {
        return INSTANCE == null ? INSTANCE = new DatabaseConnection() : INSTANCE;
    }

}
