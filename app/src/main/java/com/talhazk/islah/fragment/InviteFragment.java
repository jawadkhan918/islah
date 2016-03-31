package com.talhazk.islah.fragment;

/**
 * Created by Talhazk on 25-Mar-16.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.talhazk.islah.R;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


public class InviteFragment extends AppCompatActivity {
    private ShareDialog shareDialog;

    ImageButton facebookBtn;
    ImageButton whatsappBtn;

    boolean bclickfacebook = false;
    private String m_strShareFB = "";
    private AdView adView;
    private CallbackManager callbackManager;
    final String appUrl = "https://www.boldye.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_invite);


        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.show();

        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-7714528612911729/4846335496");
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        whatsappBtn = (ImageButton) findViewById(R.id.imageWhatsapp);
        whatsappBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                shareWhatsapp();
            }
        });
        facebookBtn = (ImageButton)findViewById(R.id.imageFacebook);
        facebookBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                shareFacebook("ISLAH and QURAN");
            }
        });

    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_invite, container,
                false);

        whatsappBtn = (ImageButton) rootView.findViewById(R.id.imageWhatsapp);
        whatsappBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                shareWhatsapp();
            }
        });
        facebookBtn = (ImageButton) rootView.findViewById(R.id.imageFacebook);
        facebookBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                shareFacebook("ISLAH and QURAN");
            }
        });

        return rootView;
    }
*/

    public void shareFacebook(final String title) {

        bclickfacebook = true;
        m_strShareFB = title;

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager,
                new FacebookCallback<Sharer.Result>() {

                    @Override
                    public void onError(FacebookException arg0) {
                        if (arg0.getMessage() != "") {
                            Toast.makeText(getApplicationContext(), "FB error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Request cancelled",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Sharer.Result result) {
                        if (result.getPostId() != "") {
                            Toast.makeText(getApplicationContext(),
                                    "Posted to owner wall successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Islah and Quran")
                    .setContentDescription(m_strShareFB)
                    .setContentUrl(Uri.parse("http://www.boldye.com")).build();
            shareDialog.show(linkContent);
        }

    }

	 /* private void openDialogFriends() {

	  Bundle params = new Bundle(); params.putString("title", "Invite Friend");
	  params.putString("message", "Check out this great Travel Hacking app\n" +
	  "https://fb.me/1494798917480556"); if (AppInviteDialog.canShow()) {
	  AppInviteContent content = new AppInviteContent.Builder()
	  .setApplinkUrl("https://fb.me/1494798917480556").build(); AppInviteDialog
	  appInviteDialog = new AppInviteDialog(getActivity());
	  appInviteDialog.registerCallback(callbackManager, new
	  FacebookCallback<AppInviteDialog.Result>() {

	  @Override public void onError(FacebookException error) { if (error
	  instanceof FacebookOperationCanceledException) {
	  Toast.makeText(getActivity(), "Request cancelled", Toast.LENGTH_SHORT)
	  .show(); } else { Toast.makeText(getActivity(), "Network Error",
	  Toast.LENGTH_SHORT).show(); } }

	  @Override public void onCancel() { // Log.i("User Canceled ", //
	  "InfoActivity, InviteCallback - CANCEL!");
          Toast.makeText(getActivity(),
	  "Request cancelled", Toast.LENGTH_SHORT).show(); }

	  @Override public void onSuccess(
	  com.facebook.share.widget.AppInviteDialog.Result result) { // TODO
	  Auto-generated method stub Toast.makeText(getActivity(), "Request sent",
	 Toast.LENGTH_SHORT).show();

	  } });

	  appInviteDialog.show(content); }

	  }


*/
    public void shareWhatsapp() {
        Uri imageUri = Uri.parse("android.resource://"
                + getApplicationContext().getPackageName() + "/drawable/logo");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        // Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        // Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Islah and Quran \nShare Qurani ayats with friends and family. \n"
                        + "https://www.boldye.com");
       shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }

    }

}
