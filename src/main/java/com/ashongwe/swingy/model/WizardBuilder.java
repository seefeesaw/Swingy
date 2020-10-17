package com.ashongwe.swingy.model;

import java.util.List;

public class WizardBuilder implements Builder {
    private int id;
    private String name;
    private HeroClass heroClass;
    private int level;
    private int experience;
    private int attack;
    private int defense;
    private int hitPoints;
    private int y;
    private int x;
    private List<Artifact> artifacts;

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setHeroClass(HeroClass heroClass) {
        this.heroClass = heroClass;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public void setAttack(int attack) {
        this.attack = attack;
    }

    @Override
    public void setDefense(int defense) {
        this.defense = defense;
    }

    @Override
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    public Wizard getResult() {
        return new Wizard(id, name, heroClass, level, experience, attack, defense, hitPoints, artifacts, y, x);
    }

}
