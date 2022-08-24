/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Template;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemplateDAO implements Dao<Long, Template>  {

    private static final TemplateDAO INSTANCE = new TemplateDAO();
    private static final String DELETE_SQL = """
            DELETE FROM template
            WHERE template_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO template (template_title, template_body, template_date) 
            VALUES (?, ?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT template_id,
                template_title,
                template_body,
                template_date 
            FROM template
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE template_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE template
            SET template_id = ?,
            template_title = ?,
            template_body = ?,
            template_date = ? 
            WHERE template_id = ?
            """;

    private TemplateDAO() {
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

    public Template save(Template template) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, template.getTitle());
            preparedStatement.setString(2, template.getBody());
            preparedStatement.setTimestamp(3, template.getDate());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                template.setId(generatedKeys.getLong("template_id"));
            }
            return template;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Template template) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setLong(1, template.getId());
            preparedStatement.setString(2, template.getTitle());
            preparedStatement.setString(3, template.getBody());
            preparedStatement.setTimestamp(4, template.getDate());
            preparedStatement.setLong(5, template.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Template> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            Template template = null;
            if (resultSet.next()) {
                template = buildTemplate(resultSet);
            }
            return Optional.ofNullable(template);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Template> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Template> templates = new ArrayList<>();
            while (resultSet.next()) {
                templates.add(buildTemplate(resultSet));
            }
            return templates;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public static TemplateDAO getInstance() {
        return INSTANCE;
    }

    private Template buildTemplate(ResultSet resultSet) throws SQLException {
        return new Template(
                resultSet.getLong("template_id"),
                resultSet.getString("template_title"),
                resultSet.getString("template_body"),
                resultSet.getTimestamp("template_date")
        );
    }
}
