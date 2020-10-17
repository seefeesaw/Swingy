package com.ashongwe.swingy.model;

import java.util.Random;

public enum VillainType {
    Centaur,
    Cyclops,
    Devil,
    Dragon,
    Genie,
    Ghost,
    Giant,
    Goblin,
    Golem,
    Monster,
    Mummy,
    Ufo,
    Vampire,
    Wolf,
    Zombie;

    public static VillainType getRandomVillain() {
        Random random = new Random();

        return values()[random.nextInt(values().length)];
    }
}
