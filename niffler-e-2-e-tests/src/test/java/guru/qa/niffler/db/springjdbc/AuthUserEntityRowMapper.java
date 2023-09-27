package guru.qa.niffler.db.springjdbc;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper INSTANCE = new AuthUserEntityRowMapper();

    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthUserEntity user = new AuthUserEntity();
        user.setId((UUID) rs.getObject("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return user;
    }
}
