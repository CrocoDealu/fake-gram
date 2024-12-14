package org.example.authentificationManagement.repository;

import org.example.authentificationManagement.entity.UserCredential;
import org.example.exceptions.ServiceValidationException;
import org.example.sharedInfrastructure.Validator;
import org.example.sharedInfrastructure.AbstractDBRepository;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserCredentialsDBRepository extends AbstractDBRepository<String, UserCredential> implements UserCredentialsRepository {

    public UserCredentialsDBRepository(Validator<UserCredential> validator, String url, String user, String password) {
        super(validator, url, user, password);

    }

    @Override
    protected UserCredential mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        return new UserCredential(username, password);
    }

    @Override
    protected String getTableName() {
        return "UserCredentials";
    }

    @Override
    public Optional<UserCredential> save(UserCredential entity) {
        validator.validate(entity);
        String query = "INSERT INTO UserCredentials(\"username\", \"password\") VALUES (?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<UserCredential> update(UserCredential entity) {
        return Optional.empty();
    }

    @Override
    public Page<UserCredential> findAllOnPage(Pageable pageable) {
        return null;
    }
}
