package com.talhazk.islah.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.talhazk.islah.*;
import com.talhazk.islah.app.Config;
import com.talhazk.islah.fragment.InviteFragment;
import com.talhazk.islah.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.app.*;

import com.talhazk.islah.model.CheckInternet;
import com.talhazk.islah.utils.DatabaseHandler;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //  private br.liveo.model.HelpLiveo mHelpLiveo;
    // String loginURL="http://www.pakwedds.com/islah/get_category.php";
    String data = "";
    String jsonResponse;
    Category item;

    public static List<Category> categoryItems = new ArrayList<Category>();
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    MyRecyclerAdapter adapter;
    TextView output;
    LinearLayoutManager linearLayoutManager;
    private ProgressDialog pDialog;
    private AdView adView;
    TextView checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      setContentView(R.layout.list_view);
        setContentView(R.layout.activity_main);

        checkInternet = (TextView) findViewById(R.id.checkInternet);
        if (CheckInternet.isNetConnected(getApplicationContext())) {
            Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(this.getResources().getColor(R.color.themecolor));
            }

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


            requestQueue = Volley.newRequestQueue(this);
            DatabaseHandler db = new DatabaseHandler(
                    getApplicationContext());
            db.getAllFavorities();


            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);


            adView = new AdView(this);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId("ca-app-pub-7714528612911729/4846335496");

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_activity_layout);
            layout.addView(adView);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) adView
                    .getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            adView.setLayoutParams(params);

            AdRequest adRequest = new AdRequest.Builder().build();

            adView.loadAd(adRequest);
            if (categoryItems.isEmpty())
                getCategoryList();
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);


            adapter = new MyRecyclerAdapter(categoryItems);
            recyclerView.setAdapter(adapter);
            output = (TextView) findViewById(R.id.category_name);
        } else {
            Toast.makeText(getApplicationContext(), "Check your Internet", Toast.LENGTH_SHORT).show();
            checkInternet.setVisibility(View.VISIBLE);
        }


    }

    private void getCategoryList() {
        showProgressDialog();

        JsonArrayRequest req = new JsonArrayRequest(Config.CATEGORY_LIST,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response.tosth", response.toString());
                        hideProgressDialog();

                        try {
                            // Parsing json array response
                            // loop through each json object


                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject category = (JSONObject) response
                                        .get(i);

                                String name = category.getString("name");
                                int id = Integer.parseInt(category.getString("id"));
                                String image = category.getString("image");
                                String logo = category.getString("logo");

                                // String logo = person.getString("email");
                                Log.e("name is", name);
                                Log.e("id is", "" + id);
                                Log.e("name is", name);
                                Log.e("name is", name);
                                //del this
                                item = new Category(id, name, logo, image);
                                categoryItems.add(item);

                                //     Toast.makeText(getApplicationContext(), "name is " +item.getName()  , Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }

                            //   output.setText(categoryItems.getName());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                checkInternet.setVisibility(View.VISIBLE);
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Try Again!!", Toast.LENGTH_SHORT).show();

            }
        });


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        requestQueue.add(req);

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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new VHItem(v);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Category getItem(int position) {
            return categoryItems.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            // Category currentItem = getItem(position);
            //  VHItem VHitem = (VHItem) holder;
            //    tempPath=currentItem.getPath();
            //   VHitem.output.setText(categoryItems.getName());
            //   VHitem.iv.setImageBitmap(camera.loadImageFromStorage(currentItem.getPath(),currentItem.getUserName()));
            Category cat = categoryItems.get(position);

            Category currentItem = getItem(position);
            VHItem VHitem = (VHItem) holder;
            VHitem.categoryName.setText(currentItem.getName());


            Picasso
                    .with(getApplicationContext())
                    .load(cat.getImage())
                    .fit() // will explain later
                    .placeholder(getApplicationContext().getResources().getDrawable(R.color.nliveo_black))
                    .error(getApplicationContext().getResources().getDrawable(R.color.nliveo_black))
                    .into(((VHItem) holder).image);

            Picasso
                    .with(getApplicationContext())
                    .load(cat.getLogo())
                    .fit() // will explain later
                    .placeholder(getApplicationContext().getResources().getDrawable(R.color.nliveo_black))
                    .error(getApplicationContext().getResources().getDrawable(R.color.nliveo_black))
                    .into(((VHItem) holder).logo);


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
            TextView categoryName;
            ImageView image;
            ImageView logo;

            public VHItem(View itemView) {
                super(itemView);

                this.categoryName = (TextView) itemView.findViewById(R.id.category_name);
                this.image = (ImageView) itemView.findViewById(R.id.image);
                this.logo = (ImageView) itemView.findViewById(R.id.logo_image);


                itemView.setClickable(true);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = getAdapterPosition();
                        Toast.makeText(v.getContext(), "clickable " + position, Toast.LENGTH_SHORT).show();

                        Intent intent;
                        intent = new Intent(getApplicationContext(), SpecificCategory.class);
                        intent.putExtra("position", position);

                        startActivity(intent);


                    }
                });
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment newContent = null;
        if (id == R.id.categoriesMenu) {
            // Handle the camera action
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);


            Toast.makeText(getApplicationContext(), "categoires", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.favoritesMenu) {

            Intent i = new Intent(getApplicationContext(),FavoritiesActivity.class);
            startActivity(i);

        } else if (id == R.id.shareMenu) {
            Intent i = new Intent(getApplicationContext(),InviteFragment.class);
            startActivity(i);

           /* FragmentManager fragmentManager = getFragmentManager();
                InviteFragment ddf = new InviteFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.invite_fragment, ddf, "dearDiaryFragment");
                fragmentTransaction.addToBackStack("dearDiaryFragment");
                fragmentTransaction.commit();*/
/*
            FragmentManager fm = getFragmentManager();
            //FragmentManager fm = this.getFragmentManager();
            InviteFragment guests = new InviteFragment();
            guests.show(fm, "Sample Fragment");
*/

       /*     InviteFragment fragmentS1 = new InviteFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.invite_fragment, fragmentS1).commit();
          */  }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

    }

    private void switchFragment(Fragment fragment) {
        if (this == null)
            return;

    }


}