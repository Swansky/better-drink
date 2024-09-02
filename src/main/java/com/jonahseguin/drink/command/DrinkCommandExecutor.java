package com.jonahseguin.drink.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static com.jonahseguin.drink.Drink.ERROR_LABEL;

public class DrinkCommandExecutor implements CommandExecutor {

    private final DrinkCommandService commandService;
    private final DrinkCommandContainer container;

    public DrinkCommandExecutor(DrinkCommandService commandService, DrinkCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
                            sender.sendMessage(ERROR_LABEL + "Sous commande inconnue: " + args[0] + ".  Utilisez '/" + label + " help' pour les commandes disponibles.");
                        } else {
                            if (container.isDefaultCommandIsHelp()) {
                                commandService.getHelpService().sendHelpFor(sender, container);
                            } else {
                                sender.sendMessage(ERROR_LABEL + "Merci de choisir une sous commande.  Utilisez '/" + label + " help' pour les commandes disponibles.");
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ERROR_LABEL + "Vous n'avez pas le droit d'executer cette commande.");
                }
                return true;
            } catch (Exception ex) {
                sender.sendMessage(ERROR_LABEL + "Impossible d'executer la commande.");
                ex.printStackTrace();
            }
        }
        return false;
    }
}
