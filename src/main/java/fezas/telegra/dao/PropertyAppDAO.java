/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.PropertyApp;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PropertyAppDAO implements Dao<Long, PropertyApp> {
    private static final PropertyAppDAO INSTANCE = new PropertyAppDAO();
    private static final String DELETE_SQL = """
            DELETE FROM property
            WHERE id_user = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO property (id_user, path_pdf, path_tlg, path_doc, min_strings_on_new_page, number_computer, service) 
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id_user,
                path_pdf,
                path_tlg,
                path_doc,
                min_strings_on_new_page,
                number_computer,
                service
            FROM property
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id_user = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE property
            SET path_pdf = ?,
            path_tlg = ?,
            path_doc = ?,
            min_strings_on_new_page = ?,
            number_computer = ?,
            service = ?
            WHERE id_user = ?
            """;
    private PropertyAppDAO() {
    }
    public static PropertyAppDAO getInstance() {
        return INSTANCE;
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
    public PropertyApp save(PropertyApp propertyApp) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, propertyApp.getIdUser());
            preparedStatement.setString(2, propertyApp.getPathPDF());
            preparedStatement.setString(3, propertyApp.getPathTLG());
            preparedStatement.setString(4, propertyApp.getPathDOC());
            preparedStatement.setInt(5, propertyApp.getMinStringsOnNewPage());
            preparedStatement.setString(6, propertyApp.getNumberComputer());
            preparedStatement.setString(7, propertyApp.getService());
            preparedStatement.executeUpdate();
            return propertyApp;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(PropertyApp propertyApp) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, propertyApp.getPathPDF());
            preparedStatement.setString(2, propertyApp.getPathTLG());
            preparedStatement.setString(3, propertyApp.getPathDOC());
            preparedStatement.setInt(4, propertyApp.getMinStringsOnNewPage());
            preparedStatement.setString(5, propertyApp.getNumberComputer());
            preparedStatement.setString(6, propertyApp.getService());
            preparedStatement.setLong(7, propertyApp.getIdUser());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<PropertyApp> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            PropertyApp propertyApp = null;
            if (resultSet.next()) {
                propertyApp = buildPropertyApp(resultSet);
            }
            return Optional.ofNullable(propertyApp);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<PropertyApp> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<PropertyApp> propertyApps = new ArrayList<>();
            while (resultSet.next()) {
                propertyApps.add(buildPropertyApp(resultSet));
            }
            return propertyApps;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }


    private PropertyApp buildPropertyApp(ResultSet resultSet) throws SQLException {
        return new PropertyApp(
                resultSet.getLong("id_user"),
                resultSet.getString("path_pdf"),
                resultSet.getString("path_tlg"),
                resultSet.getString("path_doc"),
                resultSet.getInt("min_strings_on_new_page"),
                resultSet.getString("number_computer"),
                resultSet.getString("service")
        );
    }
}