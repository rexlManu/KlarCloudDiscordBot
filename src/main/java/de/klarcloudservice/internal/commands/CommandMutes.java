package de.klarcloudservice.internal.commands;

import de.klarcloudservice.internal.command.*;
import java.io.*;
import de.klarcloudservice.internal.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.*;
import java.util.*;

public final class CommandMutes extends Command implements Serializable
{
  private final Role mute;

  public CommandMutes() {
    super("mutes");
    this.mute = DiscordBot.getGuild().getRoleById("533766822718078983");
  }

  @Override
  public void handle(final TextChannel channel, final Member member, final String[] strings) {
    if (!DiscordBot.hasPermission(Permission.KICK_MEMBERS, member)) {
      return;
    }
    final List<String> mutes = new ArrayList<String>();
    for (final Member member2 : DiscordBot.getGuild().getMembers()) {
      if (member2.getRoles().contains(this.mute)) {
        mutes.add(member2.getUser().getId());
      }
    }
    channel.sendMessage("Active mutes: " + mutes).queue();
  }
}
