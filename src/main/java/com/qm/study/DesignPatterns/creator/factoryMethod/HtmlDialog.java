package com.qm.study.DesignPatterns.creator.factoryMethod;

public class HtmlDialog extends Dialog {

    @Override
    public Button createButton() {
        return new HtmlButton();
    }
}