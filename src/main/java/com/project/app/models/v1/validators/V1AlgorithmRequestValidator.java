package com.project.app.models.v1.validators;

import com.project.app.models.v1.request.V1AlgorithmRequest;

public class V1AlgorithmRequestValidator {

    public static String validateV1AlgorithmRequest(V1AlgorithmRequest request) {
        if (request.getNodes().size() - 2 <= 0) {
            return "No nodes supplied!";
        }

        if (request.getEdges().size() == 0) {
            return "No edges supplied!";
        }

        if (request.getNodes().size() % 2 != 0) {
            return "Invalid number of nodes supplied!";
        }

        if ((request.getNodes().size() - 2)*2 != request.getEdges().size()) {
            return "Invalid node and edge input!";
        }

        return null;
    }

}
