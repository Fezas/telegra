/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.*;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeInTlgDAO {
    private static final TypeInTlgDAO INSTANCE = new TypeInTlgDAO();
    private static final Logger logger = LogManager.getLogger();
    private static final String DELETE_SQL = """
            DELETE FROM type_in_tlg
            WHERE tlg_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO type_in_tlg (tlg_id, tlg_type_id) 
            VALUES (?, ?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
            type_in_tlg.tlg_id,
            type_in_tlg.tlg_type_id,
                tlg.tlg_id,
                tlg.tlg_category_id,
                tlg.tlg_secrecy_id,
                tlg.tlg_supervisor_id,
                tlg.tlg_title,
                tlg.tlg_text,
                tlg.tlg_date_greate,
                tlg.tlg_date_input,
                tlg.tlg_date_edit,
                tlg.tlg_number,
                tlg.tlg_read,
                tlg.tlg_draft,
                tlg.tlg_sample,
                tlg.tlg_archive,
                tlg.tlg_respect,
                tlg.tlg_version,
                tlg.tlg_apps,
                type.tlg_type_description,
                type.tlg_type_name,
                category.tlg_category_name,
                category.tlg_category_description,
                secrecy.tlg_secrecy_name,
                secrecy.tlg_secrecy_short_name,
                supervisor.tlg_supervisor_position,
                supervisor.tlg_supervisor_lastname,
                supervisor.tlg_supervisor_telephone,
                supervisor.tlg_supervisor_default,
                rank.id,
                rank.name_rank,
                rank.short_name_rank 
            FROM type_in_tlg
            JOIN tlg_type type 
                ON type_in_tlg.tlg_type_id = type.tlg_type_id
            JOIN TLG tlg 
                ON type_in_tlg.tlg_id = tlg.tlg_id
            JOIN tlg_category category
             ON tlg.tlg_category_id = category.tlg_category_id 
            JOIN tlg_secrecy secrecy 
             ON tlg.tlg_secrecy_id = secrecy.tlg_secrecy_id 
            JOIN tlg_supervisor supervisor 
             ON tlg.tlg_supervisor_id = supervisor.tlg_supervisor_id
            JOIN rank rank 
             ON supervisor.tlg_supervisor_rank = rank.id 
            """;
    private static final String FIND_BY_TLG_ID_SQL = FIND_ALL_SQL + """
            WHERE type_in_tlg.tlg_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE mailing
            SET tlg_address_id = ?,
            tlg_id = ? 
            WHERE tlg_address_id = ?
            """;
    private TypeInTlgDAO() {
    }
    public static TypeInTlgDAO getInstance() {
        return INSTANCE;
    }

    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }
    public boolean clear(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(TypeInTlg typeInTlg) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, typeInTlg.getType().getTypeId());
            preparedStatement.setLong(2, typeInTlg.getTelegramma().getTlgId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public void save(Long tlgId, Integer tlgTyprId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setLong(1, tlgId);
            preparedStatement.setInt(2,tlgTyprId);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public List<Type> findByTlgId(Long tlgId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_TLG_ID_SQL)) {
            preparedStatement.setLong(1, tlgId);
            var resultSet = preparedStatement.executeQuery();
            List<Type> types = new ArrayList<>();
            while (resultSet.next()) {
                types.add(buildTypeInTlg(resultSet, tlgId));
            }
            return types;
        } catch (SQLException throwables) {
            logger.error(throwables);
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    private Type buildTypeInTlg(ResultSet resultSet, Long tlgId) throws SQLException {
        var category = new Category(
                resultSet.getInt("tlg_category_id"),
                resultSet.getString("tlg_category_name"),
                resultSet.getString("tlg_category_description")
        );
        var secrecy = new Secrecy(
                resultSet.getInt("tlg_secrecy_id"),
                resultSet.getString("tlg_secrecy_name"),
                resultSet.getString("tlg_secrecy_short_name")
        );
        var rank = new Rank(
                resultSet.getInt("id"),
                resultSet.getString("name_rank"),
                resultSet.getString("short_name_rank")
        );
        var executor = new Supervisor(
                resultSet.getInt("tlg_supervisor_id"),
                resultSet.getString("tlg_supervisor_position"),
                resultSet.getString("tlg_supervisor_lastname"),
                resultSet.getString("tlg_supervisor_telephone"),
                rank,
                resultSet.getBoolean("tlg_supervisor_default")
        );
        var type = new Type(
                resultSet.getInt("tlg_type_id"),
                resultSet.getString("tlg_type_name"),
                resultSet.getString("tlg_type_description"),
                ""
        );
        var tlg = new Telegramma(
                resultSet.getLong("tlg_id"),
                category,
                secrecy,
                executor,
                resultSet.getString("tlg_title"),
                resultSet.getString("tlg_text"),
                resultSet.getTimestamp("tlg_date_greate"),
                resultSet.getTimestamp("tlg_date_input"),
                resultSet.getTimestamp("tlg_date_edit"),
                resultSet.getString("tlg_number"),
                resultSet.getBoolean("tlg_read"),
                resultSet.getBoolean("tlg_draft"),
                resultSet.getBoolean("tlg_sample"),
                resultSet.getBoolean("tlg_archive"),
                resultSet.getBoolean("tlg_respect"),
                resultSet.getInt("tlg_version"),
                resultSet.getString("tlg_apps"),
                ""
        );
        return new TypeInTlg(
                tlg,
                type
        ).getType();
    }
}