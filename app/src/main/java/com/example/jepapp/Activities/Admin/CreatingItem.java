package com.example.jepapp.Activities.Admin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jepapp.Models.MItems;
import com.example.jepapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreatingItem  extends AppCompatActivity {
    private static final Object TAG ="Creating An Item Class";

   
    ProgressBar progressBar;
    private ImageView imageview;
    //private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    
    String imagestatement;
    EditText dish_name,dish_ingredients,item_price;
    Button createbtn;
    private Bitmap bitmap;
    private StorageReference mStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRef;
    private FirebaseAuth mAuth;
    private Uri downloadUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_create_food_item);
        //Firebase Storage 
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        //mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        
        progressBar=findViewById(R.id.progressor);
        requestMultiplePermissions();
        dish_name = findViewById(R.id.dish_name);
        dish_ingredients = findViewById(R.id.dish_ingredients);
        item_price = findViewById(R.id.pricer);
        imageview = (ImageView) findViewById(R.id.iv);
        createbtn = findViewById(R.id.create_dish);

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
                String itemprice=item_price.getText().toString().trim();
                if (downloadUrl == null)
                    imagestatement=("Empty");

                //Log.d("Image Statement :  ", imagestatement);

                if(DishName.isEmpty()||DishName.length()>100){
                    Log.d("Checker", "Name Checked");
                    Toast.makeText(getApplicationContext(), "Title field is empty or contains too many characters ", Toast.LENGTH_LONG).show();
                }
                else if (DishIng.isEmpty()||DishIng.length()>400){
                    Log.d("Checker", "Empty Amount Checked");
                    Toast.makeText(getApplicationContext(), "Ingredients field is empty or contains too many characters ", Toast.LENGTH_LONG).show();

                }
                else if (itemprice.isEmpty()||itemprice.length()>9){
                    Log.d("Checker", "Empty Amount Checked");
                    Toast.makeText(getApplicationContext(), "Item Cost field is empty or contains too many values ", Toast.LENGTH_LONG).show();

                }


                else{
                    ItemCreator(DishName,DishIng,itemprice,getDownloadUrl().toString());
                    //onBackPressed();
//                    Intent intent = new Intent(getApplicationContext(), AdminPageforViewPager.class);
//                    startActivity(intent);
                    




                }

                
            }
        });
    }

    private void ItemCreator(String dishName, String dishIng, String itemprice,String imageurl) {
        MItems mItems = new MItems(mAuth.getUid(),dishName,dishIng,Float.valueOf(itemprice),imageurl);
        String key =getDb().child("MenuItems").push().getKey();
        getDb().child("MenuItems")
                .child(key)
                .setValue(mItems);
        Log.d("Start Adding","START!");
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
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//        File wallpaperDirectory = new File(
//                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
//        // have the object build the directory structure, if needed.
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs();
//        }
//
//        try {
//            File f = new File(wallpaperDirectory, Calendar.getInstance()
//                    .getTimeInMillis() + ".jpg");
//            f.createNewFile();
//            FileOutputStream fo = new FileOutputStream(f);
//            fo.write(bytes.toByteArray());
//            MediaScannerConnection.scanFile(this.getApplicationContext(),
//                    new String[]{f.getPath()},
//                    new String[]{"image/jpeg"}, null);
//            fo.close();
//            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());
//
//            return f.getAbsolutePath();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
        return "";
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity((this))
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
                        //  Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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
        Context context= this.getApplicationContext();
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == context.) {
//            return;
//        }

        if (requestCode == GALLERY) {
            if (data != null) {
                //Transforms image data to a uri
                Uri contentURI = data.getData();
                //Get Reference to firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                //Create a folder called images in storage
                StorageReference imagesRef = storageRef.child("images");
                StorageReference userRef = imagesRef.child(mAuth.getUid());
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filename = mAuth.getUid() + "_" + timeStamp;
                StorageReference fileRef = userRef.child(filename);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    imageview.setImageBitmap(bitmap);
                    //Start of Upload Task
                    UploadTask uploadTask = fileRef.putFile(contentURI);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(CreatingItem.this, "Set Picture failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            setDownloadUrl(taskSnapshot.getUploadSessionUri());
                            //Toast.makeText(ConvergeFeed.this, "New photo uploaded", Toast.LENGTH_SHORT).show();
                            // save image to database
                            //String key = getDb().child("images").push().getKey();
                            //Image image = new Image(key, mAuth.getUid(), downloadUrl.toString());
                            //getDb().child("images").child(key).setValue(image);


                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreatingItem.this.getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Uri contentURI = getImageUri(this,thumbnail);
            //Get Reference to firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            //Create a folder called images in storage
            StorageReference imagesRef = storageRef.child("images");
            StorageReference userRef = imagesRef.child(mAuth.getUid());
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = mAuth.getUid() + "_" + timeStamp;
            StorageReference fileRef = userRef.child(filename);
            UploadTask uploadTask = fileRef.putFile(contentURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(CreatingItem.this, "Set Picture failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    setDownloadUrl(taskSnapshot.getUploadSessionUri());
                    //Toast.makeText(ConvergeFeed.this, "New photo uploaded", Toast.LENGTH_SHORT).show();
                    // save image to database
                    //String key = getDb().child("images").push().getKey();
                    //Image image = new Image(key, mAuth.getUid(), downloadUrl.toString());
                    //getDb().child("images").child(key).setValue(image);


                }
            });


            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(this.getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    public DatabaseReference getDb() {
        return myDBRef;
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public Uri getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(Uri downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}





