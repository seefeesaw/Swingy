package com.ashongwe.swingy.model;

public class Villain extends GameEntity {
    private int attack;
    private VillainType villainType;

    public Villain(VillainType villainType, int attack, int y, int x) {
        super(EntityType.Villain, y, x);
        this.attack = attack;
        this.villainType = villainType;
    }

    public VillainType getVillainType() {
        return villainType;
    }

    public void setVillainType(VillainType villainType) {
        this.villainType = villainType;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
