package com.talhazk.islah.activity;

/**
 * Created by Talhazk on 29-Mar-16.
 */
        import android.annotation.SuppressLint;
        import android.app.ListActivity;
        import android.app.ProgressDialog;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Path;
        import android.graphics.Rect;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.MediaPlayer.OnCompletionListener;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.ArrayAdapter;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
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
        import com.talhazk.islah.httpcontrol.CopyFile;
        import com.talhazk.islah.model.AppChooser;
        import com.talhazk.islah.model.Ayats;
        import com.talhazk.islah.model.AyatsList;
        import com.talhazk.islah.model.CheckInternet;
        import com.talhazk.islah.model.FavoritiesList;
        import com.talhazk.islah.utils.DatabaseHandler;


        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.File;
        import java.io.IOException;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class SpecificCategory extends ListActivity {

    RequestQueue requestQueue;

    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
    // ArrayList to show data in Listview
//    private static List<Dialogues> mDialogueList;
    private List<Ayats> ayats;
    public static int count = 0;
    // Graphics Button
    private ImageView playBtn;
    private ImageView fvrtBtn;
    private ImageView catIcon;
    private TextView mTitle;
    //private TextView mAudios;
    //private TextView mStatus;
    private ImageButton mBackBtn;
    private TextView shareBtn;
    private TextView dialogueTitle;
    private View progressDialog;

    //  ImageLoader imageLoader;

    View shareView;
    String shareName;
    // MediaPlayer
    private MediaPlayer mPlayer = new MediaPlayer();
    private ProgressDialog pDialog;

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

    // Listview Adapter
    private JobListAdapter adapter;

    // Layout for Adds
    private LinearLayout mFvrtLayout;

    TextView catName;
    int catId;
    String catImage;
    String categoryName;
    int position;
    ImageView imageExtra;
    // Ads Variable

    // facebook

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_specific_category);
        requestQueue = Volley.newRequestQueue(this);
        ayats.clear();

        imageExtra =(ImageView) findViewById(R.id.headerImage);
        catName = (TextView) findViewById(R.id.catName);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //        window.setStatusBarColor(getResources()
            //              .getColor(R.color.themecolor));
        }
        //     getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //   getActionBar().setCustomView(R.layout.custom_actionbar);
        //   TextView txt = (TextView) findViewById(R.id.actionbar_title);
        //txt.setText(HollywoodFragment.sCatName);

        // Local BroadCaster

        Intent i = getIntent();
        position = i.getExtras().getInt("position");
        catName.setText(MainActivity.categoryItems.get(position).getName());
        catId = MainActivity.categoryItems.get(position).getId();
        catImage = MainActivity.categoryItems.get(position).getImage();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);


        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("playAudio"));

        //  Categories cat = CategoriesList.get().getCat(com.rapidzz.sayinstyle.MainActivity.catId);
        //  Log.e("Stat", cat.getLink());
      //  catIcon = (ImageView) findViewById(R.id.list_item_icon);
        mTitle = (TextView) findViewById(R.id.list_item_userName);
        //	mAudios = (TextView) findViewById(R.id.list_item_total_audios);
        //	mStatus = (TextView) findViewById(R.id.list_item_icon);
        //  mTitle.setText(cat.getCat());
        //mTitle.setText(cat.get());
        //mTitle.setText(cat.getCat());
//        imageLoader = new ImageLoader(getApplicationContext());
        //      imageLoader.DisplayImage(cat.getLink(), catIcon);

        //     mBackBtn = (ImageButton) findViewById(R.id.actionbar_btn);

/*
        mBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
*/
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        ayats = AyatsList.get().getDialogues();

        Picasso
                .with(getApplicationContext())
                .load(catImage)
                .fit() // will explain later
                .placeholder(getApplicationContext().getResources().getDrawable(R.drawable.default_img))
                .error(getApplicationContext().getResources().getDrawable(R.drawable.default_img))
                .into(imageExtra);




        adapter = new JobListAdapter(ayats);
        setListAdapter(adapter);
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
                Toast.makeText(SpecificCategory.this, "Try again!! ", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub

        Toast.makeText(getApplicationContext(),"this called",Toast.LENGTH_SHORT).show();

        Log.d("a","called");

        String filePath = getDir("audio", Context.MODE_PRIVATE).toString()
                + "/"
                + AyatsList.get().getDialogues().get(position).getIslahAudio()
                + ".mp3";
        File file = new File(filePath);
        if (file.exists()) {

            if (view == null)
                view = v;
            playFlag = false;
            progressDialog = view.findViewById(R.id.progress_row);
            playBtn = (ImageView) view.findViewById(R.id.playBtn);

            playBtn.setVisibility(View.VISIBLE);
            progressDialog.setVisibility(View.GONE);
            latestPos = -1;
            playAudio(v, position);
        } else {
            if (CheckInternet.isNetConnected(getApplicationContext())) {
                playFlag = true;
                if (view == null)
                    view = v;
                progressDialog = view.findViewById(R.id.progress_row);
                playBtn = (ImageView) view.findViewById(R.id.playBtn);

                playBtn.setVisibility(View.VISIBLE);
                progressDialog.setVisibility(View.GONE);

                latestView = view = v;
                latestPos = position;
                progressDialog = v.findViewById(R.id.progress_row);
                playBtn = (ImageView) v.findViewById(R.id.playBtn);

                playBtn.setVisibility(View.INVISIBLE);
                progressDialog.setVisibility(View.VISIBLE);
                String arg[] = new String[2];

                arg[1] = AyatsList.get().getDialogues().get(position)
                        .getIslahAudio();
                arg[0] = Config.AUDIO_URL
                        + AyatsList.get().getDialogues().get(position)
                        .getIslahAudio() + ".mp3";
                new com.talhazk.islah.httpcontrol.HttpAudio(this, "")
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);

            } else {
                Toast.makeText(this, "Check your internet", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void Test(View view) {
        Log.d("this is test",ayats.get(position).getAyatTitle());
    }

    private class JobListAdapter extends ArrayAdapter<Ayats> {

        public JobListAdapter(List<Ayats> members) {
            super(getApplicationContext(), 0, members);
        }

        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.list_item_category, null);
            }

            // pos = position;
            Ayats j = getItem(position);

            dialogueTitle = (TextView) convertView
                    .findViewById(R.id.list_item_userName);
            String mDial = j.getAyatTitle().substring(0,
                    Math.min(j.getAyatTitle().length(), 30))
                    + " ...";
            dialogueTitle.setText(mDial);
       mFvrtLayout = (LinearLayout) convertView
                              .findViewById(R.id.fvrtBtnListener);

              fvrtBtn = (ImageView) convertView.findViewById(R.id.fvrtBtn);

            ImageView playBtn = (ImageView) convertView
                    .findViewById(R.id.playBtn);

            if (playPos != position)
                playBtn.setBackgroundResource(R.drawable.button_play);
            else {
                playBtn.setBackgroundResource(R.drawable.button_stop);
            }

            // adf
            progressDialog = convertView.findViewById(R.id.progress_row);

            if (latestPos != position) {
                progressDialog.setVisibility(View.GONE);
                playBtn.setVisibility(View.VISIBLE);

            } else {
                progressDialog.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.INVISIBLE);
            }


            if (FavoritiesList.get().getFvrt(
                    (ayats.get(position).getAyatId())))
                fvrtBtn.setBackgroundResource(R.drawable.star_filled);
            else {
                fvrtBtn.setBackgroundResource(R.drawable.star_unfilled);
            }


            // Favourite Button Click Listener
            mFvrtLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int posi = getListView().getPositionForView(v);
                    DatabaseHandler db = new DatabaseHandler(
                            getApplicationContext());
                    Boolean check = false;
                    if (FavoritiesList.get().getFavorities().size() > 0) {
                        check = FavoritiesList.get().getFvrt(
                                (ayats.get(posi).getAyatId()));
                        ;
                    }
                    if (!check) {

                        db.addFavourite(ayats.get(posi));
                        fvrtBtn = (ImageView) v.findViewById(R.id.fvrtBtn);
                        fvrtBtn.setBackgroundResource(R.drawable.star_filled);

                    } else {

                        fvrtBtn = (ImageView) v.findViewById(R.id.fvrtBtn);
                        fvrtBtn.setBackgroundResource(R.drawable.star_unfilled);
                        db.removeFvrt(ayats.get(posi).getAyatId());
                    }

                }
            });

            shareBtn = (TextView) convertView.findViewById(R.id.shareBtn);
            shareBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int posi = getListView().getPositionForView(v);

                    String fName = getDir("audio", Context.MODE_PRIVATE)
                            .getAbsolutePath()
                            + "/"
                            + AyatsList.get().getDialogues().get(posi)
                            .getIslahAudio() + ".mp3";

                    File file = new File(fName);
                    shareName = fName;
                    if (file.exists())
                        onClickWhatsApp(v, fName);
                    else {
                        if (CheckInternet
                                .isNetConnected(getApplicationContext())) {
                            String arg[] = new String[3];
                            arg[2] = "share";
                            arg[1] = AyatsList.get().getDialogues()
                                    .get(posi).getIslahAudio();
                            arg[0] = Config.AUDIO_URL
                                    + AyatsList.get().getDialogues()
                                    .get(posi).getIslahAudio() + ".mp3";
                            new com.talhazk.islah.httpcontrol.HttpAudio(
                                    SpecificCategory.this, "share")
                                    .executeOnExecutor(
                                            AsyncTask.THREAD_POOL_EXECUTOR, arg);
                            shareView = v;

                            // onClickWhatsApp(v, fName);
                        } else
                            Toast.makeText(getApplicationContext(),
                                    "Check your Internet", Toast.LENGTH_SHORT)
                                    .show();
                    }
                }
            });

            return convertView;

        }

        @Override
        public void notifyDataSetChanged() {
            // TODO Auto-generated method stub
            super.notifyDataSetChanged();
        }

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                adapter.notifyDataSetChanged();
                mPlayer.reset();
                playPos = -1;
                isPlay = true;
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                adapter.notifyDataSetChanged();
                mPlayer.reset();
                playPos = -1;
                isPlay = true;
            }
        }
    }

    public void playAudio(View v, int position) {

        String filePath = getDir("audio", Context.MODE_PRIVATE).toString()
                + "/"
                + AyatsList.get().getDialogues().get(position).getIslahAudio()
                + ".mp3";
        File file = new File(filePath);
        adapter.notifyDataSetChanged();
        if (file.exists()) {
            if (isPlay) {
                isPlay = !isPlay;
                playPos = pos = position;
                view = v;
                playBtn = (ImageView) view.findViewById(R.id.playBtn);
                playBtn.setBackgroundResource(R.drawable.button_stop);
                try {
                    String fName = getDir("audio", Context.MODE_PRIVATE)
                            .getAbsolutePath()
                            + "/"
                            + AyatsList.get().getDialogues().get(position)
                            .getIslahAudio() + ".mp3";
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
            } else if (pos == getListView().getPositionForView(v)) {
                if (isPlay) {
                    playBtn = (ImageView) v.findViewById(R.id.playBtn);
                    playBtn.setBackgroundResource(R.drawable.button_stop);
                } else {
                    playBtn = (ImageView) v.findViewById(R.id.playBtn);
                    playBtn.setBackgroundResource(R.drawable.button_play);
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
                                    + AyatsList.get().getDialogues()
                                    .get(position).getIslahAudio() + ".mp3";
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
                        playBtn = (ImageView) v.findViewById(R.id.playBtn);
                        playBtn.setBackgroundResource(R.drawable.button_stop);
                        isPlay = !isPlay;
                    }

                }

            }
        }

        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                playPos = -1;
                mPlayer.reset();
                isPlay = !isPlay;
                adapter.notifyDataSetChanged();
            }
        });

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });

        latestPos = -1;

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getStringExtra("action");
            if (action.equals("play")) {
                if (latestView != null) {
                    progressDialog = latestView.findViewById(R.id.progress_row);
                    playBtn = (ImageView) latestView.findViewById(R.id.playBtn);
                    progressDialog.setVisibility(View.GONE);
                    playBtn.setVisibility(View.VISIBLE);
                }

                // if (!mPlayer.isPlaying())
                if (playFlag && latestPos >= 0)

                    playAudio(latestView, latestPos);
                // latestPos = -1;
            } else if (action.equals("share")) {
                //  onClickWhatsApp(shareView, shareName);
            }
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
//for whatsapp

    public void onClickWhatsApp(View view, String fName) {

        PackageManager pm = getPackageManager();
        String completePath = new CopyFile().destinationPath(fName);
        File file = new File(completePath);
        Uri uri = Uri.fromFile(file);
        String mimeType = "audio/mpeg";

        Intent intent = new Intent(Intent.ACTION_SEND);
        List<String> whitelist = Arrays.asList("com.whatsapp",
                "com.facebook.orca", "com.viber.voip", "tencent.mm");
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        // intent.putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION);
        // intent.putExtra(EXTRA_APP_ID, YOUR_APP_ID);
        intent = AppChooser.create(pm, intent, "Share", whitelist);
        this.startActivityForResult(intent, SHARE_TO_MESSENGER_REQUEST_CODE);

    }
    private void parseData(String response) {


        int status = -1;
        JSONObject obj = null;
        JSONArray array = null;
        try {
            array = new JSONArray(response);
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
                    //       String part1 = parts[0]; // 004
                    //     String part2 = parts[1]; // 034556

/*
                    ayatTitle = part1;
                    ayatNo = "Ayat " + part2;
*/

                    ayatTitle = splitter;
                    ayatNo = "Ayat " ;

                    Ayats ayatList = new Ayats(ayatStatus, ayatId, ayatTitle, ayatNo, islahAudio);
                    ayats.add(ayatList);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                // }


            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Finally initializing our adapter
        //    adapter = new OrderHistoryAdapter(listOrderHistory, getActivity());

        //Adding adapter to recyclerview
      /*  recyclerView.setAdapter(adapter);
        adapter = new MyRecyclerAdapter(ayats);
        recyclerView.setAdapter(adapter);
      */

        adapter = new JobListAdapter(ayats);
        setListAdapter(adapter);

    }







}