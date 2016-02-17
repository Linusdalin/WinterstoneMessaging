package campaigns;

/**********************************************************************************
 *
 *              A campaign can be in different states.
 *              This defines how it will be executed
 *
 *
 */
public enum CampaignState {

    ACTIVE, REDUCED, INACTIVE, TEST_MODE;

    public boolean isLive() {
        return this == ACTIVE || this == REDUCED;
    }

    public boolean isTestMode() {
        return this == TEST_MODE;
    }

}
