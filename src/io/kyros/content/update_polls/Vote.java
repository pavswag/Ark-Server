package io.kyros.content.update_polls;

import lombok.Data;

@Data
public class Vote {
    private int pollId;
    private String playerName;
    private boolean vote; // true for yes, false for no
}
