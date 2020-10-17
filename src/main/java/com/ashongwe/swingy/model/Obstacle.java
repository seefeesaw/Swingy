package com.ashongwe.swingy.model;

public class Obstacle extends GameEntity {
    private ObstacleType obstacleType;

    public Obstacle(ObstacleType obstacleType, int y, int x) {
        super(EntityType.Obstacle, y, x);
        this.obstacleType = obstacleType;
    }

    public ObstacleType getObstacleType() {
        return obstacleType;
    }

    public void setObstacleType(ObstacleType obstacleType) {
        this.obstacleType = obstacleType;
    }
}
