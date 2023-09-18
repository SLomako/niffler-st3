package guru.qa.niffler.db.springjdbc;

import guru.qa.niffler.db.model.userdata.UserdataUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserdataUserEntityRowMapper implements RowMapper<UserdataUserEntity> {

    public static final UserdataUserEntityRowMapper INSTANCE = new UserdataUserEntityRowMapper();

    @Override
    public UserdataUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        UserdataUserEntity user = new UserdataUserEntity();
        user.setId((UUID) rs.getObject("id"));
        user.setUsername(rs.getString("username"));
        return user;
    }
}
