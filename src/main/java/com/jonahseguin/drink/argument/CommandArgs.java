package com.jonahseguin.drink.argument;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.command.CommandFlag;
import com.jonahseguin.drink.command.DrinkCommandService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class CommandArgs {

    private final DrinkCommandService commandService;
    private final CommandSender sender;
    private final List<String> args;
    private final String label;
    private final Map<Character, CommandFlag> flags;
    private final ReentrantLock lock = new ReentrantLock();
    private int index = 0;

    public CommandArgs(@Nonnull DrinkCommandService commandService, @Nonnull CommandSender sender, @Nonnull String label, @Nonnull List<String> args,
                       @Nonnull Map<Character, CommandFlag> flags) {
        Preconditions.checkNotNull(commandService, "CommandService cannot be null");
        Preconditions.checkNotNull(sender, "CommandSender cannot be null");
        Preconditions.checkNotNull(label, "Label cannot be null");
        Preconditions.checkNotNull(args, "Command args cannot be null");
        this.commandService = commandService;
        this.sender = sender;
        this.label = label;
        this.args = new ArrayList<>(args);
        this.flags = flags;
    }

    public boolean hasNext() {
        lock.lock();
        try {
            return args.size() > index;
        }
        finally {
            lock.unlock();
        }
    }

    public String next() {
        lock.lock();
        try {
            return args.get(index++);
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isSenderPlayer() {
        return sender instanceof Player;
    }

    public Player getSenderAsPlayer() {
        return (Player) sender;
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

    public String getLabel() {
        return label;
    }

    public Map<Character, CommandFlag> getFlags() {
        return flags;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public int getIndex() {
        return index;
    }
}
