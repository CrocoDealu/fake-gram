package org.example.sharedInfrastructure;

import org.example.utils.Pair;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.util.*;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> {

    private Connection connection;
    protected Validator<E> validator;
    private String url;
    private String user;
    private String password;
    public AbstractDBRepository(Validator<E> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected int count(Connection connection, BaseFilter<E> filter) throws SQLException {
        String sql = "select count(*) as count from " + getTableName();
        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " where " + sqlFilter.getFirst();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param);
            }
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfElements = 0;
                if (result.next()) {
                    totalNumberOfElements = result.getInt("count");
                }
                return totalNumberOfElements;
            }
        }
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    protected Pair<String, List<Object>> toSql(BaseFilter<E> filterDTO) {
        return new Pair<>("", Collections.emptyList());
    }

    protected abstract String getTableName();

    protected abstract E mapResultSetToEntity(ResultSet resultSet) throws SQLException;

    public Optional<E> findOne(ID id) {
        String query = "SELECT * FROM " + getTableName() + " WHERE \"username\" = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(mapResultSetToEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<E> findAllOnPage(Pageable pageable, BaseFilter<E> filter) {
        List<E> entities = new ArrayList<>();
        String query = "SELECT * FROM " + getTableName();
        Pair<String, List<Object>> pair = toSql(filter);

        if (!pair.getFirst().isEmpty()) {
            query += " WHERE " + pair.getFirst();
        }
        if (pageable != null) {
            query += " LIMIT ? OFFSET ? ";
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            int i = 1;
            for (Object param: pair.getSecond()) {
                preparedStatement.setObject(i, param);
                i++;
            }
            i--;
            if (pageable != null) {
                preparedStatement.setInt(i + 1, pageable.getPageSize());
                preparedStatement.setInt(i + 2,pageable.getPageSize() * pageable.getPageNumber());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                E entity = mapResultSetToEntity(resultSet);
                entities.add(entity);
            }
            return new Page<>(entities, count(connection, filter));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<E> findAll() {
        String query = "SELECT * FROM " + getTableName();
        try (PreparedStatement statement = getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Map<ID, E> entities = new HashMap<>();
            while (resultSet.next()) {
                E entity = mapResultSetToEntity(resultSet);
                entities.put(entity.getId(), entity);
            }
            return entities.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Optional<E> save(E entity);

    public Optional<E> delete(ID id) {
        String query = "DELETE FROM " + getTableName() + " WHERE \"username\" = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setObject(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0 ? Optional.ofNullable(findOne(id).orElse(null)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Optional<E> update(E entity);

}
