package com.jonahseguin.drink.command;

import com.jonahseguin.drink.exception.CommandStructureException;
import com.jonahseguin.drink.exception.MissingProviderException;
import com.jonahseguin.drink.parametric.CommandParameter;
import com.jonahseguin.drink.parametric.CommandParameters;
import com.jonahseguin.drink.parametric.DrinkProvider;

import java.lang.reflect.Method;
import java.util.Set;


public class DrinkCommand {

    private final DrinkCommandService commandService;
    private final String name;
    private final Set<String> allAliases;
    private final Set<String> aliases;
    private final String description;
    private final String usage;
    private final String permission;
    private final Object handler;
    private final Method method;
    private final CommandParameters parameters;
    private final DrinkProvider<?>[] providers;
    private final DrinkProvider<?>[] consumingProviders;
    private final int consumingArgCount;
    private final int requiredArgCount;
    private final boolean requiresAsync;
    private final String generatedUsage;

    public DrinkCommand(DrinkCommandService commandService, String name, Set<String> aliases, String description, String usage, String permission, Object handler, Method method) throws MissingProviderException, CommandStructureException {
        this.commandService = commandService;
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.permission = permission;
        this.handler = handler;
        this.method = method;
        this.parameters = new CommandParameters(method);
        this.providers = commandService.getProviderAssigner().assignProvidersFor(this);
        this.consumingArgCount = calculateConsumingArgCount();
        this.requiredArgCount = calculateRequiredArgCount();
        this.consumingProviders = calculateConsumingProviders();
        this.requiresAsync = calculateRequiresAsync();
        this.generatedUsage = generateUsage();
        this.allAliases = aliases;
        if (name.length() > 0 && !name.equals(DrinkCommandService.DEFAULT_KEY)) {
            allAliases.add(name);
        }
    }

    public String getMostApplicableUsage() {
        if (usage.length() > 0) {
            return usage;
        }
        else {
            return generatedUsage;
        }
    }

    public String getShortDescription() {
        if (description.length() > 24) {
            return description.substring(0, 21) + "...";
        }
        else {
            return description;
        }
    }

    private String generateUsage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.getParameters().length; i++) {
            CommandParameter parameter = parameters.getParameters()[i];
            DrinkProvider<?> provider = providers[i];
            String description = parameter.getParameter().getName(); // provider.argumentDescription()
            if (parameter.isFlag()) {
                sb.append("-").append(parameter.getFlag().value()).append(" ");
            }
            else {
                if (provider.doesConsumeArgument()) {
                    if (parameter.isOptional()) {
                        sb.append("[").append(description);
                        if (parameter.isText()) {
                            sb.append("...");
                        }
                        if (parameter.getDefaultOptionalValue() != null && !parameter.getDefaultOptionalValue().isEmpty()) {
                            sb.append(" = ").append(parameter.getDefaultOptionalValue());
                        }
                        sb.append("]");
                    } else {
                        sb.append("<").append(description);
                        if (parameter.isText()) {
                            sb.append("...");
                        }
                        sb.append(">");
                    }
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    private boolean calculateRequiresAsync() {
        for (DrinkProvider<?> provider : providers) {
            if (provider.isAsync()) {
                return true;
            }
        }
        return false;
    }

    private DrinkProvider<?>[] calculateConsumingProviders() {
        DrinkProvider<?>[] consumingProviders = new DrinkProvider<?>[consumingArgCount];
        int x = 0;
        for (DrinkProvider<?> provider : providers) {
            if (provider.doesConsumeArgument()) {
                consumingProviders[x] = provider;
                x++;
            }
        }
        return consumingProviders;
    }

    private int calculateConsumingArgCount() {
        int count = 0;
        for (DrinkProvider<?> provider : providers) {
            if (provider.doesConsumeArgument()) {
                count++;
            }
        }
        return count;
    }

    private int calculateRequiredArgCount() {
        int count = 0;
        for (int i = 0; i < parameters.getParameters().length; i++) {
            CommandParameter parameter = parameters.getParameters()[i];
            if (!parameter.isFlag() && !parameter.isOptional()) {
                DrinkProvider provider = providers[i];
                if (provider.doesConsumeArgument()) {
                    count++;
                }
            }
        }
        return count;
    }

    public DrinkCommandService getCommandService() {
        return commandService;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAllAliases() {
        return allAliases;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return permission;
    }

    public Object getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }

    public CommandParameters getParameters() {
        return parameters;
    }

    public DrinkProvider<?>[] getProviders() {
        return providers;
    }

    public DrinkProvider<?>[] getConsumingProviders() {
        return consumingProviders;
    }

    public int getConsumingArgCount() {
        return consumingArgCount;
    }

    public int getRequiredArgCount() {
        return requiredArgCount;
    }

    public boolean isRequiresAsync() {
        return requiresAsync;
    }

    public String getGeneratedUsage() {
        return generatedUsage;
    }
}
