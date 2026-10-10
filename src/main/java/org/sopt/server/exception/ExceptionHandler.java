package org.sopt.server.exception;

import org.springframework.stereotype.Component;
import org.sopt.common.response.Response;
import java.util.function.Supplier;

@Component
public class ExceptionHandler {
    public <T> Response<T> execute(Supplier<Response<T>> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException | PostNotFoundException e) {
            return new Response<>(false, e.getMessage(), null);
        }
    }
}
