package de.klarcloudservice.internal;

import de.klarcloudservice.internal.gitlab.API.GitLabAPI;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class DiscordIssue extends ListenerAdapter {
    private final GitLabAPI gitLabAPI;

    public DiscordIssue() {
        this.gitLabAPI = new GitLabAPI();
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        if (! event.getChannel().getId().equals("539060539427520544")) {
            return;
        }
        if (! event.getMessage().getContentRaw().startsWith("!")) {
            return;
        }
        final String[] split = event.getMessage().getContentRaw().replace("!", "").split(" ");
        if (split.length >= 3) {
            final String s = ", , , , ";
            final String[] strings = s.split("");
            Arrays.asList(strings);
            this.gitLabAPI.openIssue("8931291", "YKxZM3D4PyJqX_CsTqdQ", split[0], Collections.singletonList(split[1]), Arrays.stream(Arrays.copyOfRange(split, 2, split.length)).collect(Collectors.joining(" ")));
            event.getChannel().sendMessage("Done").queue();
        }
    }
}
