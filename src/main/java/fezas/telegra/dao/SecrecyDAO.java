/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Secrecy;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecrecyDAO implements Dao<Integer, Secrecy> {
    private static final SecrecyDAO INSTANCE = new SecrecyDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_secrecy
            WHERE tlg_secrecy_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_secrecy (tlg_secrecy_name, tlg_secrecy_short_name) 
            VALUES (?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg_secrecy_id,
                tlg_secrecy_name,
                tlg_secrecy_short_name 
            FROM tlg_secrecy
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_secrecy_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE tlg_secrecy
            SET tlg_secrecy_name = ?,
            tlg_secrecy_short_name = ?
            WHERE tlg_secrecy_id = ?
            """;
    private SecrecyDAO() {
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
    public Secrecy save(Secrecy secrecy) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, secrecy.getSecrecyName());
            preparedStatement.setString(2, secrecy.getSecrecyShortName());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                secrecy.setSecerecyId(generatedKeys.getInt("tlg_secrecy_id"));
            }
            return secrecy;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Secrecy secrecy) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, secrecy.getSecrecyName());
            preparedStatement.setString(2, secrecy.getSecrecyShortName());
            preparedStatement.setInt(3, secrecy.getSecerecyId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Secrecy> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Secrecy secrecy = null;
            if (resultSet.next()) {
                secrecy = buildSecrecy(resultSet);
            }
            return Optional.ofNullable(secrecy);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Secrecy> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Secrecy> secrecies = new ArrayList<>();
            while (resultSet.next()) {
                secrecies.add(buildSecrecy(resultSet));
            }
            return secrecies;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static SecrecyDAO getInstance() {
        return INSTANCE;
    }

    private Secrecy buildSecrecy(ResultSet resultSet) throws SQLException {
        return new Secrecy(
                resultSet.getInt("tlg_secrecy_id"),
                resultSet.getString("tlg_secrecy_name"),
                resultSet.getString("tlg_secrecy_short_name")
        );
    }
}
