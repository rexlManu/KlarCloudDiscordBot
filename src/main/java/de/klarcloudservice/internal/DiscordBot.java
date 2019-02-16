package de.klarcloudservice.internal;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import de.klarcloudservice.internal.command.CommandManager;
import de.klarcloudservice.internal.commands.*;
import de.klarcloudservice.internal.logging.DiscordLogger;
import de.klarcloudservice.internal.poll.PollFunction;
import de.klarcloudservice.internal.poll.management.PollManagement;
import de.klarcloudservice.internal.suggestion.Listener;
import de.klarcloudservice.internal.utility.SupportInfo;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DiscordBot {

    private static List<JDA> shards;
    private static JDA jda;
    private static final CommandManager commandManager;
    public static Guild guild;
    private static List<SupportInfo> supportChannels;
    public static final ThreadLocalRandom random;
    public static final PollManagement pollManagement;
    private static AIDataService aiDataService;

    public static synchronized void main(final String... args) {
        System.out.println("Loading...");
        DiscordBot.pollManagement.load();
        try {
            final DiscordLogger discordLogger = new DiscordLogger("https://cdn.discordapp.com/emojis/528123208642199553.png?v=1");
            DiscordBot.aiDataService = new AIDataService(new AIConfiguration("db5ce287f40042b49d3501c168a08e2c"));
            final JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken("NTA0MzYxNTE1MTg5Nzk2ODY0.Dwpffw.j7RDcRbHKa19EUqKB5cUjKosJ28").setAudioEnabled(false).setAutoReconnect(true).addEventListener(new Listeners(), new PollFunction(), new Commands(), new Listener(), discordLogger, new DiscordIssue());
            DiscordBot.jda = jdaBuilder.build().awaitReady();
            DiscordBot.guild = DiscordBot.jda.getGuildById("499666347337449472");
            DiscordBot.commandManager.registerCommand(new CommandPing());
            DiscordBot.commandManager.registerCommand(new CommandBan());
            DiscordBot.commandManager.registerCommand(new CommandKick());
            DiscordBot.commandManager.registerCommand(new CommandMute());
            DiscordBot.commandManager.registerCommand(new CommandMutes());
            DiscordBot.commandManager.registerCommand(new CommandUnmute());
            DiscordBot.commandManager.registerCommand(new CommandUserInfo());
            Thread.sleep(1000L);
            DiscordBot.jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            DiscordBot.jda.getPresence().setGame(Game.playing("Booting - KlarCloudService.de"));
            Thread.sleep(10000L);
            discordLogger.setLogChannel(DiscordBot.jda.getTextChannelById("528162837978152963"));
            final TS3Config ts3Config = new TS3Config();
            ts3Config.setHost("134.255.253.166");
            ts3Config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
            ts3Config.setConnectionHandler(new ConnectionHandler() {
                @Override
                public void onConnect(TS3Api api) {

                }

                @Override
                public void onDisconnect(final TS3Query ts3Query) {
                }
            });
            final TS3Query ts3Query = new TS3Query(ts3Config);
            ts3Query.connect();
            final TS3ApiAsync ts3ApiAsync = ts3Query.getAsyncApi();
            ts3ApiAsync.login("system", "RCgU4Ze0");
            ts3ApiAsync.selectVirtualServerById(1);
            ts3ApiAsync.setNickname("KlarCloudService");
            ts3ApiAsync.registerAllEvents();
            ts3ApiAsync.addTS3Listeners(new TS3Listener() {
                @Override
                public void onTextMessage(final TextMessageEvent textMessageEvent) {
                }

                @Override
                public void onClientJoin(final ClientJoinEvent clientJoinEvent) {
                }

                @Override
                public void onClientLeave(final ClientLeaveEvent clientLeaveEvent) {
                }

                @Override
                public void onServerEdit(final ServerEditedEvent serverEditedEvent) {
                }

                @Override
                public void onChannelEdit(final ChannelEditedEvent channelEditedEvent) {
                }

                @Override
                public void onChannelDescriptionChanged(final ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {
                }

                @Override
                public void onClientMoved(final ClientMovedEvent clientMovedEvent) {
                    if (clientMovedEvent.getTargetChannelId() == 29) {
                        final String channelPassword = nextChannelPassword();
                        ts3ApiAsync.sendPrivateMessage(clientMovedEvent.getClientId(), "Your channel password is: " + channelPassword);
                        ts3ApiAsync.sendPrivateMessage(clientMovedEvent.getClientId(), "Please wait several minutes. A moderator will help you shortly");
                        final String name = ts3ApiAsync.getClientInfo(clientMovedEvent.getClientId()).getUninterruptibly().getNickname();
                        DiscordBot.jda.getTextChannelById("543316921542508544").sendMessage(new EmbedBuilder().setAuthor("KlarCloud - Support", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setTitle("KlarCloudService - Official Discord").setDescription("Der User \"" + name + "\" wartet im Support auf dem Teamspeak").setColor(Color.MAGENTA).build()).queue();
                        final Map<ChannelProperty, String> channelPropertyStringMap = new HashMap<ChannelProperty, String>();
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_FLAG_MAXFAMILYCLIENTS_UNLIMITED, "0");
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_MAXCLIENTS, "5");
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_PASSWORD, channelPassword);
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_DESCRIPTION, "[center][size=15][B]Support Waitingroom[/B][/size]\n[size=10]\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550[/size]\n\n[size=11]Nickname: " + name + "[/size]\n[size=11]Support ID: " + nextID() + "[/size]\n\n[size=13][b]Waiting[/b][/size]\n\n[size=10]\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550[/size]\n[size=9][B]© 2019 - KlarCloudService.de - All Rights Reserved.[/B][/size]\n[size=9][B][url=https://klarcloudservice.de]Website[/url] | [url=https://discord.gg/fwe2CHD]Discord[/url] | [url=https://twitter.com/KlarCloud]Twitter[/url][/B][/size]");
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_CODEC_IS_UNENCRYPTED, "0");
                        channelPropertyStringMap.put(ChannelProperty.CPID, "29");
                        channelPropertyStringMap.put(ChannelProperty.CHANNEL_NEEDED_TALK_POWER, "0");
                        final int id = ts3ApiAsync.createChannel("\u2022 Support \u2022 " + ts3ApiAsync.getClientInfo(clientMovedEvent.getClientId()).getUninterruptibly().getNickname(), channelPropertyStringMap).getUninterruptibly();
                        DiscordBot.supportChannels.add(new SupportInfo(id, clientMovedEvent.getClientId()));
                        ts3ApiAsync.moveClient(clientMovedEvent.getClientId(), id);
                        ts3ApiAsync.moveClient(ts3ApiAsync.whoAmI().getUninterruptibly().getId(), 32);
                        for (final Client client : ts3ApiAsync.getClients().getUninterruptibly()) {
                            if (client.isInServerGroup(23)) {
                                ts3ApiAsync.pokeClient(client.getId(), "Der User " + name + " wartet im Support");
                            }
                        }
                    } else if (hasPermission(ts3ApiAsync.getClientInfo(clientMovedEvent.getClientId()).getUninterruptibly().getServerGroups())) {
                        final SupportInfo supportInfo = getSupportInfo(clientMovedEvent.getTargetChannelId());
                        if (supportInfo != null && ! supportInfo.isWork()) {
                            final Map<ChannelProperty, String> channelPropertyStringMap2 = new HashMap<ChannelProperty, String>();
                            channelPropertyStringMap2.put(ChannelProperty.CHANNEL_MAXCLIENTS, "0");
                            channelPropertyStringMap2.put(ChannelProperty.CHANNEL_DESCRIPTION, "[center][size=15][B]Support Waitingroom[/B][/size]\n[size=10]\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550[/size]\n\n[size=13][b]Working[/b][/size]\n\n[size=10]\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550[/size]\n[size=9][B]© 2019 - KlarCloudService.de - All Rights Reserved.[/B][/size]\n[size=9][B][url=https://klarcloudservice.de]Website[/url] | [url=https://discord.gg/fwe2CHD]Discord[/url] | [url=https://twitter.com/KlarCloud]Twitter[/url][/B][/size]");
                            ts3ApiAsync.editChannel(clientMovedEvent.getTargetChannelId(), channelPropertyStringMap2);
                            supportInfo.setSupID(clientMovedEvent.getClientId());
                            supportInfo.setWork(true);
                            DiscordBot.jda.getTextChannelById("543316921542508544").sendMessage(new EmbedBuilder().setAuthor("KlarCloud - Support", "https://klarcloudservice.de", "https://cdn.discordapp.com/emojis/528123208642199553.png?v=1").setTitle("KlarCloudService - Official Discord").setDescription("Der User \"" + ts3ApiAsync.getClientInfo(supportInfo.getUserID()).getUninterruptibly().getNickname() + "\" wird jetzt von \"" + ts3ApiAsync.getClientInfo(supportInfo.getSupID()).getUninterruptibly().getNickname() + "\" bearbeitet").setColor(Color.BLUE).build()).queue();
                            for (final Client client2 : ts3ApiAsync.getClients().getUninterruptibly()) {
                                if (client2.isInServerGroup(23)) {
                                    ts3ApiAsync.sendPrivateMessage(client2.getId(), "Der User " + ts3ApiAsync.getClientInfo(supportInfo.getUserID()).getUninterruptibly().getNickname() + " wird bearbeitet");
                                }
                            }
                        }
                    }
                }

                @Override
                public void onChannelCreate(final ChannelCreateEvent channelCreateEvent) {
                }

                @Override
                public void onChannelDeleted(final ChannelDeletedEvent channelDeletedEvent) {
                    final SupportInfo supportInfo = getSupportInfo(channelDeletedEvent.getChannelId());
                    if (supportInfo != null) {
                        DiscordBot.supportChannels.remove(supportInfo);
                    }
                }

                @Override
                public void onChannelMoved(final ChannelMovedEvent channelMovedEvent) {
                }

                @Override
                public void onChannelPasswordChanged(final ChannelPasswordChangedEvent channelPasswordChangedEvent) {
                }

                @Override
                public void onPrivilegeKeyUsed(final PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {
                }
            });
            DiscordBot.jda.getPresence().setStatus(OnlineStatus.ONLINE);
            final Thread thread = new Thread(() -> {
                while (true) {
                    DiscordBot.jda.getPresence().setGame(nextGame());
                    try {
                        Thread.sleep(15000L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (Throwable ex2) {
            ex2.printStackTrace();
        }
        System.out.println("Done");
    }

    private static Game nextGame() {
        switch (DiscordBot.random.nextInt(0, 5)) {
            case 1: {
                return Game.playing("with a cloud system");
            }
            case 2: {
                return Game.listening("Teamspeak");
            }
            case 3: {
                return Game.watching("to Fee-Hosting.com");
            }
            case 4: {
                return Game.watching("Discord");
            }
            case 5: {
                return Game.watching("coding tutorials");
            }
            default: {
                return Game.playing("be happy with rexlManu");
            }
        }
    }

    static String getNewestVersion() {
        try {
            final URLConnection urlConnection = new URL("https://dl.klarcloudservice.de/update/internal/version.json").openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            try (final JsonReader jsonReader = new JsonReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
                return new JsonParser().parse(jsonReader).getAsJsonObject().get("version").getAsString();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "null";
        }
    }

    private static boolean hasPermission(final int[] groups) {
        for (final int i : groups) {
            if (i == 23) {
                return true;
            }
        }
        return false;
    }

    private static String nextChannelPassword() {
        return System.currentTimeMillis() + "" + System.nanoTime();
    }

    private static SupportInfo getSupportInfo(final int channelID) {
        for (final SupportInfo supportInfo1 : DiscordBot.supportChannels) {
            if (supportInfo1.getChannelID() == channelID) {
                return supportInfo1;
            }
        }
        return null;
    }

    public static boolean hasPermission(final Permission permission, final Member user) {
        if (user.isOwner()) {
            return true;
        }
        for (final Role role : user.getJDA().getRoles()) {
            if (role.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    private static UUID nextID() {
        return UUID.randomUUID();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (! (o instanceof DiscordBot)) {
            return false;
        }
        final DiscordBot other = (DiscordBot) o;
        return other.canEqual(this);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DiscordBot;
    }

    @Override
    public int hashCode() {
        final int result = 1;
        return result;
    }

    @Override
    public String toString() {
        return "DiscordBot()";
    }

    public static List<JDA> getShards() {
        return DiscordBot.shards;
    }

    public static JDA getJda() {
        return DiscordBot.jda;
    }

    public static CommandManager getCommandManager() {
        return DiscordBot.commandManager;
    }

    public static Guild getGuild() {
        return DiscordBot.guild;
    }

    public static AIDataService getAiDataService() {
        return DiscordBot.aiDataService;
    }

    static {
        DiscordBot.shards = new ArrayList<JDA>();
        commandManager = new CommandManager();
        DiscordBot.supportChannels = new ArrayList<SupportInfo>();
        random = ThreadLocalRandom.current();
        pollManagement = new PollManagement();
    }
}
