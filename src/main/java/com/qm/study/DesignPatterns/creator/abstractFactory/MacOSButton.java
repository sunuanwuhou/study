package com.qm.study.DesignPatterns.creator.abstractFactory;

public class MacOSButton implements Button {

    @Override
    public void paint() {
        System.out.println("You have created MacOSButton.");
    }
}