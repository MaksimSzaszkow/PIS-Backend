package studia.utils;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import studia.datatypes.ReservationData;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationUtils {
    public static List<ReservationData> mapReservations(QuerySnapshot reservationsDocuments) {
        return reservationsDocuments.isEmpty()
                ? List.of()
                : reservationsDocuments.getDocuments().stream()
                .map((snapshot) -> {
                    ReservationData res = snapshot.toObject(ReservationData.class);
                    try {
                        res.setUser(FirebaseAuth.getInstance().getUser(res.getUser()).getEmail());
                    } catch (FirebaseAuthException e) {
                        throw new RuntimeException(e);
                    }
                    res.setId(snapshot.getId());
                    return res;
                })
                .collect(Collectors.toList());
    }

}
