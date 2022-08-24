/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.*;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelegrammaDAO implements Dao<Long, Telegramma>  {
    private static final TelegrammaDAO INSTANCE = new TelegrammaDAO();
    public static TelegrammaDAO getInstance() {
        return INSTANCE;
    }
    private static final String DELETE_SQL = """
            DELETE FROM TLG
            WHERE tlg_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO TLG (
                tlg_category_id,
                tlg_secrecy_id,
                tlg_supervisor_id,
                tlg_title,
                tlg_text,
                tlg_date_greate,
                tlg_date_input,
                tlg_date_edit,
                tlg_number,
                tlg_read,
                tlg_draft,
                tlg_sample,
                tlg_archive,
                tlg_respect,
                tlg_version,
                tlg_apps) 
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg.tlg_id,
                tlg.tlg_category_id,
                tlg.tlg_secrecy_id,
                tlg.tlg_supervisor_id,
                TLG.tlg_title,
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
            FROM tlg
            JOIN tlg_category category
             ON tlg.tlg_category_id = category.tlg_category_id 
            JOIN tlg_secrecy secrecy 
             ON tlg.tlg_secrecy_id = secrecy.tlg_secrecy_id 
            JOIN tlg_supervisor supervisor 
             ON tlg.tlg_supervisor_id = supervisor.tlg_supervisor_id
            JOIN rank rank
             ON supervisor.tlg_supervisor_rank = rank.id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg.tlg_id = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE tlg
            SET tlg_category_id = ?,
                tlg_secrecy_id = ?,
                tlg_supervisor_id = ?,
                tlg_title = ?,
                tlg_text = ?,
                tlg_date_greate = ?,
                tlg_date_input = ?,
                tlg_date_edit = ?,
                tlg_number = ?,
                tlg_read = ?,
                tlg_draft = ?,
                tlg_sample = ?,
                tlg_archive = ?,
                tlg_respect = ?,
                tlg_version = ?,
                tlg_apps = ?
            WHERE tlg_id = ?
            """;

    private TelegrammaDAO() {
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
    public Telegramma save(Telegramma tlg) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, tlg.getCategory().getCategoryId());
            preparedStatement.setInt(2, tlg.getSecrecy().getSecerecyId());
            preparedStatement.setInt(3, tlg.getSupervisor().getId());
            preparedStatement.setString(4, tlg.getTitle());
            preparedStatement.setString(5, tlg.getText());
            preparedStatement.setTimestamp(6, tlg.getTlgDateGreate());
            preparedStatement.setTimestamp(7, tlg.getTlgDateInput());
            preparedStatement.setTimestamp(8, tlg.getTlgDateEdit());
            preparedStatement.setString(9, tlg.getTlgNumber());
            preparedStatement.setBoolean(10, tlg.getTlgRead());
            preparedStatement.setBoolean(11, tlg.getTlgDraft());
            preparedStatement.setBoolean(12, tlg.getTlgSample());
            preparedStatement.setBoolean(13, tlg.getTlgArchive());
            preparedStatement.setBoolean(14, tlg.getTlgRespect());
            preparedStatement.setInt(15, tlg.getTlgVersion());
            preparedStatement.setString(16, tlg.getApps());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                tlg.setTlgId(generatedKeys.getLong("tlg_id"));
            }
            return tlg;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
             throw new DaoException(throwables);
        }
    }
    public void update(Telegramma tlg) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, tlg.getCategory().getCategoryId());
            preparedStatement.setInt(2, tlg.getSecrecy().getSecerecyId());
            preparedStatement.setInt(3, tlg.getSupervisor().getId());
            preparedStatement.setString(4, tlg.getTitle());
            preparedStatement.setString(5, tlg.getText());
            preparedStatement.setTimestamp(6, tlg.getTlgDateGreate());
            preparedStatement.setTimestamp(7, tlg.getTlgDateInput());
            preparedStatement.setTimestamp(8, tlg.getTlgDateEdit());
            preparedStatement.setString(9, tlg.getTlgNumber());
            preparedStatement.setBoolean(10, tlg.getTlgRead());
            preparedStatement.setBoolean(11, tlg.getTlgDraft());
            preparedStatement.setBoolean(12, tlg.getTlgSample());
            preparedStatement.setBoolean(13, tlg.getTlgArchive());
            preparedStatement.setBoolean(14, tlg.getTlgRespect());
            preparedStatement.setInt(15, tlg.getTlgVersion());
            preparedStatement.setString(16, tlg.getApps());
            preparedStatement.setLong(17, tlg.getTlgId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Telegramma> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            Telegramma tlg = null;
            if (resultSet.next()) {
                tlg = buildTelegramma(resultSet);
            }
            return Optional.ofNullable(tlg);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Telegramma> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Telegramma> telegrammas = new ArrayList<>();
            while (resultSet.next()) {
                telegrammas.add(buildTelegramma(resultSet));
            }
            return telegrammas;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }



    private Telegramma buildTelegramma(ResultSet resultSet) throws SQLException {
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
        var supervisor = new Supervisor(
                resultSet.getInt("tlg_supervisor_id"),
                resultSet.getString("tlg_supervisor_position"),
                resultSet.getString("tlg_supervisor_lastname"),
                resultSet.getString("tlg_supervisor_telephone"),
                rank,
                resultSet.getBoolean("tlg_supervisor_default")
        );
        return new Telegramma(
                resultSet.getLong("tlg_id"),
                category,
                secrecy,
                supervisor,
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
    }
}
