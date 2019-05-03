package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.tdp2.eukanuber.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterDriverUserActivity extends BaseActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    File photoFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro Conductor");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
       /*     Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();


            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);*/
            String encodedBase64 = null;
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(photoFile);
                byte[] bytes = new byte[(int)photoFile.length()];
                fileInputStreamReader.read(bytes);
                encodedBase64 = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
                Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView imageView = findViewById(R.id.imagePhoto);
                imageView.setImageBitmap(decodedByte);
                System.out.print(encodedBase64);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void goRegisterDriverCar(View view) {
        Intent intent = new Intent(this, RegisterDriverCarActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void takeAPhoto(View view){
        dispatchTakePictureIntent();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

  /*  private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // Ensure that there's a camera activity to handle the intent
      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
          // Create the File where the photo should go
          photoFile = null;
          try {
              photoFile = createImageFile();
          } catch (IOException ex) {
            System.out.print(ex.getMessage());
          }
          // Continue only if the File was successfully created
          if (photoFile != null) {
              Uri photoURI = FileProvider.getUriForFile(this,
                      "com.tdp2.eukanuber.fileprovider",
                      photoFile);
              takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
              startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
          }
      }
  }


}
