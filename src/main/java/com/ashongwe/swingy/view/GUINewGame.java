package com.ashongwe.swingy.view;

import com.ashongwe.swingy.controller.GameEngine;
import com.ashongwe.swingy.controller.Helper;
import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.controller.HibernateManager;
import com.ashongwe.swingy.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUINewGame {
    private HibernateManager hibernateManager;
    private com.ashongwe.swingy.view.Renderer renderer;
    private Helper helper;
    private int attack, defense, hitPoints;
    private JFrame frame;
    private JTextField nameField;
    private JTextField attackField;
    private JTextField defenseField;
    private JTextField hitField;
    private JLabel iconLabel;
    private JComboBox heroClassBox;
    private JComboBox artifactBox;


    public void createHero(final HibernateManager hibernateManager, Renderer renderer) {
        this.hibernateManager = hibernateManager;
        this.renderer = renderer;
        helper = new Helper();

        frame = new JFrame("Create a new Hero");
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hibernateManager.tearDown();
                frame.dispose();
                System.exit(0);
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(50, 50, 100, 30);
        mainPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 50, 190, 30);
        mainPanel.add(nameField);

        JLabel heroClassLabel = new JLabel("Hero Class");
        heroClassLabel.setBounds(50, 100, 100, 30);
        mainPanel.add(heroClassLabel);

        heroClassBox = new JComboBox(HeroClass.values());
        heroClassBox.setBounds(150, 100, 190, 30);
        heroClassBox.setSelectedIndex(-1);
        heroClassBox.addActionListener(new HeroClassBoxListener());
        mainPanel.add(heroClassBox);

        JLabel artifactLabel = new JLabel("Artefact");
        artifactLabel.setBounds(50, 150, 100, 30);
        mainPanel.add(artifactLabel);

        artifactBox = new JComboBox(Artifact.values());
        artifactBox.setBounds(150, 150, 190, 30);
        artifactBox.setSelectedIndex(-1);
        artifactBox.addActionListener(new ArtefactBoxListener());
        mainPanel.add(artifactBox);

        iconLabel = new JLabel();
        mainPanel.add(iconLabel);

        JLabel attackLabel = new JLabel("Attack");
        attackLabel.setBounds(50, 200, 100, 30);
        mainPanel.add(attackLabel);

        attackField = new JTextField("0");
        attackField.setBounds(100, 200, 50, 30);
        attackField.setEnabled(false);
        mainPanel.add(attackField);

        JLabel defenseLabel = new JLabel("Defense");
        defenseLabel.setBounds(170, 200, 100, 30);
        mainPanel.add(defenseLabel);

        defenseField = new JTextField("0");
        defenseField.setBounds(230, 200, 50, 30);
        defenseField.setEnabled(false);
        mainPanel.add(defenseField);

        JLabel hitLabel = new JLabel("Hit Points");
        hitLabel.setBounds(300, 200, 100, 30);
        mainPanel.add(hitLabel);

        hitField = new JTextField("0");
        hitField.setBounds(370, 200, 50, 30);
        hitField.setEnabled(false);
        mainPanel.add(hitField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(100, 250, 100, 30);
        saveButton.addActionListener(new SaveButtonListener());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(250, 250, 100, 30);
        cancelButton.addActionListener(new CancelButtonListener());
        buttonPanel.add(cancelButton);

        frame.add(BorderLayout.CENTER, mainPanel);
        frame.add(BorderLayout.SOUTH, buttonPanel);
        frame.setBounds(50, 50,600, 400);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize(); //Set a window on center of screen
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    public void showMessage() {
        JOptionPane.showMessageDialog(null, "Validation failed.\nPlease make sure that you complete " +
                        "all fields\nand length of name no more than 15 characters.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    class SaveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean status;

            if (heroClassBox.getSelectedIndex() < 0 || artifactBox.getSelectedIndex() < 0) {
                showMessage();
                return;
            }

            HeroClass heroClass = HeroClass.valueOf(heroClassBox.getSelectedItem().toString());
            Artifact artifact = Artifact.valueOf(artifactBox.getSelectedItem().toString());
            status = hibernateManager.saveHero(
                    nameField.getText(),
                    heroClass,
                    artifact,
                    Integer.parseInt(attackField.getText()),
                    Integer.parseInt(defenseField.getText()),
                    Integer.parseInt(hitField.getText()),
                    9 / 2,
                    9 / 2);

            if (status) {
                HeroEntity heroEntity = hibernateManager.getNewHero();
                Hero hero = HeroFactory.getInstance().buildHero(heroEntity);
                List<Artifact> artifacts = new ArrayList<>();
                for (ArtifactsEntity artifactEntity : heroEntity.getArtifacts()) {
                    artifacts.add(artifactEntity.getArtifact());
                }

                hero.setArtifacts(artifacts);
                GameEngine gameEngine = new GameEngine(hibernateManager, renderer, hero);
                gameEngine.play();

                frame.dispose();
            }
            else {
                showMessage();
            }
        }
    }

    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
            renderer.renderMenu();
        }
    }

    class HeroClassBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            BufferedImage img = null;

            hitPoints = helper.getHitPoints();

            if (artifactBox.getSelectedIndex() >= 0)
                artifactBox.setSelectedIndex(-1);

            HeroClass heroClass = HeroClass.valueOf(heroClassBox.getSelectedItem().toString());
            attack = helper.getAttack(heroClass);
            defense = helper.getDefense(heroClass);

            attackField.setText(String.valueOf(attack));
            defenseField.setText(String.valueOf(defense));
            hitField.setText(String.valueOf(hitPoints));

            try {
                img = ImageIO.read(getClass().getResource("/heroes/" + heroClassBox.getSelectedItem() + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            iconLabel.setBounds(400, 40, 150, 150);
            Image dimg = img.getScaledInstance(iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            iconLabel.setIcon(imageIcon);
        }
    }

    class ArtefactBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int value;

            if (artifactBox.getSelectedIndex() >= 0 && heroClassBox.getSelectedIndex() >= 0) {
                if (artifactBox.getSelectedItem().equals(Artifact.Weapon)) {
                    value = attack + 10;
                    attackField.setText(String.valueOf(value));
                    defenseField.setText(String.valueOf(defense));
                    hitField.setText(String.valueOf(hitPoints));
                } else if (artifactBox.getSelectedItem().equals(Artifact.Armor)) {
                    value = defense + 10;
                    attackField.setText(String.valueOf(attack));
                    defenseField.setText(String.valueOf(value));
                    hitField.setText(String.valueOf(hitPoints));
                } else {
                    value = hitPoints + 10;
                    attackField.setText(String.valueOf(attack));
                    defenseField.setText(String.valueOf(defense));
                    hitField.setText(String.valueOf(value));
                }
            }
        }
    }
}
