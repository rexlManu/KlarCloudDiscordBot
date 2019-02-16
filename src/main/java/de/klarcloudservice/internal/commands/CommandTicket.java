package de.klarcloudservice.internal.commands;

import de.klarcloudservice.internal.command.*;
import java.io.*;
import net.dv8tion.jda.core.entities.*;

public final class CommandTicket extends Command implements Serializable
{
  public CommandTicket() {
    super("ticket");
  }

  @Override
  public void handle(final TextChannel channel, final Member member, final String[] strings) {
  }
}
