package com.ashongwe.swingy.controller;

import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.view.Renderer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private HibernateManager hibernateManager;
    private Renderer renderer;
    private Hero hero;
    private List<Villain> villains;
    private List<Obstacle> obstacles;
    private List<GameEntity> entities;
    private int mapSize;
    private int wins = 0;
    private boolean status;

    public GameEngine(HibernateManager hibernateManager, Renderer renderer, Hero hero) {
        this.hibernateManager = hibernateManager;
        this.renderer = renderer;
        this.hero = hero;
    }

    public void play() {
        entities = new ArrayList<>();
        setStatus(true);
        setMapSize();
        setVillains();
        setObstacles();
        renderer.renderPlayground(this, mapSize, null);
    }

    public void continueGame() {
        entities = new ArrayList<>();
        setStatus(true);
        setMapSize();
        List<String> gameAction = null;

        try {
            InputStream inputStream = hibernateManager.loadGame(this.hero.getId());
            if (inputStream == null) {
                System.out.println("No saved game. Starting new game.");
                play();
                return;
            }

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            entities = (List<GameEntity>) objectInputStream.readObject();
            villains = (List<Villain>) objectInputStream.readObject();
            obstacles = (List<Obstacle>) objectInputStream.readObject();
            gameAction = (List<String>) objectInputStream.readObject();

            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderer.renderPlayground(this, mapSize, gameAction);
    }

    public void clear() {
        setMapSize();
        hero.setY(mapSize / 2);
        hero.setX(mapSize / 2);
        hero.setAttack(hero.getAttack() + 10);
        hero.setHitPoints(hero.getHitPoints() + 10);
        hibernateManager.updateHero(this);
        villains.clear();
        play();
    }

    public int randomGenerator(int n) {
        return (int) (Math.random() * (n));
    }

    public void setVillains() {
        this.villains = new ArrayList<>();

        int y, x;
        int attackRange = hero.getAttack() - 30;
        for (int i = 0; i < mapSize * 2; i++) {
            y = randomGenerator(mapSize);
            x = randomGenerator(mapSize);

            while ((y == hero.getY() && x == hero.getX()) || isOccupied(y, x)) {
                y = randomGenerator(mapSize);
                x = randomGenerator(mapSize);
            }

            Villain villain = new Villain(VillainType.getRandomVillain(),
                    randomGenerator(attackRange) + 40 + hero.getLevel() * 10,
                    y, x);

            this.villains.add(villain);
            entities.add(villain);
        }
    }

    public List<Villain> getVillains() {
        return villains;
    }

    public void setObstacles() {
        this.obstacles = new ArrayList<>();

        int y, x;

        for (int i = 0; i < mapSize + hero.getLevel(); i++) {
            y = randomGenerator(mapSize);
            x = randomGenerator(mapSize);

            while ((y == hero.getY() && x == hero.getX()) || isOccupied(y, x)) {
                y = randomGenerator(mapSize);
                x = randomGenerator(mapSize);
            }

            Obstacle obstacle = new Obstacle(ObstacleType.getRandomObstacle(), y, x);
            this.obstacles.add(obstacle);
            entities.add(obstacle);
        }
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<GameEntity> getGameEntities() {
        return entities;
    }

    public boolean isOccupied(int y, int x) {
        for (GameEntity entity : entities) {
            if (y == entity.getY() && x == entity.getX()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkEntity(int y, int x) {
        for (GameEntity entity : entities) {
            if (entity.getY() == y && entity.getX() == x) {
                if (entity.getEntityType().equals(EntityType.Villain))
                    return interact((Villain)entity);
                else
                    return false;
            }
        }

        return true;
    }

    public void findArtifact() {
        int n = 0;
        Artifact artifact = null;

        while (n == 0) {
            n = randomGenerator(3);

            switch (n) {
                case 1:
                    artifact = Artifact.Helm;
                    break;
                case 2:
                    artifact = Artifact.Weapon;
                    break;
                default:
                    artifact = Artifact.Armor;
                    break;
            }

            if (hero.getArtifacts().contains(artifact))
                n = 0;
            else
                break;
        }

        hero.getArtifacts().add(artifact);
        hibernateManager.updateArtifacts(hero, artifact);
        renderer.updateArtifacts();

        String msg = null;
        int points = 10 * hero.getLevel();

        switch (artifact) {
            case Armor:
                hero.setDefense(hero.getDefense() + points);
                renderer.updateDefense(hero.getDefense());
                msg = "Defense.";
                break;
            case Helm:
                hero.setHitPoints(hero.getHitPoints() + points);
                renderer.updateHitPoints(hero.getHitPoints());
                msg = "Hit Points.";
                break;
            case Weapon:
                hero.setAttack(hero.getAttack() + points);
                renderer.updateAttack(hero.getAttack());
                msg = "Attack.";
                break;
        }

        renderer.updateGameAction("Found " + artifact + ". + " + points + " to " + msg);
    }

    public void removeEntity(GameEntity entity) {
        renderer.removeVillain(entity.getY(), entity.getX());
        entities.remove(entity);
        villains.remove(entity);
    }

    public boolean interact(Villain villain) {
        int result = renderer.chooseAction(villain);
        if (result == 1) {
            if (!fight(villain)) {
                wins = 0;
                return false;
            }
        }
        else {
            if (randomGenerator(2) == 1) {
                renderer.updateGameAction("Couldn't run from the villain.");
                renderer.showMessageDialog(3, 0);
                if (!fight(villain)){
                    wins = 0;
                    return false;
                }
            }
            else {
                renderer.updateGameAction("Escaped from the villain.");
                return false;
            }
        }
        wins++;
        return true;
    }

    public boolean fight(Villain villain) {
        boolean result = true;

        int hitPoints, experience;
        if (hero.getAttack() < villain.getAttack()) {
            hitPoints = (hero.getHitPoints() + hero.getDefense()) - villain.getAttack();

            if (hitPoints > 0) {
                hero.setHitPoints(hitPoints);
                renderer.updateHitPoints(hitPoints);
                renderer.showMessageDialog(5, villain.getAttack() - hero.getDefense());
                renderer.updateGameAction(villain.getVillainType() + " does " + (villain.getAttack() - hero.getDefense()) + " damage.");
                removeEntity(villain);
            }
            else {
                hero.setHitPoints(0);
                renderer.updateHitPoints(0);
                renderer.updateGameAction("Too much damage from " + villain.getVillainType() + ".");
                renderer.showMessageDialog(4, hero.getExperience());
                result = false;
                status = false;
            }
        } else if (hero.getAttack() == villain.getAttack()) {
            hitPoints = ((hero.getHitPoints() + hero.getDefense()) - villain.getAttack()) / 2;

            if (hitPoints > 0) {
                experience = (int) (villain.getAttack() * 1.7);
                hero.setExperience(hero.getExperience() + experience);
                hero.setHitPoints(hitPoints);
                renderer.showMessageDialog(2, experience);
                renderer.updateExperience(hero.getExperience());
                renderer.updateHitPoints(hitPoints);
                renderer.updateGameAction(villain.getVillainType() + " does " + hitPoints + " damage.");
                renderer.updateGameAction("Earned " + experience + " experience after fight with " + villain.getVillainType() + ".");
                removeEntity(villain);
            }
            else {
                hero.setHitPoints(0);
                renderer.updateHitPoints(0);
                renderer.updateGameAction("Too much damage from " + villain.getVillainType() + ".");
                renderer.showMessageDialog(4, hero.getExperience());
                result = false;
                status = false;
            }
        }
        else {
            experience = (int) (villain.getAttack() * 1.7);
            renderer.showMessageDialog(2, experience);
            hero.setExperience(hero.getExperience() + experience);
            renderer.updateExperience(hero.getExperience());
            removeEntity(villain);
            renderer.updateGameAction("Earned " + experience + " experience after fight with " + villain.getVillainType() + ".");

            if (hero.getArtifacts().size() < 3) {
                if (randomGenerator(100) % 11 == 0)
                    findArtifact();
            }
        }

        return result;
    }

    public boolean checkWin() {
        int level = hero.getLevel();

        if (wins == 5) {
            wins = 0;
            hero.setHitPoints(hero.getHitPoints() + 10);
            renderer.updateHitPoints(hero.getHitPoints());
            renderer.updateGameAction("Earned 10 Hit Points after good fights.");
        }

        if (hero.getExperience() >= (level * 1000 + Math.pow(level - 1, 2) * 450))
            return true;
        return false;
    }

    public void heroMoved(HeroMove move) {
        int y, x;

        y = hero.getY();
        x = hero.getX();

        switch (move) {
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        if (isOccupied(y, x)) {
            if (!checkEntity(y, x)) {
                return;
            }
        }

        if (status) {
            int oldY = hero.getY();
            int oldX = hero.getX();

            hero.setY(y);
            hero.setX(x);

            if (checkWin()) {
                hero.setLevel(hero.getLevel() + 1);
                status = false;

                if (hero.getLevel() > 5) {
                    renderer.showMessageDialog(0, hero.getExperience());
                    hero.setHitPoints(0);
                }
                else {
                    renderer.showMessageDialog(1, 0);
                    clear();
                }
            }
            else
                renderer.renderHero(oldY, oldX, y, x);
        }
    }

    public Hero getHero() {
        return hero;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize() {
        this.mapSize = (hero.getLevel() - 1) * 5 + 10 - (hero.getLevel() % 2);;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
