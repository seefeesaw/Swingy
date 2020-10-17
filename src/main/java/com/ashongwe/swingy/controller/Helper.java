package com.ashongwe.swingy.controller;

import com.ashongwe.swingy.model.HeroClass;
import com.ashongwe.swingy.model.*;

public class Helper {
    private int attack;
    private int defense;
    private int hitPoints = 120;

    public int getAttack(HeroClass heroClass) {
        if (heroClass == null)
            return 0;

        switch (heroClass) {
            case Elf:
                attack = 105;
                break;
            case Dwarf:
                attack = 110;
                break;
            case Wizard:
                attack = 100;
                break;
        }

        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense(HeroClass heroClass) {
        if (heroClass == null)
            return 0;

        switch (heroClass) {
            case Elf:
                defense = 50;
                break;
            case Dwarf:
                defense = 60;
                break;
            case Wizard:
                defense = 65;
                break;
        }

        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getHitPoints() {
        return hitPoints;
    }
}
