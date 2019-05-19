package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.gson.JsonObject;
import com.tdp2.eukanuber.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterDriverUserActivity extends BaseActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String USER_REGISTER_SETTINGS = "USER_REGISTER";

    File profilePictureFile = null;
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
            Bitmap image = rotateAndResizeFile(profilePictureFile);
            ImageView profilePictureImage = findViewById(R.id.profilePicture);
            profilePictureImage.setImageBitmap(image);
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            profilePictureFile = null;
        }

    }

    private Bitmap rotateAndResizeFile(File file) {
        Bitmap image = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap bitmapImageRotated = rotateBitmapIfNecessary(bitmapImage, file);
            image = resizeImage(bitmapImageRotated);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;

    }

    private Bitmap rotateBitmapIfNecessary(Bitmap bitmap, File file) {
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface ei = new ExifInterface(file.getAbsolutePath());
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

    private Bitmap resizeImage(Bitmap source) {
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();
        int dstWidth = 0;
        int dstHeight = 0;
        if (srcWidth > srcHeight) {
            dstWidth = 400;
            dstHeight = (dstWidth * srcHeight) / srcWidth;
        }
        if (srcWidth <= srcHeight) {
            dstHeight = 400;
            dstWidth = (dstHeight * srcWidth) / srcHeight;
        }
        return Bitmap.createScaledBitmap(source, dstWidth, dstHeight, false);
    }

    public void goRegisterDriverCar(View view) {
        EditText name = mActivity.findViewById(R.id.inputRegisterName);
        EditText lastname = mActivity.findViewById(R.id.inputRegisterLastname);
        if (name.getText().toString().isEmpty()) {
            showMessage("El nombre es obligatorio");
            return;
        }
        if (lastname.getText().toString().isEmpty()) {
            showMessage("El apellido es obligatorio");
            return;
        }
        if (profilePictureFile == null) {
            showMessage("La imagen de perfil es obligatoria");
            return;
        }
        JsonObject userRegister = new JsonObject();
        try {
            userRegister.addProperty("name", name.getText().toString());
            userRegister.addProperty("lastname", lastname.getText().toString());
            Bitmap profileImage = rotateAndResizeFile(profilePictureFile);
            userRegister.addProperty("profilePicture", bitmapToBase64(profileImage));
            SharedPreferences settings = getSharedPreferences(RegisterDriverUserActivity.USER_REGISTER_SETTINGS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("userRegister", userRegister.toString());
            editor.commit();
            Intent intent = new Intent(this, RegisterDriverCarActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }


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
            profilePictureFile = null;
            try {
                profilePictureFile = createImageFile();
            } catch (IOException ex) {
                System.out.print(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (profilePictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tdp2.eukanuber.fileprovider",
                        profilePictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

}
