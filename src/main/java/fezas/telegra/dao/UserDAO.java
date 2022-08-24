package fezas.telegra.dao;

import fezas.telegra.entity.User;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements Dao <Long,User>{
    private static final UserDAO INSTANCE = new UserDAO();
    private static final String DELETE_SQL = """
            DELETE FROM users
            WHERE user_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO users (user_login, user_password, user_telephone, user_position, user_role, user_fio) 
            VALUES (?, ?, ?, ?, ?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT user_id,
                user_login,
                user_password,
                user_telephone,
                user_position,
                user_role,
                user_fio 
            FROM users
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE user_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE users
            SET user_login = ?,
                user_password = ?,
                user_telephone = ?,
                user_position = ?,
                user_role = ?,
                user_fio = ?
            WHERE user_id = ?
            """;
    private static final String LOGIN_SQL = FIND_ALL_SQL + """
            WHERE user_login = ? 
            AND user_password = ?
            """;

    public UserDAO() {
    }

    public User save(User user) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getUserLogin());
            preparedStatement.setString(2, user.getUserPassword());
            preparedStatement.setString(3, user.getUserTelephone());
            preparedStatement.setString(4, user.getUserPosition());
            preparedStatement.setString(5, user.getUserRole());
            preparedStatement.setString(6, user.getUserFIO());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getLong("user_id"));
            }
            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(User user) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, user.getUserLogin());
            preparedStatement.setString(2, user.getUserPassword());
            preparedStatement.setString(3, user.getUserTelephone());
            preparedStatement.setString(4, user.getUserPosition());
            preparedStatement.setString(5, user.getUserRole());
            preparedStatement.setString(6, user.getUserFIO());
            preparedStatement.setLong(7, user.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);

        }
    }

    public Optional<User> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = buildUser(resultSet);
            }
            return Optional.ofNullable(user);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public User login(String login, String password) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(LOGIN_SQL)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildUser(resultSet);
            } else return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<User> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<User> tickets = new ArrayList<>();
            while (resultSet.next()) {
                tickets.add(buildUser(resultSet));
            }
            return tickets;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static UserDAO getInstance() {
        return INSTANCE;
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("user_id"),
                resultSet.getString("user_login"),
                resultSet.getString("user_password"),
                resultSet.getString("user_telephone"),
                resultSet.getString("user_position"),
                resultSet.getString("user_role"),
                resultSet.getString("user_fio")

        );
    }
}
