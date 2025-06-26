package io.kyros.content.donationcampaign;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class DonationCampaignData {
    private List<DonationCampaignPot> donationCampaignPots;
}
