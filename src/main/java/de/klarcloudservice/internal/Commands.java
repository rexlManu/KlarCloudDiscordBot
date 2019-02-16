package de.klarcloudservice.internal;

import ai.api.*;
import ai.api.model.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public final class Commands extends ListenerAdapter {
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getMember() == null || event.getMember().getUser().isBot()) {
            return;
        }
        if (event.getMessage().getContentRaw().startsWith("!")) {
            final String command = event.getMessage().getContentRaw().replaceFirst("!", "");
            DiscordBot.getCommandManager().handle(event.getTextChannel(), event.getMember(), command);
        } else if (event.getMessage().getContentRaw().startsWith("&")) {
            try {
                final AIRequest aiRequest = new AIRequest(event.getMessage().getContentRaw().replaceFirst("&", ""));
                final AIResponse aiResponse = DiscordBot.getAiDataService().request(aiRequest);
                if (aiResponse.getStatus().getCode() == 200) {
                    if (aiResponse.getResult().getFulfillment().getSpeech().trim().isEmpty()) {
                        event.getChannel().sendMessage("Hmm, try again.").queue();
                    } else {
                        event.getChannel().sendMessage(aiResponse.getResult().getFulfillment().getSpeech().replace("@everyone", "@ everyone").replace("@here", "@ here")).queue();
                    }
                }
            } catch (AIServiceException ex) {
                ex.printStackTrace();
            }
        }
    }
}
