package com.qm.study.DesignPatterns.creator.abstractFactory;

public class WindowsButton implements Button {

    @Override
    public void paint() {
        System.out.println("You have created WindowsButton.");
    }
}