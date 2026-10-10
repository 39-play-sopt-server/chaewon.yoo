package org.sopt.server.exception;

import org.sopt.common.response.Response;
import java.util.function.Supplier;

public class ExceptionHandler {
    public <T> Response<T> execute(Supplier<Response<T>> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException | PostNotFoundException e) {
            return new Response<>(false, e.getMessage(), null);
        }
    }
}
