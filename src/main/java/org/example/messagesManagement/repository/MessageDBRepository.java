package org.example.messagesManagement.repository;

import org.example.utils.Pair;
import org.example.messagesManagement.entity.Message;
import org.example.sharedInfrastructure.Validator;
import org.example.sharedInfrastructure.AbstractDBRepository;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageDBRepository extends AbstractDBRepository<Pair<String, Pair<String, LocalDateTime>>, Message> implements MessageRepository {

    public MessageDBRepository(Validator<Message> validator, String url, String user, String password) {
        super(validator, url, user, password);
    }

    @Override
    protected String getTableName() {
        return "messages";
    }

    @Override
    protected Message mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        String fromUsername = resultSet.getString("from_username");
        String toUsername = resultSet.getString("to_username");
        String content = resultSet.getString("message");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        Message message = new Message(fromUsername, toUsername, content, date);
        String fromReplied = resultSet.getString("from_replied_username");
        String toReplied = resultSet.getString("to_replied_username");
        Timestamp dateReply = resultSet.getTimestamp("date_of_replied");
        if (fromReplied != null && toReplied != null && dateReply != null) {
            Message message1 = new Message(fromReplied, toReplied, "", dateReply.toLocalDateTime());
            message.setReply(message1);
        }
        return message;
    }

    @Override
    public Optional<Message> save(Message entity) {
        String query = "INSERT INTO messages (from_username, to_username, message, date, from_replied_username, to_replied_username, date_of_replied) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, entity.getFromUsername());
            preparedStatement.setString(2, entity.getToUsername());
            preparedStatement.setString(3, entity.getMessage());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(entity.getDateSend()));
            if (entity.getReply() != null) {
                preparedStatement.setString(5, entity.getReply().getFromUsername());
                preparedStatement.setString(6, entity.getReply().getToUsername());
                preparedStatement.setTimestamp(7, Timestamp.valueOf(entity.getReply().getDateSend()));
            } else {
                preparedStatement.setNull(5, Types.NULL);
                preparedStatement.setNull(6, Types.NULL);
                preparedStatement.setNull(7, Types.NULL);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> update(Message entity) {
        String query = "UPDATE " + getTableName() + " SET message = ?, from_replied_username = ?, to_replied_username = ?, date_of_replied = ? WHERE from_username = ? AND to_username = ? AND date = ?";
        try(Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, entity.getMessage());
            if (entity.getReply() != null) {
                preparedStatement.setString(2, entity.getReply().getFromUsername());
                preparedStatement.setString(3, entity.getReply().getToUsername());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(entity.getReply().getDateSend()));
            } else {
                preparedStatement.setNull(2, Types.NULL);
                preparedStatement.setNull(3, Types.NULL);
                preparedStatement.setNull(4, Types.NULL);
            }
            preparedStatement.setString(5, entity.getFromUsername());
            preparedStatement.setString(6, entity.getToUsername());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(entity.getReply().getDateSend()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Pair<String, Pair<String, LocalDateTime>> entityId) {
        String query = "DELETE FROM " + getTableName() + " WHERE \"from_username\" = ? and \"to_username\" = ? AND \"date\" = ?" ;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entityId.getFirst());
            statement.setString(2, entityId.getSecond().getFirst());
            statement.setTimestamp(3, Timestamp.valueOf(entityId.getSecond().getSecond()));
            int rowsAffected = statement.executeUpdate();
            Pair<String, Pair<String, LocalDateTime>> mId = new Pair<>(entityId.getFirst(), new Pair<>(entityId.getSecond().getFirst(), entityId.getSecond().getSecond()));
            return rowsAffected > 0 ? findOne(mId) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> findOne(Pair<String, Pair<String, LocalDateTime>> entityId) {
        String query = "SELECT * FROM " + getTableName() + " WHERE \"from_username\" = ? and \"to_username\" = ? and \"date\" = ?" ;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entityId.getFirst());
            statement.setString(2, entityId.getSecond().getFirst());
            statement.setTimestamp(3, Timestamp.valueOf(entityId.getSecond().getSecond()));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(mapResultSetToEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> findAll() {
        String query = "SELECT * FROM " + getTableName();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Pair<String, Pair<String, LocalDateTime>>, Message> entities = new HashMap<>();
            while (resultSet.next()) {
                Message entity = mapResultSetToEntity(resultSet);
                String from = resultSet.getObject("from_username", String.class);
                String to = resultSet.getObject("to_username", String.class);
                Timestamp date = resultSet.getObject("date", Timestamp.class);
                Pair<String, Pair<String, LocalDateTime>> id = new Pair<>(
                        from,
                        new Pair<>(
                                to,
                                date.toLocalDateTime()
                        )
                );

                entities.put(id, entity);
            }
            return entities.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Message> findAllOnPage(Pageable pageable) {
        return null;
    }
}
