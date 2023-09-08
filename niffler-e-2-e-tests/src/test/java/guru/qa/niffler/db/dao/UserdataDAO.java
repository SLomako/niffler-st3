package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

import java.util.List;
import java.util.UUID;

public interface UserdataDAO {

    UUID createUser(UserDataUserEntity user);

    void deleteUserById(UUID userId);

    List<UserDataUserEntity> getAllUsers();

    void renameUserNameById(UUID userId, String newUsername);
}
