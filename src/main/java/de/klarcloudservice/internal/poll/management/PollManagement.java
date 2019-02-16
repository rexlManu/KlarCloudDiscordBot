package de.klarcloudservice.internal.poll.management;

import com.google.gson.reflect.TypeToken;
import de.klarcloudservice.internal.poll.RunningPoll;
import de.klarcloudservice.internal.utility.Configuration;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PollManagement {
    private Map<UUID, RunningPoll> list;
    private Map<Long, UUID> uuidMap;

    public PollManagement() {
        this.list = new ConcurrentHashMap<>();
        this.uuidMap = new ConcurrentHashMap<>();
    }

    public void load() {
        if (! Files.exists(Paths.get("polls.json"))) {
            new Configuration().addProperty("polls", Collections.emptyList()).saveAsConfigurationFile(Paths.get("polls.json"));
        }
        final List<RunningPoll> polls = Configuration.loadConfiguration(Paths.get("polls.json")).getValue("polls", new TypeToken<List<RunningPoll>>() {}.getType());
        if (polls != null) {
            polls.forEach(e -> {
                this.list.put(e.getPollUID(), e);
                this.uuidMap.put(e.getMessageID(), e.getPollUID());
            });
        }
    }

    public void createPoll(final RunningPoll runningPoll) {
        this.list.put(runningPoll.getPollUID(), runningPoll);
        this.uuidMap.put(runningPoll.getMessageID(), runningPoll.getPollUID());
        Configuration.loadConfiguration(Paths.get("polls.json")).addProperty("polls", this.list.values()).saveAsConfigurationFile(Paths.get("polls.json"));
    }

    public void deletePoll(final RunningPoll runningPoll) {
        this.list.remove(runningPoll.getPollUID());
        this.uuidMap.remove(runningPoll.getMessageID());
        Configuration.loadConfiguration(Paths.get("polls.json")).addProperty("polls", this.list.values()).saveAsConfigurationFile(Paths.get("polls.json"));
    }

}
