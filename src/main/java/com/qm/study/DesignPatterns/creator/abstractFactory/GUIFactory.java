package com.qm.study.DesignPatterns.creator.abstractFactory;

public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}