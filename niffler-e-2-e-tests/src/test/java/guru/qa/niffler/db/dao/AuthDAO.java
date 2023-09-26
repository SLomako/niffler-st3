package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.auth.AuthUserEntity;

import java.util.List;
import java.util.UUID;

public interface AuthDAO {

    UUID createUser(AuthUserEntity user);

    void deleteUserById(UUID userId);

    List<AuthUserEntity> getAllUsers();

    void renameUserNameById(UUID userId, String newUsername);

    AuthUserEntity getUserById(UUID userId);
}
