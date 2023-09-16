package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.db.model.userdata.CurrencyValues;
import guru.qa.niffler.db.model.userdata.UserdataUserEntity;
import guru.qa.niffler.db.springjdbc.ServiceDB;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class UserdataDAOSpringJdbc implements UserdataDAO {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;

    public UserdataDAOSpringJdbc() {
        JdbcTransactionManager transactionTemplate = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));
        this.transactionTemplate = new TransactionTemplate(transactionTemplate);
        this.jdbcTemplate = new JdbcTemplate(transactionTemplate.getDataSource());
    }

    @Override
    public UUID createUser(UserdataUserEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement usersPs = connection
                    .prepareStatement("INSERT INTO users (username, currency) VALUES (?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
            usersPs.setString(1, user.getUsername());
            usersPs.setString(2, CurrencyValues.RUB.name());
            return usersPs;
        }, keyHolder);

        UUID userId = (UUID) keyHolder.getKeyList().get(0).get("id");
        return userId;
    }

    @Override
    public void deleteUserById(UUID userId) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.update("DELETE FROM friends WHERE user_id = ?", userId);
            jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
            return null;
        });
    }

    @Override
    public List<UserdataUserEntity> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", UserdataUserEntityRowMapper.INSTANCE);
    }

    @Override
    public void renameUserNameById(UUID userId, String newUsername) {
        jdbcTemplate.update("UPDATE users SET username = ? WHERE id = ?", newUsername, userId);
    }
}
