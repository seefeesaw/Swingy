package com.ashongwe.swingy.model;

import java.util.Random;

public enum ObstacleType {
    Tree,
    Rock,
    Reed,
    Fire,
    Flower,
    RedFlower,
    Sunflower;

    public static ObstacleType getRandomObstacle() {
        Random random = new Random();

        return values()[random.nextInt(values().length)];
    }
}
