package com.example.jepapp.Fragments.Admin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jepapp.R;
import com.example.jepapp.RequestHandler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateItem extends Fragment {

    private static final Object TAG ="Create Item Class";
    //SessionPref session;
    ProgressBar progressBar;
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/dishmenu";
    private int GALLERY = 1, CAMERA = 2;
    String creatorurl = "http://legacydevs.com/CreateItems.php";
    String uploadpath= "http://legacydevs.com/uploads";
    String imagestatement;
    EditText dish_name,dish_ingredients;
    Button createbtn;

    private RequestQueue mRequestq;
    private Bitmap bitmap;
    private static CreateItem createiteminstance;

    Fragment f = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.admin_create_food_item, container, false);
        progressBar=rootView.findViewById(R.id.progressor);
        requestMultiplePermissions();
        dish_name = rootView.findViewById(R.id.dish_name);
        dish_ingredients = rootView.findViewById(R.id.dish_ingredients);
        imageview = (ImageView) rootView.findViewById(R.id.iv);
        createbtn = rootView.findViewById(R.id.create_dish);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String DishName=dish_name.getText().toString().trim();
                String DishIng=dish_ingredients.getText().toString().trim();
                if (bitmap == null){
                    imagestatement=("E");

                }
                else {
                    String uploadimage=getStringImage(bitmap);
                    imagestatement=uploadimage;
                }
                Log.d("Image Statement :  ", imagestatement);

                if(DishName.isEmpty()||DishName.length()>100){
                    Log.d("Checker", "Name Checked");
                    Toast.makeText(getContext(), "Title field is empty or contains too many characters ", Toast.LENGTH_LONG).show();
                }
                else if (DishIng.isEmpty()||DishIng.length()>400){
                    Log.d("Checker", "Empty Amount Checked");
                    Toast.makeText(getContext(), "Ingredients field is empty or contains too many characters ", Toast.LENGTH_LONG).show();

                }


                else{
                    ItemCreator();

                }

                //ItemCreator();
            }
        });

        return rootView;
    }

    /**
     * Function to store menuitem in MySQL database will post params(name of dish,
     * ingredients, picture) to the creation url
     * */
    private void ItemCreator() {
        final String dishname=dish_name.getText().toString().trim();
        final String dishing=dish_ingredients.getText().toString().trim();
        final String image=imagestatement;

        class ItemCreator extends AsyncTask<Void,Void,String>{
            //private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //Creates a request handler object
                RequestHandler requestHandler = new RequestHandler();

                //Creating input parameters
                HashMap<String, String> params = new HashMap<String, String>();
                // params.put("user_id",session.GetKeyUserId()); Correct code for user id;
                params.put("user_id","ehdffhn");
                params.put("title", dishname);
                params.put("ingredients", dishing);
                params.put("image_ref", image);

                // Returns rhe server response
                return  requestHandler.sendPostRequest(creatorurl,params);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //display the progress bar while request is executed
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d(String.valueOf(TAG), "Creation Response: " + response);

                        Toast.makeText(getContext(), "Item has been successfully created", Toast.LENGTH_LONG).show();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(String.valueOf(TAG), "Creation Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        };


            ItemCreator IC=new ItemCreator();
            IC.execute();
        }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this.getContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity((Activity) this.getContext())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                          //  Toast.makeText(CreateItem.getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                       // Toast.makeText(f, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this.getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context context= this.getContext();
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == context.) {
//            return;
//        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    // Toast.makeText(CreateItem.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateItem.this.getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(this.getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }



}