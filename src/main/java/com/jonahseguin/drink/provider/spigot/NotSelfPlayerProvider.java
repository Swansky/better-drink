package com.jonahseguin.drink.provider.spigot;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

public class NotSelfPlayerProvider extends DrinkProvider<Player> {
    private final JavaPlugin plugin;

    public NotSelfPlayerProvider(JavaPlugin plugin) {
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
    public Player provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        Player player = arg.getSenderAsPlayer();
        String targetName = arg.get();
        if (targetName.equals(player.getName())) {
            throw new CommandExitMessage("Vous ne pouvez pas rentrer votre propre pseudo.");
        }

        Player player1 = Bukkit.getPlayer(targetName);
        if (player1 == null) {
            throw new CommandExitMessage("Le nom de joueur entré n'est pas valide ou le joueur n'est pas connecté.");
        }
        return player1;
    }

    @Override
    public String argumentDescription() {
        return "Joueur";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return plugin.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).filter(s -> prefix.length() == 0 || s.startsWith(prefix)).toList();
    }
}
