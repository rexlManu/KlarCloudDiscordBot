package de.klarcloudservice.internal.poll;

import net.dv8tion.jda.core.entities.Member;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class RunningPoll implements Serializable {

    private static final long serialVersionUID = - 3101186244297763853L;
    private UUID pollUID;
    private long messageID;
    private List<Member> pollAnswers;

    public UUID getPollUID() {
        return this.pollUID;
    }

    public long getMessageID() {
        return this.messageID;
    }

    public List<Member> getPollAnswers() {
        return this.pollAnswers;
    }

    public RunningPoll(final UUID pollUID, final long messageID, final List<Member> pollAnswers) {
        this.pollUID = pollUID;
        this.messageID = messageID;
        this.pollAnswers = pollAnswers;
    }
}
