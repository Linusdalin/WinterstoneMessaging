package sound;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.tools.jar.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-29
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public class SoundPlayer {

    private static final String SoundDir = "C:\\Users\\Linus\\Documents\\GitHub\\WinterstoneMessaging\\";

    public static final String ReadyBeep = "beep-01a.wav";


    public synchronized void playSound(final String soundFile) {

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


    public synchronized void playSoundOld(final String soundFile) {

        new Thread(new Runnable() {
      // The wrapper thread is unnecessary, unless it blocks on the
      // Clip finishing; see comments.
        public void run() {
          try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream(SoundDir + soundFile));

              System.out.println("Opening file " + SoundDir + soundFile);
            clip.open(inputStream);
            clip.start();
          } catch (Exception e) {
            System.err.println(e.getMessage());
          }
        }
      }).start();
    }


}
