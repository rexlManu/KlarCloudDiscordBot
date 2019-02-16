package de.klarcloudservice.internal.command;

import java.io.*;
import net.dv8tion.jda.core.entities.*;

public abstract class Command implements Serializable
{
  private static final long serialVersionUID = -8432972203934300544L;
  private final String name;
  private final String[] aliases;

  protected Command(final String name) {
    this.name = name;
    this.aliases = new String[0];
  }

  protected Command(final String name, final String[] aliases) {
    this.name = name;
    this.aliases = new String[0];
  }

  public abstract void handle(final TextChannel p0, final Member p1, final String[] p2);

  public String getName() {
    return this.name;
  }

  public String[] getAliases() {
    return this.aliases;
  }
}
