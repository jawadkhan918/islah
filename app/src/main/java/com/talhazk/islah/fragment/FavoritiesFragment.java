package com.talhazk.islah.fragment;

/**
 * Created by Talhazk on 30-Mar-16.
 */
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.talhazk.islah.R;
import com.talhazk.islah.httpcontrol.CopyFile;
import com.talhazk.islah.app.Config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//import android.R;
//import android.R.*;
import com.talhazk.islah.activity.AudioListActivity;
import com.talhazk.islah.model.CheckInternet;
import com.talhazk.islah.utils.DatabaseHandler;
import com.talhazk.islah.activity.AudioListActivity;
import com.talhazk.islah.model.Favorities;
import com.talhazk.islah.model.FavoritiesList;
import com.talhazk.islah.utils.DatabaseHandler;
import com.talhazk.islah.model.AppChooser;
import com.talhazk.islah.model.FavoritiesList;


public class FavoritiesFragment extends ListFragment {

    public FavoritiesFragment() {
    }

    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;

    private List<Favorities> mFvrtlist;
    Boolean isPlay = true;
    ImageView playBtn;
    TextView dialogueTitle;
    ImageView fvrtBtn;
    private View progressDialog;
    int pos;
    private int latestPos = -1;
    private boolean playFlag = false;
    View view;
    private View latestView;
    static int playPos = -1;
    JobListAdapter adapter;
    MediaPlayer mPlayer = new MediaPlayer();
    LinearLayout mFvrLayout;
    TextView shareBtn;
    private AdView adView;
    View shareView;
    String shareName;
    TextView favrtText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getActivity().setTitle(
                Html.fromHtml("<font color='#ffffff'>Favorities</font>"));
        mFvrtlist = FavoritiesList.get().getFavorities();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorities,
                container, false);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mReceiver, new IntentFilter("playAudio"));

        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-7714528612911729/4846335496");

        RelativeLayout layout = (RelativeLayout) rootView
                .findViewById(R.id.favorities_layout);
        layout.addView(adView);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) adView
                .getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        adView.setLayoutParams(params);

        AdRequest adRequest = new AdRequest.Builder().build();
        // AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        favrtText = (TextView) rootView.findViewById(R.id.favrtText);
        if(FavoritiesList.get().getFavorities().size()<=0){
            favrtText.setVisibility(View.VISIBLE);
        }else{
            favrtText.setVisibility(View.GONE);
        }
        adView.loadAd(adRequest);
        adapter = new JobListAdapter(mFvrtlist);
        setListAdapter(adapter);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub

        String filePath = getActivity().getDir("audio", Context.MODE_PRIVATE)
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
            if (CheckInternet.isNetConnected(getActivity())) {
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
                new com.talhazk.islah.httpcontrol.HttpAudio(getActivity(), "")
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);

            } else {
                Toast.makeText(getActivity(), "Check your internet",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class JobListAdapter extends ArrayAdapter<Favorities> {

        public JobListAdapter(List<Favorities> fvrtlist) {
            super(getActivity(), 0, fvrtlist);
        }

        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_category, null);
            }
            Favorities j = getItem(position);

            dialogueTitle = (TextView) convertView
                    .findViewById(R.id.list_item_userName);

            String mDial = j.getMfvrt().substring(0,
                    Math.min(j.getMfvrt().length(), 30))
                    + " ...";
            dialogueTitle.setText(mDial);
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
                    Boolean flag = new DatabaseHandler(getActivity())
                            .removeFvrt(mFvrtlist.get(
                                    getListView().getPositionForView(v))
                                    .getId());
                    if (flag) {

                        if(FavoritiesList.get().getFavorities().size()<=0){
                            favrtText.setVisibility(View.VISIBLE);
                        }else{
                            favrtText.setVisibility(View.GONE);
                        }

                        if (pos > getListView().getPositionForView(v))
                            playPos = playPos - 1;
                        pos = pos - 1;
                        adapter.notifyDataSetChanged();
                    }

                }
            });
            shareBtn = (TextView) convertView.findViewById(R.id.shareBtn);
            shareBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int posi = getListView().getPositionForView(v);

                    String fName = getActivity().getDir("audio",
                            Context.MODE_PRIVATE).getAbsolutePath()
                            + "/"
                            + FavoritiesList.get().getFavorities().get(posi)
                            .getLink() + ".mp3";

                    File file = new File(fName);
                    shareName = fName;
                    if (file.exists())
                        onClickWhatsApp(v, fName);
                    else {
                        if (CheckInternet.isNetConnected(getActivity())) {
                            String arg[] = new String[3];
                            arg[2] = "share";
                            arg[1] = FavoritiesList.get().getFavorities()
                                    .get(posi).getLink();
                            arg[0] = Config.AUDIO_URL
                                    + FavoritiesList.get().getFavorities()
                                    .get(posi).getLink() + ".mp3";
                            new com.talhazk.islah.httpcontrol.HttpAudio(
                                    getActivity(), "share").executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR, arg);
                            shareView = v;

                            // onClickWhatsApp(v, fName);
                        } else
                            Toast.makeText(getActivity(),
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
        String filePath = getActivity().getDir("audio", Context.MODE_PRIVATE)
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
                    String fName = getActivity().getDir("audio",
                            Context.MODE_PRIVATE).getAbsolutePath()
                            + "/"
                            + FavoritiesList.get().getFavorities()
                            .get(position).getLink() + ".mp3";

                    mPlayer.setDataSource(getActivity(), Uri.parse(fName));
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
                            String fName = getActivity().getDir("audio",
                                    Context.MODE_PRIVATE).getAbsolutePath()
                                    + "/"
                                    + FavoritiesList.get().getFavorities()
                                    .get(position).getLink() + ".mp3";

                            mPlayer.setDataSource(getActivity(),
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mReceiver);
    }

    public void onClickWhatsApp(View view, String fName) {

        PackageManager pm = getActivity().getPackageManager();
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
