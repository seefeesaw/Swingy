package com.ashongwe.swingy.view;

import com.ashongwe.swingy.controller.GameEngine;
import com.ashongwe.swingy.controller.HeroMove;
import com.ashongwe.swingy.model.Villain;
import com.ashongwe.swingy.controller.HibernateManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

public class GUIRenderer implements com.ashongwe.swingy.view.Renderer, KeyListener {
    private final HibernateManager hibernateManager;
    private GameEngine game;
    private JLabel heroLabel;
    private int iconSize;
    private int mapSize;
    private JFrame frame;
    private JLabel[][] iconLabels;
    private JLabel experienceLabel;
    private JLabel attackLabel;
    private JLabel defenseLabel;
    private JLabel hitLabel;
    private Image heroImage;
    private List<RenderedEntity> renderedEntities;
    private List<String> gameAction = null;
    private JLabel[] artifactsLabel;
    private JTextArea actionArea;

    public GUIRenderer(HibernateManager hibernateManager) {
        this.hibernateManager = hibernateManager;
    }

    @Override
    public void renderMenu() {
        GUIMenu menu = new GUIMenu();
        menu.showMenu(hibernateManager, this);
    }

    @Override
    public void renderPlayground(GameEngine game, int mapSize, List<String> gameAction) {
        this.game = game;
        this.mapSize = mapSize;
        renderedEntities = new ArrayList<>();

        BufferedImage bufferedHeroImage = null;
        try {
            bufferedHeroImage = ImageIO.read(getClass().getResource("/heroes/" + game.getHero().getHeroClass() + ".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        iconSize = 45;
        assert bufferedHeroImage != null;
        heroImage = bufferedHeroImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        heroLabel = new JLabel(new ImageIcon(heroImage));

        frame = new JFrame("Swingy");
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveGame();
                hibernateManager.tearDown();
                frame.dispose();
                System.exit(0);
            }
        });

        GridLayout grid = new GridLayout(mapSize, mapSize);

        JPanel mapPanel = new JPanel(grid);

        iconLabels = new JLabel[mapSize][mapSize];
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(getClass().getResource("/grass.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        assert bufferedImage != null;
        Image image = bufferedImage.getScaledInstance(iconSize, iconSize + 10, Image.SCALE_SMOOTH);
        ImageIcon mapIcon = new ImageIcon(image);

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                iconLabels[y][x] = new JLabel();
                iconLabels[y][x].setSize(iconSize, iconSize);
                iconLabels[y][x].setLayout(new BorderLayout());
                iconLabels[y][x].setIcon(mapIcon);
                mapPanel.add(iconLabels[y][x]);
            }
        }

        iconLabels[game.getHero().getY()][game.getHero().getX()].add(heroLabel);
        iconLabels[game.getHero().getY()][game.getHero().getX()].setFocusable(true);
        iconLabels[game.getHero().getY()][game.getHero().getX()].addKeyListener(this);

        renderVillains();
        renderObstacle();

        frame.add(BorderLayout.WEST, mapPanel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Hero Info"));

        Font font = new Font("Verdana", Font.PLAIN, 12);

        JLabel pictureLabel = new JLabel(new ImageIcon(heroImage));
        pictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Name: " + game.getHero().getName());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setFont(font);
        JLabel classLabel = new JLabel("Hero Class: " + game.getHero().getHeroClass());
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        classLabel.setFont(font);
        JLabel levelLabel = new JLabel("Level: " + game.getHero().getLevel());
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setFont(font);
        experienceLabel = new JLabel("Experience: " + game.getHero().getExperience());
        experienceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        experienceLabel.setFont(font);
        attackLabel = new JLabel("Attack: " + game.getHero().getAttack());
        attackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        attackLabel.setFont(font);
        defenseLabel = new JLabel("Defense: " + game.getHero().getDefense());
        defenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        defenseLabel.setFont(font);
        hitLabel = new JLabel("Hit Points: " + game.getHero().getHitPoints());
        hitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hitLabel.setFont(font);

        infoPanel.add(pictureLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(classLabel);
        infoPanel.add(levelLabel);
        infoPanel.add(experienceLabel);
        infoPanel.add(attackLabel);
        infoPanel.add(defenseLabel);
        infoPanel.add(hitLabel);

        JPanel artifactPanel = new JPanel(new GridLayout());
        artifactPanel.setBorder(BorderFactory.createTitledBorder("Artifacts"));

        artifactsLabel = new JLabel[5];

        for (int i = 0; i < 5; i++) {
            artifactsLabel[i] = new JLabel();

            if (i >= 1 && i <= game.getHero().getArtifacts().size()) {
                try {
                    bufferedImage = ImageIO.read(getClass().getResource("/artifacts/" + game.getHero().getArtifacts().get(i - 1) + ".png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                image = bufferedImage.getScaledInstance(iconSize + 10, iconSize + 10, Image.SCALE_SMOOTH);
                artifactsLabel[i].setIcon(new ImageIcon(image));
            }
            artifactPanel.add(artifactsLabel[i]);
        }

        infoPanel.add(artifactPanel);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder("Game action"));

        actionArea = new JTextArea();
        actionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setViewportView(actionArea);

        if (gameAction == null) {
            this.gameAction = new ArrayList<>();
            this.gameAction.add("Level " + game.getHero().getLevel() + ".");
            this.gameAction.add("Let the adventure begin!");
        }
        else {
            this.gameAction = gameAction;
        }

        for (int i = 0; i < this.gameAction.size(); i++) {
            actionArea.append(this.gameAction.get(i) + "\n");
        }

        actionPanel.add(scrollPane);

        infoPanel.add(actionPanel);
        frame.add(BorderLayout.CENTER, infoPanel);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.setFocusable(false);
        saveButton.addActionListener(new SaveButtonListener());
        JButton toCLIButton = new JButton("Change view");
        toCLIButton.setFocusable(false);
        toCLIButton.addActionListener(new ToCLIButtonListener());
        JButton backButton = new JButton("Back");
        backButton.setFocusable(false);
        backButton.addActionListener(new BackButtonListener());

        buttonPanel.add(saveButton);
        buttonPanel.add(toCLIButton);
        buttonPanel.add(backButton);
        frame.add(BorderLayout.SOUTH, buttonPanel);

        frame.setBounds(50, 50, mapSize * iconSize * 2, mapSize * iconSize + 50);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize(); //Set a window on center of screen
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    @Override
    public void updateGameAction(String str) {
        gameAction.add(str);
        actionArea.append(str + "\n");
    }

    @Override
    public void updateAttack(int attack) {
        attackLabel.setText("Attack: " + attack);
    }

    @Override
    public void updateDefense(int defense) {
        defenseLabel.setText("Defense: " + defense);
    }

    @Override
    public void updateHitPoints(int hitPoints) {
        hitLabel.setText("Hit Points: " + hitPoints);
    }

    @Override
    public void updateExperience(int experience) {
        experienceLabel.setText("Experience: " + experience);
    }

    @Override
    public void updateArtifacts() {
        BufferedImage bufferedImage = null;
        int i = game.getHero().getArtifacts().size();
        try {
            bufferedImage = ImageIO.read(getClass().getResource("/artifacts/" +
                    game.getHero().getArtifacts().get(i - 1) + ".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        assert bufferedImage != null;
        Image image = bufferedImage.getScaledInstance(iconSize + 10, iconSize + 10, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(image);
        artifactsLabel[i].setIcon(icon);

        JOptionPane.showMessageDialog(null, "You found artifact!\n",
                "A New Artifact", JOptionPane.PLAIN_MESSAGE, icon);
    }

    @Override
    public int chooseAction(Villain villain) {
        RenderedEntity entity = null;

        for (int i = 0; i < renderedEntities.size(); i++) {
            if (villain.getY() == renderedEntities.get(i).getEntity().getY() && villain.getX() == renderedEntities.get(i).getEntity().getX()) {
                entity = renderedEntities.get(i);
                break;
            }
        }

        assert entity != null;
        ImageIcon icon = new ImageIcon(entity.getImage());

        Object[] options = {"Run", "Fight"};
        int result = JOptionPane.showOptionDialog(null,
                villain.getVillainType() + "\n" + "Attack: " + villain.getAttack(),
                "Fight or run?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                icon,
                options,
                options[1]);

        return result;
    }

    @Override
    public void showMessageDialog(int flag, int val) {
        ImageIcon icon0 = new ImageIcon(getClass().getResource("/message/medal.png"));
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/message/battle.png"));
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/message/award.png"));
        ImageIcon icon3 = new ImageIcon(getClass().getResource("/message/run.png"));
        ImageIcon icon4 = new ImageIcon(getClass().getResource("/message/swords.png"));

        if (flag == 1) {
            JOptionPane.showInternalMessageDialog(null, "Level " + (game.getHero().getLevel()),
                    "Level Up", PLAIN_MESSAGE, icon1);
            renderedEntities.clear();
            frame.dispose();
        } else if (flag == 2) {
            JOptionPane.showMessageDialog(null, "You are lucky!\n+" + val + " experience.",
                    "Win a fight", JOptionPane.PLAIN_MESSAGE, icon2);
        } else if (flag == 3) {
            JOptionPane.showMessageDialog(null, "Not so fast", "Fight anyway!",
                    INFORMATION_MESSAGE, icon3);
        } else if (flag == 4) {
            JOptionPane.showMessageDialog(null, "You lose!\nYour score: " + val,
                    "GAME OVER", JOptionPane.PLAIN_MESSAGE, icon4);
        }
        else if (flag == 5) {
            JOptionPane.showMessageDialog(null, "Villain wins this fight!\nDamage: " + val,
                    "Lose a fight", JOptionPane.PLAIN_MESSAGE, icon1);
        }
        else if (flag == 0) {
            JOptionPane.showMessageDialog(null, "Congratulation! You win the game!\nYour score: " + val,
                    "WIN", JOptionPane.PLAIN_MESSAGE, icon0);

            saveGame();
        }
    }

    @Override
    public void renderHero(int oldY, int oldX, int newY, int newX) {
        iconLabels[oldY][oldX].remove(heroLabel);

        frame.revalidate();
        frame.repaint();

        iconLabels[oldY][oldX].revalidate();
        iconLabels[oldY][oldX].repaint();

        heroLabel = new JLabel(new ImageIcon(heroImage));
        iconLabels[newY][newX].add(heroLabel);
    }

    @Override
    public void renderVillains() {
        BufferedImage bufferedImage = null;
        Image image;
        JLabel label;

        for (int i = 0; i < game.getVillains().size(); i++) {

            try {
                bufferedImage = ImageIO.read(getClass().getResource("/villains/" +
                        game.getVillains().get(i).getVillainType() + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            assert bufferedImage != null;
            image = bufferedImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            label = new JLabel(new ImageIcon(image));

            iconLabels[game.getVillains().get(i).getY()][game.getVillains().get(i).getX()].add(label);

            renderedEntities.add(new RenderedEntity(game.getVillains().get(i), label, image));
        }
    }

    @Override
    public void renderObstacle() {
        BufferedImage bufferedImage = null;
        Image image;
        JLabel label;

        for (int i = 0; i < game.getObstacles().size(); i++) {

            try {
                bufferedImage = ImageIO.read(getClass().getResource("/obstacles/" +
                        game.getObstacles().get(i).getObstacleType() + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            assert bufferedImage != null;
            image = bufferedImage.getScaledInstance(iconSize - 10, iconSize - 10, Image.SCALE_SMOOTH);
            label = new JLabel(new ImageIcon(image));

            iconLabels[game.getObstacles().get(i).getY()][game.getObstacles().get(i).getX()].add(label);

            renderedEntities.add(new RenderedEntity(game.getObstacles().get(i), label, image));
        }
    }

    @Override
    public void removeVillain(int y, int x) {
        for (int i = 0; i < renderedEntities.size(); i++) {
            if (y == renderedEntities.get(i).getEntity().getY() && x == renderedEntities.get(i).getEntity().getX()) {
                iconLabels[y][x].remove(renderedEntities.get(i).getLabel());
                renderedEntities.remove(i);
                break;
            }
        }

        frame.revalidate();
        frame.repaint();

        iconLabels[y][x].revalidate();
        iconLabels[y][x].repaint();
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
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (game.isStatus()) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN && game.getHero().getY() < mapSize - 1) {
                game.heroMoved(HeroMove.DOWN);
            } else if (e.getKeyCode() == KeyEvent.VK_UP && game.getHero().getY() != 0) {
                game.heroMoved(HeroMove.UP);
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && game.getHero().getX() != 0) {
                game.heroMoved(HeroMove.LEFT);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && game.getHero().getX() < mapSize - 1) {
                game.heroMoved(HeroMove.RIGHT);
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && game.isStatus()) {
                saveGame();
                renderedEntities.clear();
                frame.dispose();
                com.ashongwe.swingy.view.Renderer renderer = new CLIRenderer(hibernateManager);
                game = new GameEngine(hibernateManager, renderer, game.getHero());
                game.continueGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class SaveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveGame();
        }
    }

    class ToCLIButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (game.isStatus()) {
                saveGame();
                renderedEntities.clear();
                frame.dispose();
                Renderer renderer = new CLIRenderer(hibernateManager);
                game = new GameEngine(hibernateManager, renderer, game.getHero());
                game.continueGame();
            }
        }
    }

    class BackButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveGame();
            frame.dispose();
            renderMenu();
        }
    }
}
