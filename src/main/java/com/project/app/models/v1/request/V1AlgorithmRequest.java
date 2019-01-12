package com.project.app.models.v1.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V1AlgorithmRequest {

    @NotNull
    private List<V1NodeRequest> nodes;

    @NotNull
    private List<V1EdgeRequest> edges;

}
