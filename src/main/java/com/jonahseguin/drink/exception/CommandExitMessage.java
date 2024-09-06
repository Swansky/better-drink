package com.jonahseguin.drink.exception;

import com.jonahseguin.drink.TranslateUtils;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;


public class CommandExitMessage extends Exception {


    private final TranslatableComponent displayableMessage;

    public CommandExitMessage(TranslatableComponent message) {
        this.displayableMessage = message;
    }

    public void print(CommandSender sender) {
        sender.sendMessage(TranslateUtils.makeErrorMessage(displayableMessage));
    }
}
