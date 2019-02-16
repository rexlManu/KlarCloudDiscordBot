package de.klarcloudservice.internal;

import net.dv8tion.jda.client.events.relationship.FriendRequestReceivedEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public final class Listeners extends ListenerAdapter {
    private final EmbedBuilder rulesDoneBuilder;
    private final EmbedBuilder rulesRemovedBuilder;

    public Listeners() {
        this.rulesDoneBuilder = new EmbedBuilder().setAuthor("Rules", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setTitle("KlarCloudService - Official Discord").setDescription("You successfully accepted the rules.").setColor(Color.GREEN);
        this.rulesRemovedBuilder = new EmbedBuilder().setAuthor("Rules", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setTitle("KlarCloudService - Official Discord").setDescription("You're not longer accepting the rules").setColor(Color.RED);
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        if (event.getMember().getUser().isBot()) {
            return;
        }
        final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("KlarCloudService - Official Discord").setColor(getRandomColour()).setAuthor("Welcome", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setDescription("Welcome <@" + event.getMember().getUser().getId() + ">").addField(new MessageEmbed.Field("to the official KlarCloudService Discord.", "Bitte lies die Regeln | Please read the rules", true));
        DiscordBot.getJda().getTextChannelById("543310467121020949").sendMessage(embedBuilder.build()).queueAfter(1L, TimeUnit.SECONDS);
        DiscordBot.getJda().getTextChannelById("528974624600621058").sendMessage("<@" + event.getMember().getUser().getId() + ">").queueAfter(1L, TimeUnit.SECONDS, message -> message.delete().queueAfter(100L, TimeUnit.MILLISECONDS));
        event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("528972695636017182")).queue();
    }

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent event) {
        final Message message = DiscordBot.getGuild().getTextChannelById("528192090056818689").getMessageById("543313428757086218").complete();
        for (final MessageReaction messageReaction : message.getReactions()) {
            if ((messageReaction.getUsers()).complete().contains(event.getUser())) {
                messageReaction.removeReaction(event.getUser()).queue();
            }
        }
    }

    @Override
    public void onMessageReactionAdd(final MessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        if (! event.getMessageId().equalsIgnoreCase("543313428757086218")) {
            return;
        }
        if (event.getReactionEmote().getName().equalsIgnoreCase("\u2705")) {
            event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("543219915541446658")).queue();
            event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("543219921170071630")).queue();
            event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(this.rulesDoneBuilder.build()).queue());
        }
    }

    @Override
    public void onMessageReactionRemove(final MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        if (! event.getMessageId().equalsIgnoreCase("543313428757086218")) {
            return;
        }
        if (event.getReactionEmote().getName().equalsIgnoreCase("\u2705")) {
            event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("543219915541446658")).queue();
            event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("543219921170071630")).queue();
            event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(this.rulesRemovedBuilder.build()).queue());
        }
    }

    @Override
    public void onFriendRequestReceived(final FriendRequestReceivedEvent event) {
        event.getFriendRequest().accept().queue();
    }

    public static Color getRandomColour() {
        switch (DiscordBot.random.nextInt(0, 5)) {
            case 1: {
                return Color.RED;
            }
            case 2: {
                return Color.BLUE;
            }
            case 3: {
                return Color.YELLOW;
            }
            case 4: {
                return Color.CYAN;
            }
            case 5: {
                return Color.ORANGE;
            }
            default: {
                return Color.GREEN;
            }
        }
    }
}
