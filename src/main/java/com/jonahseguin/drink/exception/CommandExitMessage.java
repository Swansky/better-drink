package com.jonahseguin.drink.exception;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static com.jonahseguin.drink.Drink.ERROR_LABEL;

public class CommandExitMessage extends Exception {

    public CommandExitMessage(String message) {
        super(message);
    }

    public void print(CommandSender sender) {
        sender.sendMessage(ERROR_LABEL + getMessage());
    }
}
