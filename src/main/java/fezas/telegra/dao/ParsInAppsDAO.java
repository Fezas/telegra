/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.*;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParsInAppsDAO {
    private static final ParsInAppsDAO INSTANCE = new ParsInAppsDAO();
    private static final String DELETE_TLG_SQL = """
            DELETE FROM pars_in_apps
            WHERE app_id = ?
            """;
    //postgresql
    //private static final String SAVE_SQL = """
    //        INSERT INTO pars_in_tlgs (tlg_id, tlg_secrecy_paragraph_id)
    //        VALUES (?, ?)
    //        ON CONFLICT (tlg_id, tlg_secrecy_paragraph_id) DO NOTHING
    //        """;

    private static final String SAVE_SQL = """
            INSERT INTO pars_in_apps (app_id, tlg_secrecy_paragraph_id) 
            VALUES (?, ?)
            """;

    private static final String FIND_ALL_PARS_BY_TLG_ID_SQL = """
            SELECT 
            tlg_application.app_id,
            tsp.tlg_secrecy_paragraph_id,
            tsp.tlg_secrecy_paragraph_text
            FROM pars_in_apps
            JOIN tlg_application 
                ON tlg_application.app_id = pars_in_apps.app_id
            JOIN tlg_secrecy_paragraph tsp 
                ON tsp.tlg_secrecy_paragraph_id = pars_in_apps.tlg_secrecy_paragraph_id
            WHERE tlg_application.app_id = ?
            ORDER BY tsp.tlg_secrecy_paragraph_id
            """;

    private static final String UPDATE_SQL = """
            UPDATE tlg_application
            SET app_id = ?,
            tlg_secrecy_paragraph_id = ? 
            WHERE app_id = ?
            """;
    private ParsInAppsDAO() {
    }

    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_TLG_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }
    public boolean clear(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_TLG_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(ParsInApps parsInApps) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            //preparedStatement.setLong(1, parsInApps.getApplicationDoc());
            preparedStatement.setLong(2, parsInApps.getParagraph().getSecrecyParagraphId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public List<ParagraphEntity> findParsByAppId(Long appId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_PARS_BY_TLG_ID_SQL)) {
            preparedStatement.setLong(1, appId);
            List<ParagraphEntity> paragraphsInApp = new ArrayList<>();
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                paragraphsInApp.add(buildParsInApps(resultSet, appId));
            }
            return paragraphsInApp;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static ParsInAppsDAO getInstance() {
        return INSTANCE;
    }

    private ParagraphEntity buildParsInApps(ResultSet resultSet, Long appId) throws SQLException {
        ApplicationDocDAO applicationDocDAO = ApplicationDocDAO.getInstance();
        Optional<ApplicationDoc> applicationDoc = applicationDocDAO.findById(appId);
        var paragraph = new ParagraphEntity(
                resultSet.getInt("tlg_secrecy_paragraph_id"),
                resultSet.getString("tlg_secrecy_paragraph_text"),
                ""
        );
        return new ParsInApps(
                applicationDoc,
                paragraph
        ).getParagraph();
    }

    public void save(Long appId, Integer appSecPar) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setLong(1, appId);
            preparedStatement.setInt(2, appSecPar);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
}
