package org.example.friendshipManagement.repository;

import org.example.utils.Pair;
import org.example.dto.FriendshipFilterDTO;
import org.example.friendshipManagement.entity.Friendship;
import org.example.sharedInfrastructure.AbstractDBRepository;
import org.example.friendshipManagement.entity.validator.FriendshipValidator;
import org.example.sharedInfrastructure.BaseFilter;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDBRepository extends AbstractDBRepository<Pair<String, String>, Friendship> implements FriendshipRepository {

    public FriendshipDBRepository(FriendshipValidator friendshipValidator, String url, String user, String password) {
        super(friendshipValidator, url, user, password);
    }

    @Override
    protected Friendship mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        String username1 = resultSet.getString("username_1");
        String username2 = resultSet.getString("username_2");
        LocalDateTime friendshipDate = resultSet.getTimestamp("friendship_date").toLocalDateTime();
        return new Friendship(username1, username2, friendshipDate);
    }

    @Override
    protected String getTableName() {
        return "Friendships";
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        String query = "INSERT INTO Friendships(\"username_1\", \"username_2\", \"friendship_date\") VALUES (?,?,?)";
        validator.validate(entity);
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, entity.getUsername1());
            statement.setString(2, entity.getUsername2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Pair<String, String> id) {
        String query = """
            DELETE FROM friendships
            WHERE ("username_1" = ? AND "username_2" = ?)
            OR ("username_1" = ? AND "username_2" = ?)
            RETURNING "username_1", "username_2", "friendship_date";
        """;
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, id.getFirst());
            statement.setString(2, id.getSecond());
            statement.setString(3, id.getSecond());
            statement.setString(4, id.getFirst());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Friendship deletedFriendship = new Friendship(
                            id.getFirst(),
                            id.getSecond(),
                            rs.getTimestamp("friendship_date").toLocalDateTime()
                    );
                    return Optional.of(deletedFriendship);
                } else {
                    System.out.println("No friendship found between " + id.getFirst() + " and " + id.getSecond());
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting friendship between: " + id.getFirst() + " and " + id.getSecond(), e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }

    public void deleteForUsername(String username) {
        String query = """
                DELETE FROM friendships
                WHERE "username_1" = ? OR "username_2" = ?
        """;
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, username);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting friendship");
        }
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable, FriendshipFilterDTO friendshipFilterDTO) {
        return super.findAllOnPage(pageable, friendshipFilterDTO);
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, null);
    }

    @Override
    protected Pair<String, List<Object>> toSql(BaseFilter<Friendship> filterDTO) {
        if (filterDTO == null) {
            return new Pair<>("", Collections.emptyList());
        }
        if (filterDTO instanceof FriendshipFilterDTO friendshipFilter) {
            List<String> conditions = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            friendshipFilter.getUsername().ifPresent(username -> {
                conditions.add("username_1 = ? OR username_2 = ?");
                params.add(username);
                params.add(username);
            });

            friendshipFilter.getStartDate().ifPresent(startDate -> {
                conditions.add("friendship_date > ?");
                params.add(startDate);
            });
            return new Pair<>(String.join(" AND ", conditions), params);
        }
        return new Pair<>("", Collections.emptyList());
    }
}