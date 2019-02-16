package de.klarcloudservice.internal.suggestion;

import de.klarcloudservice.internal.DiscordBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {
    private final List<String> id;
    private final List<String> emotes;
    private final List<String> allowed;

    public Listener() {
        this.id = Arrays.asList("543265793828323338", "543302248403304448");
        this.emotes = Arrays.asList("\ud83d\udd59", "\u2705", "\u274c");
        this.allowed = Arrays.asList("\ud83d\udd59", "\u2705", "\u274c", "\ud83d\udc4e", "\ud83d\udc4d");
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        if (event.getChannel().getId() != null && this.id.contains(event.getChannel().getId())) {
            event.getMessage().addReaction("\ud83d\udc4d").queue();
            event.getMessage().addReaction("\ud83d\udc4e").queue();
            event.getMessage().addReaction("\ud83d\udd59").queue();
            event.getMessage().addReaction("\u2705").queue();
            event.getMessage().addReaction("\u274c").queue();
        }
    }

    @Override
    public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot() || ! this.id.contains(event.getChannel().getId())) {
            return;
        }
        if (! this.allowed.contains(event.getReaction().getReactionEmote().getName())) {
            event.getReaction().removeReaction(event.getUser()).queueAfter(1L, TimeUnit.SECONDS);
            return;
        }
        if (this.check(event.getMember())) {
            return;
        }
        if (event.getChannel().getId() != null && this.id.contains(event.getChannel().getId())) {
            if (this.emotes.contains(event.getReaction().getReactionEmote().getName()) && ! this.check(event.getMember())) {
                event.getReaction().removeReaction(event.getUser()).queueAfter(1L, TimeUnit.SECONDS);
                return;
            }
            final Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
            if (! this.hasPermissionAndAddedEmeote(message, event.getMember().getUser().getId(), event.getReaction().getReactionEmote().getName())) {
                event.getReaction().removeReaction(event.getUser()).queueAfter(1L, TimeUnit.SECONDS);
            }
        }
    }

    private boolean hasPermissionAndAddedEmeote(final Message message, final String userID, final String emote) {
        for (final MessageReaction messageReaction : message.getReactions()) {
            if (messageReaction.getReactionEmote().getName().equalsIgnoreCase(emote)) {
                continue;
            }
            if ((messageReaction.getUsers()).complete().contains(DiscordBot.getJda().getUserById(userID))) {
                return false;
            }
        }
        return true;
    }

    private boolean check(final Member user) {
        for (final Role role : user.getRoles()) {
            if (role.getPermissions().contains(Permission.ADMINISTRATOR)) {
                return true;
            }
        }
        return false;
    }
}
