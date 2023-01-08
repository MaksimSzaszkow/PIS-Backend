package studia.utils;

import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import studia.datatypes.UserData;

public class UserRole {

    public static String getRole(String authToken) {
        var payload = authToken.split("\\.")[1];
        var json = new String(Base64.getDecoder().decode(payload));

        try {
            var objMapper = new ObjectMapper();
            var userData = objMapper.readValue(json, UserData.class);
            var userRecord = FirebaseAuth.getInstance().getUser(userData.user_id);

            if (userRecord.getCustomClaims().containsKey("role")) {
                return userRecord.getCustomClaims().get("role").toString();
            } else {
                return "";
            }
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        } catch (FirebaseAuthException e) {
            throw new AssertionError(e);
        }
    }

}
