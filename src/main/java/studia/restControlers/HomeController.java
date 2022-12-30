package studia.restControlers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.service.Firebase;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class HomeController {

    @Inject private Firebase firebase;

    @Produces(MediaType.TEXT_PLAIN)
    @Get("/verify-auth")
    public String index(Principal principal) throws InterruptedException, ExecutionException {
        Firestore db = firebase.getDb();

        DocumentReference docRef = db.collection("test").document("test");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getData().toString();
        }

        return principal.getName();
    }
}
