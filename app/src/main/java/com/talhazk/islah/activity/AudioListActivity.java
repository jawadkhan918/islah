package com.talhazk.islah.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.talhazk.islah.R;
import com.talhazk.islah.app.Config;
import com.talhazk.islah.model.Ayats;
import com.talhazk.islah.utils.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Talhazk on 07-Mar-16.
 */

public class AudioListActivity extends AppCompatActivity {

    //  public  ayats = new ArrayList<Ayats>();
    private ProgressDialog pDialog;
    private List<Ayats> ayats;
    String jsonResponse;
    RequestQueue requestQueue;
    MyRecyclerAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ImageView mainImage;
    public ImageButton play;
    TextView catName;
    int catId;
    String catImage;
    private String mMsg = "Try Again.";
    int position;
    MediaPlayer mediaPlayer = null;
    //start
    private MediaPlayer mPlayer = new MediaPlayer();

    // Listview

    // Flags to take Decisions
    private Boolean isPlay = true;
    private boolean playFlag = false;

    // Row Positions
    private int pos;
    private int latestPos = -1;

    // Row view
    private View latestView;
    private View view;

    // Audio Play Pos
    private static int playPos = -1;
//end
private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayats_list);
        requestQueue = Volley.newRequestQueue(this);
        /*if(ayats.isEmpty())
            getCategoryList();
*/


        ayats = new ArrayList<>();
        this.mainImage = (ImageView) findViewById(R.id.headerImage);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);


        catName = (TextView) findViewById(R.id.catName);
        Intent i = getIntent();
        position = i.getExtras().getInt("position");
        catName.setText(MainActivity.categoryItems.get(position).getName());
        catId = MainActivity.categoryItems.get(position).getId();
        catImage = MainActivity.categoryItems.get(position).getImage();
        Log.i("image url is.", catImage);
        Log.i("pos", "" + position);
        Log.i("sda", "" + MainActivity.categoryItems.get(position));
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Picasso
                .with(getApplicationContext())
                .load(catImage)
                .fit() // will explain later
                .placeholder(getApplicationContext().getResources().getDrawable(R.drawable.default_img))
                .error(getApplicationContext().getResources().getDrawable(R.drawable.default_img))
                .into(mainImage);


        makeStringReq();


    }


    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
        Log.e("dialog is invoked", "true");

    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
        Log.e("dialog not invoked", "false");

    }

    private void makeStringReq() {
        showProgressDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.AYAT_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("response", response.toString());
                hideProgressDialog();
                parseData(response);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(AudioListActivity.this, "Try again!! ", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Config.CAT_ID, "" + catId);

                return params;
            }

        };
        ;

        // Adding request to request queue
        // AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(strReq);

    }

    private void parseData(String response) {


        int status = -1;
        JSONObject obj = null;
        JSONArray array = null;
        try {
            array = new JSONArray(response);
            //status = obj.getInt(Config.API_STATUS);

          /*  if (status == 0) {

               // array = obj.getJSONArray(Config.JASON_ARR);
            } else {

                mMsg = obj.getString(Config.API_MSG);
            //    Toast.makeText(getActivity(),mMsg, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (status == 0) {*/
            for (int i = 0; i < array.length(); i++) {
                //  Restaurant superHero = new SuperHeroes();
                JSONObject json = null;


                try {
                    json = array.getJSONObject(i);

                    int ayatStatus = -1;
                    int ayatId = -1;
                    String ayatTitle = "";
                    String islahAudio = "";
                    String ayatNo = "";
                    String splitter = "";

                    ayatStatus = Integer.parseInt(json.getString(Config.AYAT_STATUS));
                    ayatId = Integer.parseInt(json.getString(Config.AYAT_ID));

                    splitter = json.getString(Config.AYAT_TITLE);

                    islahAudio = json.getString(Config.ISLAH_AUDIO);

                    String[] parts = splitter.split("Ayat");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556

                    ayatTitle = part1;
                    ayatNo = "Ayat " + part2;

                    Ayats ayatList = new Ayats(ayatStatus, ayatId, ayatTitle, ayatNo, islahAudio);
                    ayats.add(ayatList);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                // }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Finally initializing our adapter
        //    adapter = new OrderHistoryAdapter(listOrderHistory, getActivity());

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
        adapter = new MyRecyclerAdapter(ayats);
        recyclerView.setAdapter(adapter);


    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //    private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 0;


        java.util.List listItems;

        public MyRecyclerAdapter(java.util.List listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ayats_list_item, parent, false);
                return new VHItem(v);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Ayats getItem(int position) {

            return ayats.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Ayats currentItem = getItem(position);
            VHItem VHitem = (VHItem) holder;
            VHitem.ayatTitle.setText(currentItem.getAyatTitle());
            VHitem.ayatNo.setText(currentItem.getAyatNo());

            // StudentList currentItem = getItem(position);
            // VHItem VHitem = (VHItem) holder;
            //    tempPath=currentItem.getPath();
            // VHitem.txtName.setText(currentItem.getUserName());
            //   VHitem.iv.setImageBitmap(camera.loadImageFromStorage(currentItem.getPath(),currentItem.getUserName()));


        }

        //    need to override this method
        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }

        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }

        class VHItem extends RecyclerView.ViewHolder {
            TextView ayatTitle;
            TextView ayatNo;
            public ImageButton play;
            public ImageButton whatsappBtn;
            public ImageButton fbBtn;


            public VHItem(final View itemView) {
                super(itemView);

                this.ayatNo = (TextView) itemView.findViewById(R.id.ayatNo);
                this.ayatTitle = (TextView) itemView.findViewById(R.id.titleName);
                this.play = (ImageButton) itemView.findViewById(R.id.playButton);
                this.whatsappBtn = (ImageButton) itemView.findViewById(R.id.whatsappBtn);
                this.fbBtn= (ImageButton)itemView.findViewById(R.id.fbBtn);

                //itemView.setClickable(true);
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  switch (play.getId()) {
                        //case R.id.playButton:
                        play.setBackgroundResource(R.drawable.button_pause);
                        int pos = getAdapterPosition();
                        Toast.makeText(v.getContext(), "clickable " + pos, Toast.LENGTH_SHORT).show();
                        playAyat(itemView, pos);
             //yahan play aayat ki jagah play audio laga...for 1sst ayat only
                        //    break;
            //    playAudio(itemView,0);
                        //}
                    }
                });

                whatsappBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  switch (play.getId()) {
                        //case R.id.playButton:
                        whatsappShare(pos);

                        whatsappBtn.setBackgroundResource(R.drawable.button_pause);
                        int pos = getAdapterPosition();
                        Toast.makeText(v.getContext(), "clicker " + pos, Toast.LENGTH_SHORT).show();
                        //     playAyat(itemView, pos);


                    }
                });

                fbBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  switch (play.getId()) {
                        //case R.id.playButton:
                      //  playAudio(pos);

                        fbBtn.setBackgroundResource(R.drawable.button_pause);
                        int pos = getAdapterPosition();
                        Toast.makeText(v.getContext(), "clicker " + pos, Toast.LENGTH_SHORT).show();
                        //     playAyat(itemView, pos);
                        playing(pos);

                    }
                });
            }
        }
    }

    private void whatsappShare(int pos) {
        PackageManager pm = getPackageManager();
        String audioId = ayats.get(pos).getIslahAudio();

//        String completePath = new CopyFile().destinationPath(fName);
  //      File file = new File(completePath);

     String fName=   this.getDir("audio",
                Context.MODE_PRIVATE).getAbsolutePath()
                + "/"
                + ayats.get(pos).getIslahAudio() + ".mp3";

//        String completePath = new CopyFile().destinationPath(fName);
  //      File file = new File(completePath);
    //    Uri uri = Uri.fromFile(file);
        String mimeType = "audio/mpeg";
/*
        Intent intent = new Intent(Intent.ACTION_SEND);
        List<String> whitelist = Arrays.asList("com.whatsapp",
                "com.facebook.orca", "com.viber.voip", "tencent.mm");
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent = AppChooser.create(pm, intent, "Share", whitelist);

        Uri uri = Uri.parse("file:///data/data/com.talhazk.islah/app_audio/"+ audioId +".mp3");
        String mimeType = "audio/mp3";*/

        Intent intent = new Intent(Intent.ACTION_SEND);
        List<String> whitelist = Arrays.asList("com.whatsapp",
                "com.facebook.orca", "com.viber.voip", "tencent.mm");
        intent.setType(mimeType);
      //  intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setPackage("com.whatsapp");
        // intent.putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION);
        // intent.putExtra(EXTRA_APP_ID, YOUR_APP_ID);
        //intent = AppChooser.create(pm, intent, "Share", whitelist);
        this.startActivityForResult(intent, SHARE_TO_MESSENGER_REQUEST_CODE);
     /*   String audioId = ayats.get(pos).getIslahAudio();
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setPackage("com.whatsapp");
        shareIntent.setType("audio/mpeg");
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file:///data/data/com.talhazk.islah/app_audio/"+audioId+".mp3"));
        startActivity(Intent.createChooser(shareIntent, "Listen"));*/
    }

    private void playAyat(View view, int pos) {
        String audioId = ayats.get(pos).getIslahAudio();
        String arg[] = new String[2];

        arg[1] = audioId;
        arg[0] = Config.AUDIO_URL + audioId + ".mp3";
        new com.talhazk.islah.httpcontrol.HttpAudio(this, "")
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);
        //playit(pos);

       // playAudio(view, pos);

        /*mediaPlayer = new MediaPlayer();
        String file_path = Config.AUDIO_URL+audioId+".mp3";
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(file_path);
            mediaPlayer.prepare();
            mediaPlayer.start();
          // play.setBackgroundResource(R.drawable.button_pause);
        }catch (IOException e){
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                mediaPlayer=null;
            //    btn.setEnabled(true);
            }
        });*/

    }

    public void playAudio( int position) {
        Log.d("play audio","called");
        String audioId = ayats.get(position).getIslahAudio();

        String filePath = getDir("audio", Context.MODE_PRIVATE).toString()
                + "/"
                + Config.AUDIO_URL + audioId
                + ".mp3";
        Log.d("playAudio", "audio called" + filePath);
        File file = new File(filePath);
//        adapter.notifyDataSetChanged();
        if (file.exists()) {
            if (isPlay) {
                isPlay = !isPlay;
                playPos = pos = position;
                //view = v;
//                playBtn = (ImageView) view.findViewById(R.id.playBtn);
                //              playBtn.setBackgroundResource(R.drawable.ic_action_stop);
                try {
                    String fName = getDir("audio", Context.MODE_PRIVATE)
                            .getAbsolutePath()
                            + "/"
                            + Config.AUDIO_URL + audioId + ".mp3";
                    mPlayer.setDataSource(this, Uri.parse(fName));
                    mPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }/*
            } else if (pos == getListView().getPositionForView(v)) {
                if (isPlay) {
               //     playBtn = (ImageView) v.findViewById(R.id.playBtn);
                 //   playBtn.setBackgroundResource(R.drawable.ic_action_stop);
                } else {
                   // playBtn = (ImageView) v.findViewById(R.id.playBtn);
                   // playBtn.setBackgroundResource(R.drawable.ic_action_play);
                    if (mPlayer != null) {
                        // mPlayer.stop();
                        if (mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer.reset();
                            playPos = -1;

                        }
                    }
                }
                isPlay = !isPlay;

                // pos = position;
            } else {

                // Log.e("PLAY", );
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                        mPlayer.reset();
                        playPos = -1;
                        adapter.notifyDataSetChanged();
                        isPlay = true;

                    }
                    pos = position;

                    if (isPlay) {
                        playPos = position;
                        try {
                            String fName = getDir("audio", Context.MODE_PRIVATE)
                                    .getAbsolutePath()
                                    + "/"
                                    + Config.AUDIO_URL + audioId + ".mp3";
                            mPlayer.setDataSource(this, Uri.parse(fName));
                            mPlayer.prepareAsync();
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        // Log.e("Button", " Media Player");
             //           playBtn = (ImageView) v.findViewById(R.id.playBtn);
               //         playBtn.setBackgroundResource(R.drawable.ic_action_stop);
                        isPlay = !isPlay;
                    }

                }

            }
        }

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                playPos = -1;
                mPlayer.reset();
                isPlay = !isPlay;
                adapter.notifyDataSetChanged();
            }
        });
*/
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                }
            });

            //   latestPos = -1;

        }
    }


        public void playing(int pos){
            String audioId = ayats.get(pos).getIslahAudio();
            String filePath = getDir("audio", Context.MODE_PRIVATE).toString()
                    + "/"
                    + Config.AUDIO_URL + audioId
                    + ".mp3";
            Log.d("playAudio", "audio called" + filePath);
            File file = new File(filePath);
            if(file.exists()){
                Log.d("file exits in this path",""+filePath);
            }else{ Log.d("file exits in this path","not exits");}
            Uri myUri1 = Uri.parse("file:///data/data/com.talhazk.islah/app_audio/"+ audioId +".mp3");
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


            try {
                mPlayer.setDataSource(getApplicationContext(), myUri1);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mPlayer.prepare();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            }
            mPlayer.start();
        }

}