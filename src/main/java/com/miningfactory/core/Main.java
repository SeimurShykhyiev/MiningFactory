package com.miningfactory.core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Mining Factory - 2D Industrial Simulator");
        config.setWindowedMode(1920, 1000);
        config.setForegroundFPS(60);
        config.setResizable(true);
        new Lwjgl3Application(new Game(), config);
    }
}