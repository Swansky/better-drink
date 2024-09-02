package com.jonahseguin.drink.command;


import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

import static com.jonahseguin.drink.Drink.ERROR_LABEL;

public class DrinkAuthorizer {

    private String noPermissionMessage = ERROR_LABEL + "Vous n'avez pas le droit d'executer cette commande.";

    public boolean isAuthorized(@Nonnull CommandSender sender, @Nonnull DrinkCommand command) {
        if (command.getPermission() != null && !command.getPermission().isEmpty()) {
            if (!sender.hasPermission(command.getPermission()) && !sender.isOp()) {
                sender.sendMessage(noPermissionMessage);
                return false;
            }
        }
        return true;
    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public void setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = noPermissionMessage;
    }
}
