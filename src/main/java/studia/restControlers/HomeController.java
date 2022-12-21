package studia.restControlers;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.io.IOException;
import java.security.Principal;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class HomeController {

    @Produces(MediaType.TEXT_PLAIN)
    @Get
    public String index(Principal principal) throws IOException {
        var loader = new ResourceResolver()
                    .getLoader(ClassPathResourceLoader.class)
                    .get();
        
        var credentials = GoogleCredentials.fromStream(
                    loader.getResourceAsStream("firebase-adminsdk.json").get());
        
        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
        .setProjectId("pis-projekt-a4a77")
        .setCredentials(credentials)
        .build();

        Firestore db = firestoreOptions.getService();

        DocumentReference docRef = db.collection("test").document("test");
        System.out.println(docRef);

        return principal.getName();
    }
}
