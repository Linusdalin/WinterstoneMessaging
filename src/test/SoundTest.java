package test;


import campaigns.FirstPaymentCampaign;
import campaigns.GettingStartedCampaign;
import email.NotificationEmail;
import email.ReleaseEmail;
import org.junit.Test;
import output.DeliveryException;
import output.EmailHandler;
import remoteData.dataObjects.Payment;
import remoteData.dataObjects.User;
import sound.SoundPlayer;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  Sending a beep
 *
 *
 */

public class SoundTest {


    @Test
    public void beepTest(){

        SoundPlayer player = new SoundPlayer();
        player.playSound(SoundPlayer.ReadyBeep);


    }


}
