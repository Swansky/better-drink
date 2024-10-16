package com.jonahseguin.drink.exception;

import com.jonahseguin.drink.TranslateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;


public class CommandExitMessage extends Exception {


    private final Component displayableMessage;

    public CommandExitMessage(TranslatableComponent message) {
        this.displayableMessage = message;
    }

    public CommandExitMessage(String message) {
        this.displayableMessage = Component.text(message);
    }

    public CommandExitMessage(Component component) {
        this.displayableMessage = component;
    }

    public void print(CommandSender sender) {
        sender.sendMessage(TranslateUtils.makeErrorMessage(displayableMessage));
    }
}
