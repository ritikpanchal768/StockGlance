package com.example.stockglance.common;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StockGlanceUtils {


    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // Specify the date format
            .create();

    /**
     * Copies all non-null fields from source object to target object.
     *
     * @param source The source object from which to copy fields.
     * @param target The target object to which fields are copied.
     * @throws IllegalArgumentException if source or target is null.
     * @throws IllegalAccessException   if fields are inaccessible.
     */
    public static void copyNonNullFields(Object source, Object target) throws IllegalAccessException {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (value != null) {
                Field targetField = null;
                try {
                    targetField = target.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true);

                    // Check type compatibility before setting the value
                    if (targetField.getType().isAssignableFrom(field.getType())) {
                        targetField.set(target, value);
                    } else {
                        throw new IllegalArgumentException(
                                "Type mismatch: Cannot assign " + field.getType() + " to " + targetField.getType()
                        );
                    }
                } catch (NoSuchFieldException e) {
                    // Skip if the target does not have the field
                    continue;
                }
            }
        }
    }

    public String generateCashExpenseId(String prifix) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = formatter.format(new Date());
        return "CASH" + timestamp;            //  ID based on the current timestamp:
    }


    public static String toJSON(Object object){

        if (object == null){
            return null;
        }

        String objectJson = gson.toJson(object);
        return objectJson;
    }
}
