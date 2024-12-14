package org.example.userManagement.repository;

import org.example.dto.UserFilterDTO;
import org.example.exceptions.ServiceValidationException;
import org.example.sharedInfrastructure.AbstractDBRepository;
import org.example.userManagement.entity.User;
import org.example.userManagement.entity.validator.UserValidator;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;


import java.sql.*;
import java.util.*;

public class UserDBRepository extends AbstractDBRepository<String, User> implements UserRepository {
    public UserDBRepository(UserValidator userValidator, String url, String user, String password) {
        super(userValidator, url, user, password);
    }

    @Override
    protected User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        String username = resultSet.getString("username");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String profileImage = resultSet.getString("profile_image");
        String bio = resultSet.getString("bio");
        return new User(firstName, lastName, username, profileImage, bio);
    }

    @Override
    protected String getTableName() {
        return "Users";
    }

    @Override
    public Optional<User> save(User entity) {
        String query = """
                INSERT INTO Users("first_name", "last_name", "username", "profile_image", "bio")
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT ("username")
                DO NOTHING
        """;
        validator.validate(entity);
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getId());
            statement.setString(4, entity.getProfileImage());
            statement.setString(5, entity.getBio());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceValidationException("Username already exsists");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<User> update(User entity) {
        String query = """
                UPDATE Users
                        SET first_name = ?,
                            last_name = ?,
                            profile_image = ?,
                            bio = ?
                        WHERE username = ?;
        """;
        validator.validate(entity);
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getProfileImage());
            preparedStatement.setString(4, entity.getBio());
            preparedStatement.setString(5, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, null);
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, UserFilterDTO userFilterDTO) {
        return super.findAllOnPage(pageable, userFilterDTO);
    }
}