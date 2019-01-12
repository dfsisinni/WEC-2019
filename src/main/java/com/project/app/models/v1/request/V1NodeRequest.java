package com.project.app.models.v1.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V1NodeRequest {

    private int id;
    private String label;
    private int value;

}
