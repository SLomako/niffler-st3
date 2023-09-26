package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.springjdbc.ServiceDB;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthDAOJdbc implements AuthDAO {

    private final String insertUserQuery = """
            INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
            VALUES(?, ?, ?, ?, ?, ?)
            """;

    private final String insertAuthorityQuery = """
            INSERT INTO authorities (user_id, authority)
            VALUES(?, ?)
            """;

    private static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);
    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UUID createUser(AuthUserEntity user) {
        UUID userId = null;

        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement usersPS =
                         conn.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement authorityPS =
                         conn.prepareStatement(insertAuthorityQuery)) {
                insertUser(usersPS, user);
                userId = retrieveGeneratedUserId(usersPS);
                insertAuthorities(authorityPS, userId);
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
        return userId;
    }

    private void insertUser(PreparedStatement userPS, AuthUserEntity user) throws SQLException {
        userPS.setString(1, user.getUsername());
        userPS.setString(2, pe.encode(user.getPassword()));
        userPS.setBoolean(3, user.getEnabled());
        userPS.setBoolean(4, user.getAccountNonExpired());
        userPS.setBoolean(5, user.getAccountNonLocked());
        userPS.setBoolean(6, user.getCredentialsNonExpired());
        userPS.executeUpdate();
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

    private void insertAuthorities(PreparedStatement authorityPS, UUID userId) throws SQLException {
        for (Authority authority : Authority.values()) {
            authorityPS.setObject(1, userId);
            authorityPS.setString(2, authority.name());
            authorityPS.addBatch();
            authorityPS.clearParameters();
        }
        authorityPS.executeBatch();
    }

    @Override
    public void deleteUserById(UUID userId) {
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement authorityPs = conn.prepareStatement("DELETE FROM authorities WHERE user_id = ?");
                 PreparedStatement usersPs = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                authorityPs.setObject(1, userId);
                usersPs.setObject(1, userId);
                authorityPs.executeUpdate();
                usersPs.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> getAllUsers() {
        List<AuthUserEntity> userEntityList = new ArrayList<>();
        try (Connection conn = authDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement("SELECT * FROM users")) {
            ResultSet rs = usersPs.executeQuery();
            while (rs.next()) {
                UUID id = (UUID) rs.getObject("id");
                String username = rs.getString("username");
                userEntityList.add(new AuthUserEntity(id, username));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntityList;
    }

    @Override
    public void renameUserNameById(UUID userId, String newUsername) {
        try (Connection conn = authDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement("UPDATE users SET username = ? WHERE id = ?")) {
            usersPs.setString(1, newUsername);
            usersPs.setObject(2, userId);
            usersPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        AuthUserEntity user = new AuthUserEntity();
        boolean userDataFetched = false;
        try (Connection conn = authDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement(
                     "SELECT * FROM users u JOIN authorities a ON u.id = a.user_id WHERE u.id = ?")) {
            usersPs.setObject(1, userId);

            try (ResultSet resultSet = usersPs.executeQuery()) {
                while (resultSet.next()) {
                    if (!userDataFetched) {
                        user.setId(resultSet.getObject("id", UUID.class));
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setEnabled(resultSet.getBoolean("enabled"));
                        user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                        user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                        user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                        userDataFetched = true;
                    }

                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    Authority authority = Authority.valueOf(resultSet.getString("authority"));
                    authorityEntity.setAuthority(authority);
                    user.addAuthorities(authorityEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
