package studia.exceptionHandlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {ReservedRoomException.class, ExceptionHandler.class})
public class ReservedRoomExceptionHandler implements ExceptionHandler<ReservedRoomException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, ReservedRoomException exception) {
        return HttpResponse.badRequest(exception.getMessage());
    }
}