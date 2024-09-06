package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateProvider extends DrinkProvider<Date> {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final DrinkProvider<Date> INSTANCE = new DateProvider(new SimpleDateFormat(DATE_FORMAT));

    private final DateFormat dateFormat;

    public DateProvider(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

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
    public Date provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String dateString = arg.get();
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new CommandExitMessage(Component.translatable("error.provider.date", DATE_FORMAT));
        }
    }

    @Override
    public String argumentDescription() {
        return DATE_FORMAT;
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        List<String> suggestions = new ArrayList<>();
        String format = dateFormat.format(new Date());
        String suggestion = prefix + format.substring(prefix.length());
        suggestions.add(suggestion);
        return suggestions;
    }
}
