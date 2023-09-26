package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserdataUserEntity;

import java.util.List;
import java.util.UUID;

public interface UserdataDAO {

    UUID createUser(UserdataUserEntity user);

    void deleteUserById(UUID userId);

    List<UserdataUserEntity> getAllUsers();

    void renameUserNameById(UUID userId, String newUsername);
}
