package de.klarcloudservice.internal.commands;

import de.klarcloudservice.internal.DiscordBot;
import de.klarcloudservice.internal.command.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class CommandBan extends Command implements Serializable {
    private static final long serialVersionUID = 5938715767146360626L;

    public CommandBan() {
        super("ban", new String[]{"punish"});
    }

    @Override
    public void handle(final TextChannel channel, final Member member, final String[] strings) {
        if (! DiscordBot.hasPermission(Permission.BAN_MEMBERS, member)) {
            return;
        }
        if (strings.length <= 2) {
            channel.sendMessage("!ban <ID> <timeindays> <reason>").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        final Member target = DiscordBot.getGuild().getMemberById(strings[0]);
        if (target == null) {
            channel.sendMessage("Member not found ").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        if (! this.isInteger(strings[1]) || Integer.valueOf(strings[1]) < 0) {
            channel.sendMessage("Please give a valid time").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        DiscordBot.getGuild().getController().ban(target, Integer.valueOf(strings[1]), Arrays.stream(Arrays.copyOfRange(strings, 2, strings.length)).collect(Collectors.joining(" "))).queue();
        channel.sendMessage("The user " + target.getUser().getName() + " was banned for " + strings[1] + " days.").queue();
    }

    private boolean isInteger(final String in) {
        try {
            Integer.parseInt(in);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }
}
