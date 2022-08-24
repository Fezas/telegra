/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Type;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeDAO implements Dao<Integer, Type> {
    private static final TypeDAO INSTANCE = new TypeDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_type
            WHERE tlg_type_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_type (tlg_type_name, tlg_type_description) 
            VALUES (?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg_type_id,
                tlg_type_name,
                tlg_type_description 
            FROM tlg_type
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_type_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE tlg_type
            SET tlg_type_name = ?,
            tlg_type_description = ?
            WHERE tlg_type_id = ?
            """;
    private TypeDAO() {
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
    public Type save(Type type) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, type.getTypeName());
            preparedStatement.setString(2, type.getTypeDesc());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                type.setTypeId(generatedKeys.getInt("tlg_type_id"));
            }
            return type;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Type type) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, type.getTypeName());
            preparedStatement.setString(2, type.getTypeDesc());
            preparedStatement.setInt(3, type.getTypeId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Type> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Type type = null;
            if (resultSet.next()) {
                type = buildType(resultSet);
            }
            return Optional.ofNullable(type);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Type> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Type> types = new ArrayList<>();
            while (resultSet.next()) {
                types.add(buildType(resultSet));
            }
            return types;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static TypeDAO getInstance() {
        return INSTANCE;
    }

    private Type buildType(ResultSet resultSet) throws SQLException {
        return new Type(
                resultSet.getInt("tlg_type_id"),
                resultSet.getString("tlg_type_name"),
                resultSet.getString("tlg_type_description"),
                ""
        );
    }
}