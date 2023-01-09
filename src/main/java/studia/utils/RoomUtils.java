package studia.utils;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import studia.datatypes.ReservationData;
import studia.datatypes.RoomData;

import java.util.List;
import java.util.stream.Collectors;

public class RoomUtils {
    public static List<RoomData> mapRooms(QuerySnapshot roomDocuments) {
        return roomDocuments.isEmpty()
                ? List.of()
                : roomDocuments.getDocuments().stream()
                .map((room) -> {
                    RoomData res = room.toObject(RoomData.class);
                    res.setId(room.getId());
                    return res;
                })
                .collect(Collectors.toList());
    }

}
