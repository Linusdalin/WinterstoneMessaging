package campaigns;

/**********************************************************************************
 *
 *              A campaign can be in different states.
 *              This defines how it will be executed
 *
 *
 */
public enum CampaignState {

    ACTIVE, INACTIVE, TEST_NODE;

    public boolean isLive() {
        return this == ACTIVE;
    }

    public boolean isTestMode() {
        return this == TEST_NODE;
    }

}
