package com.ashongwe.swingy.view;

import com.ashongwe.swingy.controller.GameEngine;
import com.ashongwe.swingy.model.*;
import com.ashongwe.swingy.controller.HibernateManager;
import com.ashongwe.swingy.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GUIContinue {
    private HibernateManager hibernateManager;
    private com.ashongwe.swingy.view.Renderer renderer;
    private JFrame frame;
    private String[] columnName = {"Name", "Hero Class", "Level", "Experience"};
    private JTable table;
    private JButton continueButton;
    private JButton cancelButton;
    private List<HeroEntity> heroEntities;

    public void uploadHeroList(final HibernateManager hibernateManager, com.ashongwe.swingy.view.Renderer renderer) {
        this.hibernateManager = hibernateManager;
        this.renderer = renderer;
        heroEntities = hibernateManager.getListHeroes();

        if (heroEntities.size() == 0) {
            showMessage();
            renderer.renderMenu();
            return;
        }

        frame = new JFrame("Load the Game");
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hibernateManager.tearDown();
                frame.dispose();
                System.exit(0);
            }
        });


        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.setColumnIdentifiers(columnName);
        table = new JTable();
        table.setModel(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    startGame();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        for (int i = 0; i < heroEntities.size(); i++) {
            model.addRow(new Object[]{heroEntities.get(i).getName(), heroEntities.get(i).getHeroClass(),
                    heroEntities.get(i).getLevel(), heroEntities.get(i).getExperience()});
        }

        frame.add(BorderLayout.CENTER, scrollPane);

        JPanel buttonPanel = new JPanel();

        continueButton = new JButton("Continue");
        continueButton.addActionListener(new ContinueButtonListener());
        buttonPanel.add(continueButton);

        JButton toCLIButton = new JButton("Change view");
        toCLIButton.addActionListener(new ToCLIButtonListener());
        buttonPanel.add(toCLIButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelButtonListener());
        buttonPanel.add(cancelButton);

        frame.add(BorderLayout.SOUTH, buttonPanel);

        frame.setBounds(50, 50,600, 400);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize(); //Set a window on center of screen
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    public void startGame() {
        HeroEntity heroEntity = heroEntities.get(table.getSelectedRow());
        Hero hero = HeroFactory.getInstance().buildHero(heroEntity);

        List<Artifact> artifacts = new ArrayList<>();
        for (ArtifactsEntity artifactEntity : heroEntity.getArtifacts()) {
            artifacts.add(artifactEntity.getArtifact());
        }

        hero.setArtifacts(artifacts);

        GameEngine gameEngine = new GameEngine(hibernateManager, renderer, hero);
        gameEngine.continueGame();
        frame.dispose();
    }

    public void showMessage() {
        JOptionPane.showMessageDialog(null, "No saved games found!",
                "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    class ContinueButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (table.getSelectedRow() >= 0) {
                startGame();
            } else {
                JOptionPane.showMessageDialog(null, "Please choose a hero",
                        "Choose a hero", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class ToCLIButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
            Renderer renderer = new CLIRenderer(hibernateManager);
            CLIContinue cliContinue = new CLIContinue();
            cliContinue.uploadHeroList(hibernateManager, renderer);
        }
    }

    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
            renderer.renderMenu();
        }
    }
}
