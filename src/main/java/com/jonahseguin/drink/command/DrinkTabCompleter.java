package com.jonahseguin.drink.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class DrinkTabCompleter implements TabCompleter {

    private final DrinkCommandService commandService;
    private final DrinkCommandContainer container;

    public DrinkTabCompleter(DrinkCommandService commandService, DrinkCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(container.getName())) {
            Map.Entry<DrinkCommand, String[]> data = container.getCommand(args);
            if (data != null && data.getKey() != null) {
                String tabCompleting = "";
                int tabCompletingIndex = 0;
                if (data.getValue().length > 0) {
                    tabCompleting = data.getValue()[data.getValue().length - 1];
                    tabCompletingIndex = data.getValue().length - 1;
                }

                DrinkCommand drinkCommand = data.getKey();

                if (drinkCommand.getConsumingProviders().length > tabCompletingIndex &&
                        (sender.hasPermission(drinkCommand.getPermission()) || sender.isOp() || drinkCommand.getPermission().isEmpty())) {
                    List<String> s = drinkCommand.getConsumingProviders()[tabCompletingIndex].getSuggestions(sender, tabCompleting);
                    if (s != null) {
                        List<String> suggestions = new ArrayList<>(s);
                        // if (args.length == 0 || args.length == 1) {
                        StringBuilder tC = new StringBuilder();
                        for (String arg : args) {
                            tC.append(arg).append(" ");
                        }
                        tC.deleteCharAt(tC.length() - 1);
                        suggestions.addAll(container.getCommandSuggestions(sender, tC.toString()));
                        //}
                        return suggestions;
                    } else {
                        //if (args.length == 0 || args.length == 1) {
                        StringBuilder tC = new StringBuilder();
                        for (String arg : args) {
                            tC.append(arg).append(" ");
                        }
                        tC.deleteCharAt(tC.length() - 1);
                        return container.getCommandSuggestions(sender, tC.toString());
                        //}
                    }
                } else {
                    StringJoiner joiner = new StringJoiner(" ");
                    for (String arg : args) {
                        joiner.add(arg);
                    }
                    return container.getCommandSuggestions(sender, joiner.toString());
                }
            } else {
                //if (args.length == 0 || args.length == 1) {
                StringJoiner joiner = new StringJoiner(" ");
                for (String arg : args) {
                    joiner.add(arg);
                }

                return container.getCommandSuggestions(sender, joiner.toString());
                // }
            }
        }
        return Collections.emptyList();
    }
}
