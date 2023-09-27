package guru.qa.niffler.db.dao.hibernate;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

public class AuthDAOHibernate extends JpaService implements AuthDAO {

    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.AUTH).createEntityManager());
    }

    @Override
    public UUID createUser(AuthUserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        persist(user);
        return user.getId();
    }

    @Override
    public void deleteUserById(UUID userId) {
        removeById(AuthUserEntity.class, userId);
    }

    @Override
    public List<AuthUserEntity> getAllUsers() {
        return em.createQuery("SELECT u FROM AuthUserEntity u", AuthUserEntity.class).getResultList();
    }

    @Override
    public void renameUserNameById(UUID userId, String newUsername) {
        findAndMerge(AuthUserEntity.class, userId, entity -> {
            AuthUserEntity user = (AuthUserEntity) entity;
            user.setUsername(newUsername);
        });
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        return find(AuthUserEntity.class, userId);
    }
}
