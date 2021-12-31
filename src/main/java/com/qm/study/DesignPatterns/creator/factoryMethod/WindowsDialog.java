package com.qm.study.DesignPatterns.creator.factoryMethod;

public class WindowsDialog extends Dialog {

    @Override
    public Button createButton() {
        return new WindowsButton();
    }
}