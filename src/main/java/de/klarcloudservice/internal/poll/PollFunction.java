package de.klarcloudservice.internal.poll;

import de.klarcloudservice.internal.DiscordBot;
import de.klarcloudservice.internal.poll.management.PollManagement;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PollFunction extends ListenerAdapter {
    private List<Long> setup;
    private List<Long> next;
    private Map<Long, PollSetup> setupMap;

    public PollFunction() {
        this.setup = new ArrayList<Long>();
        this.next = new ArrayList<Long>();
        this.setupMap = new ConcurrentHashMap<Long, PollSetup>();
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getChannel().getType().equals(ChannelType.PRIVATE)) {
            if (this.setup.contains(event.getAuthor().getIdLong())) {
                if (event.getMessage().getContentRaw().equalsIgnoreCase("exit")) {
                    event.getChannel().sendMessage("You cancelled the poll setup").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                    this.setupMap.remove(event.getAuthor().getIdLong());
                    this.setup.remove(event.getAuthor().getIdLong());
                    this.next.remove(event.getAuthor().getIdLong());
                    return;
                }
                if (event.getMessage().getContentDisplay().equalsIgnoreCase("next")) {
                    if (this.next.contains(event.getMessage().getIdLong())) {
                        event.getChannel().sendMessage("Send our next answer, please").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                    } else if (this.setupMap.get(event.getAuthor().getIdLong()).getAnswers().size() == 20) {
                        event.getChannel().sendMessage("You can't add another answer").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                    } else {
                        this.next.add(event.getAuthor().getIdLong());
                        event.getChannel().sendMessage("Send our next answer, please").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                    }
                    return;
                }
                if (! this.setupMap.containsKey(event.getAuthor().getIdLong())) {
                    this.setupMap.put(event.getAuthor().getIdLong(), new PollSetup(event.getMessage().getContentDisplay(), event.getAuthor().getIdLong()));
                    this.next.add(event.getAuthor().getIdLong());
                    event.getChannel().sendMessage("Please add an answer").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                } else if (! this.next.contains(event.getAuthor().getIdLong())) {
                    if (! this.setupMap.get(event.getAuthor().getIdLong()).isChannel()) {
                        long channel2 = 0L;
                        try {
                            channel2 = Long.parseLong(event.getMessage().getContentRaw());
                        } catch (Throwable t) {}
                        if (channel2 == 0L) {
                            event.getChannel().sendMessage("Please provide a valid channelID").queue(message -> message.delete().queueAfter(20L, TimeUnit.SECONDS));
                        } else {
                            TextChannel textChannel = null;
                            try {
                                textChannel = DiscordBot.getJda().getTextChannelById(channel2);
                            } catch (Throwable t2) {}
                            if (textChannel == null) {
                                event.getChannel().sendMessage("Channel not found").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
                            } else {
                                final PollSetup pollSetup = this.setupMap.get(event.getAuthor().getIdLong());
                                pollSetup.setChannelID(channel2);
                                pollSetup.setSize(pollSetup.getAnswers().size());
                                final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(pollSetup.getQuestion()).setColor(Color.MAGENTA).setAuthor(event.getAuthor().getName(), "https://klarcloudservice.de", event.getAuthor().getAvatarUrl());
                                do {
                                    embedBuilder.addField(new MessageEmbed.Field(this.intInEmojiString(pollSetup.getAnswers().size()) + " " + pollSetup.getAnswers().poll(), "", true));
                                } while (! pollSetup.getAnswers().isEmpty());
                                final PollManagement[] pollManagement = {null};
                                final RunningPoll runningPoll2 = null;
                                final PollSetup pollSetup2 = null;
                                final int[] size = new int[1];
                                textChannel.sendMessage(embedBuilder.build()).queue(message -> {
                                    pollManagement[0] = DiscordBot.pollManagement;
                                    new RunningPoll(UUID.randomUUID(), message.getIdLong(), new ArrayList<Member>());
                                    pollManagement[0].createPoll(runningPoll2);
                                    size[0] = pollSetup2.getSize();
                                    do {
                                        message.addReaction(this.intInEmojiStringCode(size[0])).queue();
                                    } while (-- size[0] != 0);
                                    message.addReaction("\\uD83C\\uDD70").queue();
                                    event.getChannel().sendMessage("Success, your poll has been started").queue(message1 -> message1.delete().queueAfter(20L, TimeUnit.SECONDS));
                                    return;
                                }, throwable -> event.getChannel().sendMessage("Could not send a message in this channel! Please check my permissions").queue(message -> message.delete().queueAfter(20L, TimeUnit.SECONDS)));
                                this.next.remove(event.getAuthor().getIdLong());
                                this.setupMap.remove(event.getAuthor().getIdLong());
                                this.setup.remove(event.getAuthor().getIdLong());
                            }
                        }
                    } else {
                        event.getChannel().sendMessage("An error occurred. Please contact _Klaro :)").queue();
                    }
                } else {
                    event.getChannel().sendMessage("You added \"" + event.getMessage().getContentDisplay() + "\" to the available answers").queue();
                    this.setupMap.get(event.getAuthor().getIdLong()).getAnswers().add(event.getMessage().getContentDisplay());
                    if (this.setupMap.get(event.getAuthor().getIdLong()).getAnswers().size() == 1) {
                        event.getChannel().sendMessage("Send our next answer, please").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
                    } else {
                        event.getChannel().sendMessage("If you want to add another answer just type \"next\"").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
                        this.next.remove(event.getAuthor().getIdLong());
                    }
                }
            } else {
                event.getChannel().sendMessage("You have to start the setup, first").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            }
        } else if (event.getMessage().getContentRaw().startsWith("kc!poll")) {
            if (! this.check(event.getMember())) {
                event.getChannel().sendMessage("Sorry, but you do not have enough permissions to use this command").queue(message -> message.delete().queueAfter(3L, TimeUnit.SECONDS));
                return;
            }
            final String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2 && args[1].equalsIgnoreCase("startsetup") && ! this.setup.contains(event.getMember().getUser().getIdLong())) {
                event.getAuthor().openPrivateChannel().queue(channel -> {
                    channel.sendMessage("Please enter the question. If you want to cancel the setup please type \"exit\"").queue(message -> event.getChannel().sendMessage("Check your DM's :)").queue(message1 -> message1.delete().queueAfter(5L, TimeUnit.SECONDS)), throwable -> event.getChannel().sendMessage("You have to enable your DM's :)").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS)));
                    channel.sendMessage("Please a channelID for your poll to start, when you added all questions").queue();
                    this.setup.add(event.getAuthor().getIdLong());
                });
            } else if (this.setup.contains(event.getMember().getUser().getIdLong())) {
                event.getChannel().sendMessage("You have to complete your setup first").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
            } else {
                event.getChannel().sendMessage("Type \"kc!poll startSetup\" to start a poll setup in a channel on your discord server").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
            }
        } else if (event.getMessage().getContentRaw().startsWith("kc!endpoll")) {
            if (! this.check(event.getMember())) {
                event.getChannel().sendMessage("I'm sorry but you do not have enough permissions to use this command").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                return;
            }
            final String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 3) {
                if (this.isLong(args[1]) && this.isLong(args[2]) && event.getJDA().getTextChannelById(args[1]) != null && event.getJDA().getTextChannelById(args[1]).getMessageById(args[2]) != null) {
                    final UUID pollUID = DiscordBot.pollManagement.getUuidMap().getOrDefault(Long.parseLong(args[2]), null);
                    if (pollUID != null) {
                        final RunningPoll runningPoll = DiscordBot.pollManagement.getList().get(DiscordBot.pollManagement.getUuidMap().get(Long.parseLong(args[2])));
                        DiscordBot.pollManagement.deletePoll(runningPoll);
                        final EmbedBuilder embedBuilder2 = new EmbedBuilder().setTitle("Poll ended").setColor(Color.RED).setAuthor("KlarCloudService", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setDescription("Poll has ended, no more reactions are possible");
                        event.getJDA().getTextChannelById(args[1]).sendMessage(embedBuilder2.build()).queue();
                    } else {
                        event.getChannel().sendMessage("No specified poll found").queue(message -> message.delete().queueAfter(5L, TimeUnit.SECONDS));
                    }
                } else {
                    event.getChannel().sendMessage("Please send a valid channelID and messageID").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
                }
            } else {
                event.getChannel().sendMessage("Please send a valid channel ID as second argument").queue(message -> message.delete().queueAfter(10L, TimeUnit.SECONDS));
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        if (DiscordBot.pollManagement.getUuidMap().getOrDefault(event.getMessageIdLong(), null) != null) {
            System.out.println("teststttss");
            final RunningPoll runningPoll = DiscordBot.pollManagement.getList().get(DiscordBot.pollManagement.getUuidMap().get(event.getMessageIdLong()));
            if (runningPoll.getPollAnswers().contains(event.getMember())) {
                event.getReaction().removeReaction(event.getUser()).queueAfter(1L, TimeUnit.SECONDS);
                event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Entscheide dich f\u00fcr ein Emote! | Please chose one emote!").setDescription("Du musst dich f\u00fcr ein Emote entscheiden! You can only chose one emote!").setAuthor("KlarCloudService", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").build()).queue(message -> message.delete().queueAfter(7L, TimeUnit.SECONDS));
            } else {
                runningPoll.getPollAnswers().add(event.getMember());
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(final GuildMessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        if (DiscordBot.pollManagement.getUuidMap().getOrDefault(event.getMessageIdLong(), null) != null) {
            DiscordBot.pollManagement.getList().get(DiscordBot.pollManagement.getUuidMap().get(event.getMessageIdLong())).getPollAnswers().remove(event.getMember());
        }
    }

    private boolean isLong(final String test) {
        try {
            Long.parseLong(test);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    private boolean check(final Member user) {
        for (final Role role : user.getRoles()) {
            if (role.getPermissions().contains(Permission.ADMINISTRATOR) || role.getPermissions().contains(Permission.MANAGE_SERVER)) {
                return true;
            }
        }
        return false;
    }

    private String intInEmojiString(final int i) {
        switch (i) {
            case 19: {
                return ":regional_indicator_a:";
            }
            case 18: {
                return ":regional_indicator_b:";
            }
            case 17: {
                return ":regional_indicator_c:";
            }
            case 16: {
                return ":regional_indicator_d:";
            }
            case 15: {
                return ":regional_indicator_e:";
            }
            case 14: {
                return ":regional_indicator_f:";
            }
            case 13: {
                return ":regional_indicator_g:";
            }
            case 12: {
                return ":regional_indicator_h:";
            }
            case 11: {
                return ":regional_indicator_i:";
            }
            case 10: {
                return ":regional_indicator_j:";
            }
            case 9: {
                return ":regional_indicator_k:";
            }
            case 8: {
                return ":regional_indicator_l:";
            }
            case 7: {
                return ":regional_indicator_m:";
            }
            case 6: {
                return ":regional_indicator_n:";
            }
            case 5: {
                return ":regional_indicator_o:";
            }
            case 4: {
                return ":regional_indicator_p:";
            }
            case 3: {
                return ":regional_indicator_q:";
            }
            case 2: {
                return ":regional_indicator_r:";
            }
            case 1: {
                return ":regional_indicator_s:";
            }
            case 0: {
                return ":regional_indicator_t:";
            }
            default: {
                return ":regional_indicator_z:";
            }
        }
    }

    private String intInEmojiStringCode(final int i) {
        switch (i) {
            case 19: {
                return "\ud83c\udde6";
            }
            case 18: {
                return "\ud83c\udde7";
            }
            case 17: {
                return "\ud83c\udde8";
            }
            case 16: {
                return "\ud83c\udde9";
            }
            case 15: {
                return "\ud83c\uddea";
            }
            case 14: {
                return "\ud83c\uddeb";
            }
            case 13: {
                return "\ud83c\uddec";
            }
            case 12: {
                return "\ud83c\udded";
            }
            case 11: {
                return "\ud83c\uddee";
            }
            case 10: {
                return "\ud83c\uddef";
            }
            case 9: {
                return "\ud83c\uddf0";
            }
            case 8: {
                return "\ud83c\uddf1";
            }
            case 7: {
                return "\ud83c\uddf2";
            }
            case 6: {
                return "\ud83c\uddf3";
            }
            case 5: {
                return "\ud83c\uddf4";
            }
            case 4: {
                return "\ud83c\uddf5";
            }
            case 3: {
                return "\ud83c\uddf6";
            }
            case 2: {
                return "\ud83c\uddf7";
            }
            case 1: {
                return "\ud83c\uddf8";
            }
            case 0: {
                return "\ud83c\uddf9";
            }
            default: {
                return "\ud83c\uddff";
            }
        }
    }
}
