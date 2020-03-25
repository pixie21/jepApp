package com.example.jepapp.Fragments.Admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jepapp.Adapters.Admin.AllOrdersAdapter;
import com.example.jepapp.R;
import com.example.jepapp.SwipeController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Orders extends Fragment   {
    List<com.example.jepapp.Models.Orders> allordersbreakfast, allorderslunch;
    RecyclerView recyclerView_breakfast, recyclerView_lunch;
    ProgressDialog progressDialog;
    DatabaseReference databaseReferencebreakfast, databaseReferencelunch, myDBref;
    private LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private FirebaseAuth mAuth;
    SwipeController swipeControl = null;
    private View view;
    public AllOrdersAdapter adapterbreakfast, adapterlunch;
    private Paint p = new Paint();
    private FloatingActionButton lunch_refresh, breakfast_refresh;
    SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    SwipeRefreshLayout rswipeRefreshLayoutbreakfast, rswipeRefreshLayoutlunch;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.admin_fragment_order_, container, false);
        recyclerView_breakfast = (RecyclerView) rootView.findViewById(R.id.ordersbreakfastlist);
        recyclerView_lunch = rootView.findViewById(R.id.orderslunchlist);
        allordersbreakfast = new ArrayList<>();
        allorderslunch = new ArrayList<>();
        lunch_refresh = rootView.findViewById(R.id.lunch_refresh);
        breakfast_refresh = rootView.findViewById(R.id.breakfast_refresh);
        adapterbreakfast = new AllOrdersAdapter(getContext(),allordersbreakfast);
        adapterlunch = new AllOrdersAdapter(getContext(), allorderslunch);
        myDBref = FirebaseDatabase.getInstance().getReference("JEP");
        linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_breakfast.getContext(), linearLayoutManager.getOrientation());
        linearLayoutManager2 = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView_lunch.getContext(), linearLayoutManager2.getOrientation());
        recyclerView_breakfast.setLayoutManager(linearLayoutManager);
        recyclerView_lunch.setLayoutManager(linearLayoutManager2);
        recyclerView_breakfast.setAdapter(adapterbreakfast);
        recyclerView_lunch.setAdapter(adapterlunch);
        setHasOptionsMenu(true);
        lunch_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterlunch.notifyDataSetChanged();
            }
        });
        breakfast_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterbreakfast.notifyDataSetChanged();
            }
        });
        //initializing the productlist

//        menuItem = menu.getItem(0)

//


        mAuth = FirebaseAuth.getInstance();
        getBreakfastOrders();
        getLunchOrders();



       // initSwipe();
        return  rootView;
    }

    private void getBreakfastOrders() {
        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Loading Breakfast Orders");

        progressDialog.show();
        Query query = FirebaseDatabase.getInstance().getReference("JEP").child("BreakfastOrders")
                    .orderByChild("status").equalTo("Incomplete");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allordersbreakfast.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    com.example.jepapp.Models.Orders allfoodorders = snapshot.getValue(com.example.jepapp.Models.Orders.class);

                    allordersbreakfast.add(allfoodorders);

                }
                Collections.reverse(allordersbreakfast);
                adapterbreakfast.notifyDataSetChanged();

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();

                }

        });

    }
    private void getLunchOrders() {
        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Loading Lunch Orders");

        progressDialog.show();

        Query query = FirebaseDatabase.getInstance().getReference("JEP").child("LunchOrders")
                .orderByChild("status").equalTo("Incomplete");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allorderslunch.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    com.example.jepapp.Models.Orders allfoodorders = snapshot.getValue(com.example.jepapp.Models.Orders.class);

                    allorderslunch.add(allfoodorders);

                }
                Collections.reverse(allorderslunch);
                adapterlunch.notifyDataSetChanged();

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();

            }

        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final List<com.example.jepapp.Models.Orders> combinedlist = new ArrayList<>();
        combinedlist.addAll(allordersbreakfast);
        combinedlist.addAll(allorderslunch);
        inflater.inflate(R.menu.main_menu, menu);
        android.view.MenuItem searchItem = menu.findItem(R.id.action_search);
        // searchItem.setVisible(false);
        //getActivity().invalidateOptionsMenu(); Removed because of scrolling toolbar animation
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
//        searchView.setIconified(false);
        if (searchItem != null){
            searchView = (SearchView)searchItem.getActionView();
        }
        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    Log.d("Query", newText);
                    String userInput = newText.toLowerCase();
                    List<com.example.jepapp.Models.Orders> newList = new ArrayList<>();
                    List<com.example.jepapp.Models.Orders> newListlunch = new ArrayList<>();
                    // for (com.example.jepapp.Models.Orders orders : allorderslist) {

                    //if (!searchView.isIconified()) {
                        getActivity().onSearchRequested();
                        //  com.example.jepapp.Models.Orders orders;
                        for (int i = 0; i< allordersbreakfast.size(); i++){
                            //Log.e("idk",allorderslist.get(i).getOrdertitle().toLowerCase());
                            ArrayList<String> orderstuff = allordersbreakfast.get(i).getOrdertitle();
                            String listString = "";
                            for (String s : orderstuff)
                            {
                                listString += s + "\t";
                            }
                            //Todo address this by uncommenting
                            if (allordersbreakfast.get(i).getUsername().toLowerCase().contains(userInput)|| listString.toLowerCase().contains(userInput))
                            {

                                newList.add(allordersbreakfast.get(i));
                                //Log.e("Eror", newList.get(0).getOrdertitle());
                            }


                       // }

                    }    for (int i = 0; i< allorderslunch.size(); i++){
                        //Log.e("idk",allorderslist.get(i).getOrdertitle().toLowerCase());
                        ArrayList<String> orderstufflunch = allorderslunch.get(i).getOrdertitle();
                        String listStringLunch = "";
                        for (String s : orderstufflunch)
                        {
                            listStringLunch += s + "\t";
                        }
                        //Todo address this by uncommenting
                        if (allorderslunch.get(i).getUsername().toLowerCase().contains(userInput)|| listStringLunch.toLowerCase().contains(userInput))
                        {

                            newListlunch.add(allorderslunch.get(i));
                            //Log.e("Eror", newList.get(0).getOrdertitle());
                        }


                        // }

                    }

                        adapterbreakfast.updateList(newList);
                        adapterlunch.updateList(newListlunch);

                    return true;
                }


            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:

                return false;
            default:
                break;

        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

    }
    public void deleteItem(com.example.jepapp.Models.Orders remove){
        //Todo address this by uncommenting
        // databaseReference.child(remove.getKey()).removeValue();
        // Log.e("Keytime", remove.getKey());

    }



}