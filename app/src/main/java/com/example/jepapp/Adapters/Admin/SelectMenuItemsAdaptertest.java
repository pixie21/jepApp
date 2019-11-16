package com.example.jepapp.Adapters.Admin;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jepapp.Models.Admin_Made_Menu;
import com.example.jepapp.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class SelectMenuItemsAdaptertest extends RecyclerView.Adapter<SelectMenuItemsAdaptertest.ViewHolder> {

    Context context;
    private boolean a;

    private ArrayList<String> arrayListQuantity = new ArrayList();
    private ArrayList<String> arrayListTitle = new ArrayList();
    private ArrayList<String> arrayListChecker = new ArrayList();
    List<Admin_Made_Menu> MainImageUploadInfoList;

    public SelectMenuItemsAdaptertest(Context context, List<Admin_Made_Menu> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_custom_row_layout,null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//    }

    @Override
    public void onBindViewHolder(final SelectMenuItemsAdaptertest.ViewHolder holder, final int position) {

        final Admin_Made_Menu item = MainImageUploadInfoList.get(position);

        holder.Title.setText(item.getTitle());
        holder.checkBox.setChecked(isA());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int l= 0;
                if (holder.checkBox.isChecked()){
                    Log.d("Checkers", "checked");

                    holder.Quantity.setVisibility(VISIBLE);

                    //arrayListTitle.add(item.getTitle());

//                    holder.Quantity.setOnEditorActionListener(new holder.Quantity.onEditorAction();{
//
//                    });
//                    holder.Quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//                        public void onFocusChange(View v, boolean hasFocus) {
//                            if(!hasFocus)
//                                arrayListQuantity.add(holder.Quantity.getText().toString());
////                            Log.e("quanity being added", arrayListQuantity.get(0));
//                           // return true; // consume.
//                        }
//                    });
                    arrayListChecker.add(item.getTitle());
                    holder.Quantity.setTextColor(ContextCompat.getColor(context, R.color.red));
                    holder.Title.setTextColor(ContextCompat.getColor(context,R.color.red));
                    holder.checkBox.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                    //holder.checkBox.set(ContextCompat.getColor(context,R.color.red));
                    holder.Quantity.setOnEditorActionListener(


                            new EditText.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                   // holder.checkBox.setEnabled(false);
//                                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                                            actionId == EditorInfo.IME_ACTION_DONE ||
//                                            event != null &&
//                                                    event.getAction() == KeyEvent.ACTION_DOWN &&
//                                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                                        if (event == null || !event.isShiftPressed()) {
                                    // the user is done typing.
                                    switch (actionId) {
                                        case EditorInfo.IME_ACTION_DONE:
                                        case EditorInfo.IME_ACTION_NEXT:
                                        case EditorInfo.IME_ACTION_PREVIOUS:
                                            arrayListQuantity.add(holder.Quantity.getText().toString());
                                            Log.e("quanity being added", arrayListQuantity.get(0));
                                            holder.Quantity.setTextColor(ContextCompat.getColor(context, R.color.green));
                                            holder.Title.setTextColor(ContextCompat.getColor(context,R.color.green));
                                            holder.checkBox.setHintTextColor(ContextCompat.getColor(context, R.color.green));
                                            arrayListTitle.add(item.getTitle());
                                            return true; // consume.
                                    }


                                    return false; // pass on to other listeners.
                                }
                            }
                    );

//                    holder.Quantity.addTextChangedListener(new TextWatcher() {
//                        boolean isonTextChanged = false;
//                        @Override
//                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                            //isonTextChanged = true;
//                            arrayListQuantity.add(charSequence.toString());
//                            Log.e("quanity being added",arrayListQuantity.get(0));
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
////                            if (isonTextChanged){
////                                isonTextChanged = false;
//
//                          //  }
//
//                        }
//                    });


//

                }else{

                    Log.d("Checkers", "unchecked");
                    arrayListChecker.remove(item.getTitle());
                    holder.Quantity.setVisibility(View.INVISIBLE);
                    arrayListTitle.remove(item.getTitle());
                    arrayListQuantity.remove(holder.Quantity.getText());
                    holder.Title.setTextColor(ContextCompat.getColor(context,R.color.black));
                    //holder.Quantity.setBackgroundColor(clear);
                    holder.checkBox.setBackgroundColor(ContextCompat.getColor(context, R.color.clear));


                }
            }

        });
       // Intent intent = new Intent(getApplicationContext(),NextActivity.class);
        //
    }



    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Title;
        public CheckBox checkBox;
        public EditText Quantity;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {

            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.itemtitle);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox1);

            Quantity = (EditText) itemView.findViewById(R.id.quantity);
        }
    }
    public boolean isA() {
        return a;
    }

    public void setA(boolean a) {
        this.a = a;
    }
    public ArrayList<String> getArrayListQuantity() {
        return arrayListQuantity;
    }

    public void setArrayListQuantity(ArrayList<String> arrayListQuantity) {
        this.arrayListQuantity = arrayListQuantity;
    }

    public ArrayList<String> getArrayListTitle() {
        return arrayListTitle;
    }

    public void setArrayListTitle(ArrayList<String> arrayListTitle) {
        this.arrayListTitle = arrayListTitle;
    }

    public void setArrayListChecker(ArrayList<String> arrayListChecker) {
        this.arrayListChecker = arrayListChecker;
    }
    public ArrayList<String> getArrayListChecker() {return  arrayListChecker;}
}