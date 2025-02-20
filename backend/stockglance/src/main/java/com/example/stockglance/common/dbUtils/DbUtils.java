package com.example.stockglance.common.dbUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DbUtils {

    final String url = "jdbc:postgresql://localhost:5432/stockglance";
    final String user = "root";
    final String password = "ritik768";

    /**
     * Returns an object of type T populated with data from the database.
     *
     * @param query      The SQL query to execute
     * @param clazz      The class type to map the result to
     * @param parameters Parameters to set in the prepared statement
     * @return A populated instance of type T
     * @throws Exception if an error occurs
     */
    public <T> T returnedAsObject(String query, Class<T> clazz, Object... parameters) throws Exception {
        T result = null;
        try (Connection con = DriverManager.getConnection(url, user, password)) {

            // Use PreparedStatement to prevent SQL injection
            PreparedStatement preparedStatement = con.prepareStatement(query);

            // Set parameters (if any) to the PreparedStatement
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object object = parameters[i];
                    if (object instanceof String) {
                        preparedStatement.setString(i + 1, (String) object);
                    } else if (object instanceof Integer) {
                        preparedStatement.setInt(i + 1, (Integer) object);
                    } else if (object instanceof BigDecimal) {
                        preparedStatement.setBigDecimal(i + 1, (BigDecimal) object);
                    } else {
                        preparedStatement.setObject(i + 1, object);
                    }
                }
            }

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Assuming we are retrieving only one object from the query
            if (resultSet.next()) {
                result = clazz.getDeclaredConstructor().newInstance(); // Create an instance of T

                // Iterate over the fields of class T and its superclasses
                Class<?> currentClass = clazz;
                while (currentClass != null) {
                    for (Field field : currentClass.getDeclaredFields()) {
                        field.setAccessible(true); // Allow access to private fields

                        // Get the column value using field name
                        Object value = resultSet.getObject(field.getName());
                        if (value != null) {
                            field.set(result, value); // Set the value to the corresponding field
                        }
                    }
                    currentClass = currentClass.getSuperclass(); // Move to superclass
                }
            }

            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error retrieving object: " + e.getMessage());
        }
        return result;
    }
    private ObjectMapper mapper = new ObjectMapper(); // JSON mapper

    /**
     * Saves an object with dynamic fields to the specified table.
     *
     * @param object    The object to save, with its fields saved as columns
     * @param tableName The name of the database table
     * @throws Exception if an error occurs during the save operation
     */
    public <T> void saveObject(T object, String tableName) throws Exception {
        // Convert the entire object to JSON for the `data` column
        String json = mapper.writeValueAsString(object);

        // Use reflection to dynamically get all field names and values
        Map<String, Object> fieldMap = new HashMap<>();
        // Use reflection to dynamically get all fields, including inherited ones
        Class<?> clazz = object.getClass();

        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // Access private fields
                Object fieldValue = field.get(object);

                if (!isSerializable(fieldValue) && !(field.getName().equalsIgnoreCase("createdOn")
                        || field.getName().equalsIgnoreCase("modifiedOn")
                        || field.getName().equalsIgnoreCase("dateOfBirth")
                        || field.getName().equalsIgnoreCase("transactionDate"))) {
                    // If the field is not serializable, convert it to a JSON string
                    fieldValue = mapper.writeValueAsString(fieldValue); // Convert to JSON string
                }
                fieldMap.put(field.getName(), fieldValue); // Store field name and processed value

            }
            clazz = clazz.getSuperclass(); // Move to superclass
        }

        // SQL part to add the JSON data column
        StringBuilder columns = new StringBuilder("");
        StringBuilder placeholders = new StringBuilder("");

        // Add field names dynamically from the object
        Integer i=1;
        for (String fieldName : fieldMap.keySet()) {
            if(fieldMap.size()==i){
                columns.append(fieldName).append("");
                placeholders.append("?");
            }
            else {
                columns.append(fieldName).append(" ,");
                placeholders.append("? ,");
            }
            i++;

        }

        // Construct final SQL statement
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Set the JSON data column as the first parameter
//            stmt.setString(1, json);

            // Set field values dynamically in the prepared statement
            int paramIndex = 1;
            for (Object value : fieldMap.values()) {
                stmt.setObject(paramIndex++, value);
            }

            // Execute the insert
            stmt.executeUpdate();
            System.out.println("Object saved with dynamic fields in table: " + tableName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error saving object: " + e.getMessage());
        }
    }

    /** Updates an object in the specified table with dynamic fields.
     *
     * @param object    The object to update, with its fields saved as columns
     * @param tableName The name of the database table
     * @param id        The ID of the record to update
     * @throws Exception if an error occurs during the update operation
     */
    public <T> void updateObject(T object, String tableName, String id) throws Exception {
        // Use reflection to dynamically get all field names and values from the object and its superclasses
        Map<String, Object> fieldMap = new HashMap<>();
        Class<?> currentClass = object.getClass();

        while (currentClass != null) { // Loop through the class hierarchy
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true); // Access private fields
                Object fieldValue = field.get(object);

                // If the field is not serializable, convert it to a JSON string
                if (!isSerializable(fieldValue) && !(field.getName().equalsIgnoreCase("createdOn") || field.getName().equalsIgnoreCase("modifiedOn"))) {
                    fieldValue = mapper.writeValueAsString(fieldValue); // Convert to JSON string
                }
                fieldMap.put(field.getName(), fieldValue); // Store field name and processed value

                // Debugging: Print each field and its value
                System.out.println("Field: " + field.getName() + ", Value: " + fieldValue);
            }
            currentClass = currentClass.getSuperclass(); // Move to superclass
        }

        // Build the SET clause of the SQL statement dynamically
        StringBuilder setClause = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(fieldName).append(" = ?");
        }

        // Construct final SQL statement
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE id = ?";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Set field values dynamically in the prepared statement
            int paramIndex = 1;
            for (Object value : fieldMap.values()) {
                stmt.setObject(paramIndex++, value);
            }

            // Set the ID as the last parameter
            stmt.setString(paramIndex, id);

            // Execute the update
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Object updated with dynamic fields in table: " + tableName);
            } else {
                System.out.println("No record found with ID: " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating object: " + e.getMessage());
        }
    }

    public <T> List<T> returnedAsList(String query, Class<T> clazz, Object... parameters) throws Exception {
        List<T> resultList = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            // Set parameters (if any) in the prepared statement
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    preparedStatement.setObject(i + 1, parameters[i]);
                }
            }

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Map each row to an instance of the specified class
            while (resultSet.next()) {
                T instance = clazz.getDeclaredConstructor().newInstance(); // Create an instance of T

                // Iterate over fields of the class and its superclasses
                Class<?> currentClass = clazz;
                while (currentClass != null) {
                    for (Field field : currentClass.getDeclaredFields()) {
                        field.setAccessible(true); // Allow access to private fields

                        Object value = resultSet.getObject(field.getName());

                        // Check if the field type requires deserialization from JSON
                        if (value instanceof String && !field.getType().isPrimitive() && !field.getType().equals(String.class)) {
                            // Deserialize JSON into the field's type
                            value = mapper.readValue((String) value, field.getType());
                        }

                        // Set the value to the corresponding field in the instance
                        if (value != null) {
                            field.set(instance, value);
                        }
                    }
                    currentClass = currentClass.getSuperclass(); // Move to the superclass
                }

                resultList.add(instance); // Add the populated instance to the result list
            }

            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error retrieving list of objects: " + e.getMessage());
        }

        return resultList;
    }
    /**
     * Checks if a given object is serializable to be used directly in SQL.
     */
    private boolean isSerializable(Object obj) {
        return (obj == null) || (obj instanceof String || obj instanceof Number || obj instanceof Boolean);
    }
}



