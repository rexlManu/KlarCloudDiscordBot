package de.klarcloudservice.internal.commands;

import de.klarcloudservice.internal.command.*;
import java.io.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.*;
import de.klarcloudservice.internal.*;

public final class CommandPing extends Command implements Serializable
{
  private static final long serialVersionUID = 3252643308256798167L;

  public CommandPing() {
    super("ping");
  }

  @Override
  public void handle(final TextChannel channel, final Member member, final String[] strings) {
    channel.sendMessage(new EmbedBuilder().setColor(Listeners.getRandomColour()).setAuthor("KlarCloudService").setAuthor("KlarCloud", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setDescription("Current Bot ping " + DiscordBot.getJda().getPing()).build()).queue();
  }
}
