package io.kyros.content.donationcampaign;

import io.kyros.model.items.GameItem;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationCampaignPot {
    private int amountRequired;
    private GameItem[] prize;
}
