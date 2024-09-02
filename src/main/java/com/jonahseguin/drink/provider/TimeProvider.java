package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeProvider extends DrinkProvider<Date> {
    public static final DrinkProvider<Date> INSTANCE = new TimeProvider();
    public static final String DATE_PATTERN = "dd/MM/yyyy-HH:mm:ss";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

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
            throw new CommandExitMessage("Le format de date n'est pas valide. %s 03/05/2022-12:00:00".formatted(DATE_PATTERN));
        }
    }

    @Override
    public String argumentDescription() {
        return DATE_PATTERN;
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
