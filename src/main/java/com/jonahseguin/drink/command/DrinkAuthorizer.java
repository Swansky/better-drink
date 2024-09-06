package com.jonahseguin.drink.command;


import com.jonahseguin.drink.TranslateUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class DrinkAuthorizer {


    public boolean isAuthorized(@Nonnull CommandSender sender, @Nonnull DrinkCommand command) {
        if (command.getPermission() != null && !command.getPermission().isEmpty()) {
            if (!sender.hasPermission(command.getPermission()) && !sender.isOp()) {
                sender.sendMessage(TranslateUtils.NO_PERMISSION_MESSAGE);
                return false;
            }
        }
        return true;
    }
}
