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

public final class CommandMute extends Command implements Serializable {
    public CommandMute() {
        super("mute");
    }

    @Override
    public void handle(final TextChannel channel, final Member member, final String[] strings) {
        if (! DiscordBot.hasPermission(Permission.KICK_MEMBERS, member)) {
            return;
        }
        if (strings.length <= 1) {
            channel.sendMessage("!mute <ID> <reason>").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        final Member target = DiscordBot.getGuild().getMemberById(strings[0]);
        if (target == null) {
            channel.sendMessage("Member not found ").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        final String reason = Arrays.stream(Arrays.copyOfRange(strings, 1, strings.length)).collect(Collectors.joining(" "));
        DiscordBot.getGuild().getController().addSingleRoleToMember(target, DiscordBot.getGuild().getRoleById("533766822718078983")).queue();
        channel.sendMessage("The user " + target.getUser().getName() + " was muted").queue();
        target.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You were muted because of \"" + reason + "\"").queue());
    }
}
