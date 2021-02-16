package ec.richardnarvaez.chatf.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import ec.richardnarvaez.chatf.R;

public class VideoCallActivity extends AppCompatActivity {
    private MediaPlayer callingMediaPlayer;
    private MediaPlayer incomingCallMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

// to wake up screen
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAGCALL");
        wakeLock.acquire();

// to release screen lock
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();


        // For Incoming Call
// 1. declare
//// .2 in onCreate, if I'm the person that's calling, ring the phone
//        incomingCallMediaPlayer = MediaPlayer.create(this, R.raw.incoming);
//        incomingCallMediaPlayer.setLooping(true);
//        incomingCallMediaPlayer.start();
//// 3. when I pick up, stop the player
//        incomingCallMediaPlayer.stop();

// I play R.raw.incoming if I'm being called.
// I play R.raw.outgoing when I'm calling.
// I understand if I'm the one calling from the number of participants in the "room" (this is a video call terminology) and by passing in a variable through the intent

// For Outgoing Call
// 1. declare

//// 2. in onCreate, if I'm being called, ring the phone
//        callingMediaPlayer = MediaPlayer.create(this, R.raw.outgoing);
//        callingMediaPlayer.setLooping(true);
//        callingMediaPlayer.start();
//// 3. when another "participant" (this is a video call terminology) joins the "room" I stop playing the sound
//        callingMediaPlayer.stop();

    }
}