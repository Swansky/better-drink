package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.annotation.*;
import com.jonahseguin.drink.argument.ArgumentParser;
import com.jonahseguin.drink.argument.CommandArgs;
import com.jonahseguin.drink.exception.*;
import com.jonahseguin.drink.modifier.DrinkModifier;
import com.jonahseguin.drink.modifier.ModifierService;
import com.jonahseguin.drink.parametric.BindingContainer;
import com.jonahseguin.drink.parametric.DrinkBinding;
import com.jonahseguin.drink.parametric.DrinkProvider;
import com.jonahseguin.drink.parametric.ProviderAssigner;
import com.jonahseguin.drink.parametric.binder.DrinkBinder;
import com.jonahseguin.drink.provider.*;
import com.jonahseguin.drink.provider.spigot.*;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jonahseguin.drink.Drink.ERROR_LABEL;

public class DrinkCommandService implements CommandService {

    public static final String DEFAULT_KEY = "DRINK_DEFAULT";

    private final JavaPlugin plugin;
    private final CommandExtractor extractor;
    private final DrinkHelpService helpService;
    private final ProviderAssigner providerAssigner;
    private final ArgumentParser argumentParser;
    private final ModifierService modifierService;
    private final DrinkSpigotRegistry spigotRegistry;
    private final FlagExtractor flagExtractor;
    private DrinkAuthorizer authorizer;

    private final ConcurrentMap<String, DrinkCommandContainer> commands = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, BindingContainer<?>> bindings = new ConcurrentHashMap<>();

    public DrinkCommandService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.extractor = new CommandExtractor(this);
        this.helpService = new DrinkHelpService(this);
        this.providerAssigner = new ProviderAssigner(this);
        this.argumentParser = new ArgumentParser(this);
        this.modifierService = new ModifierService(this);
        this.spigotRegistry = new DrinkSpigotRegistry(this);
        this.flagExtractor = new FlagExtractor(this);
        this.authorizer = new DrinkAuthorizer();

        this.bindDefaults();
    }

    private void bindDefaults() {
        bind(Boolean.class).toProvider(BooleanProvider.INSTANCE);
        bind(boolean.class).toProvider(BooleanProvider.INSTANCE);
        bind(Double.class).toProvider(DoubleProvider.INSTANCE);
        bind(double.class).toProvider(DoubleProvider.INSTANCE);
        bind(Integer.class).toProvider(IntegerProvider.INSTANCE);
        bind(int.class).toProvider(IntegerProvider.INSTANCE);
        bind(Long.class).toProvider(LongProvider.INSTANCE);
        bind(long.class).toProvider(LongProvider.INSTANCE);
        bind(String.class).toProvider(StringProvider.INSTANCE);
        bind(UUID.class).toProvider(UUIDProvider.INSTANCE);
        bind(String.class).annotatedWith(Text.class).toProvider(TextProvider.INSTANCE);
        bind(String.class).annotatedWith(EnumValues.class).toProvider(EnumValuesProvider.INSTANCE);
        bind(Date.class).toProvider(DateProvider.INSTANCE);
        bind(Date.class).annotatedWith(Time.class).toProvider(TimeProvider.INSTANCE);
        bind(Date.class).annotatedWith(Duration.class).toProvider(DurationProvider.INSTANCE);
        bind(CommandArgs.class).toProvider(CommandArgsProvider.INSTANCE);

        bind(OfflinePlayer.class).toProvider(new OfflinePlayerProvider(plugin));
        bind(CommandSender.class).annotatedWith(Sender.class).toProvider(CommandSenderProvider.INSTANCE);
        bind(ConsoleCommandSender.class).annotatedWith(Sender.class).toProvider(ConsoleCommandSenderProvider.INSTANCE);
        bind(Player.class).annotatedWith(Sender.class).toProvider(PlayerSenderProvider.INSTANCE);
        bind(Player.class).toProvider(new PlayerProvider(plugin));
        bind(Material.class).toProvider(new EnumProvider<>(Material.class));

        bind(Integer.class).annotatedWith(Positive.class).toProvider(IntegerProvider.INSTANCE);
        bind(int.class).annotatedWith(Positive.class).toProvider(IntegerProvider.INSTANCE);
    }

    @Override
    public void setAuthorizer(@Nonnull DrinkAuthorizer authorizer) {
        Preconditions.checkNotNull(authorizer, "Authorizer cannot be null");
        this.authorizer = authorizer;
    }

    @Override
    public void registerCommands() {
        commands.values().forEach(cmd -> spigotRegistry.register(cmd, cmd.isOverrideExistingCommands()));
    }

    @Override
    public DrinkCommandContainer register(@Nonnull Object handler, @Nonnull String name, @Nullable String... aliases) throws CommandRegistrationException {
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        Preconditions.checkNotNull(name, "Name cannot be null");
        Preconditions.checkState(!name.isEmpty(), "Name cannot be empty (must be > 0 characters in length)");
        Set<String> aliasesSet = new HashSet<>();
        if (aliases != null) {
            aliasesSet.addAll(Arrays.asList(aliases));
            aliasesSet.removeIf(String::isEmpty);
        }
        try {
            Map<String, DrinkCommand> extractCommands = extractor.extractCommands(handler);
            if (extractCommands.isEmpty()) {
                throw new CommandRegistrationException("There were no commands to register in the " + handler.getClass().getSimpleName() + " class (" + extractCommands.size() + ")");
            }
            DrinkCommandContainer container = new DrinkCommandContainer(this, handler, name, aliasesSet, extractCommands);
            commands.put(getCommandKey(name), container);
            return container;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register command '" + name + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public DrinkCommandContainer registerSub(@Nonnull DrinkCommandContainer root, @Nonnull Object handler) {
        Preconditions.checkNotNull(root, "Root command container cannot be null");
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        try {
            Map<String, DrinkCommand> extractCommands = extractor.extractCommands(handler);
            extractCommands.forEach((s, d) -> root.getCommands().put(s, d));
            return root;
        } catch (MissingProviderException | CommandStructureException ex) {
            throw new CommandRegistrationException("Could not register sub-command in root '" + root + "' with handler '" + handler.getClass().getSimpleName() + "': " + ex.getMessage(), ex);
        }
    }

    @Override
    public <T> void registerModifier(@Nonnull Class<? extends Annotation> annotation, @Nonnull Class<T> type, @Nonnull DrinkModifier<T> modifier) {
        modifierService.registerModifier(annotation, type, modifier);
    }

    void executeCommand(@Nonnull CommandSender sender, @Nonnull DrinkCommand command, @Nonnull String label, @Nonnull String[] args) {
        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(command, "Command cannot be null");
        Preconditions.checkNotNull(label, "Label cannot be null");
        Preconditions.checkNotNull(args, "Args cannot be null");
        if (authorizer.isAuthorized(sender, command)) {
            if (command.isRequiresAsync()) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> finishExecution(sender, command, label, args));
            } else {
                finishExecution(sender, command, label, args);
            }
        }
    }

    private void finishExecution(@Nonnull CommandSender sender, @Nonnull DrinkCommand command, @Nonnull String label, @Nonnull String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        try {
            argList = argumentParser.combineMultiWordArguments(argList);
            Map<Character, CommandFlag> flags = flagExtractor.extractFlags(argList);
            final CommandArgs commandArgs = new CommandArgs(this, sender, label, argList, flags);
            CommandExecution execution = new CommandExecution(this, sender, argList, commandArgs, command);
            Object[] parsedArguments = argumentParser.parseArguments(execution, command, commandArgs);
            if (!execution.isCanExecute()) {
                return;
            }
            try {
                command.getMethod().invoke(command.getHandler(), parsedArguments);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                sender.sendMessage(ERROR_LABEL + "Impossible d'executer la commande. Merci de contacter un administrateur.");
                throw new DrinkException("Failed to execute command '" + command.getName() + "' with arguments '" + StringUtils.join(Arrays.asList(args), ' ') + " for sender " + sender.getName(), ex);
            }
        } catch (CommandExitMessage ex) {
            ex.print(sender);
        } catch (CommandArgumentException ex) {
            sender.sendMessage(ERROR_LABEL + ex.getMessage());
            helpService.sendUsageMessage(sender, getContainerFor(command), command);
        }
    }

    @Nullable
    public DrinkCommandContainer getContainerFor(@Nonnull DrinkCommand command) {
        Preconditions.checkNotNull(command, "DrinkCommand cannot be null");
        for (DrinkCommandContainer container : commands.values()) {
            if (container.getCommands().containsValue(command)) {
                return container;
            }
        }
        return null;
    }

    @Nullable
    public <T> BindingContainer<T> getBindingsFor(@Nonnull Class<T> type) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        if (bindings.containsKey(type)) {
            return (BindingContainer<T>) bindings.get(type);
        }
        return null;
    }

    @Nullable
    @Override
    public DrinkCommandContainer get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(getCommandKey(name));
    }

    public String getCommandKey(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        if (name.isEmpty()) {
            return DEFAULT_KEY;
        }
        return name.toLowerCase();
    }

    @Override
    public <T> DrinkBinder<T> bind(@Nonnull Class<T> type) {
        Preconditions.checkNotNull(type, "Type cannot be null for bind");
        return new DrinkBinder<>(this, type);
    }

    public <T> void bindProvider(@Nonnull Class<T> type, @Nonnull Set<Class<? extends Annotation>> annotations, @Nonnull DrinkProvider<T> provider) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(annotations, "Annotations cannot be null");
        Preconditions.checkNotNull(provider, "Provider cannot be null");
        BindingContainer<T> container = getBindingsFor(type);
        if (container == null) {
            container = new BindingContainer<>(type);
            bindings.put(type, container);
        }
        DrinkBinding<T> binding = new DrinkBinding<>(type, annotations, provider);
        container.getBindings().add(binding);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public CommandExtractor getExtractor() {
        return extractor;
    }

    public DrinkHelpService getHelpService() {
        return helpService;
    }

    public ProviderAssigner getProviderAssigner() {
        return providerAssigner;
    }

    public ArgumentParser getArgumentParser() {
        return argumentParser;
    }

    public ModifierService getModifierService() {
        return modifierService;
    }

    public DrinkSpigotRegistry getSpigotRegistry() {
        return spigotRegistry;
    }

    public FlagExtractor getFlagExtractor() {
        return flagExtractor;
    }

    public DrinkAuthorizer getAuthorizer() {
        return authorizer;
    }

    public ConcurrentMap<String, DrinkCommandContainer> getCommands() {
        return commands;
    }

    public ConcurrentMap<Class<?>, BindingContainer<?>> getBindings() {
        return bindings;
    }
}
