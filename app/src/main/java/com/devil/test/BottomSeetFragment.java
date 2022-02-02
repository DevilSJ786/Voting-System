package com.devil.test;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.devil.test.Util.PollApi;
import com.devil.test.model.Polls;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class BottomSeetFragment extends BottomSheetDialogFragment {
    private EditText pollName, op1, op2, op3;
    private String poll, ops1, ops2, ops3;
    private Button publishButton;
    private Polls polls;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference_poll = db.collection("Polls");


    public BottomSeetFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        pollName = view.findViewById(R.id.poll_name);
        op1 = view.findViewById(R.id.option1_et);
        op2 = view.findViewById(R.id.option2_et);
        op3 = view.findViewById(R.id.option3_et);
        publishButton = view.findViewById(R.id.publish_poll);
        return view;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PollApi pollApi = PollApi.getInstance();
        polls = new Polls();
        publishButton.setOnClickListener(v -> {
            poll = pollName.getText().toString().trim();
            ops1 = op1.getText().toString().trim();
            ops2 = op2.getText().toString().trim();
            ops3 = op3.getText().toString().trim();

            if (!TextUtils.isEmpty(poll) && !TextUtils.isEmpty(ops1) && !TextUtils.isEmpty(ops2) && !TextUtils.isEmpty(ops3)) {
                polls.setUserId(pollApi.getUserId());
                polls.setTitle(poll);
                polls.setChoice1(ops1);
                polls.setChoice2(ops2);
                polls.setChoice3(ops3);
                polls.setVote1(0);
                polls.setVote2(0);
                polls.setVote3(0);
                polls.setTimeAdded(new Timestamp(new Date()));

                String  s=polls.getUserId()+polls.getTimeAdded().getSeconds();
                DocumentReference documentRef=db.collection("Polls").document(s);
                documentRef.set(polls).addOnSuccessListener(unused -> {
                    Snackbar.make(publishButton, R.string.success, Snackbar.LENGTH_SHORT).show();
                    if (BottomSeetFragment.this.isVisible()) {
                        pollName.setText("");
                        op1.setText("");
                        op2.setText("");
                        op3.setText("");
                        BottomSeetFragment.this.dismiss();
                    }

                }).addOnFailureListener(e -> Log.d("jat", "fail: poll not save " + e.getMessage()));
            } else {
                Toast.makeText(requireContext(), "enter all detail", Toast.LENGTH_SHORT).show();
            }

        });
    }
}