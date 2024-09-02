package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.annotation.EnumValues;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class EnumValuesProvider extends DrinkProvider<String> {
    public static final DrinkProvider<String> INSTANCE = new EnumValuesProvider();


    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public String provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String value = arg.get();
        EnumValues annotation = (EnumValues) annotations.get(0);
        String[] values = annotation.value();
        for (String s : values) {
            if (s.equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new CommandExitMessage("Invalid value. Valid values: " + String.join(", ", values));
    }

    @Override
    public String argumentDescription() {
        return "enum list";
    }

    @Override
    public List<String> getSuggestions(CommandSender sender, @NotNull String prefix, @NotNull List<? extends Annotation> annotations) {
        EnumValues annotation = (EnumValues) annotations.get(0);
        String[] values = annotation.value();
        List<String> suggestions = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(value);
            }
        }
        return suggestions;
    }
}
