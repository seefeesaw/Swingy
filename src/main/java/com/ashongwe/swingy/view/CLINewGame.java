package com.ashongwe.swingy.view;

import com.ashongwe.swingy.controller.GameEngine;
import com.ashongwe.swingy.controller.Helper;
import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.controller.HibernateManager;
import com.ashongwe.swingy.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLINewGame {
    private HibernateManager hibernateManager;
    private Renderer renderer;
    private Scanner scanner;
    private Helper helper;

    private String name;
    private HeroClass heroClass;
    private Artifact artifact;
    private int attack;
    private int defense;
    private int hitPoints;

    public void createHero(final HibernateManager hibernateManager, Renderer renderer) {
        scanner = new Scanner(System.in);
        helper = new Helper();

        this.hibernateManager = hibernateManager;
        this.renderer = renderer;

        System.out.print("\033\143");

        System.out.println(ColorType.WHITE + "* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.print("*                    ");
        System.out.print(ColorType.CYAN + "New Game");
        System.out.println(ColorType.WHITE + "                     *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println();

        System.out.println("Let's create a new hero:\n");
        System.out.print("Name: ");
        name = scanner.next();

        heroClass = chooseHeroClass();
        artifact = chooseArtifact();
        calculateValues();
        System.out.print(ColorType.RESET);
        saveHero();
    }

    public void saveHero() {
        boolean status;

        status = hibernateManager.saveHero(
                name,
                heroClass,
                artifact,
                attack,
                defense,
                hitPoints,
                9 / 2,
                9 / 2);

        if (status) {
            System.out.println("A new hero created!");

            HeroEntity heroEntity = hibernateManager.getNewHero();
            Hero hero = HeroFactory.getInstance().buildHero(heroEntity);
            List<Artifact> artifacts = new ArrayList<>();
            for (ArtifactsEntity artifactEntity : heroEntity.getArtifacts()) {
                artifacts.add(artifactEntity.getArtifact());
            }

            hero.setArtifacts(artifacts);
            GameEngine gameEngine = new GameEngine(hibernateManager, renderer, hero);
            gameEngine.play();
        }
        else {
            System.out.println("\nValidation failed. See trace above.");
            System.out.println(ColorType.WHITE + "\nPress any key to return to Main Menu.");
            System.out.print(ColorType.RESET);
            scanner.next();
            renderer.renderMenu();
        }
    }

    public HeroClass chooseHeroClass() {
        HeroClass heroClass;
        String option;

        System.out.println("\nChoose a Hero Class:");
        System.out.println("             Attack   Defence");
        System.out.println("(1) Elf      " + helper.getAttack(HeroClass.Elf) + "      " + helper.getDefense(HeroClass.Elf));
        System.out.println("(2) Dwarf    " + helper.getAttack(HeroClass.Dwarf) + "      " + helper.getDefense(HeroClass.Dwarf));
        System.out.println("(3) Wizard   " + helper.getAttack(HeroClass.Wizard) + "      " + helper.getDefense(HeroClass.Wizard));
        System.out.print("> ");

        option = scanner.next();

        switch (option) {
            case "1":
                heroClass = HeroClass.Elf;
                break;
            case "2":
                heroClass = HeroClass.Dwarf;
                break;
            case "3":
                heroClass = HeroClass.Wizard;
                break;
            default:
                heroClass = null;
                break;
        }

        return heroClass;
    }

    public Artifact chooseArtifact() {
        Artifact artifact = null;
        String option;
        boolean selected = false;

        do {
            System.out.println(ColorType.WHITE + "\nChoose an Artifact:");
            System.out.println("(1) Weapon     +10 to Attack");
            System.out.println("(2) Armor      +10 to Defence");
            System.out.println("(3) Helm       +10 to Hit Points");
            System.out.print("> ");

            option = scanner.next();

            if (option.equals("1") || option.equals("2") || option.equals("3"))
                selected = true;
            else
                System.out.println(ColorType.RESET + "\n*** Unknown option! ***");
        } while (!selected);

        switch (option) {
            case "1":
                artifact = Artifact.Weapon;
                break;
            case "2":
                artifact = Artifact.Armor;
                break;
            case "3":
                artifact = Artifact.Helm;
                break;
        }

        return artifact;
    }

    public void calculateValues() {
        attack = helper.getAttack(heroClass);
        defense = helper.getDefense(heroClass);
        hitPoints = helper.getHitPoints();

        switch (artifact) {
            case Weapon:
                attack += 10;
                break;
            case Armor:
                defense += 10;
                break;
            case Helm:
                hitPoints += 10;
                break;
        }
    }
}
