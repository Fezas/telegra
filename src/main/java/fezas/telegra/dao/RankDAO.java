/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Rank;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RankDAO implements Dao<Integer, Rank> {
    private static final RankDAO INSTANCE = new RankDAO();
    private static final String DELETE_SQL = """
            DELETE FROM rank
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO rank (name_rank, short_name_rank) 
            VALUES (?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                name_rank,
                short_name_rank 
            FROM rank
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE rank
            SET name_rank = ?,
            short_name_rank = ?
            WHERE id = ?
            """;
    private RankDAO() {
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
    public Rank save(Rank rank) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, rank.getNameRank());
            preparedStatement.setString(2, rank.getShortNameRank());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                rank.setId(generatedKeys.getInt("id"));
            }
            return rank;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Rank rank) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, rank.getNameRank());
            preparedStatement.setString(2, rank.getShortNameRank());
            preparedStatement.setInt(3, rank.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Rank> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Rank rank = null;
            if (resultSet.next()) {
                rank = buildSecrecy(resultSet);
            }
            return Optional.ofNullable(rank);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Rank> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Rank> ranks = new ArrayList<>();
            while (resultSet.next()) {
                ranks.add(buildSecrecy(resultSet));
            }
            return ranks;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static RankDAO getInstance() {
        return INSTANCE;
    }

    private Rank buildSecrecy(ResultSet resultSet) throws SQLException {
        return new Rank(
                resultSet.getInt("id"),
                resultSet.getString("name_rank"),
                resultSet.getString("short_name_rank")
        );
    }
}
