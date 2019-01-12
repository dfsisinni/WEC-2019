package com.project.app.models.v1.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V1EdgeRequest {

    private int from;
    private int to;
    private int value;
    private String arrows;

}
