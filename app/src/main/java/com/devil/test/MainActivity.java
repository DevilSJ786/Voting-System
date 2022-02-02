package com.devil.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devil.test.Util.PollApi;
import com.devil.test.model.Polls;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private BottomSeetFragment bottomSeetFragment;
    public FirebaseUser onlineUser;
    public String userId;
    public String v1 = "vote1";
    public String v2 = "vote2";
    public String v3 = "vote3";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference_poll = db.collection("Polls");
    FirestoreRecyclerAdapter<Polls, PollViewHolder> firestoreRecyclerAdapter;


    @Override
    protected void onStart() {
        super.onStart();
        onlineUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private int getRendomcolor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.p1);
        colorcode.add(R.color.p2);
        colorcode.add(R.color.p3);
        colorcode.add(R.color.p4);
        colorcode.add(R.color.p5);
        colorcode.add(R.color.p6);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("all");



        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        onlineUser = firebaseAuth.getCurrentUser();

        Query query = collectionReference_poll.orderBy("timeAdded", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Polls> allusersPoll = new FirestoreRecyclerOptions.Builder<Polls>()
                .setQuery(query, Polls.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Polls, PollViewHolder>(allusersPoll) {
            @Override
            protected void onBindViewHolder(@NonNull PollViewHolder holder, int position, @NonNull Polls polls) {
                String timeAgo = (String) DateUtils.getRelativeTimeSpanString(polls.getTimeAdded().getSeconds() * 1000);
                holder.poll.setText(polls.getTitle());
                holder.op1.setText(polls.getChoice1());
                holder.op2.setText(polls.getChoice2());
                holder.op3.setText(polls.getChoice3());
                holder.rtime.setText(timeAgo);
                int colorCode = getRendomcolor();

                holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));
                holder.op1.setTextColor(holder.itemView.getResources().getColor(colorCode, null));
                holder.op2.setTextColor(holder.itemView.getResources().getColor(colorCode, null));
                holder.op3.setTextColor(holder.itemView.getResources().getColor(colorCode, null));
                String docId = firestoreRecyclerAdapter.getSnapshots().getSnapshot(position).getId();

                holder.op1.setOnClickListener(v -> {
                    String s = onlineUser.getUid() + polls.getTimeAdded().getSeconds();
                    Log.d("sj", "online:" + s + " docId:" + docId);
                    int vote = polls.getVote1();
                    vote++;
                    vote(polls, docId, vote, v1);
                });
                holder.op2.setOnClickListener(v -> {
                    int vote = polls.getVote2();
                    vote++;
                    vote(polls, docId, vote, v2);
                });
                holder.op3.setOnClickListener(v -> {
                    int vote = polls.getVote3();
                    vote++;
                    vote(polls, docId, vote, v3);
                });
                holder.cardView.setOnClickListener(v -> {

                    String s = onlineUser.getUid() + polls.getTimeAdded().getSeconds();
                    if (s.equals(docId)) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Result").setOnMenuItemClickListener(item -> {
                            Intent intent = new Intent(v.getContext(), ShowPoll.class);
                            intent.putExtra("docId", docId);
                            intent.putExtra(v1, polls.getVote1());
                            intent.putExtra(v2, polls.getVote2());
                            intent.putExtra(v3, polls.getVote3());
                            intent.putExtra("userId", polls.getUserId());
                            intent.putExtra("title", polls.getTitle());
                            intent.putExtra("choice1", polls.getChoice1());
                            intent.putExtra("choice2", polls.getChoice2());
                            intent.putExtra("choice3", polls.getChoice3());
                            startActivity(intent);
                            return false;
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_row, parent, false);

                return new PollViewHolder(view);
            }
        };
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firestoreRecyclerAdapter);


        authStateListener = firebaseAuth -> {
            onlineUser = firebaseAuth.getCurrentUser();
            if (onlineUser != null) {
                //user log in
                userId = onlineUser.getUid();
                PollApi pollApi = PollApi.getInstance();
                pollApi.setUserId(userId);
            } else {
                login();
            }
        };

        bottomSeetFragment = new BottomSeetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        SwipeRefreshLayout swipeRefreshLayout =findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showBottomSheetDialog());

    }

    private void vote(@NonNull Polls polls, String docId, int vote, String str) {
        String s = onlineUser.getUid() + polls.getTimeAdded().getSeconds();

        if (!s.equals(docId)) { //check same user not voting
            DocumentReference voterRef = db.collection("Users").document(onlineUser.getUid());
            voterRef.get().addOnCompleteListener(task -> {
                Boolean vot = task.getResult().getBoolean(docId);

                if (vot == null || vot) {
                    //vote here
                    Log.d("jaat", "voted is done" + vot + docId);
                    DocumentReference documentRef = db.collection("Polls").document(docId);
                    documentRef.update(str, vote).addOnSuccessListener(unused -> voterRef.update(docId, false).addOnSuccessListener(unused1 -> Toast.makeText(MainActivity.this, "Vote done", Toast.LENGTH_SHORT).show()));
                } else {
                    Log.d("jaat", "vot:pre voted ");
                    Toast.makeText(MainActivity.this, "Already Voted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Log.d("jaat", "fail:not exists " + e.getMessage()));

        } else {
            Toast.makeText(MainActivity.this, "You can't vote", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void showBottomSheetDialog() {
        bottomSeetFragment.show(getSupportFragmentManager(), bottomSeetFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            login();
            return true;
        }
        if (id==R.id.about){
            Snackbar.make(getWindow().getDecorView(),R.string.dev_by,Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PollViewHolder extends RecyclerView.ViewHolder {
        TextView poll, op1, op2, op3, rtime;
        CardView cardView;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            poll = itemView.findViewById(R.id.row_poll);
            op1 = itemView.findViewById(R.id.row_op1);
            op2 = itemView.findViewById(R.id.row_op2);
            op3 = itemView.findViewById(R.id.row_op3);
            rtime = itemView.findViewById(R.id.row_time);
        }
    }

}