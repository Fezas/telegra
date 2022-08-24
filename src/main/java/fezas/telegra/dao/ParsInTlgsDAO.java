/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.ParagraphEntity;
import fezas.telegra.entity.ParsInTlgs;
import fezas.telegra.entity.Telegramma;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParsInTlgsDAO {
    private static final ParsInTlgsDAO INSTANCE = new ParsInTlgsDAO();
    private static final String DELETE_TLG_SQL = """
            DELETE FROM pars_in_tlgs
            WHERE tlg_id = ?
            """;
    //postgresql
    //private static final String SAVE_SQL = """
    //        INSERT INTO pars_in_tlgs (tlg_id, tlg_secrecy_paragraph_id)
    //        VALUES (?, ?)
    //        ON CONFLICT (tlg_id, tlg_secrecy_paragraph_id) DO NOTHING
    //        """;

    private static final String SAVE_SQL = """
            INSERT INTO pars_in_tlgs (tlg_id, tlg_secrecy_paragraph_id) 
            VALUES (?, ?)
            """;

    private static final String FIND_ALL_PARS_BY_TLG_ID_SQL = """
            SELECT 
            TLG.tlg_id,
            tsp.tlg_secrecy_paragraph_id,
            tsp.tlg_secrecy_paragraph_text
            FROM pars_in_tlgs
            JOIN TLG 
                ON TLG.tlg_id = pars_in_tlgs.tlg_id
            JOIN tlg_secrecy_paragraph tsp 
                ON tsp.tlg_secrecy_paragraph_id = pars_in_tlgs.tlg_secrecy_paragraph_id
            WHERE TLG.tlg_id = ?
            ORDER BY tsp.tlg_secrecy_paragraph_id
            """;

    private static final String UPDATE_SQL = """
            UPDATE mailing
            SET tlg_address_id = ?,
            tlg_id = ? 
            WHERE tlg_address_id = ?
            """;
    private ParsInTlgsDAO() {
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

    public void update(ParsInTlgs par) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    public List<ParagraphEntity> findParsByTlgId(Long tlgId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_PARS_BY_TLG_ID_SQL)) {
            preparedStatement.setLong(1, tlgId);
            List<ParagraphEntity> paragraphsInTlg = new ArrayList<>();
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                paragraphsInTlg.add(buildParsInTlgs(resultSet, tlgId));
            }
            return paragraphsInTlg;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static ParsInTlgsDAO getInstance() {
        return INSTANCE;
    }

    private ParagraphEntity buildParsInTlgs(ResultSet resultSet, Long tlgId) throws SQLException {
        TelegrammaDAO telegrammaDAO = TelegrammaDAO.getInstance();
        Optional<Telegramma> tlg = telegrammaDAO.findById(tlgId);
        var paragraph = new ParagraphEntity(
                resultSet.getInt("tlg_secrecy_paragraph_id"),
                resultSet.getString("tlg_secrecy_paragraph_text"),
                ""
        );
        return new ParsInTlgs(
                tlg,
                paragraph
        ).getParagraph();
    }

    public void save(Long tlgId, Integer tlgSecPar) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setLong(1, tlgId);
            preparedStatement.setInt(2,tlgSecPar);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
}
