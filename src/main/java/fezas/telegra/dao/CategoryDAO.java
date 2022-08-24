/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Category;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO implements Dao<Integer, Category> {
    private static final CategoryDAO INSTANCE = new CategoryDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_category
            WHERE tlg_category_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_category (tlg_category_name, tlg_category_description) 
            VALUES (?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg_category_id,
                tlg_category_name,
                tlg_category_description 
            FROM tlg_category
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_category_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE tlg_category
            SET tlg_category_name = ?,
            tlg_category_description = ?
            WHERE tlg_category_id = ?
            """;
    private CategoryDAO() {
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
    public Category save(Category category) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getCategoryDesc());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                category.setCategoryId(generatedKeys.getInt("tlg_category_id"));
            }
            return category;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Category category) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getCategoryDesc());
            preparedStatement.setInt(3, category.getCategoryId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Category> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Category category = null;
            if (resultSet.next()) {
                category = buildCategory(resultSet);
            }
            return Optional.ofNullable(category);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Category> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Category> categories = new ArrayList<>();
            while (resultSet.next()) {
                categories.add(buildCategory(resultSet));
            }
            return categories;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static CategoryDAO getInstance() {
        return INSTANCE;
    }

    private Category buildCategory(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getInt("tlg_category_id"),
                resultSet.getString("tlg_category_name"),
                resultSet.getString("tlg_category_description")
        );
    }
}
