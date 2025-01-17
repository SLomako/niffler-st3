package guru.qa.niffler.jupiter.exstension;

import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.dao.hibernate.AuthDAOHibernate;
import guru.qa.niffler.db.dao.hibernate.UserdataDAOHibernate;
import guru.qa.niffler.jupiter.annotation.Dao;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class DaoExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        Field[] declaredField = testInstance.getClass().getDeclaredFields();
        for (Field field : declaredField) {
            if (field.isAnnotationPresent(Dao.class)) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(AuthDAO.class)) {
                    AuthDAO authUserDAO = new AuthDAOHibernate();
                    field.set(testInstance, authUserDAO);
                } else if (field.getType().isAssignableFrom(UserdataDAO.class)) {
                    UserdataDAO userDataDAO = new UserdataDAOHibernate();
                    field.set(testInstance, userDataDAO);
                }
            }
        }
    }
}
