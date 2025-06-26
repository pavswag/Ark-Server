package io.kyros.content.update_polls;

import lombok.Data;

@Data
public class PlayerUpdateVote {
    private String playerName;
    private VotingState voteState;

    public PlayerUpdateVote(String playerName, VotingState voteState) {
        this.playerName = playerName;
        this.voteState = voteState;
    }
}
