package studia.datatypes;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonClassDescription
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData {
    public String user_id;
}
