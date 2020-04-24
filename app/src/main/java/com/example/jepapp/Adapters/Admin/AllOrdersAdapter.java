package com.example.jepapp.Adapters.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jepapp.Activities.Admin.AdminCart;
import com.example.jepapp.Models.Cart;
import com.example.jepapp.Models.MItems;
import com.example.jepapp.Models.Orders;
import com.example.jepapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.ProductViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;
    private List<Orders> allOrdersList;
    private List<MItems> mitemslist;
    private static int currentPosition = -1;


    public AllOrdersAdapter(Context mCtx, List<Orders> allOrdersList) {
        this.mCtx = mCtx;
        this.allOrdersList = allOrdersList;

    }


    @NotNull
    @Override
    public AllOrdersAdapter.ProductViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.allorderslayout, parent, false);
        return new AllOrdersAdapter.ProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position) {
        final Orders item = allOrdersList.get(position);
        //check the status of the order and display the appropriate linear layouts with buttons

            if (currentPosition == position) {
                //creating an animation
                Animation slideDown = AnimationUtils.loadAnimation(mCtx, R.anim.slide_down);

                //toggling visibility
                holder.ordersbuttonlayout.setVisibility(View.VISIBLE);

                //adding sliding effect
                holder.ordersbuttonlayout.startAnimation(slideDown);
            } else if (currentPosition == -1) {
                Animation slideUp = AnimationUtils.loadAnimation(mCtx, R.anim.slide_up);
                holder.ordersbuttonlayout.setVisibility(View.GONE);

                //adding sliding effect
                holder.ordersbuttonlayout.startAnimation(slideUp);

            }

        if (item.getStatus().toLowerCase().equals("cancelled")){
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(mCtx,
                                "Order has been cancelled, no further edition can be made",
                                Toast.LENGTH_LONG);
                        toast.show();
                }
            });
        }
        else {
            //showing buttons on item click
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //getting the position of the item to expand it
                    if (currentPosition == position) {
                        currentPosition = -1;

                    } else {
                        currentPosition = position;
                    }
                    //reloading the list
                    notifyDataSetChanged();
                }

            });
        }
        //place order details into a list and splitting the list to display order details in different lines
        ArrayList<String> orderstuff = item.getOrdertitle();
        String listString = "";
        for (String s : orderstuff) {
            listString += s + "\t";
        }
        String newlistString = listString.replace(", ", "\n");


        //binding the data with the viewholder views
        holder.allOrdersCustomer.setText("Name:" + item.getUsername());
        holder.allOrdersID.setText(item.getOrderID());
        holder.allOrdersTitle.setText("Items:\n" + newlistString);
        holder.allOrdersCost.setText("Total:" + item.getCost());
        holder.allOrdersDate.setText("Date:" + item.getDate());
        holder.allOrdersStatus.setText("Status: " + item.getStatus());
        holder.allOrdersTime.setText("Time:" + item.getTime());
        holder.allOrdersRequests.setText("Special request:\n" + item.getRequest());
        holder.allOrdersPayBy.setText("Paid by:" + String.valueOf(item.getPaidby()));
        holder.allOrdersPaymentType.setText(item.getPayment_type());

        //if cancel button is clicked find the order in the database and set the status as cancelled
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder canceldialog = new AlertDialog.Builder(mCtx,R.style.datepicker);
                canceldialog.create();
                canceldialog.setTitle("Cancel Order");
                canceldialog.setMessage("Are you sure you would like to cancel this order? \nNB This action cannot be undone");
                canceldialog.setCancelable(true);
                canceldialog.setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //update item in firebase
                        update_status(item, "cancelled");
                        notifyDataSetChanged();
                    }
                });
                canceldialog.setNegativeButton(R.string.dialogNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = canceldialog.create();
                alertDialog.show();
            }
        });
        //Set an order as prepared in firebase
        holder.prepared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder prepareddialog = new AlertDialog.Builder(mCtx,R.style.datepicker);
                prepareddialog.create();
                prepareddialog.setMessage("Set order as prepared");
                prepareddialog.setCancelable(true);
                prepareddialog.setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //update item in firebase
                        update_status(item, "Prepared");

                    }
                });
                prepareddialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = prepareddialog.create();
                alertDialog.show();
            }
        });
        //Change an order's payment type
        holder.payment_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder editpaymentaction = new AlertDialog.Builder(mCtx,R.style.datepicker);
                editpaymentaction.setMessage("Select payment type below");
                editpaymentaction.setCancelable(true);
                editpaymentaction.setPositiveButton((R.string.lunchcard), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update_status_payment(item, "Lunch Card");
                    }
                });
                editpaymentaction.setNegativeButton((R.string.cash), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update_status_payment(item, "Cash");
                    }
                });
                editpaymentaction.create();
                editpaymentaction.show();
            }
        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mitemslist = new ArrayList<>();
                //get order items details
                ArrayList<String> dialogorderstuff = item.getOrdertitle();
                //convert order items to strings
                String dialoglistString = "";
                String newdialoglistString = "";
                for (String s : dialogorderstuff) {
                    dialoglistString += s + "\t";
                }
                //get only the quantity number
                newdialoglistString = dialoglistString.replace("(x", ", ");
                newdialoglistString = newdialoglistString.replace(")","");
                //splits a single order item into name and quantity without the "x"
                List<String> items = Arrays.asList(newdialoglistString.split("\\s*,\\s*"));

                List<String> name = new ArrayList<>();
                final List<String> number = new ArrayList<>();
                //separates the items list into 2 lists containing names only and quantities only
                for(int j=0; j != items.size(); j++) {
                    if (j % 2 == 0) { // Even
                        name.add(items.get(j));
                    } else { // Odd
                        number.add(items.get(j));
                    }
                }
                // for each item in the names list, find its corresponding details in the MenuItems table in firebase
                for (int i=0; i< name.size(); i++){
                    Query query = FirebaseDatabase.getInstance().getReference("JEP").child("MenuItems").orderByChild("title").equalTo(name.get(i));
                    final int finalI = i;
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mitemslist.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                MItems breakfastDetails = snapshot.getValue(MItems.class);

                                mitemslist.add(breakfastDetails);
                            }

                            MItems newmitem = mitemslist.get(0);
                            //creates a cart for admin with the current order detail
                            String type = "breakfast";
                            String username = "Admin";
                            com.example.jepapp.Models.Cart cart = new Cart(newmitem.getPrice().toString(),newmitem.getImage(),newmitem.getTitle(), number.get(finalI),type,username);
                            FirebaseDatabase.getInstance().getReference("JEP").child("BreakfastCart")
                                    .child(username)
                                    .child(newmitem.getTitle())
                                    .setValue(cart);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                //send order details in the form of a intent bundle and launch Admin Cart interface
                Bundle bundle = new Bundle();

                bundle.putString("ordertype", item.getType());
                bundle.putString("id", item.getOrderID());
                Intent intent = new Intent(mCtx, AdminCart.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mCtx.startActivity(intent);
            }

        });


    }
//update the status of an order payment type
    private void update_status_payment(Orders item, final String payment) {
        if (item.getType().equals("Breakfast")){
            DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("JEP").child("BreakfastOrders");
            databaseReference.child(item.getOrderID()).child("payment_type").setValue(payment);
            notifyDataSetChanged();
        }
        else if (item.getType().equals("Lunch")){
            DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("JEP").child("LunchOrders");
            databaseReference.child(item.getOrderID()).child("payment_type").setValue(payment);
           notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return allOrdersList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView allOrdersTitle, allOrdersID, allOrdersCustomer, allOrdersDate, allOrdersStatus, allOrdersRequests, allOrdersTime, allOrdersPaymentType, allOrdersPayBy, allOrdersCost;
        LinearLayout parentLayout, ordersbuttonlayout;
        Button collect_payment;
        ImageButton prepared, cancel, edit, payment_type;

        public ProductViewHolder(View itemView) {
            super(itemView);
            allOrdersID = itemView.findViewById(R.id.allordersid);
            allOrdersTitle = itemView.findViewById(R.id.allorderstitle);
            allOrdersCustomer = itemView.findViewById(R.id.allorderscustomername);
            allOrdersDate = itemView.findViewById(R.id.allordersdate);
            allOrdersPaymentType = itemView.findViewById(R.id.allorderspaymenttype);
            allOrdersTime = itemView.findViewById(R.id.allorderstime);
            allOrdersStatus = itemView.findViewById(R.id.allordersstatus);
            ordersbuttonlayout = itemView.findViewById(R.id.ordersbuttonslayout);
            payment_type = itemView.findViewById(R.id.button_paymenttype);
            allOrdersRequests = itemView.findViewById(R.id.allordersrequest);
            prepared = itemView.findViewById(R.id.button_prepared);
            parentLayout = itemView.findViewById(R.id.PARENT);
            cancel = itemView.findViewById(R.id.button_cancel);
            edit = itemView.findViewById(R.id.button_edit);
            collect_payment = itemView.findViewById(R.id.collectpayment);
            allOrdersPayBy = itemView.findViewById(R.id.allorderspayby);
            allOrdersCost = itemView.findViewById(R.id.allorderscost);

        }
    }
    //reload adapter with the results of the search
    public void updateList(List<Orders> newList){
        allOrdersList = new ArrayList<>();
        allOrdersList = newList;
        notifyDataSetChanged();
    }
    //update the status of an order
    private void update_status(Orders item, final String status) {
        if (item.getType().equals("Breakfast")){
            DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("JEP").child("BreakfastOrders");
            databaseReference.child(item.getOrderID()).child("status").setValue(status);
            notifyDataSetChanged();

        }
        else if (item.getType().equals("Lunch")){
            DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("JEP").child("LunchOrders");
            databaseReference.child(item.getOrderID()).child("status").setValue(status);
           notifyDataSetChanged();

        }
    }


}
