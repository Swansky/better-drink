package com.jonahseguin.drink.provider.spigot;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

public class OfflinePlayerProvider extends DrinkProvider<OfflinePlayer> {
    private final JavaPlugin plugin;

    public OfflinePlayerProvider(JavaPlugin plugin) {
        this.plugin = plugin;
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
    public OfflinePlayer provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg.get());
        if (offlinePlayer.hasPlayedBefore()) {
            return offlinePlayer;
        }
        throw new CommandExitMessage(Component.translatable("error.provider.player_name.invalid", arg.get()));
    }

    @Override
    public String argumentDescription() {
        return null;
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return plugin.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).filter(s -> prefix.isEmpty() || s.startsWith(prefix)).toList();
    }
}
