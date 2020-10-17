package com.ashongwe.swingy.model;

import java.util.List;

public class Elf extends Hero {

    public Elf(int id, String name, HeroClass heroClass, int level, int experience, int attack, int defense, int hitPoints,
               List<Artifact> artifacts, int y, int x) {
        super(id, name, heroClass, level, experience, attack, defense, hitPoints, artifacts, y, x);
    }
}
