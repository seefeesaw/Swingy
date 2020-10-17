package com.ashongwe.swingy.view;

import com.ashongwe.swingy.controller.GameEngine;
import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.controller.HibernateManager;
import com.ashongwe.swingy.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLIContinue {
    private HibernateManager hibernateManager;
    private Renderer renderer;
    private Scanner scanner;
    private List<HeroEntity> heroEntities;

    public void uploadHeroList(final HibernateManager hibernateManager, Renderer renderer) {
        this.hibernateManager = hibernateManager;
        this.renderer = renderer;
        heroEntities = hibernateManager.getListHeroes();

        scanner = new Scanner(System.in);

        System.out.print("\033\143");

        System.out.println(ColorType.WHITE + "* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.print("*                  ");
        System.out.print(ColorType.CYAN + "Continue Game");
        System.out.println(ColorType.WHITE + "                  *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println(ColorType.RESET);

        if (heroEntities.size() == 0) {
            showMessage();
            System.out.println(ColorType.WHITE + "\nPress any key to return to Main Menu.");

            scanner.next();
            renderer.renderMenu();
        }
        else
            showHeroList();
    }

    public void showMessage() {
        System.out.println("\nNo saved games found!");
    }

    public void showHeroList() {
        boolean selected = false;
        String option;
        int id = -1;

        do {
            System.out.println(ColorType.WHITE + "Choose a hero: ");
            System.out.print(ColorType.RESET);

            for (int i = 0; i < heroEntities.size(); i++) {
                System.out.println("(" + i + ") " + heroEntities.get(i).getName() +
                        " (Class: " + heroEntities.get(i).getHeroClass() +
                        ", Level: " + heroEntities.get(i).getLevel() +
                        ", Experience: " + heroEntities.get(i).getExperience() + ")");
            }

            System.out.println(ColorType.WHITE + "\nEnter number of hero or: (B) to Main menu, (G) to GUI view, (X) to Exit\n");
            System.out.print("> ");

            option = scanner.next();
            System.out.print(ColorType.RESET);

            if (option.toLowerCase().equals("b") || option.toLowerCase().equals("g") || option.toLowerCase().equals("x")) {
                chooseDefaultOption(option);
                break;
            }

            try {
                id = Integer.parseInt(option);
            } catch (NumberFormatException e) {
                System.out.println("\n*** Unknown option! ***\n");
                continue;
            }

            if (id >=0 && id < heroEntities.size())
                selected = true;
            else
                System.out.println("\n*** Unknown option! ***\n");
        } while (!selected);

        if (!option.equals("g"))
            startGame(heroEntities.get(id));
    }

    public void startGame(HeroEntity heroEntity) {

        Hero hero = HeroFactory.getInstance().buildHero(heroEntity);

        List<Artifact> artifacts = new ArrayList<>();
        for (ArtifactsEntity artifactEntity : heroEntity.getArtifacts()) {
            artifacts.add(artifactEntity.getArtifact());
        }

        hero.setArtifacts(artifacts);
        GameEngine gameEngine = new GameEngine(hibernateManager, renderer, hero);
        gameEngine.continueGame();
    }

    public void chooseDefaultOption(String option) {
        switch (option) {
            case "b":
                renderer.renderMenu();
                break;
            case "g":
                System.out.print("\033\143");
                renderer = new GUIRenderer(hibernateManager);
                GUIContinue guiContinue = new GUIContinue();
                guiContinue.uploadHeroList(hibernateManager, renderer);
                break;
            case "x":
                hibernateManager.tearDown();
                scanner.close();
                System.exit(0);
                break;
        }
    }
}
