package de.klarcloudservice.internal.logging;

import net.dv8tion.jda.core.hooks.*;
import java.util.*;
import net.dv8tion.jda.core.*;
import java.awt.*;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.user.update.*;
import net.dv8tion.jda.core.events.message.guild.*;
import net.dv8tion.jda.core.events.message.guild.react.*;
import net.dv8tion.jda.core.events.message.priv.*;
import de.klarcloudservice.internal.*;
import net.dv8tion.jda.core.events.channel.text.update.*;
import net.dv8tion.jda.core.events.channel.text.*;
import net.dv8tion.jda.core.events.channel.voice.update.*;
import net.dv8tion.jda.core.events.channel.voice.*;
import net.dv8tion.jda.core.events.channel.category.update.*;
import net.dv8tion.jda.core.events.channel.category.*;
import net.dv8tion.jda.core.events.guild.*;
import net.dv8tion.jda.core.events.guild.update.*;
import net.dv8tion.jda.core.events.role.*;
import net.dv8tion.jda.core.events.role.update.*;
import net.dv8tion.jda.client.events.relationship.*;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.entities.*;

public final class DiscordLogger extends ListenerAdapter
{
    private String avatar;
    private TextChannel logChannel;
    private Map<Long, Message> messages;

    public DiscordLogger(final String avatarUrl) {
        this.messages = new HashMap<Long, Message>();
        this.avatar = avatarUrl;
    }

    public void setLogChannel(final TextChannel textChannel) {
        this.logChannel = textChannel;
    }

    private EmbedBuilder defaultBuilder() {
        return new EmbedBuilder().setAuthor("KlarCloudService", "https://klarcloudservice.de", this.avatar).setTitle("Logging | KlarCloud");
    }

    private EmbedBuilder description(final Color color, final String description) {
        return this.defaultBuilder().setColor(color).setDescription(description);
    }

    @Override
    public void onReconnect(final ReconnectedEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "Der Bot hat sich erfolgreich zum Websocket zur\u00fcckverbunden").build()).queue();
    }

    @Override
    public void onException(final ExceptionEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Ein Fehler ist aufgetreten {" + event.getCause().getMessage() + "}").build()).queue();
    }

    @Override
    public void onUserUpdateName(final UserUpdateNameEvent event) {
        this.logChannel.sendMessage(this.description(Color.ORANGE, "Der User " + event.getNewName() + " hat seinen Namen ge\u00e4ndert").addField("Vorher:", event.getOldName(), true).build()).queue();
    }

    @Override
    public void onUserUpdateAvatar(final UserUpdateAvatarEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "Der User " + event.getEntity().getName() + " hat seinen Avatar ge\u00e4ndert").setImage(event.getNewAvatarUrl()).build()).queue();
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        this.messages.put(event.getMessageIdLong(), event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(final GuildMessageUpdateEvent event) {
        final Message message = this.messages.get(event.getMessageIdLong());
        this.logChannel.sendMessage(this.description(Color.BLUE, "Nachricht wurde ge\u00e4ndert").addField("Message: ", (message != null) ? message.getContentRaw() : "Nachricht konnte nicht gefunden werden :(", true).build()).queue();
        if (message != null) {
            this.messages.remove(event.getMessageIdLong());
            this.messages.put(event.getMessageIdLong(), event.getMessage());
        }
        else {
            this.messages.put(event.getMessageIdLong(), event.getMessage());
        }
    }

    @Override
    public void onGuildMessageDelete(final GuildMessageDeleteEvent event) {
        final Message message = this.messages.get(event.getMessageIdLong());
        this.logChannel.sendMessage(this.description(Color.RED, "Nachricht wurde gel\u00f6scht").addField("Message: ", (message != null) ? message.getContentRaw() : "Nachricht konnte nicht gefunden werden :(", true).build()).queue();
    }

    @Override
    public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event) {
        this.logChannel.sendMessage(this.description(Color.PINK, "Reaction wurde hinzugef\u00fcgt").addField("User: ", event.getUser().getName(), true).addField("Reaction: ", (event.getReaction().getReactionEmote().getName() != null) ? event.getReaction().getReactionEmote().getName() : "An internal error occurred", true).build()).queue();
    }

    @Override
    public void onGuildMessageReactionRemove(final GuildMessageReactionRemoveEvent event) {
        this.logChannel.sendMessage(this.description(Color.PINK, "Reaction wurde entfernt").addField("User: ", event.getUser().getName(), true).addField("Reaction: ", (event.getReaction().getReactionEmote().getName() != null) ? event.getReaction().getReactionEmote().getName() : "An internal error occurred", true).build()).queue();
    }

    @Override
    public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {
        if (event.getAuthor().getId().equalsIgnoreCase(DiscordBot.getJda().getSelfUser().getId())) {
            return;
        }
        this.logChannel.sendMessage(this.description(Color.PINK, "Private Nachricht empfangen").addField("Message: ", event.getMessage().getContentRaw(), true).addField("Von: ", event.getAuthor().getName(), true).build()).queue();
    }

    @Override
    public void onTextChannelDelete(final TextChannelDeleteEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Channel gel\u00f6scht").addField("Typ: ", "Text", true).addField("Name: ", event.getChannel().getName(), true).build()).queue();
    }

    @Override
    public void onTextChannelUpdateName(final TextChannelUpdateNameEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "Channel Name ge\u00e4ndert").addField("Vorher: ", event.getOldName(), true).addField("Nacher: ", event.getNewName(), true).build()).queue();
    }

    @Override
    public void onTextChannelUpdateTopic(final TextChannelUpdateTopicEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "TextChannel Topic ge\u00e4ndert").addField("Vorher: ", (event.getOldTopic() != null) ? event.getOldTopic() : "none", true).addField("Neue Topic: ", event.getNewTopic(), true).build()).queue();
    }

    @Override
    public void onTextChannelUpdatePosition(final TextChannelUpdatePositionEvent event) {
        this.logChannel.sendMessage(this.description(Color.ORANGE, "Channel position ge\u00e4ndert {" + event.getChannel().getName() + "}").addField("Vorher", event.getOldPosition() + "", true).addField("Nacher: ", event.getNewPosition() + "", true).build()).queue();
    }

    @Override
    public void onTextChannelUpdatePermissions(final TextChannelUpdatePermissionsEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Permissions des Channels " + event.getChannel().getName() + " ge\u00e4ndert").addField("Ge\u00e4nderte Rollen f\u00fcr: ", event.getChangedRoles() + "", true).addField("Ge\u00e4nderte Memeber: ", event.getChangedMembers() + "", true).build()).queue();
    }

    @Override
    public void onTextChannelUpdateNSFW(final TextChannelUpdateNSFWEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "TextChannel NSWF Update").addField("Channel: ", event.getChannel().getName(), true).addField("Vorher: ", event.getOldNSFW() + "", true).addField("Nachher: ", event.getChannel().isNSFW() + "", true).build()).queue();
    }

    @Override
    public void onTextChannelUpdateSlowmode(final TextChannelUpdateSlowmodeEvent event) {
        this.logChannel.sendMessage(this.description(Color.ORANGE, "Slowmode f\u00fcr Channel " + event.getChannel().getName() + " ge\u00e4ndert").addField("Vorher: ", event.getOldSlowmode() + "", true).addField("Nachher: ", event.getNewSlowmode() + "", true).build()).queue();
    }

    @Override
    public void onTextChannelCreate(final TextChannelCreateEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "Channel erstellt {" + event.getChannel().getName() + "}").build()).queue();
    }

    @Override
    public void onVoiceChannelDelete(final VoiceChannelDeleteEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Channel gel\u00f6scht").addField("Typ: ", "Voice", true).addField("Name: ", event.getChannel().getName(), true).build()).queue();
    }

    @Override
    public void onVoiceChannelUpdateName(final VoiceChannelUpdateNameEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "VoiceChannel Name ge\u00e4ndert").addField("Vorher: ", event.getOldName(), true).addField("Nacher: ", event.getNewName(), true).build()).queue();
    }

    @Override
    public void onVoiceChannelUpdatePosition(final VoiceChannelUpdatePositionEvent event) {
        this.logChannel.sendMessage(this.description(Color.ORANGE, "VoiceChannel position ge\u00e4ndert {" + event.getChannel().getName() + "}").addField("Vorher", event.getOldPosition() + "", true).addField("Nacher: ", event.getNewPosition() + "", true).build()).queue();
    }

    @Override
    public void onVoiceChannelUpdateUserLimit(final VoiceChannelUpdateUserLimitEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "VoiceChannel maxUser ge\u00e4ndert {" + event.getChannel().getName() + "}").addField("Vorher: ", event.getOldValue() + "", true).addField("Nachher: ", event.getNewValue() + "", true).build()).queue();
    }

    @Override
    public void onVoiceChannelUpdatePermissions(final VoiceChannelUpdatePermissionsEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Permissions des VoiceChannels " + event.getChannel().getName() + " ge\u00e4ndert").addField("Ge\u00e4nderte Rollen f\u00fcr: ", event.getChangedRoles() + "", true).addField("Ge\u00e4nderte Memeber: ", event.getChangedMembers() + "", true).build()).queue();
    }

    @Override
    public void onVoiceChannelCreate(final VoiceChannelCreateEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "VoiceChannel erstellt {" + event.getChannel().getName() + "}").build()).queue();
    }

    @Override
    public void onCategoryDelete(final CategoryDeleteEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Kategorie gel\u00f6scht: " + event.getCategory().getName()).build()).queue();
    }

    @Override
    public void onCategoryUpdateName(final CategoryUpdateNameEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Kategorie Name ge\u00e4ndert").addField("Vorher: ", event.getOldName(), true).addField("Nachhher: ", event.getNewName(), true).build()).queue();
    }

    @Override
    public void onCategoryCreate(final CategoryCreateEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Kategorie erstellt: " + event.getCategory().getName()).build()).queue();
    }

    @Override
    public void onGuildBan(final GuildBanEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "User gebannt " + event.getUser().getName()).build()).queue();
    }

    @Override
    public void onGuildUnban(final GuildUnbanEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "User entbannt " + event.getUser().getName()).build()).queue();
    }

    @Override
    public void onGuildUpdateAfkChannel(final GuildUpdateAfkChannelEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "Afk-Channel ge\u00e4ndert {" + event.getNewAfkChannel() + "}").build()).queue();
    }

    @Override
    public void onGuildUpdateAfkTimeout(final GuildUpdateAfkTimeoutEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "Afk-timeout ge\u00e4ndert {" + event.getNewAfkTimeout() + "}").build()).queue();
    }

    @Override
    public void onGuildUpdateIcon(final GuildUpdateIconEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "Guild-Icon ge\u00e4ndert").setImage(event.getNewIconUrl()).build()).queue();
    }

    @Override
    public void onGuildUpdateName(final GuildUpdateNameEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "Guild-Name ge\u00e4ndert").setImage(event.getNewName()).build()).queue();
    }

    @Override
    public void onGuildUpdateOwner(final GuildUpdateOwnerEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "Owner ist nun " + event.getNewOwner()).build()).queue();
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "User beigetreten {" + event.getUser().getName() + "}").setImage(event.getMember().getUser().getAvatarUrl()).build()).queue();
    }

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent event) {
        this.logChannel.sendMessage(this.description(Color.MAGENTA, "User verlassen {" + event.getUser().getName() + "}").build()).queue();
    }

    @Override
    public void onGuildMemberNickChange(final GuildMemberNickChangeEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Nickname ge\u00e4ndert").addField("Vorher:", (event.getPrevNick() != null) ? event.getPrevNick() : "none", true).addField("Nachher", event.getNewNick(), true).build()).queue();
    }

    @Override
    public void onRoleCreate(final RoleCreateEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Rolle erstellt {" + event.getRole().getName() + "}").build()).queue();
    }

    @Override
    public void onRoleDelete(final RoleDeleteEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Rolle gel\u00f6scht {" + event.getRole().getName() + "}").build()).queue();
    }

    @Override
    public void onRoleUpdateName(final RoleUpdateNameEvent event) {
        this.logChannel.sendMessage(this.description(Color.RED, "Rollen-Name geupdatet {" + event.getRole().getName() + "}").build()).queue();
    }

    @Override
    public void onFriendAdded(final FriendAddedEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "Freundes-Anfrage von " + event.getFriend().getUser().getName() + " angenommen.").build()).queue();
    }

    @Override
    public void onFriendRemoved(final FriendRemovedEvent event) {
        this.logChannel.sendMessage(this.description(Color.GREEN, "Freund " + event.getFriend().getUser().getName() + " entfernt.").build()).queue();
    }

    @Override
    public void onGuildMemberRoleAdd(final GuildMemberRoleAddEvent event) {
        event.getRoles().forEach(e -> this.logChannel.sendMessage(this.description(Color.RED, "Dem User " + event.getUser().getName() + " wurde die Rolle `" + e.getName() + "` hinzugef\u00fcgt.").build()).queue());
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        event.getRoles().forEach(e -> this.logChannel.sendMessage(this.description(Color.RED, "Dem User " + event.getUser().getName() + " wurde die Rolle `" + e.getName() + "` entfernt.").build()).queue());
    }
}
