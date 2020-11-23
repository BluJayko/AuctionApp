package com.learning.biddingswipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private TextView username;

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;

    private ProgressBar progressBar;



    private Button btnEnterBid;
    private Dialog MyDialog;
    private Handler mHandler;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = findViewById(R.id.Username);
        username.setText(fbAuth.getCurrentUser().getEmail());



        mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(com.learning.biddingswipe.HomeActivity.this, HomeActivity.class));

            }
        }, 20000);



        //Connect the android devices to variables
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");


        mDrawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler_view_home);
        progressBar = findViewById(R.id.progress_bar_home);

        //Start enabling them. and Settings
        navigationView.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));






    }


    //Use this to add Highest Bid along with USERNAME LINKED TO IT
    public void AlertDialog(final PostViewHolder holder){
        MyDialog = new Dialog(HomeActivity.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.bid_dialog);
        MyDialog.setTitle("Bid");

        btnEnterBid = MyDialog.findViewById(R.id.btnEnterBidDialog);
        final EditText editEnterBid = MyDialog.findViewById(R.id.editEnterBid);

        btnEnterBid.setEnabled(true);

        btnEnterBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bidAmount = editEnterBid.getText().toString().trim();
                holder.didBid = true;
                System.out.println(holder.bidAmount+"="+holder.highestBid.getText().toString());
                System.out.println(Integer.parseInt(holder.bidAmount) + " = " + Integer.parseInt(holder.highestBid.getText().toString()));
                if( Integer.parseInt(holder.bidAmount) > Integer.parseInt(holder.highestBid.getText().toString()))
                {
                    holder.highestBid.setText(holder.bidAmount);


                    String temp = holder.highestBid.getText().toString();

                    System.out.println("value:"+holder.key);

                    String key = holder.key;
                    mDatabase.child(key+"/bidAmount").setValue(temp);
                    mDatabase.child(key+"/highestBidder").setValue(fbAuth.getCurrentUser().getEmail());

                }
                Toast.makeText(getApplicationContext(), "Your Bid is: " + holder.bidAmount, Toast.LENGTH_SHORT).show();
                MyDialog.dismiss();
            }
        });

        MyDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        recyclerView.setVisibility(View.GONE);

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(mDatabase, Post.class)
                .build();




        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull Post model) {
                holder.itemName.setText(model.getItemName());
                holder.itemDesc.setText(model.getDescription());
                holder.baseBid.setText("Base Bid: Rs." + model.getBaseBid());
                holder.endTime.setText("End Time: " + model.getEndTime());
                holder.highestBid.setText(model.getBidAmount());
                holder.highestBidder.setText(model.getHighestBidder());
                holder.key = model.getKey();


                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                String currentDateTimeString = simpleDateFormat.format(new Date());
                System.out.println(model.getEndTime().substring(0,2)+":"+model.getEndTime().substring(3,5));
                System.out.println(currentDateTimeString.substring(0,2)+":"+currentDateTimeString.substring(3,5));

                String time1 = model.getEndTime().substring(0,2)+""+model.getEndTime().substring(3,5);
                String time2 = currentDateTimeString.substring(0,2)+""+currentDateTimeString.substring(3,5);
                if(!model.getEndTime().isEmpty()) {
                    if (model.getEndTime().equals(currentDateTimeString) || Integer.parseInt(time1) < Integer.parseInt(time2)) {
                        System.out.println("Remove Item from database and give the item to winner");
                        String theName = model.getItemName();
                        Toast.makeText(getApplicationContext(), "The winner is user: " + holder.highestBidder.getText().toString(), Toast.LENGTH_SHORT).show();
                        if (holder.highestBidder.getText().toString().equals(fbAuth.getCurrentUser().getEmail())) {
                            Toast.makeText(getApplicationContext(), "YOU WON!!!", Toast.LENGTH_SHORT).show();
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("text/plain");
                            System.out.println(fbAuth.getCurrentUser().getEmail());
                            emailIntent.putExtra(Intent.EXTRA_EMAIL  , fbAuth.getCurrentUser().getEmail());
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "You won: "+holder.itemName.getText().toString());
                            emailIntent.putExtra(Intent.EXTRA_TEXT   , "Item Desc:"+holder.itemDesc.getText().toString()+"\n Bought At: "+holder.highestBid.getText().toString()+"\n Sent to -> "+holder.highestBidder.getText().toString()+"\n CONGRATULATIONS!!!");
                            try {
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                finish();
                                Log.i("Finished sending email...", "");
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(HomeActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        mDatabase.child(holder.key).removeValue();
                        Toast.makeText(getApplicationContext(), "BIDDING FOR THIS ITEM IS OVER!", Toast.LENGTH_SHORT).show();


                    } else {
                        System.out.println("Not yet");
                    }
                }


                Picasso.get().load(model.getImageUrl()).into(holder.imageView);

                holder.bidBn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!holder.didBid)
                            AlertDialog(holder);
                        else
                            Toast.makeText(getApplicationContext(), "Your Bid is: " + holder.bidAmount, Toast.LENGTH_LONG).show();
                    }
                });

            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_post, viewGroup, false);

                return new PostViewHolder(view);
            }
        };







        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();

    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemDesc;
        TextView baseBid;
        TextView endTime;
        ImageView imageView;
        TextView highestBidder;
        TextView highestBid;
        String key = "";
        Button bidBn;
        boolean didBid;
        String bidAmount;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemDesc = itemView.findViewById(R.id.description);
            baseBid = itemView.findViewById(R.id.base_bid);
            endTime = itemView.findViewById(R.id.end_time);
            imageView = itemView.findViewById(R.id.image_view);
            highestBid = itemView.findViewById(R.id.highest_bid);
            highestBidder = itemView.findViewById(R.id.highest_bidder);


            bidBn = itemView.findViewById(R.id.btnEnterBid);
            didBid = false;
            bidAmount = "";
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();



        if (id == R.id.add) {

            mHandler.removeCallbacksAndMessages(null);
            Intent AddPostActivity = new Intent(HomeActivity.this, AddPostActivity.class);
            startActivity(AddPostActivity);

        } else if (id == R.id.logout) {

            FirebaseAuth.getInstance().signOut();
            mHandler.removeCallbacksAndMessages(null);
            Intent loginActivity = new Intent(HomeActivity.this, LogInActivity.class);
            startActivity(loginActivity);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}