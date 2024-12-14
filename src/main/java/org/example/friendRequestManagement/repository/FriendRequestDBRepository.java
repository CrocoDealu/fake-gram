package org.example.friendRequestManagement.repository;

import org.example.utils.Pair;
import org.example.dto.FriendRequestFilterDTO;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.friendRequestManagement.entity.validator.FriendRequestValidator;
import org.example.sharedInfrastructure.AbstractDBRepository;
import org.example.sharedInfrastructure.BaseFilter;
import org.example.utils.FriendshipRequestStatus;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendRequestDBRepository extends AbstractDBRepository<Pair<String, String>, FriendRequest> implements FriendRequestRepository {

    public FriendRequestDBRepository(FriendRequestValidator validator, String url, String user, String password) {
        super(validator, url, user, password);
    }

    @Override
    protected String getTableName() {
        return "FriendshipRequests";
    }

    @Override
    protected FriendRequest mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        String fromUsername = resultSet.getString("from_username");
        String toUsername = resultSet.getString("to_username");
        String status = resultSet.getString("status");
        LocalDateTime friendshipDate = resultSet.getTimestamp("friendship_request_date").toLocalDateTime();
        return new FriendRequest(fromUsername, toUsername, status, friendshipDate);
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        String query = "INSERT INTO " + getTableName() + "(\"from_username\", \"to_username\",  \"status\", \"friendship_request_date\") VALUES (?,?,?,?)";
        validator.validate(entity);
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, entity.getFromUsername());
            statement.setString(2, entity.getToUsername());
            statement.setString(3, entity.getStatus());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        String query = "UPDATE " + getTableName() + " SET status=? WHERE from_username=? and to_username=?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, entity.getStatus());
            statement.setString(2, entity.getFromUsername());
            statement.setString(3, entity.getToUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    public void deleteAcceptedOrDeclined() {
        String query = "DELETE FROM " + getTableName() + " WHERE status IN (?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, FriendshipRequestStatus.ACCEPTED.toString());
            statement.setString(2, FriendshipRequestStatus.DECLINED.toString());
            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public Map<String, Boolean> getForUser(String username) {
//        String query = "SELECT * FROM " + getTableName() + " WHERE from_username=? OR to_username=?";
//        Map<String, Boolean> friendRequests = new HashMap<>();
//        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
//            statement.setString(1, username);
//            statement.setString(2, username);
//
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                String fromUsername = resultSet.getString("from_username");
//                String toUsername = resultSet.getString("to_username");
//                if (!friendRequests.containsKey(fromUsername) && !Objects.equals(fromUsername, username)) {
//                    friendRequests.put(fromUsername, true);
//                }
//                if (!friendRequests.containsKey(toUsername) && !Objects.equals(toUsername, username)) {
//                    friendRequests.put(toUsername, true);
//                }
//            }
//            return friendRequests;
//        } catch (SQLException e) {
//            throw new RuntimeException("Error while getting friendship request");
//        }
//    }

    @Override
    public Page<FriendRequest> findAllOnPage(Pageable pageable) {
        return super.findAllOnPage(pageable, null);
    }

    @Override
    public Page<FriendRequest> findAllOnPage(Pageable pageable, FriendRequestFilterDTO friendRequestFilterDTO) {
        return super.findAllOnPage(pageable, friendRequestFilterDTO);
    }

    @Override
    protected Pair<String, List<Object>> toSql(BaseFilter<FriendRequest> filterDTO) {
        if (filterDTO == null) {
            return new Pair<>("", Collections.emptyList());
        }
        if (filterDTO instanceof FriendRequestFilterDTO filter) {
            List<String> conditions = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            filter.getUsername().ifPresent(username -> {
                conditions.add("from_username = ? OR to_username = ?");
                params.add(username);
                params.add(username);
            });

            filter.getStatus().ifPresent(status -> {
                conditions.add("status = ?");
                params.add(status);
            });
            return new Pair<>(String.join(" AND ", conditions), params);
        }
        return new Pair<>("", Collections.emptyList());
    }
}
