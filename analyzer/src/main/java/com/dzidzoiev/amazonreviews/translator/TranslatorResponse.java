package com.dzidzoiev.amazonreviews.translator;

public class TranslatorResponse {

    private String text;

    public TranslatorResponse() {
    }

    public String getText() {
        return text;
    }

    public TranslatorResponse setText(String text) {
        this.text = text;
        return this;
    }
}
