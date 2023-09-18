package guru.qa.niffler.db.dao.hibernate;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.userdata.UserdataUserEntity;

import java.util.List;
import java.util.UUID;

public class UserdataDAOHibernate extends JpaService implements UserdataDAO {

    public UserdataDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.USERDATA).createEntityManager());
    }

    @Override
    public UUID createUser(UserdataUserEntity user) {
        persist(user);
        return user.getId();
    }

    @Override
    public void deleteUserById(UUID userId) {
        removeById(UserdataUserEntity.class, userId);
    }

    @Override
    public List<UserdataUserEntity> getAllUsers() {
        return em.createQuery("SELECT u FROM UserdataUserEntity u", UserdataUserEntity.class).getResultList();
    }

    @Override
    public void renameUserNameById(UUID userId, String newUsername) {
        UserdataUserEntity user = find(UserdataUserEntity.class, userId);
        user.setUsername(newUsername);
        merge(user);
    }
}
