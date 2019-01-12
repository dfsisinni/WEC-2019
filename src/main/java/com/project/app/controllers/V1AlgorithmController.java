package com.project.app.controllers;

import com.project.app.models.v1.request.V1AlgorithmRequest;
import com.project.app.models.v1.request.V1EdgeRequest;
import com.project.app.models.v1.request.V1NodeRequest;
import com.project.app.models.v1.response.V1AlgorithmResponse;
import com.project.app.models.v1.response.V1EdgeResponse;
import com.project.app.models.v1.response.V1Response;
import com.project.app.models.v1.validators.V1AlgorithmRequestValidator;
import lombok.val;
import org.springframework.web.bind.annotation.*;
import com.project.app.models.algorithm.Node;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/algorithm")
public class V1AlgorithmController {

    @PostMapping
    public V1Response compute(@NotNull @RequestBody @Valid V1AlgorithmRequest request) {
        val result = V1AlgorithmRequestValidator.validateV1AlgorithmRequest(request);
        if (result != null) {
            return V1Response.error(result);
        }

        val numberOfProcesses = (request.getNodes().size() - 2)/2;

        int firstLineStartingCost = 0;
        val firstLine = new Node[numberOfProcesses];
        val fromFirstLine = new int[numberOfProcesses];
        val lowestCostLineOne = new int[numberOfProcesses];
        int firstLineEndingCost = 0;

        int secondLineStartingCost = 0;
        val secondLine = new Node[numberOfProcesses];
        val fromSecondLine = new int[numberOfProcesses];
        val lowestCostLineTwo = new int[numberOfProcesses];
        int secondLineEndingCost = 0;

        for (V1NodeRequest node : request.getNodes()) {
            if (node.getId() == 0 || node.getId() == 9999) {
                continue;
            }

            int index = (int) Math.floor(node.getId()*1.0/2.0);
            if (node.getId() % 2 == 0) {
                secondLine[index - 1] = Node.builder()
                    .id(node.getId())
                    .value(node.getValue())
                    .build();
            } else  {
                firstLine[index] = Node.builder()
                        .id(node.getId())
                        .value(node.getValue())
                        .build();
            }
        }

        for (V1EdgeRequest edge : request.getEdges()) {
            if (edge.getFrom() == 0) {
                if (edge.getTo() == 1) {
                    firstLineStartingCost = edge.getValue();
                } else if (edge.getTo() == 2) {
                    secondLineStartingCost = edge.getValue();
                }
            } else if (edge.getTo() == 9999) {
                if (edge.getFrom() % 2 == 0) {
                    secondLineEndingCost = edge.getValue();
                } else {
                    firstLineEndingCost = edge.getValue();
                }
            } else {
                if (edge.getFrom() % 2 == 0) {
                    //from second line
                    //2, 4, 6, 8
                    fromSecondLine[edge.getFrom()/2] = edge.getValue();
                } else {
                    fromFirstLine[(edge.getFrom() + 1)/2] = edge.getValue();
                    //from first line
                    //1, 3, 5
                }
            }
        }


        lowestCostLineOne[0] = firstLineStartingCost + firstLine[0].getValue();
        lowestCostLineTwo[0] = secondLineStartingCost + secondLine[0].getValue();

        List<Node> oldFirstList = new ArrayList<Node>();
        List<Node> oldSecondList = new ArrayList<Node>();

        for (int i = 1; i < numberOfProcesses; i++) {
            val newFirstList = new ArrayList<Node>();
            val newSecondList = new ArrayList<Node>();

            int fromLineOneToLineOne = lowestCostLineOne[i - 1] + firstLine[i].getValue();
            int fromLineTwoToLineOne = lowestCostLineTwo[i - 1] + fromSecondLine[i] + firstLine[i].getValue();

            int fromLineOneToLineTwo = lowestCostLineOne[i - 1] + fromFirstLine[i] + secondLine[i].getValue();
            int fromLineTwoToLineTwo = lowestCostLineTwo[i - 1] + secondLine[i].getValue();


            lowestCostLineOne[i] = Math.min(fromLineOneToLineOne, fromLineTwoToLineOne);
            if (lowestCostLineOne[i] == fromLineOneToLineOne) {
                newFirstList.addAll(oldFirstList);
                newFirstList.add(firstLine[i - 1]);
            } else {
                newFirstList.addAll(oldSecondList);
                newFirstList.add(secondLine[i - 1]);
            }

            lowestCostLineTwo[i] = Math.min(fromLineOneToLineTwo, fromLineTwoToLineTwo);
            if (lowestCostLineTwo[i] == fromLineOneToLineTwo) {
                newSecondList.addAll(oldFirstList);
                newSecondList.add(firstLine[i - 1]);
            } else {
                newSecondList.addAll(oldSecondList);
                newSecondList.add(secondLine[i - 1]);
            }

            oldFirstList = newFirstList;
            oldSecondList = newSecondList;

            if (numberOfProcesses == i + 1) {
                oldFirstList.add(firstLine[i]);
                oldSecondList.add(secondLine[i]);
            }
        }

        int lineOneCost = lowestCostLineOne[numberOfProcesses - 1] + firstLineEndingCost;
        int lineTwoCost = lowestCostLineTwo[numberOfProcesses - 1] + secondLineEndingCost;

        val resultingNodes = lineOneCost < lineTwoCost ? oldFirstList : oldSecondList;

        /*if (lineOneCost < lineTwoCost) {
            for (Node nd : oldFirstList) {
                System.out.println(nd.getValue());
            }
        } else {
            for (Node nd: oldSecondList) {
                System.out.println(nd.getValue());
            }
        }*/

        val edges = new ArrayList<V1EdgeResponse>();
        edges.add(V1EdgeResponse.builder()
            .from(0)
            .to(resultingNodes.get(0).getId())
            .build());

        for (int i = 1; i < resultingNodes.size(); i++) {
            edges.add(
                    V1EdgeResponse.builder()
                        .from(resultingNodes.get(i - 1).getId())
                        .to(resultingNodes.get(i).getId())
                    .build()
            );
        }

        edges.add(
                V1EdgeResponse.builder()
                    .from(resultingNodes.get(resultingNodes.size() - 1).getId())
                    .to(9999)
                .build()
        );

        val response = V1AlgorithmResponse.builder()
                .nodes(resultingNodes.stream().map(node -> node.getId()).collect(Collectors.toList()))
                .edges(edges)
                .build();

        return V1Response.of(response);
    }


    @RequestMapping(value = "/simplified", method = RequestMethod.POST)
    public V1Response computeSimple(@NotNull @RequestBody @Valid V1AlgorithmRequest request) {
        val result = V1AlgorithmRequestValidator.validateV1AlgorithmRequest(request);
        if (result != null) {
            return V1Response.error(result);
        }

        val numberOfProcesses = (request.getNodes().size() - 2)/2;

        int firstLineStartingCost = 0;
        val firstLine = new Node[numberOfProcesses];
        val fromFirstLine = new int[numberOfProcesses];
        val lowestCostLineOne = new int[numberOfProcesses];
        int firstLineEndingCost = 0;

        int secondLineStartingCost = 0;
        val secondLine = new Node[numberOfProcesses];
        val fromSecondLine = new int[numberOfProcesses];
        val lowestCostLineTwo = new int[numberOfProcesses];
        int secondLineEndingCost = 0;

        for (V1NodeRequest node : request.getNodes()) {
            if (node.getId() == 0 || node.getId() == 9999) {
                continue;
            }

            int index = (int) Math.floor(node.getId()*1.0/2.0);
            if (node.getId() % 2 == 0) {
                secondLine[index - 1] = Node.builder()
                        .id(node.getId())
                        .value(node.getValue())
                        .build();
            } else  {
                firstLine[index] = Node.builder()
                        .id(node.getId())
                        .value(node.getValue())
                        .build();
            }
        }

        for (V1EdgeRequest edge : request.getEdges()) {
            if (edge.getFrom() == 0) {
                if (edge.getTo() == 1) {
                    firstLineStartingCost = edge.getValue();
                } else if (edge.getTo() == 2) {
                    secondLineStartingCost = edge.getValue();
                }
            } else if (edge.getTo() == 9999) {
                if (edge.getFrom() % 2 == 0) {
                    secondLineEndingCost = edge.getValue();
                } else {
                    firstLineEndingCost = edge.getValue();
                }
            } else {
                if (edge.getFrom() % 2 == 0) {
                    //from second line
                    //2, 4, 6, 8
                    fromSecondLine[edge.getFrom()/2] = edge.getValue();
                } else {
                    fromFirstLine[(edge.getFrom() + 1)/2] = edge.getValue();
                    //from first line
                    //1, 3, 5
                }
            }
        }


        lowestCostLineOne[0] = firstLineStartingCost + firstLine[0].getValue();
        lowestCostLineTwo[0] = secondLineStartingCost + secondLine[0].getValue();

        List<Node> oldFirstList = new ArrayList<Node>();
        List<Node> oldSecondList = new ArrayList<Node>();

        for (int i = 1; i < numberOfProcesses; i++) {
            val newFirstList = new ArrayList<Node>();
            val newSecondList = new ArrayList<Node>();

            int fromLineOneToLineOne = lowestCostLineOne[i - 1] + firstLine[i].getValue();
            int fromLineTwoToLineOne = lowestCostLineTwo[i - 1] + fromSecondLine[i] + firstLine[i].getValue();

            int fromLineOneToLineTwo = lowestCostLineOne[i - 1] + fromFirstLine[i] + secondLine[i].getValue();
            int fromLineTwoToLineTwo = lowestCostLineTwo[i - 1] + secondLine[i].getValue();


            lowestCostLineOne[i] = Math.min(fromLineOneToLineOne, fromLineTwoToLineOne);
            if (lowestCostLineOne[i] == fromLineOneToLineOne) {
                newFirstList.addAll(oldFirstList);
                newFirstList.add(firstLine[i - 1]);
            } else {
                newFirstList.addAll(oldSecondList);
                newFirstList.add(secondLine[i - 1]);
            }

            lowestCostLineTwo[i] = Math.min(fromLineOneToLineTwo, fromLineTwoToLineTwo);
            if (lowestCostLineTwo[i] == fromLineOneToLineTwo) {
                newSecondList.addAll(oldFirstList);
                newSecondList.add(firstLine[i - 1]);
            } else {
                newSecondList.addAll(oldSecondList);
                newSecondList.add(secondLine[i - 1]);
            }

            oldFirstList = newFirstList;
            oldSecondList = newSecondList;

            if (numberOfProcesses == i + 1) {
                oldFirstList.add(firstLine[i]);
                oldSecondList.add(secondLine[i]);
            }
        }

        int lineOneCost = lowestCostLineOne[numberOfProcesses - 1] + firstLineEndingCost;
        int lineTwoCost = lowestCostLineTwo[numberOfProcesses - 1] + secondLineEndingCost;

        val resultingNodes = lineOneCost < lineTwoCost ? oldFirstList : oldSecondList;

        /*if (lineOneCost < lineTwoCost) {
            for (Node nd : oldFirstList) {
                System.out.println(nd.getValue());
            }
        } else {
            for (Node nd: oldSecondList) {
                System.out.println(nd.getValue());
            }
        }*/

        val edges = new ArrayList<V1EdgeResponse>();
        edges.add(V1EdgeResponse.builder()
                .from(0)
                .to(resultingNodes.get(0).getId())
                .build());

        for (int i = 1; i < resultingNodes.size(); i++) {
            edges.add(
                    V1EdgeResponse.builder()
                            .from(resultingNodes.get(i - 1).getId())
                            .to(resultingNodes.get(i).getId())
                            .build()
            );
        }

        edges.add(
                V1EdgeResponse.builder()
                        .from(resultingNodes.get(resultingNodes.size() - 1).getId())
                        .to(9999)
                        .build()
        );

        val response = V1AlgorithmResponse.builder()
                .nodes(resultingNodes.stream().map(node -> node.getId()).collect(Collectors.toList()))
                .edges(edges)
                .build();


        val cost = Math.min(lineOneCost, lineTwoCost);

        String path = "";

        if (lineOneCost < lineTwoCost) {
            path += "Start on Line One Cost: " + firstLineStartingCost + "\n";
        } else {
            path += "Start on Line Two Cost: " + secondLineStartingCost + "\n";
        }

        for (int i = 0; i < resultingNodes.size(); i++) {
            val node = resultingNodes.get(i);
            val line = node.getId() % 2 == 0 ? 2 : 1;

            path += "Line #: " + line;
            path += ", Node Value: " + node.getValue();
            path += "\n";
        }

        path += "\n\n";
        path += "Total Cost: " + cost;


        return V1Response.of(path);
    }

}
