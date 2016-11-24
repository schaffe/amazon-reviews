package com.dzidzoiev.amazonreviews.translator;

public class TranslateMessage {
    private final String input_lang;
    private final String output_lang;
    private final String text;

    public TranslateMessage(String input_lang, String output_lang, String text) {
        this.input_lang = input_lang;
        this.output_lang = output_lang;
        this.text = text;
    }

    public String getInput_lang() {
        return input_lang;
    }

    public String getOutput_lang() {
        return output_lang;
    }

    public String getText() {
        return text;
    }
}
