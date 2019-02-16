package de.klarcloudservice.internal.music;

import net.dv8tion.jda.core.hooks.*;
import net.dv8tion.jda.core.events.message.guild.*;
import java.util.concurrent.*;
import net.dv8tion.jda.core.managers.*;
import net.dv8tion.jda.core.entities.*;

public class MusicJoinCommand extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getMessage().getContentRaw().startsWith("#")) {
            return;
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase("#join")) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
                event.getChannel().sendMessage("Bot is already connected to a voice channel").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
            }
            else if (!event.getMember().getVoiceState().inVoiceChannel()) {
                event.getChannel().sendMessage("You're not connected to a voice channel").queue(message -> message.delete().queueAfter(15L, TimeUnit.SECONDS));
            }
            else {
                final VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
                if (voiceChannel.getUserLimit() == voiceChannel.getMembers().size()) {
                    event.getChannel().sendMessage("User limit reached").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
                }
                else {
                    event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                    event.getChannel().sendMessage("Joined!").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
                }
            }
        }
        else if (event.getMessage().getContentRaw().equalsIgnoreCase("#leave")) {
            if (event.getGuild().getAudioManager().getConnectedChannel() != null && event.getMember().getVoiceState().inVoiceChannel() && event.getMember().getVoiceState().getChannel().getMembers().contains(event.getGuild().getSelfMember())) {
                event.getGuild().getAudioManager().closeAudioConnection();
                event.getChannel().sendMessage("Leaved").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
            }
        }
        else if (event.getMessage().getContentRaw().equalsIgnoreCase("#play")) {}
    }
}
