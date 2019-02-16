package de.klarcloudservice.internal.poll;

import lombok.Data;

import java.io.Serializable;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

@Data
class PollSetup implements Serializable {

    private static final long serialVersionUID = 5014701625932721608L;
    private String question;
    private Deque<String> answers;
    private int size;
    private boolean channel;
    private long channelID;
    private long userID;

    PollSetup(final String question, final long userID) {
        this.answers = new LinkedBlockingDeque<String>();
        this.question = question;
        this.userID = userID;
    }
}
