package test;


import campaigns.*;
import email.EmailInterface;
import org.junit.Test;
import output.DeliveryException;
import output.EmailHandler;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;
import rewards.RewardRepository;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  Sending a test email
 *
 *
 */

public class SendAllEmail {

    private static final User user       = new User("627716024", "627716024", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male", Timestamp.valueOf("2016-01-01 00:00:00"));

    private static final Payment payment = new Payment("627716024", 30, "", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 0, 0, 0);

    //Statically construct all mails

    private static final EmailInterface[] all = {

            FirstPaymentCampaign.firstDepositEmail(user, payment, "test-1"),

            GettingStartedCampaign.gettingStartedEmail1(user, "test-1"),
            GettingStartedCampaign.gettingStartedEmail2(user, "test-1"),

            ChurnPokeCampaign.churnPokeEmail(user, "test-1"),

            MobilePokeNotification.getMail1(user),
            MobilePokeNotification.getMail2(user),
            MobilePokeNotification.getMail3(user),
            MobilePokeNotification.getMail4(user),
            MobilePokeNotification.getMail5(user),
            MobilePokeNotification.getMail6(user),
            MobilePokeNotification.getMail7(user, "test-1"),

            CoinsLeftCampaign.coinLeftEmail(user, "test-1"),

            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[0], 10, "dummy"),
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[1], 20, "dummy") ,
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[6], 23, "dummy"),
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[7], 25, "dummy"),
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[2], 48, "dummy"),
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[3], 97, "dummy"),
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[4], 148, "dummy"),
            LevelUpCampaign.getLevelUpEmail(LevelUpCampaign.messages[5], 198, "dummy"),

            MysteryMondayCampaign.mysteryMondayEmail(user, RewardRepository.mysteryMonday1),
            MysteryMondayMobileCampaign.mysteryMondayEmail(user, RewardRepository.mysteryMonday1),

            TryNewGameOS2345Campaign.gameActivationEmail(user, RewardRepository.OS2345High, "test-1"),
            TryNewGameMobileClockworkCampaign.gameActivationEmail(user, RewardRepository.M_ClockworkFrequent, "test-1"),

            MobileCrossPromotionCampaign.tryMobile2(user, "test-1", RewardRepository.mobilePaying),
            ReactivationEmailCampaign.comebackEmail(user, 1000, "test-1"),

            TournamentLaunchCampaign.tournamentLaunchEmail(user, "test-1")

    };


    private static final EmailInterface[] test = {

            TournamentLaunchCampaign.tournamentLaunchEmail(user, "test-1")

    };



    @Test
    public void sendAll(){

        try{

            boolean success;
            EmailHandler handler;

            for (EmailInterface email : test) {

                handler = new EmailHandler()
                        .withEmail(email)
                        .toRecipient(user.id);

                success = handler.send();

                assertThat("Should work", success, is(true) );

            }


            }catch(DeliveryException e){

                assertTrue(false);
            }

        }

}