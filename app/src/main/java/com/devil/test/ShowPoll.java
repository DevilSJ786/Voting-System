package com.devil.test;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowPoll extends AppCompatActivity {
    private TextView title, ch1, ch2, ch3, v1, v2, v3;
    private Button showButton;
    private Bundle b;
    private int win_1, win_2, win_3;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference_poll = db.collection("Polls");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_poll);

        title = findViewById(R.id.s_title);
        ch1 = findViewById(R.id.s_Choice1);
        ch2 = findViewById(R.id.s_Choice2);
        ch3 = findViewById(R.id.s_Choice3);
        v1 = findViewById(R.id.s_vote1);
        v2 = findViewById(R.id.s_vote2);
        v3 = findViewById(R.id.s_vote3);
        showButton = findViewById(R.id.s_rusultButton);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        b = getIntent().getExtras();
        if (b != null) {
            title.setText(b.getString("title"));
            ch1.setText(b.getString("choice1"));
            ch2.setText(b.getString("choice2"));
            ch3.setText(b.getString("choice3"));
            v1.setText(String.valueOf(b.getInt("vote1")));
            v2.setText(String.valueOf(b.getInt("vote2")));
            v3.setText(String.valueOf(b.getInt("vote3")));
            win_1 = b.getInt("vote1");
            win_2 = b.getInt("vote2");
            win_3 = b.getInt("vote3");
        }

        showButton.setOnClickListener(v -> {
            showButton.startAnimation(animation);
            if (win_1 > win_2 && win_1 > win_3) {
                send_notification(b.getString("choice1"));
            } else if (win_2 > win_1 && win_2 > win_3) {
                send_notification(b.getString("choice2"));
            } else if (win_3 > win_1 && win_3 > win_2) {
                send_notification(b.getString("choice3"));
            }
            else {
                send_notification("Draw Poll");
            }
            deletePoll();
        });

    }

    private void send_notification(String winer) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",
                b.getString("title").trim(), winer, getApplicationContext(), ShowPoll.this);
        notificationsSender.SendNotifications();
    }

    private void deletePoll() {
        collectionReference_poll.document(b.getString("docId")).delete();
        finish();
    }
}