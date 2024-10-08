package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DrinkCommandContainer extends Command implements PluginIdentifiableCommand {

    private final DrinkCommandService commandService;
    private final Object object;
    private final String name;
    private final Set<String> aliases;
    private final Map<String, DrinkCommand> commands;
    private final DrinkCommand defaultCommand;
    private final DrinkCommandExecutor executor;
    private final DrinkTabCompleter tabCompleter;
    private boolean overrideExistingCommands = true;
    private boolean defaultCommandIsHelp = false;

    public DrinkCommandContainer(DrinkCommandService commandService, Object object, String name, Set<String> aliases, Map<String, DrinkCommand> commands) {
        super(name, "", "/" + name, new ArrayList<>(aliases));
        this.commandService = commandService;
        this.object = object;
        this.name = name;
        this.aliases = aliases;
        this.commands = commands;
        this.defaultCommand = calculateDefaultCommand();
        this.executor = new DrinkCommandExecutor(commandService, this);
        this.tabCompleter = new DrinkTabCompleter(commandService, this);
        if (defaultCommand != null) {
            setUsage("/" + name + " " + defaultCommand.getGeneratedUsage());
            setDescription(defaultCommand.getDescription());
            setPermission(defaultCommand.getPermission());
        }
    }

    public final DrinkCommandContainer registerSub(@Nonnull Object handler) {
        return commandService.registerSub(this, handler);
    }

    public List<String> getCommandSuggestions(CommandSender sender, @Nonnull String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix cannot be null");
        final String p = prefix.toLowerCase();
        Set<String> suggestions = new HashSet<>();
        for (DrinkCommand c : commands.values()) {
            for (String alias : c.getAllAliases()) {
                if (!alias.toLowerCase().startsWith(p)) {
                    continue;
                }
                if (alias.equalsIgnoreCase(prefix)) {
                    return new ArrayList<>(suggestions);
                }
                String[] aliasSplit = alias.split(" ");
                if (p.isBlank()) {
                    suggestions.add(aliasSplit[0]);
                    break;
                }
                String[] prefixSplit = p.split(" ");
                if (prefixSplit.length > 1 && p.endsWith(" ")) {
                    prefixSplit = new String[prefixSplit.length + 1];
                    System.arraycopy(p.split(" "), 0, prefixSplit, 0, prefixSplit.length - 1);
                    prefixSplit[prefixSplit.length - 1] = "";
                }
                if (prefixSplit.length > aliasSplit.length) {
                    return new ArrayList<>(suggestions);
                }
                boolean match = true;
                if (prefixSplit.length > 1) {
                    for (int i = 0; i < prefixSplit.length - 1; i++) {
                        if (!aliasSplit[i].equalsIgnoreCase(prefixSplit[i])) {
                            match = false;
                        }
                    }
                    if (match) {
                        if (aliasSplit[prefixSplit.length - 1].startsWith(prefixSplit[prefixSplit.length - 1])) {
                            suggestions.add(aliasSplit[prefixSplit.length - 1]);
                        }
                    }
                } else {
                    if (p.endsWith(" ")) {
                        if (prefixSplit[0].equalsIgnoreCase(aliasSplit[0]) && (aliasSplit.length > 1)) {
                            suggestions.add(aliasSplit[1]);

                        }
                    } else if (aliasSplit[0].startsWith(prefixSplit[0])) {
                        suggestions.add(aliasSplit[0]);
                    }
                }
            }
        }
        return new ArrayList<>(suggestions);
    }

    private DrinkCommand calculateDefaultCommand() {
        for (DrinkCommand dc : commands.values()) {
            if (dc.getName().isEmpty() || dc.getName().equals(DrinkCommandService.DEFAULT_KEY)) {
                // assume default!
                return dc;
            }
        }
        return null;
    }

    @Nullable
    public DrinkCommand get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(commandService.getCommandKey(name));
    }

    @Nullable
    public DrinkCommand getByKeyOrAlias(@Nonnull String key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        if (commands.containsKey(key)) {
            return commands.get(key);
        }
        for (DrinkCommand drinkCommand : commands.values()) {
            if (drinkCommand.getAliases().contains(key)) {
                return drinkCommand;
            }
        }
        return null;
    }

    /**
     * Gets a sub-command based on given arguments and also returns the new actual argument values
     * based on the arguments that were consumed for the sub-command key
     *
     * @param args the original arguments passed in
     * @return the DrinkCommand (if present, Nullable) and the new argument array
     */
    @Nullable
    public Map.Entry<DrinkCommand, String[]> getCommand(String[] args) {
        for (int i = (args.length - 1); i >= 0; i--) {
            String key = commandService.getCommandKey(StringUtils.join(Arrays.asList(Arrays.copyOfRange(args, 0, i + 1)), ' '));
            DrinkCommand drinkCommand = getByKeyOrAlias(key);
            if (drinkCommand != null) {
                return new AbstractMap.SimpleEntry<>(drinkCommand, Arrays.copyOfRange(args, i + 1, args.length));
            }
        }
        return new AbstractMap.SimpleEntry<>(getDefaultCommand(), args);
    }

    @Nullable
    public DrinkCommand getDefaultCommand() {
        return defaultCommand;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, String s, String[] strings) {
        return executor.onCommand(commandSender, this, s, strings);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        return Objects.requireNonNull(tabCompleter.onTabComplete(sender, this, alias, args));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args, Location location) throws IllegalArgumentException {
        return Objects.requireNonNull(tabCompleter.onTabComplete(sender, this, alias, args));
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return commandService.getPlugin();
    }

    public DrinkCommandService getCommandService() {
        return commandService;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    public Set<String> getDrinkAliases() {
        return aliases;
    }

    public Map<String, DrinkCommand> getCommands() {
        return commands;
    }

    public DrinkCommandExecutor getExecutor() {
        return executor;
    }

    public DrinkTabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public boolean isOverrideExistingCommands() {
        return overrideExistingCommands;
    }

    public DrinkCommandContainer setOverrideExistingCommands(boolean overrideExistingCommands) {
        this.overrideExistingCommands = overrideExistingCommands;
        return this;
    }

    public boolean isDefaultCommandIsHelp() {
        return defaultCommandIsHelp;
    }

    public DrinkCommandContainer setDefaultCommandIsHelp(boolean defaultCommandIsHelp) {
        this.defaultCommandIsHelp = defaultCommandIsHelp;
        return this;
    }
}
