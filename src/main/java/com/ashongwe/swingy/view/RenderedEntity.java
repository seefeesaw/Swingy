package com.ashongwe.swingy.view;

import com.ashongwe.swingy.model.GameEntity;

import javax.swing.*;
import java.awt.*;

public class RenderedEntity {
    private GameEntity entity;
    private JLabel label;
    private Image image;

    public RenderedEntity(GameEntity entity, JLabel label, Image image) {
        this.entity = entity;
        this.label = label;
        this.image = image;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public Image getImage() {
        return image;
    }

    public GameEntity getEntity() {
        return entity;
    }

    public void setVillain(GameEntity entity) {
        this.entity = entity;
    }
}
