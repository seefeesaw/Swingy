package com.ashongwe.swingy.controller;

import com.ashongwe.swingy.view.GUIRenderer;
import com.ashongwe.swingy.view.Renderer;
import com.ashongwe.swingy.view.CLIRenderer;

public class Swingy {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("usage: java -jar swingy.jar [console/gui]");
        }

        HibernateManager hibernateManager = HibernateManager.getHibernateManager();

        Renderer renderer = null;
        if (args[0].equals("gui")) {
            renderer = new GUIRenderer(hibernateManager);
        } else if (args[0].equals("console")) {
            renderer = new CLIRenderer(hibernateManager);
        }
        else {
            System.out.println("usage: java -jar swingy.jar [console/gui]");
            System.exit(1);
        }

        renderer.renderMenu();
    }
}
