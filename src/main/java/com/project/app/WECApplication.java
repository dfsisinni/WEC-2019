package com.project.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WECApplication {

    public static void main(String[] args) {
        SpringApplication.run(WECApplication.class, args);

//        int firstStartingCost = 2;
//        int firstLine[] = {1, 10};
//        int switchingFromLineOne[] = {0, 2};
//        int firstLineEndingCost = 5;
//
//        int secondStartingCost = 6;
//        int secondLine[] = {3, 2};
//        int switchingFromLineTwo[] = {0, 5};
//        int secondLineEndingCost = 20;
//
//        int lowestCostToLineOne[] = new int[2];
//        int lowestCostToLineTwo[] = new int[2];
//
//        lowestCostToLineOne[0] = firstStartingCost + firstLine[0];
//        lowestCostToLineTwo[0] = secondStartingCost + secondLine[0];
//
//        for (int i = 1; i < 2; i++) {
//            int fromLineOneToLineOne = lowestCostToLineOne[i - 1] + firstLine[i];
//            int fromLineTwoToLineOne = lowestCostToLineTwo[i - 1] + switchingFromLineTwo[i] + firstLine[i];
//
//            int fromLineOneToLineTwo = lowestCostToLineOne[i - 1] + switchingFromLineOne[i] + secondLine[i];
//            int fromLineTwoToLineTwo = lowestCostToLineTwo[i - 1] + secondLine[i];
//
//            lowestCostToLineOne[i] = Math.min(fromLineOneToLineOne, fromLineTwoToLineOne);
//            lowestCostToLineTwo[i] = Math.min(fromLineOneToLineTwo, fromLineTwoToLineTwo);
//        }
//
//        int lineOneCost = lowestCostToLineOne[1] + firstLineEndingCost;
//        int lineTwoCost = lowestCostToLineTwo[1] + secondLineEndingCost;
//
//        System.out.println(Math.min(lineOneCost, lineTwoCost));

    }

}

