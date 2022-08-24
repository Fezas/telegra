package fezas.telegra.dao;

import fezas.telegra.entity.ParagraphEntity;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParagraphDAO implements Dao<Integer, ParagraphEntity> {
    private static final ParagraphDAO INSTANCE = new ParagraphDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_secrecy_paragraph
            WHERE tlg_secrecy_paragraph_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_secrecy_paragraph (tlg_secrecy_paragraph_id, tlg_secrecy_paragraph_text) 
            VALUES (?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg_secrecy_paragraph_id,
                tlg_secrecy_paragraph_text 
            FROM tlg_secrecy_paragraph
            ORDER BY tlg_secrecy_paragraph_id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_secrecy_paragraph_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE tlg_secrecy_paragraph
            SET tlg_secrecy_paragraph_text = ?
            WHERE tlg_secrecy_paragraph_id = ?
            """;
    private static final String UPDATE_WITH_ID_SQL = """
            UPDATE tlg_secrecy_paragraph
            SET tlg_secrecy_paragraph_text = ?,
            tlg_secrecy_paragraph_id = ? 
            WHERE tlg_secrecy_paragraph_id = ?
            """;

    private ParagraphDAO() {
    }

    public ParagraphEntity save(ParagraphEntity paragraph) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, paragraph.getSecrecyParagraphId());
            preparedStatement.setString(2, paragraph.getSecrecyParagraphText());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                paragraph.setSecrecyParagraphId(generatedKeys.getInt("tlg_secrecy_paragraph_id"));
            }
            return paragraph;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(ParagraphEntity paragraph) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, paragraph.getSecrecyParagraphText());
            preparedStatement.setInt(2, paragraph.getSecrecyParagraphId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public void updateAll(ParagraphEntity paragraph, Integer oldId) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_WITH_ID_SQL)) {
            preparedStatement.setString(1, paragraph.getSecrecyParagraphText());
            preparedStatement.setInt(2, paragraph.getSecrecyParagraphId());
            preparedStatement.setInt(3, oldId);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    public Optional<ParagraphEntity> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            ParagraphEntity paragraph = null;
            if (resultSet.next()) {
                paragraph = buildSecParagraph(resultSet);
            }
            return Optional.ofNullable(paragraph);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<ParagraphEntity> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<ParagraphEntity> paragraphs = new ArrayList<>();
            while (resultSet.next()) {
                paragraphs.add(buildSecParagraph(resultSet));
            }
            return paragraphs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
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

    public static ParagraphDAO getInstance() {
        return INSTANCE;
    }

    private ParagraphEntity buildSecParagraph(ResultSet resultSet) throws SQLException {
        return new ParagraphEntity(
                resultSet.getInt("tlg_secrecy_paragraph_id"),
                resultSet.getString("tlg_secrecy_paragraph_text"),
                ""
        );
    }
}
