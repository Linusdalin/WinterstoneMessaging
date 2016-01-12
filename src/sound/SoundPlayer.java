package sound;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.InputStream;

/*****************************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-29
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */

public class SoundPlayer {

    private static final String SoundDir = "C:\\Users\\Linus\\Documents\\GitHub\\WinterstoneMessaging\\";

    public static final String ReadyBeep    = "fanfare.wav";
    public static final String FailBeep     = "beep-01a.wav";


    public static synchronized void playSound(final String soundFile) {

          try {

              System.out.println("Opening file " + SoundDir + soundFile);
              InputStream is = new FileInputStream(SoundDir + soundFile);

              // create an audiostream from the inputstream
              AudioStream audioStream = new AudioStream(is);

              // play the audio clip with the audioplayer class
              AudioPlayer.player.start(audioStream);

          } catch (Exception e) {

                e.printStackTrace();
          }

    }



}
