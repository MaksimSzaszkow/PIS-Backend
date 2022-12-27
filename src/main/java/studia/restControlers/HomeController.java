package studia.restControlers;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import studia.service.Firebase;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class HomeController {

    @Inject private Firebase firebase;

    @Produces(MediaType.TEXT_PLAIN)
    @Get("/verify-auth")
    public String index(Principal principal) throws IOException, InterruptedException, ExecutionException {
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
