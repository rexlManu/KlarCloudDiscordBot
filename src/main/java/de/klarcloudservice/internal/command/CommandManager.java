package de.klarcloudservice.internal.command;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class CommandManager implements Serializable {
    private static final long serialVersionUID = 2131779633515032284L;
    private List<Command> commands;

    public CommandManager() {
        this.commands = new ArrayList<Command>();
    }

    public boolean handle(final TextChannel channel, final Member sender, final String command) {
        final String[] strings = command.split(" ");
        if (strings.length <= 0) {
            return false;
        }
        final Optional<Command> cmd = this.commands.stream().filter(command1 -> command1.getName().equalsIgnoreCase(strings[0]) || Arrays.asList(command1.getAliases()).contains(strings[0])).findFirst();
        if (cmd.isPresent()) {
            final Command command2 = cmd.get();
            final String string = command.replace(command.contains(" ") ? (command.split(" ")[0] + " ") : command, "");
            try {
                if (string.equalsIgnoreCase("")) {
                    command2.handle(channel, sender, new String[0]);
                } else {
                    final String[] arguments = string.split(" ");
                    command2.handle(channel, sender, arguments);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public CommandManager registerCommand(final Command command) {
        this.commands.add(command);
        return this;
    }

    public CommandManager unregisterCommand(final String name) {
        final List<Command> commands = this.commands;
        commands.forEach(e -> {
            if (e.getName().equalsIgnoreCase(name)) {
                this.commands.remove(e);
            }
            return;
        });
        return this;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public CommandManager clearCommands() {
        this.commands.clear();
        return this;
    }
}
