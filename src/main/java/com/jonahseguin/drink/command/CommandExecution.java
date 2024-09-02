package com.jonahseguin.drink.command;

import com.jonahseguin.drink.argument.CommandArgs;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandExecution {

    private final DrinkCommandService commandService;
    private final CommandSender sender;
    private final List<String> args;
    private final CommandArgs commandArgs;
    private final DrinkCommand command;
    private boolean canExecute = true;

    public CommandExecution(DrinkCommandService commandService, CommandSender sender, List<String> args, CommandArgs commandArgs, DrinkCommand command) {
        this.commandService = commandService;
        this.sender = sender;
        this.args = args;
        this.commandArgs = commandArgs;
        this.command = command;
    }

    public void preventExecution() {
        canExecute = false;
    }

    public DrinkCommandService getCommandService() {
        return commandService;
    }

    public CommandSender getSender() {
        return sender;
    }

    public List<String> getArgs() {
        return args;
    }

    public CommandArgs getCommandArgs() {
        return commandArgs;
    }

    public DrinkCommand getCommand() {
        return command;
    }

    public boolean isCanExecute() {
        return canExecute;
    }
}
