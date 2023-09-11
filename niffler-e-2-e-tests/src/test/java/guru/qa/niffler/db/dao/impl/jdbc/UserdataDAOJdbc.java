package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.model.userdata.CurrencyValues;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.db.springjdbc.ServiceDB;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserdataDAOJdbc implements UserdataDAO {

    DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public UUID createUser(UserDataUserEntity user) {
        UUID userID;
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, CurrencyValues.RUB.name());
                usersPs.executeUpdate();
                userID = retrieveGeneratedUserId(usersPs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userID;
    }

    private UUID retrieveGeneratedUserId(PreparedStatement userPS) throws SQLException {
        try (ResultSet generatedKeys = userPS.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return UUID.fromString(generatedKeys.getString("id"));
            } else {
                throw new IllegalStateException("ResultSet is null");
            }
        }
    }

    @Override
    public void deleteUserById(UUID userId) {
        try (Connection conn = userdataDs.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement usersPs = conn.prepareStatement("DELETE FROM users WHERE id = ?");
                 PreparedStatement friendsPs = conn.prepareStatement("DELETE FROM friends WHERE user_id = ?")) {
                friendsPs.setObject(1, userId);
                usersPs.setObject(1, userId);
                friendsPs.executeUpdate();
                usersPs.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserDataUserEntity> getAllUsers() {
        List<UserDataUserEntity> userEntityList = new ArrayList<>();
        try (Connection conn = userdataDs.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users")) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                UUID id = (UUID) rs.getObject("id");
                String username = rs.getString("username");
                userEntityList.add(new UserDataUserEntity(id, username));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntityList;
    }

    @Override
    public void renameUserNameById(UUID userId, String newUsername) {
        try (Connection conn = userdataDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement("UPDATE users SET username = ? WHERE id = ?")) {
            usersPs.setString(1, newUsername);
            usersPs.setObject(2, userId);
            usersPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

