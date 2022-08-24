/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.dao;

import fezas.telegra.entity.Address;
import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressDAO implements Dao<Integer, Address> {
    private static final AddressDAO INSTANCE = new AddressDAO();
    private static final String DELETE_SQL = """
            DELETE FROM tlg_address
            WHERE tlg_address_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tlg_address (tlg_address_callsign, tlg_address_name, tlg_address_person, tlg_address_person_respect) 
            VALUES (?, ?, ?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT tlg_address_id,
                tlg_address_callsign,
                tlg_address_name,
                tlg_address_person,
                tlg_address_person_respect
            FROM tlg_address
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tlg_address_id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE tlg_address
            SET tlg_address_callsign = ?,
            tlg_address_name = ?,
            tlg_address_person = ?,
            tlg_address_person_respect = ? 
            WHERE tlg_address_id = ?
            """;
    private AddressDAO() {
    }
    public static AddressDAO getInstance() {
        return INSTANCE;
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
    public Address save(Address address) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, address.getTlgAddressCallsign());
            preparedStatement.setString(2, address.getTlgAddressName());
            preparedStatement.setString(3, address.getTlgAddressPerson());
            preparedStatement.setString(4, address.getTlgAddressPersonRespect());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                address.setTlgAddressId(generatedKeys.getInt("tlg_address_id"));
            }
            return address;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public void update(Address address) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, address.getTlgAddressCallsign());
            preparedStatement.setString(2, address.getTlgAddressName());
            preparedStatement.setString(3, address.getTlgAddressPerson());
            preparedStatement.setString(4, address.getTlgAddressPersonRespect());
            preparedStatement.setInt(5, address.getTlgAddressId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Address> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Address address = null;
            if (resultSet.next()) {
                address = buildAddress(resultSet);
            }
            return Optional.ofNullable(address);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }

    public List<Address> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Address> addresses = new ArrayList<>();
            while (resultSet.next()) {
                addresses.add(buildAddress(resultSet));
            }
            return addresses;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
    }



    private Address buildAddress(ResultSet resultSet) throws SQLException {
        return new Address(
                resultSet.getInt("tlg_address_id"),
                resultSet.getString("tlg_address_callsign"),
                resultSet.getString("tlg_address_name"),
                resultSet.getString("tlg_address_person"),
                resultSet.getString("tlg_address_person_respect"),
                ""
        );
    }
}