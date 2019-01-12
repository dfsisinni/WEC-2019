package com.project.app.models.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class V1AlgorithmResponse {

    private List<Integer> nodes;
    private List<V1EdgeResponse> edges;

}
