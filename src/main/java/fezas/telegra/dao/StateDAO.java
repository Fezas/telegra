/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StateDAO {
    private static final StateDAO INSTANCE = new StateDAO();

    private static final String SIZE_BD = """
            SELECT pg_size_pretty(pg_database_size('telegra'));
            """;
    private static final String COUNT_TLG = """
            SELECT COUNT(*) FROM tlg
            """;
    public static StateDAO getInstance() {
        return INSTANCE;
    }

    public String size() {
        try (var connection = ConnectionManager.get();
             var statement  = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SIZE_BD);
            if (resultSet.next()) return resultSet.getString("pg_size_pretty");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
        return null;
    }
    public String countTlg() {
        try (var connection = ConnectionManager.get();
             var statement  = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(COUNT_TLG);
            if (resultSet.next()) return resultSet.getString("COUNT(*)");//для pg просто count
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
        return null;
    }
}
