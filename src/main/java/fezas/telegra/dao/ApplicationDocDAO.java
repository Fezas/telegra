/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.ApplicationDoc;
import fezas.telegra.entity.Secrecy;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationDocDAO implements Dao<Long, ApplicationDoc> {
    private static final ApplicationDocDAO INSTANCE = new ApplicationDocDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_application
            WHERE app_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_application (app_name, app_ext, app_size, app_sec, tlg_id, app_exe, app_numb) 
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String SEARCH_MAX_ID = """
            SELECT MAX(app_id)
            FROM 
            tlg_application;
            """;

    private static final String FIND_ALL_SQL = """
            SELECT app_id,
                app_name,
                app_ext,
                app_size,
                app_sec,
                tlg_id,
                app_exe,
                app_numb,
                secrecy.tlg_secrecy_id,
                secrecy.tlg_secrecy_name,
                secrecy.tlg_secrecy_short_name 
            FROM tlg_application
            JOIN tlg_secrecy secrecy 
             ON app_sec = secrecy.tlg_secrecy_id 
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_id = ?
            """;

    private static final String FIND_BY_NAME_SQL = """
            SELECT app_id,
                app_name,
                app_numb
            FROM tlg_application
            WHERE app_name = ? AND app_numb = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE tlg_application
            SET app_name = ?,
            app_ext = ?,
            app_size = ?,
            app_sec = ?,
            tlg_id = ?,
            app_exe = ?,
            app_numb = ? 
            WHERE app_id = ?
            """;
    private static final String UPDATE_ID_TLG_SQL = """
            UPDATE tlg_application
            SET tlg_id = ?
            WHERE app_id = ?
            """;

    private ApplicationDocDAO() {
    }
    public static ApplicationDocDAO getInstance() {
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
    public ApplicationDoc save(ApplicationDoc applicationDoc) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, applicationDoc.getAppName());
            preparedStatement.setString(2, applicationDoc.getAppExt());
            preparedStatement.setString(3, applicationDoc.getAppSize());
            preparedStatement.setInt(4, applicationDoc.getAppSec().getSecerecyId());
            preparedStatement.setLong(5, applicationDoc.getTlgId());
            preparedStatement.setString(6, applicationDoc.getAppExe());
            preparedStatement.setString(7, applicationDoc.getAppNumb());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                applicationDoc.setAppId(generatedKeys.getLong("app_id"));
            }
            return applicationDoc;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(ApplicationDoc applicationDoc) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, applicationDoc.getAppName());
            preparedStatement.setString(2, applicationDoc.getAppExt());
            preparedStatement.setString(3, applicationDoc.getAppSize());
            preparedStatement.setInt(4, applicationDoc.getAppSec().getSecerecyId());
            preparedStatement.setLong(5, applicationDoc.getTlgId());
            preparedStatement.setString(6, applicationDoc.getAppExe());
            preparedStatement.setString(7, applicationDoc.getAppNumb());
            preparedStatement.setLong(8, applicationDoc.getAppId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public void updateIdTlg(Long tlgId, Long appId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_ID_TLG_SQL)) {
            preparedStatement.setLong(1, tlgId);
            preparedStatement.setLong(2, appId);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<ApplicationDoc> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            ApplicationDoc applicationDoc = null;
            if (resultSet.next()) {
                applicationDoc = buildApplicationDoc(resultSet);
            }
            return Optional.ofNullable(applicationDoc);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<ApplicationDoc> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<ApplicationDoc> applicationDocs = new ArrayList<>();
            while (resultSet.next()) {
                applicationDocs.add(buildApplicationDoc(resultSet));
            }
            return applicationDocs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }
    public List<ApplicationDoc> findAllFromTlg(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            List<ApplicationDoc> applicationDocs = new ArrayList<>();
            while (resultSet.next()) {
                applicationDocs.add(buildApplicationDoc(resultSet));
            }
            return applicationDocs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }
    public Long findByName(String name, String numb) {
        Long id = null;
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, numb);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getLong("app_id");
            }
            return id;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    private ApplicationDoc buildApplicationDoc(ResultSet resultSet) throws SQLException {
        var secrecy = new Secrecy(
                resultSet.getInt("tlg_secrecy_id"),
                resultSet.getString("tlg_secrecy_name"),
                resultSet.getString("tlg_secrecy_short_name")
        );
        return new ApplicationDoc(
                resultSet.getLong("app_id"),
                resultSet.getString("app_name"),
                resultSet.getString("app_ext"),
                resultSet.getString("app_size"),
                secrecy,
                resultSet.getLong("tlg_id"),
                resultSet.getString("app_exe"),
                resultSet.getString("app_numb"),
                ""
        );
    }
}