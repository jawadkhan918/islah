package com.talhazk.islah.activity;

/**
 * Created by Talhazk on 30-Mar-16.
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
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.talhazk.islah.R;
import com.talhazk.islah.app.Config;
import com.talhazk.islah.httpcontrol.CopyFile;
import com.talhazk.islah.model.AppChooser;
import com.talhazk.islah.model.Ayats;
import com.talhazk.islah.model.AyatsList;
import com.talhazk.islah.model.CheckInternet;
import com.talhazk.islah.model.Favorities;
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

public class FavoritiesActivity extends ListActivity {


    RequestQueue requestQueue;

    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
    // ArrayList to show data in Listview
//    private static List<Dialogues> mDialogueList;
    private List<Ayats> ayats;
    private List<Favorities> mFvrtlist;
    public static int count = 0;
    // Graphics Button
    private ImageView playBtn;
    private ImageView fvrtBtn;
    private ImageView catIcon;
    private TextView mTitle;
    private TextView ayatCatId;
    private TextView ayatTitle;
    private Button mBackButton;
    //   private TextView mAyatNo;

    //private TextView mAudios;
    //private TextView mStatus;
    private ImageButton mBackBtn;
    private ImageView shareBtn;
    private TextView ayatNo;

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
    TextView favrtText;

    // Listview Adapter
    private JobListAdapter adapter;

    LinearLayout mFvrLayout;
    // Layout for Adds
    private LinearLayout mFvrtLayout;

    TextView catName;
    int catId;
    String catImage;
    String categoryName;
    int position;
    ImageView imageExtra;
    private AdView adView;

    // Ads Variable

    // facebook

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_favorities);
        //requestQueue = Volley.newRequestQueue(this);
        // ayats.clear();


        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-7714528612911729/4846335496");

      /*  favrtText = (TextView)findViewById (R.id.favrtText);
        if(FavoritiesList.get().getFavorities().size()<=0){
            favrtText.setVisibility(View.VISIBLE);
        }else{
            favrtText.setVisibility(View.GONE);
        }
*/
        mBackBtn = (ImageButton) findViewById(R.id.backBtn);

        mBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.favorities_layout);
        layout.addView(adView);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) adView
                .getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        adView.setLayoutParams(params);
        DatabaseHandler db = new DatabaseHandler(
                getApplicationContext());
        db.getAllFavorities();


        AdRequest adRequest = new AdRequest.Builder().build();
        //  AdRequest.Builder.addTestDevice("79176A4D625C750A744A6477BF2E301D");
        adView.loadAd(adRequest);
        imageExtra = (ImageView) findViewById(R.id.headerImage);
        catName = (TextView) findViewById(R.id.catName);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources()
                    .getColor(R.color.themecolor));
        }
      /*       getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
           getActionBar().setCustomView(R.layout.custom_actionbar);
          TextView txt = (TextView) findViewById(R.id.actionbar_title);
        txt.setText("ca");
*/
        // Local BroadCaster

/*
        Intent i = getIntent();
        position = i.getExtras().getInt("position");
        catName.setText(MainActivity.categoryItems.get(position).getName());
        catId = MainActivity.categoryItems.get(position).getId();
        catImage = MainActivity.categoryItems.get(position).getImage();
*/

        mFvrtlist = FavoritiesList.get().getFavorities();
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
        /*mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        ayats = AyatsList.get().getDialogues();
*/


        adapter = new JobListAdapter(mFvrtlist);
        setListAdapter(adapter);
        //    makeStringReq();


    }


    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
        ayats.clear();
        Log.e("dialog is invoked", "true");

    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
        Log.e("dialog not invoked", "false");

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub

        String filePath = this.getDir("audio", Context.MODE_PRIVATE)
                .toString()
                + "/"
                + FavoritiesList.get().getFavorities().get(position).getLink()
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
            if (CheckInternet.isNetConnected(this)) {
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
                playBtn = (ImageView) latestView.findViewById(R.id.playBtn);

                playBtn.setVisibility(View.INVISIBLE);
                progressDialog.setVisibility(View.VISIBLE);
                String arg[] = new String[2];

                arg[1] = FavoritiesList.get().getFavorities().get(position)
                        .getLink();
                arg[0] = Config.AUDIO_URL
                        + FavoritiesList.get().getFavorities().get(position)
                        .getLink() + ".mp3";
                new com.talhazk.islah.httpcontrol.HttpAudio(this, "")
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);

            } else {
                Toast.makeText(this, "Check your internet",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void Test(View view) {
        Log.d("this is test", ayats.get(position).getAyatTitle());
    }

    private class JobListAdapter extends ArrayAdapter<Favorities> {

        public JobListAdapter(List<Favorities> fvrtlist) {
            super(getApplicationContext(), 0, fvrtlist);
        }

        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.list_item_category_fav, null);
            }
            Favorities j = getItem(position);

            ayatTitle = (TextView) convertView
                    .findViewById(R.id.list_item_userName);
         ayatCatId = (TextView) convertView.findViewById(R.id.ayatCatid);
            ayatNo = (TextView) convertView.findViewById(R.id.ayatNo);

            String mDial = j.getMfvrt();
            String mCatId = j.getmCatId();
            String mAyatNo = j.getAyatNo() ;
            //MainActivity.

            ayatTitle.setText(mDial);
            ayatCatId.setText(mCatId);
            ayatNo.setText(mAyatNo);
            // dialogueTitle.setText(j.getMfvrt());

            fvrtBtn = (ImageView) convertView.findViewById(R.id.fvrtBtn);
            fvrtBtn.setBackgroundResource(R.drawable.star_filled);

            playBtn = (ImageView) convertView.findViewById(R.id.playBtn);
            if (playPos != position)
                playBtn.setBackgroundResource(R.drawable.button_play);
            else {
                playBtn.setBackgroundResource(R.drawable.button_stop);
            }
            progressDialog = convertView.findViewById(R.id.progress_row);

            if (latestPos != position) {
                progressDialog.setVisibility(View.GONE);
                playBtn.setVisibility(View.VISIBLE);

            } else {
                progressDialog.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.INVISIBLE);
            }
            mFvrLayout = (LinearLayout) convertView
                    .findViewById(R.id.fvrtBtnListener);
            mFvrLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (playPos == getListView().getPositionForView(v))
                        if (mPlayer != null) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.stop();
                                mPlayer.reset();
                                playPos = -1;
                                isPlay = true;
                            }
                        }
                    Boolean flag = new DatabaseHandler(getApplicationContext())
                            .removeFvrt(mFvrtlist.get(
                                    getListView().getPositionForView(v))
                                    .getId());
                    if (flag) {

                        if (FavoritiesList.get().getFavorities().size() <= 0) {
                            favrtText.setVisibility(View.VISIBLE);
                        } else {
                            favrtText.setVisibility(View.GONE);
                        }

                        if (pos > getListView().getPositionForView(v))
                            playPos = playPos - 1;
                        pos = pos - 1;
                        adapter.notifyDataSetChanged();
                    }

                }
            });
            shareBtn = (ImageView) convertView.findViewById(R.id.shareBtn);
            shareBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int posi = getListView().getPositionForView(v);

                    String fName = getApplicationContext().getDir("audio",
                            Context.MODE_PRIVATE).getAbsolutePath()
                            + "/"
                            + FavoritiesList.get().getFavorities().get(posi)
                            .getLink() + ".mp3";

                    File file = new File(fName);
                    shareName = fName;
                    if (file.exists())
                        onClickWhatsApp(v, fName);
                    else {

                        if (CheckInternet.isNetConnected(getApplicationContext())) {
                            String arg[] = new String[3];
                            arg[2] = "share";
                            arg[1] = FavoritiesList.get().getFavorities()
                                    .get(posi).getLink();
                            arg[0] = Config.AUDIO_URL
                                    + FavoritiesList.get().getFavorities()
                                    .get(posi).getLink() + ".mp3";
                            new com.talhazk.islah.httpcontrol.HttpAudio(
                                    FavoritiesActivity.this, "share").executeOnExecutor(
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
            getListView().invalidateViews();
        }

    }

    @Override
    public void onStop() { // TODO Auto-generated method stub
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
    public void onPause() { // TODO Auto-generated method stub
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
        String filePath = this.getDir("audio", Context.MODE_PRIVATE)
                .toString()
                + "/"
                + FavoritiesList.get().getFavorities().get(position).getLink()
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
                    String fName = this.getDir("audio",
                            Context.MODE_PRIVATE).getAbsolutePath()
                            + "/"
                            + FavoritiesList.get().getFavorities()
                            .get(position).getLink() + ".mp3";

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

                        v.findViewById(R.id.playBtn).setBackgroundResource(
                                R.drawable.star_filled);

                        isPlay = true;

                    }
                    pos = position;

                    if (isPlay) {
                        playPos = position;
                        try {
                            String fName = this.getDir("audio",
                                    Context.MODE_PRIVATE).getAbsolutePath()
                                    + "/"
                                    + FavoritiesList.get().getFavorities()
                                    .get(position).getLink() + ".mp3";

                            mPlayer.setDataSource(this,
                                    Uri.parse(fName));
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

                    playBtn = (ImageView) latestView.findViewById(R.id.playBtn);
                    progressDialog = latestView.findViewById(R.id.progress_row);
                }
                progressDialog.setVisibility(View.GONE);
                playBtn.setVisibility(View.VISIBLE);
                // if (!mPlayer.isPlaying())
                if (playFlag && latestPos >= 0)

                    playAudio(latestView, latestPos);
                // latestPos = -1;
            } else if (action.equals("share")) {
                onClickWhatsApp(shareView, shareName);
            }
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mReceiver);
    }

    public void onClickWhatsApp(View view, String fName) {

        PackageManager pm = this.getPackageManager();
        String completePath = new CopyFile().destinationPath(fName);
        File file = new File(completePath);
        Uri uri = Uri.fromFile(file);
        String mimeType = "audio/mpeg";

        Intent intent = new Intent(Intent.ACTION_SEND);
        List<String> whitelist = Arrays.asList("com.whatsapp",
                "com.facebook.orca", "com.viber.voip", "tencent.mm");
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent = AppChooser.create(pm, intent, "Share", whitelist);
        this.startActivityForResult(intent, SHARE_TO_MESSENGER_REQUEST_CODE);

    }
}




