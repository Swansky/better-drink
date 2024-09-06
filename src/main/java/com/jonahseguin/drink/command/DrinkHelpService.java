package com.jonahseguin.drink.command;

import com.jonahseguin.drink.TranslateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;


public class DrinkHelpService {

    private final DrinkCommandService commandService;
    private HelpFormatter helpFormatter;

    public DrinkHelpService(DrinkCommandService commandService) {
        this.commandService = commandService;
        this.helpFormatter = (sender, container) -> {
            net.kyori.adventure.text.TextComponent.Builder helpMessageBuilder = Component.text();
            LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.legacy('&');

            helpMessageBuilder.append(componentSerializer.deserialize("&7&m--------------------------------"));
            helpMessageBuilder.append(componentSerializer.deserialize("&bHelp &7- &6/" + container.getName()));
            helpMessageBuilder.append(componentSerializer.deserialize(""));
            helpMessageBuilder.append(componentSerializer.deserialize(""));
            for (DrinkCommand c : container.getCommands().values()) {
                if (sender.hasPermission(c.getPermission()) || sender.isOp()) {
                    TextComponent msg = componentSerializer.deserialize("&7/" + container.getName() + (!c.getName().isEmpty() ? " &e" + c.getName() : "") + " &7" + c.getMostApplicableUsage() + " &7- &f" + c.getShortDescription());
                    msg = msg.hoverEvent(HoverEvent.showText(componentSerializer.deserialize("&7/" + container.getName() + " " + c.getName() + " - &f" + c.getDescription())));
                    msg = msg.clickEvent(ClickEvent.suggestCommand("/" + container.getName() + " " + c.getName()));
                    helpMessageBuilder.append(msg.append(Component.newline()));
                }
            }
            helpMessageBuilder.append(componentSerializer.deserialize("&7&m--------------------------------"));
            sender.sendMessage(helpMessageBuilder.build());
        };
    }

    public void sendHelpFor(CommandSender sender, DrinkCommandContainer container) {

        this.helpFormatter.sendHelpFor(sender, container);
    }

    public void sendUsageMessage(CommandSender sender, DrinkCommandContainer container, DrinkCommand command) {
        sender.sendMessage(getUsageMessage(container, command));
    }

    public Component getUsageMessage(DrinkCommandContainer container, DrinkCommand command) {
        Component usage = TranslateUtils.makeErrorMessage("error.command.usage", Component.text(container.getName()));

        if (!command.getName().isEmpty()) {
            usage = usage.append(Component.text(command.getName() + " "));
        }
        if (command.getUsage() != null && !command.getUsage().isEmpty()) {
            usage = usage.append(Component.text(command.getUsage()));
        } else {
            usage = usage.append(Component.text(command.getGeneratedUsage()));
        }
        return usage;
    }

    public DrinkCommandService getCommandService() {
        return commandService;
    }

    public HelpFormatter getHelpFormatter() {
        return helpFormatter;
    }

    public void setHelpFormatter(HelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
    }
}
