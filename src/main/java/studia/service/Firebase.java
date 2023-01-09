package studia.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class Firebase {

    private boolean initialized = false;
    private Firestore db;

    @PostConstruct
    public void init() {
        if (initialized) {
            return;
        }

        try {
            if (new ResourceResolver().getLoader(ClassPathResourceLoader.class).isEmpty())
                throw new IOException("ResourceResolver is empty");

            var loader = new ResourceResolver()
                    .getLoader(ClassPathResourceLoader.class)
                    .get();


            if ((loader.getResourceAsStream("firebase-adminsdk.json").isEmpty()))
                throw new IOException("firebase-adminsdk.json is empty");

            var credentials = GoogleCredentials.fromStream(
                    loader.getResourceAsStream("firebase-adminsdk.json").get());

            var firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            var firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            db = firestoreOptions.getService();

            initialized = true;
        } catch (IOException e) {
            LoggerFactory
                    .getLogger(Firebase.class)
                    .error("Failed to initialize Firebase authentication", e);
        }
    }

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public Firestore getDb() {
        return db;
    }

}