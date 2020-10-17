package com.ashongwe.swingy.view;

import com.ashongwe.swingy.controller.GameEngine;
import com.ashongwe.swingy.controller.HeroMove;
import com.ashongwe.swingy.model.Obstacle;
import com.ashongwe.swingy.model.Villain;
import com.ashongwe.swingy.controller.HibernateManager;
import com.ashongwe.swingy.model.HeroEntity;

import java.util.*;

public class CLIRenderer implements Renderer {
    private final HibernateManager hibernateManager;
    private GameEngine game;
    private Scanner scanner;
    private String[][] map;
    private Map<String, ColorType> renderedEntities;
    private List<String> gameAction;
    private int mapSize;
    private boolean isRunning = false;

    public CLIRenderer(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    @Override
    public void renderMenu() {
        scanner = new Scanner(System.in);
        String option;
        boolean selected = false;

        System.out.print("\033\143");

        System.out.println(ColorType.WHITE + "* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.print("*                ");
        System.out.print(ColorType.CYAN + "Welcome to Swingy");
        System.out.println(ColorType.WHITE + "                *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println();

        do {
            System.out.println(ColorType.WHITE + "Choose an option:");
            System.out.println("(1) New Game");
            System.out.println("(2) Continue");
            System.out.println("(3) Leaderboard");
            System.out.println("(4) Exit\n");
            System.out.print("> ");

            option = scanner.next();

            if (option.equals("1") || option.equals("2") || option.equals("3") || option.equals("4"))
                selected = true;
            else
                System.out.println(ColorType.RESET + "\n*** Unknown option! ***\n");
        } while (!selected);

        System.out.print(ColorType.RESET);

        switch (option) {
            case "1" :
                CLINewGame newGame = new CLINewGame();
                newGame.createHero(hibernateManager, this);
                break;
            case "2" :
                CLIContinue cContinue = new CLIContinue();
                cContinue.uploadHeroList(hibernateManager, this);
                break;
            case "3" :
                showLeaderboard();
                break;
            case "4" :
                hibernateManager.tearDown();
                scanner.close();
                System.exit(0);
                break;
        }
    }

    public void showLeaderboard() {
        System.out.print("\033\143");
        System.out.println(ColorType.WHITE + "* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.print("*                   ");
        System.out.print(ColorType.CYAN + "Leaderboard");
        System.out.println(ColorType.WHITE + "                   *");
        System.out.println("*                                                 *");
        System.out.println("*                                                 *");
        System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * *");
        System.out.println(ColorType.RESET);

        List<HeroEntity> heroEntities = hibernateManager.getLeaderboard();


        if (heroEntities.size() == 0) {
            System.out.println("Leaderboard is empty!");
        }
        else {
            Collections.sort(heroEntities, new Comparator<HeroEntity>() {
                @Override
                public int compare(HeroEntity o1, HeroEntity o2) {
                    return Integer.compare(o2.getExperience(), o1.getExperience());
                }
            });

            for (int i = 0; i < heroEntities.size(); i++) {
                System.out.println(String.format("%d. %s (Hero Class: %s, Level: %d, Experience: %d)", +
                        i + 1, heroEntities.get(i).getName(), heroEntities.get(i).getHeroClass().toString(), +
                        heroEntities.get(i).getLevel(), heroEntities.get(i).getExperience()));
            }
        }

        System.out.println(ColorType.WHITE + "\nPress any key to return to Main Menu.");
        scanner.next();

        System.out.print(ColorType.RESET);

        renderMenu();
    }

    @Override
    public void renderPlayground(GameEngine game, int mapSize, List<String> gameAction) {
        this.game = game;
        this.mapSize = mapSize;

        if (scanner == null)
            scanner = new Scanner(System.in);

        if (gameAction == null) {
            this.gameAction = new ArrayList<>();
            this.gameAction.add("Level " + game.getHero().getLevel() + ".");
            this.gameAction.add("Let the adventure begin!");
        }
        else
            this.gameAction = gameAction;

        System.out.print("\033\143");
        map = new String[mapSize][mapSize];

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                map[y][x] = "[ ]";
            }
        }

        renderedEntities = new HashMap<>();
        renderedEntities.put("[ ]", ColorType.GREEN);
        renderedEntities.put("[H]", ColorType.YELLOW);
        renderedEntities.put("[V]", ColorType.RED);
        renderedEntities.put("[*]", ColorType.BLUE);

        map[game.getHero().getY()][game.getHero().getX()] = "[H]";
        renderVillains();
        renderObstacle();

        if (!isRunning)
            gameLoop();
    }

    public void renderMap() {
        System.out.print("\033\143");
        System.out.println();
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                System.out.print(renderedEntities.get(map[y][x]) + map[y][x]);

                if (y <= 7 && x == mapSize - 1)
                    renderInfo(y);
            }

            System.out.println();
        }

        System.out.println();

        System.out.println(renderedEntities.get("[H]") + "[H]" + ColorType.RESET + " Hero, " +
                renderedEntities.get("[V]") + "[V]" + ColorType.RESET + " Villain, " +
                renderedEntities.get("[*]") + "[*]" + ColorType.RESET + " Obstacle");

        System.out.println(ColorType.WHITE + "\n(C) to Save, (B) to Main Menu, (G) to GUI view, (X) to Exit");
        System.out.println(ColorType.RESET);

        renderHistory();
    }

    public void renderInfo(int y) {
        switch (y) {
            case 0:
                System.out.print(ColorType.RESET + "       Name: " + game.getHero().getName());
                break;
            case 1:
                System.out.print(ColorType.RESET + "       Hero Class: " + game.getHero().getHeroClass());
                break;
            case 2:
                System.out.print(ColorType.RESET + "       Level: " + game.getHero().getLevel());
                break;
            case 3:
                System.out.print(ColorType.RESET + "       Experience: " + game.getHero().getExperience());
                break;
            case 4:
                System.out.print(ColorType.RESET + "       Attack: " + game.getHero().getAttack());
                break;
            case 5:
                System.out.print(ColorType.RESET + "       Defense: " + game.getHero().getDefense());
                break;
            case 6:
                System.out.print(ColorType.RESET + "       Hit Points: " + game.getHero().getHitPoints());
                break;
            case 7:
                System.out.print(ColorType.RESET + "       Artifacts: ");

                for (int i = 0; i < game.getHero().getArtifacts().size(); i++) {
                    System.out.print(game.getHero().getArtifacts().get(i));
                        if (i < game.getHero().getArtifacts().size() - 1)
                            System.out.print(", ");
                }
                break;
        }
    }

    public void renderHistory() {
        System.out.println("Game action:");
        int size;

        if (gameAction.size() > 5)
            size = gameAction.size() - 5;
        else
            size = 0;

        for (int i = size; i < gameAction.size(); i++) {
            System.out.println("   " + gameAction.get(i));
        }
    }

    public boolean gameLoop() {
        String option = null;

        isRunning = true;
        while (game.isStatus()) {
            renderMap();

            boolean selected = false;

            do {
                System.out.println(ColorType.WHITE + "\nChoose a direction:");
                System.out.println("(N) North");
                System.out.println("(E) East");
                System.out.println("(S) South");
                System.out.println("(W) West");
                System.out.print("> ");

                option = scanner.next();

                if (option.toLowerCase().equals("n") ||
                        option.toLowerCase().equals("e") ||
                        option.toLowerCase().equals("s") ||
                        option.toLowerCase().equals("w") ||
                        option.toLowerCase().equals("c") ||
                        option.toLowerCase().equals("b") ||
                        option.toLowerCase().equals("g") ||
                        option.toLowerCase().equals("x")
                )
                    selected = true;
                else
                    System.out.println(ColorType.RESET + "*** Unknown option! ***");
            } while (!selected);

            switch (option) {
                case "n":
                    if (game.getHero().getY() != 0)
                        game.heroMoved(HeroMove.UP);
                    break;
                case "e":
                    if (game.getHero().getX() < mapSize - 1)
                        game.heroMoved(HeroMove.RIGHT);
                    break;
                case "s":
                    if (game.getHero().getY() < mapSize - 1)
                        game.heroMoved(HeroMove.DOWN);
                    break;
                case "w":
                    if (game.getHero().getX() != 0)
                        game.heroMoved(HeroMove.LEFT);
                    break;
                case "c":
                    saveGame();
                    break;
                case "b":
                    saveGame();
                    game.setStatus(false);
                    renderMenu();
                    break;
                case "x":
                    saveGame();
                    game.setStatus(false);
                    break;
            }

            if (option.equals("g")) {
                break;
            }

            System.out.println(ColorType.RESET);
        }

        saveGame();

        assert option != null;
        if (option.equals("g")) {
            return changeView();
        }
        else {
            hibernateManager.tearDown();
            scanner.close();
            System.exit(0);
        }
        return true;
    }

    public boolean changeView() {
        isRunning = false;
        System.out.print(ColorType.RESET);
        System.out.print("\033\143");
        Renderer renderer = new GUIRenderer(hibernateManager);
        game = new GameEngine(hibernateManager, renderer, game.getHero());
        game.continueGame();
        return true;
    }

    @Override
    public void saveGame() {
        hibernateManager.updateHero(game);

        try {
            hibernateManager.saveGame(game, gameAction);
            updateGameAction("Game saved.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateGameAction(String str) {
        gameAction.add(str);
    }

    @Override
    public void updateAttack(int attack) {

    }

    @Override
    public void updateDefense(int defense) {

    }

    @Override
    public void updateHitPoints(int hitPoints) {

    }

    @Override
    public void updateExperience(int experience) {

    }

    @Override
    public void updateArtifacts() {

    }

    @Override
    public int chooseAction(Villain villain) {
        boolean selected = false;
        String option;

        System.out.println("\nYou meet with " + villain.getVillainType() +
                " (Attack: " + villain.getAttack() + "). Fight or Run?");

        do {
            System.out.println(ColorType.WHITE + "(1) Fight");
            System.out.println("(2) Run");
            System.out.print("> ");

            option = scanner.next();

            if (option.equals("1") || option.equals("2"))
                selected = true;
            else
                System.out.println(ColorType.RESET + "\n*** Unknown option! ***\n");
        } while (!selected);

        if ("1".equals(option)) {
            return 1;
        }
        return 0;
    }

    @Override
    public void showMessageDialog(int flag, int val) {
        if (flag == 1) {
            gameAction.add("Level " + game.getHero().getLevel());
        } else if (flag == 4) {
            gameAction.add("You lose! Your score: " + val);
            gameAction.add(ColorType.WHITE + "\nGAME OVER!");
            System.out.println(ColorType.RESET);
            renderMap();
        }
        else if (flag == 5) {
            gameAction.add("Villain wins this fight!");
        }
        else if (flag == 0) {
            saveGame();
            gameAction.add(ColorType.WHITE + "\nCongratulation! You win the game! Your score: " + val);
            hibernateManager.tearDown();
            scanner.close();
            System.exit(0);
        }
    }

    @Override
    public void renderHero(int oldY, int oldX, int newY, int newX) {
        map[oldY][oldX] = "[ ]";
        map[newY][newX] = "[H]";
    }

    @Override
    public void renderVillains() {
        for (Villain villain : game.getVillains()) {
            map[villain.getY()][villain.getX()] = "[V]";
        }
    }

    @Override
    public void renderObstacle() {
        for (Obstacle obstacle : game.getObstacles()) {
            map[obstacle.getY()][obstacle.getX()] = "[*]";
        }
    }

    @Override
    public void removeVillain(int y, int x) {

    }
}
