package com.jonahseguin.drink.exception;

import com.jonahseguin.drink.TranslateUtils;
import net.kyori.adventure.text.Component;

public class CommandArgumentException extends Exception {

    private final Component displayableMessage;


    public CommandArgumentException(String key, Component... args) {
        this.displayableMessage = TranslateUtils.makeErrorMessage(key, args);
    }

    public CommandArgumentException() {
        this("error.parameter.generic_invalid");
    }


    public CommandArgumentException(Throwable cause) {
        super(cause);
        this.displayableMessage = TranslateUtils.makeErrorMessage("error.parameter.generic_invalid");
    }

    public Component getDisplayableMessage() {
        return displayableMessage;
    }
}
