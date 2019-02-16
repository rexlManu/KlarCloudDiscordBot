package de.klarcloudservice.internal.commands;

import de.klarcloudservice.internal.command.*;
import java.io.*;
import java.util.concurrent.*;
import de.klarcloudservice.internal.*;
import net.dv8tion.jda.core.*;
import java.awt.*;
import net.dv8tion.jda.core.entities.*;

public final class CommandUserInfo extends Command implements Serializable
{
    public CommandUserInfo() {
        super("userinfo");
    }

    @Override
    public void handle(final TextChannel channel, final Member member, final String[] strings) {
        if (strings.length != 1) {
            channel.sendMessage("!userinfo <ID>").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        final Member target = DiscordBot.getGuild().getMemberById(strings[0]);
        if (target == null) {
            channel.sendMessage("Member not found ").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            return;
        }
        final EmbedBuilder embedBuilder = new EmbedBuilder().setAuthor("KlarCloudService", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setColor(Color.MAGENTA).setImage(target.getUser().getAvatarUrl()).setTitle("UserInfo " + strings[0]).setDescription("UserName: " + target.getUser().getName()).addField("Ping: ", target.getJDA().getPing() + "", true);
        channel.sendMessage(embedBuilder.build()).queue();
    }
}
