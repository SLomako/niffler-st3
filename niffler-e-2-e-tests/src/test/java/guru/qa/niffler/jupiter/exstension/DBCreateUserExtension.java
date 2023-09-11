package guru.qa.niffler.jupiter.exstension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthDAO;
import guru.qa.niffler.db.dao.UserdataDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.jupiter.annotation.AuthUserId;
import guru.qa.niffler.jupiter.annotation.DBCreateUser;
import guru.qa.niffler.jupiter.annotation.Dao;
import guru.qa.niffler.jupiter.annotation.UserdataUserId;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DBCreateUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBCreateUserExtension.class);

    private final DaoExtension daoExtension = new DaoExtension();

    @Dao
    private AuthDAO authUserDAO;
    @Dao
    private UserdataDAO userDataDAO;

    private Faker faker = new Faker();


    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        daoExtension.postProcessTestInstance(this, context);

        Method testMethod = context.getRequiredTestMethod();
        DBCreateUser annotation = testMethod.getAnnotation(DBCreateUser.class);

        if (annotation != null) {
            String username = annotation.username().equals("random") ? faker.name().firstName() : annotation.username();
            String password = annotation.password();

            AuthUserEntity currentUserAuthDB = new AuthUserEntity();
            currentUserAuthDB.setUsername(username);
            currentUserAuthDB.setPassword(password);
            currentUserAuthDB.setEnabled(true);
            currentUserAuthDB.setAccountNonExpired(true);
            currentUserAuthDB.setAccountNonLocked(true);
            currentUserAuthDB.setCredentialsNonExpired(true);
            currentUserAuthDB.setAuthorities(
                    Arrays.stream(Authority.values()).map(a -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(a);
                        return ae;
                    }).toList()
            );
            UUID createdAuthUserId = authUserDAO.createUser(currentUserAuthDB);

            UserDataUserEntity currentUserUserdataDB = new UserDataUserEntity();
            currentUserUserdataDB.setUsername(username);
            UUID createdUserdataUserId = userDataDAO.createUser(currentUserUserdataDB);

            Map<String, Object> userDataMap = new HashMap<>();
            userDataMap.put("createdAuthUserId", createdAuthUserId);
            userDataMap.put("createdUserdataUserId", createdUserdataUserId);
            userDataMap.put("currentUserAuthDB", currentUserAuthDB);
            userDataMap.put("currentUserUserdataDB", currentUserUserdataDB);

            context.getStore(NAMESPACE).put(context.getUniqueId(), userDataMap);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map userDataMap = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);

        UUID createdAuthUserId = (UUID) userDataMap.get("createdAuthUserId");
        UUID createdUserdataUserId = (UUID) userDataMap.get("createdUserdataUserId");

        if (createdAuthUserId != null) {
            authUserDAO.deleteUserById(createdAuthUserId);
        }
        if (createdUserdataUserId != null) {
            userDataDAO.deleteUserById(createdUserdataUserId);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        return type.equals(UserDataUserEntity.class)
                || type.equals(AuthUserEntity.class)
                || type.equals(AuthDAO.class)
                || type.equals(UserdataDAO.class)
                || type.equals(UUID.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        Parameter parameter = parameterContext.getParameter();

        Map userDataMap = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);

        UUID createdAuthUserId = (UUID) userDataMap.get("createdAuthUserId");
        UUID createdUserdataUserId = (UUID) userDataMap.get("createdUserdataUserId");
        AuthUserEntity currentUserAuthDB = (AuthUserEntity) userDataMap.get("currentUserAuthDB");
        UserDataUserEntity currentUserUserdataDB = (UserDataUserEntity) userDataMap.get("currentUserUserdataDB");

        if (type.equals(UserDataUserEntity.class)) {
            return currentUserUserdataDB;
        } else if (type.equals(AuthUserEntity.class)) {
            return currentUserAuthDB;
        } else if (type.equals(AuthDAO.class)) {
            return authUserDAO;
        } else if (type.equals(UserdataDAO.class)) {
            return userDataDAO;
        } else if (type.equals(UUID.class)) {
            if (parameter.isAnnotationPresent(AuthUserId.class)) {
                return createdAuthUserId;
            } else if (parameter.isAnnotationPresent(UserdataUserId.class)) {
                return createdUserdataUserId;
            }
        }
        throw new ParameterResolutionException("Unsupported parameter: " + type);
    }
}


