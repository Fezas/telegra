/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Rank;
import fezas.telegra.entity.Supervisor;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupervisorDAO implements Dao<Integer, Supervisor> {
    private static final SupervisorDAO INSTANCE = new SupervisorDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_supervisor
            WHERE tlg_supervisor_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_supervisor (tlg_supervisor_position, tlg_supervisor_lastname, tlg_supervisor_telephone, tlg_supervisor_default, tlg_supervisor_rank) 
            VALUES (?, ?, ?, ?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg_supervisor_id,
                   tlg_supervisor_position,
                   tlg_supervisor_lastname,
                   tlg_supervisor_telephone,
                   tlg_supervisor_default,
                   rank.id,
                   rank.name_rank,
                   rank.short_name_rank
            FROM tlg_supervisor
                     JOIN rank rank
                          ON tlg_supervisor.tlg_supervisor_rank = rank.id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_supervisor_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE tlg_supervisor
            SET tlg_supervisor_position = ?,
            tlg_supervisor_lastname = ?,
            tlg_supervisor_telephone = ?,
            tlg_supervisor_default = ?,
            tlg_supervisor_rank = ? 
            WHERE tlg_supervisor_id = ?
            """;

    private static final String UPDATE_SQL_DEF_FALSE_ALL = """
            UPDATE tlg_supervisor
            SET tlg_supervisor_default = false 
            """;
    private SupervisorDAO() {
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

    public boolean updateDefAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL_DEF_FALSE_ALL)) {
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }
    public Supervisor save(Supervisor supervisor) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, supervisor.getPosition());
            preparedStatement.setString(2, supervisor.getLastname());
            preparedStatement.setString(3, supervisor.getTelephone());
            preparedStatement.setBoolean(4, supervisor.isDef());
            preparedStatement.setInt(5, supervisor.getRank().getId());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                supervisor.setId(generatedKeys.getInt("tlg_supervisor_id"));
            }
            return supervisor;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Supervisor supervisor) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, supervisor.getPosition());
            preparedStatement.setString(2, supervisor.getLastname());
            preparedStatement.setString(3, supervisor.getTelephone());
            preparedStatement.setBoolean(4, supervisor.isDef());
            preparedStatement.setInt(5, supervisor.getRank().getId());
            preparedStatement.setInt(6, supervisor.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Supervisor> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Supervisor executor = null;
            if (resultSet.next()) {
                executor = buildExecutor(resultSet);
            }
            return Optional.ofNullable(executor);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Supervisor> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Supervisor> executors = new ArrayList<>();
            while (resultSet.next()) {
                executors.add(buildExecutor(resultSet));
            }
            return executors;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static SupervisorDAO getInstance() {
        return INSTANCE;
    }

    private Supervisor buildExecutor(ResultSet resultSet) throws SQLException {
        var rank = new Rank(
                resultSet.getInt("id"),
                resultSet.getString("name_rank"),
                resultSet.getString("short_name_rank")
        );

        return new Supervisor(
                resultSet.getInt("tlg_supervisor_id"),
                resultSet.getString("tlg_supervisor_position"),
                resultSet.getString("tlg_supervisor_lastname"),
                resultSet.getString("tlg_supervisor_telephone"),
                rank,
                resultSet.getBoolean("tlg_supervisor_default")
        );
    }

}