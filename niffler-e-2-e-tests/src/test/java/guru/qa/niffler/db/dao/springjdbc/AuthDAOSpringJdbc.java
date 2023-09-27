package guru.qa.niffler.db.dao.springjdbc;

import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.springjdbc.AuthUserEntityRowMapper;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.ServiceDB;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class AuthDAOSpringJdbc implements AuthDAO {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthDAOSpringJdbc() {
        JdbcTransactionManager transactionTemplate = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH));
        this.transactionTemplate = new TransactionTemplate(transactionTemplate);
        this.jdbcTemplate = new JdbcTemplate(transactionTemplate.getDataSource());
    }

    @Override
    public UUID createUser(AuthUserEntity user) {
        return transactionTemplate.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement usersPs = con.prepareStatement(
                        "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                "VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());
                return usersPs;
            }, kh);

            UUID userId = (UUID) kh.getKeyList().get(0).get("id");

            for (Authority authority : Authority.values()) {
                jdbcTemplate.update(
                        "INSERT INTO authorities (user_id, authority) VALUES (?, ?)",
                        userId, authority.name()
                );
            }
            return userId;
        });
    }

    @Override
    public void deleteUserById(UUID userId) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update("DELETE FROM authorities WHERE user_id = ?", userId);
            jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
            return null;
        });
    }

    @Override
    public List<AuthUserEntity> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", AuthUserEntityRowMapper.INSTANCE);
    }

    @Override
    public void renameUserNameById(UUID userId, String newUsername) {
        jdbcTemplate.update("UPDATE users SET username = ? WHERE id = ?", newUsername, userId);
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        AuthUserEntity user = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ? ",
                AuthUserEntityRowMapper.INSTANCE,
                userId
        );

        List<AuthorityEntity> authorities = jdbcTemplate.query(
                "SELECT * FROM authorities WHERE user_id = ?",
                (rs, rowNum) -> {
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
                    return authorityEntity;
                },
                userId
        );
        user.setAuthorities(authorities);
        return user;
    }
}
