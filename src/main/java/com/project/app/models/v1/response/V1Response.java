package com.project.app.models.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class V1Response<T> {

    private boolean success;
    private T response;

    public static V1Response<String> error(String message) {
        return new V1Response<String>(false, message);
    }

    public static <T> V1Response<T> of(T message) {
        return new V1Response<T>(true, message);
    }

}
