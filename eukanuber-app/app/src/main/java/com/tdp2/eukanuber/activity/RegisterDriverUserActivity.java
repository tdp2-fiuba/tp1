package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterDriverUserActivity extends BaseActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActivity = this;

        getSupportActionBar().setTitle("Registro Conductor");
        AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
    //    ImageView profilePicture = findViewById(R.id.profilePicture);
   //     profilePicture.setVisibility(View.GONE);
        GraphRequest request = GraphRequest.newMeRequest(
                accessTokenFacebook,
                (object, response) -> {
                    try {
                        EditText name = mActivity.findViewById(R.id.inputRegisterName);
                        EditText lastname = mActivity.findViewById(R.id.inputRegisterLastname);
                        name.setText(object.getString("first_name"));
                        lastname.setText(object.getString("last_name"));
                    } catch (Exception ex) {
                        System.out.print(ex.getMessage());
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String encodedBase64 = null;
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(photoFile);
                byte[] bytes = new byte[(int) photoFile.length()];
                fileInputStreamReader.read(bytes);
                encodedBase64 = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap bitmapImageRotated = rotateBitmapIfNecessary(bitmapImage);
                ImageView profilePicture = findViewById(R.id.profilePicture);
                profilePicture.setImageBitmap(bitmapImageRotated);
          //      profilePicture.setVisibility(View.VISIBLE);
                System.out.print(encodedBase64);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Bitmap rotateBitmapIfNecessary(Bitmap bitmap) {
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface ei = new ExifInterface(photoFile.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        return rotatedBitmap;

    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void goRegisterDriverCar(View view) {
        Intent intent = new Intent(this, RegisterDriverCarActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void takeAPhoto(View view) {
        dispatchTakePictureIntent();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
