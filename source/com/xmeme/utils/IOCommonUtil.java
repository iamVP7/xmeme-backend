package com.xmeme.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

public class IOCommonUtil {
    public static boolean isValidObject(Object objectToCheck) {
        return objectToCheck != null;
    }

    public static boolean isValidLong(long numberToCheck){
        return numberToCheck != -1L;
    }
    public static boolean isValidString(String objectToCheck) {
        return !isValidObject(objectToCheck) || !objectToCheck.equalsIgnoreCase(Constants.EMPTY_STRING);
    }

    public static JSONObject addJSONKeyValue(JSONObject objecToModifiy, String keyToAdd, Object valueToInsert ) {
        try {

            if ( !isValidJSON(objecToModifiy) ) {
                objecToModifiy = new JSONObject();
            }
            if ( isValidString(keyToAdd) && isValidObject(valueToInsert) ) {
                objecToModifiy.put(keyToAdd, valueToInsert);
            }
        } catch ( JSONException exception ) {

        }
        return objecToModifiy;
    }
    public static boolean isValidJSON( JSONObject jsonObjectToCheck ) {
        return IOCommonUtil.isValidObject(jsonObjectToCheck) && !jsonObjectToCheck.isEmpty();
    }
    public static long getCurrentMillSecondsinUTC() {
        return Instant.now(Clock.systemUTC()).toEpochMilli();
    }

    public static boolean isValidList(List<?> listToCheck){
        return listToCheck != null && !listToCheck.isEmpty();
    }
}
