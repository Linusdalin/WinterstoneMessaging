package test;


import org.junit.Test;
import sound.SoundPlayer;

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
        player.playSound(SoundPlayer.FailBeep);


    }

    @Test
    public void successTest(){

        SoundPlayer player = new SoundPlayer();
        player.playSound(SoundPlayer.ReadyBeep);


    }

}
