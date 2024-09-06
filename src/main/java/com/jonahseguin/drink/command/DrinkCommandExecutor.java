package com.jonahseguin.drink.command;

import com.jonahseguin.drink.TranslateUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DrinkCommandExecutor implements CommandExecutor {

    private final DrinkCommandService commandService;
    private final DrinkCommandContainer container;

    public DrinkCommandExecutor(DrinkCommandService commandService, DrinkCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase(container.getName())) {
            try {
                Map.Entry<DrinkCommand, String[]> data = container.getCommand(args);
                String permission = container.getPermission();
                if (permission == null) permission = "";
                if (sender.hasPermission(permission) || sender.isOp()) {
                    // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                    if (data != null && data.getKey() != null) {
                        if (args.length > 0) {
                            if (args[args.length - 1].equalsIgnoreCase("help")
                                    && !data.getKey().getName().equalsIgnoreCase("help")
                                    && (sender.hasPermission(permission) || sender.isOp())) {
                                // Send help if they ask for it, if they registered a custom help sub-command, allow that to override our help menu
                                commandService.getHelpService().sendHelpFor(sender, container);
                                return true;
                            }
                        }
                        commandService.executeCommand(sender, data.getKey(), label, data.getValue());
                    } else {
                        if (args.length > 0) {
                            if (args[args.length - 1].equalsIgnoreCase("help")) {
                                commandService.getHelpService().sendHelpFor(sender, container);
                                return true;
                            }

                            sender.sendMessage(TranslateUtils.makeErrorMessage("error.subcommand.unknown", Component.text(args[0]), Component.text(label)));
                        } else {
                            if (container.isDefaultCommandIsHelp()) {
                                commandService.getHelpService().sendHelpFor(sender, container);
                            } else {
                                sender.sendMessage(TranslateUtils.makeErrorMessage("error.subcommand.not_provide", Component.text(label)));
                            }
                        }
                    }
                } else {
                    sender.sendMessage(TranslateUtils.NO_PERMISSION_MESSAGE);
                }
                return true;
            } catch (Exception ex) {
                sender.sendMessage(TranslateUtils.EXCEPTION_MESSAGE);
                commandService.getPlugin().getSLF4JLogger().error("parse command", ex);
            }
        }
        return false;
    }
}
