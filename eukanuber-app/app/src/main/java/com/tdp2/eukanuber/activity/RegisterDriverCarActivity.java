package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserCar;
import com.tdp2.eukanuber.model.UserImage;
import com.tdp2.eukanuber.model.UserRegisterRequest;
import com.tdp2.eukanuber.services.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDriverCarActivity extends BaseActivity {
    File carPictureFile = null;
    File licensePictureFile = null;
    File insurancePictureFile = null;
    static final int REQUEST_TAKE_PHOTO_CAR = 1;
    static final int REQUEST_TAKE_PHOTO_LICENSE = 2;
    static final int REQUEST_TAKE_PHOTO_INSURANCE = 3;
    Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver_car);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = this;
        getSupportActionBar().setTitle("Registro Conductor");
        ImageButton carImageViewButton = findViewById(R.id.carImageViewButton);
        carImageViewButton.setVisibility(View.GONE);
        ImageButton licenseImageViewButton = findViewById(R.id.licenseImageViewButton);
        licenseImageViewButton.setVisibility(View.GONE);
        ImageButton insuranceImageViewButton = findViewById(R.id.insuranceImageViewButton);
        insuranceImageViewButton.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO_CAR && resultCode == RESULT_OK) {
            if (carPictureFile != null) {
                ImageButton carImageViewButton = findViewById(R.id.carImageViewButton);
                carImageViewButton.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO_CAR && resultCode == RESULT_CANCELED) {
            carPictureFile = null;
        }
        if (requestCode == REQUEST_TAKE_PHOTO_INSURANCE && resultCode == RESULT_OK) {
            if (insurancePictureFile != null) {
                ImageButton insuranceImageViewButton = findViewById(R.id.insuranceImageViewButton);
                insuranceImageViewButton.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO_INSURANCE && resultCode == RESULT_CANCELED) {
            insurancePictureFile = null;
        }

        if (requestCode == REQUEST_TAKE_PHOTO_LICENSE && resultCode == RESULT_OK) {
            if (licensePictureFile != null) {
                ImageButton licenseImageViewButton = findViewById(R.id.licenseImageViewButton);
                licenseImageViewButton.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO_LICENSE && resultCode == RESULT_CANCELED) {
            licensePictureFile = null;
        }
    }


    public void viewCarImage(View view) {
        viewImage(carPictureFile);
    }

    public void viewLicenseImage(View view) {
        viewImage(licensePictureFile);
    }

    public void viewInsuranceImage(View view) {
        viewImage(insurancePictureFile);
    }

    public void viewImage(File file) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.tdp2.eukanuber.fileprovider",
                    file);
            intent.setDataAndType(photoURI, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }


    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void loadCarImage(View view) {
        dispatchTakePictureCarIntent();
    }

    private void dispatchTakePictureCarIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            carPictureFile = null;
            try {
                carPictureFile = createImageFile();
            } catch (IOException ex) {
                System.out.print(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (carPictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tdp2.eukanuber.fileprovider",
                        carPictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_CAR);
            }
        }
    }

    public void loadLicenseImage(View view) {
        dispatchTakePictureLicenseIntent();
    }


    private void dispatchTakePictureLicenseIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            licensePictureFile = null;
            try {
                licensePictureFile = createImageFile();
            } catch (IOException ex) {
                System.out.print(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (licensePictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tdp2.eukanuber.fileprovider",
                        licensePictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_LICENSE);
            }
        }
    }

    public void loadInsuranceImage(View view) {
        dispatchTakePictureInsuranceIntent();
    }


    private void dispatchTakePictureInsuranceIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            insurancePictureFile = null;
            try {
                insurancePictureFile = createImageFile();
            } catch (IOException ex) {
                System.out.print(ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (insurancePictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tdp2.eukanuber.fileprovider",
                        insurancePictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_INSURANCE);
            }
        }
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


    public void submitRegisterDriver(View view) {
        EditText carBrand = findViewById(R.id.inputRegisterCarBrand);
        EditText carModel = findViewById(R.id.inputRegisterCarModel);
        EditText carPatent = findViewById(R.id.inputRegisterCarPatent);
        if (carBrand.getText().toString().isEmpty()) {
            showMessage("La marca del vehículo es obligatoria");
            return;
        }
        if (carModel.getText().toString().isEmpty()) {
            showMessage("El modelo del vehículo es obligatorio");
            return;
        }
        if (carPatent.getText().toString().isEmpty()) {
            showMessage("La patente del vehículo es obligatoria");
            return;
        }
        if (carPictureFile == null) {
            showMessage("La imagen frontal del vehículo es obligatoria");
            return;
        }
        if (licensePictureFile == null) {
            showMessage("La imagen de la licencia de conducir es obligatoria");
            return;
        }
        if (insurancePictureFile == null) {
            showMessage("La imagen del seguro es obligatoria");
            return;
        }
        SharedPreferences settings = getSharedPreferences(RegisterDriverUserActivity.USER_REGISTER_SETTINGS, 0);
        SharedPreferences settingsSecurity = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);

        String userRegisterStr = settings.getString("userRegister", null);
        JsonParser parser = new JsonParser();
        JsonObject userRegister = (JsonObject) parser.parse(userRegisterStr);
        userRegister.addProperty("carBrand", carBrand.getText().toString());
        userRegister.addProperty("carModel", carModel.getText().toString());
        userRegister.addProperty("carPatent", carPatent.getText().toString());
        userRegister.addProperty("carPicture", getBase64FromFile(carPictureFile));
        userRegister.addProperty("licensePicture", getBase64FromFile(licensePictureFile));
        userRegister.addProperty("insurancePicture", getBase64FromFile(insurancePictureFile));
        AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setFbId(accessTokenFacebook.getUserId());
        userRegisterRequest.setFbAccessToken(accessTokenFacebook.getToken());
        userRegisterRequest.setFirstName(userRegister.get("name").toString());
        userRegisterRequest.setLastName(userRegister.get("lastname").toString());
        userRegisterRequest.setUserType(User.USER_TYPE_DRIVER);
        userRegisterRequest.setPosition("");
        UserImage profileImage = new UserImage();
        profileImage.setFileName(User.PROFILE_IMAGE_NAME);
        profileImage.setFileContent(userRegister.get("profilePicture").toString());
        userRegisterRequest.addImage(profileImage);

        UserImage carImage = new UserImage();
        carImage.setFileName(User.CAR_IMAGE_NAME);
        carImage.setFileContent(userRegister.get("carPicture").toString());
        userRegisterRequest.addImage(carImage);

        UserImage licenseImage = new UserImage();
        licenseImage.setFileName(User.LICENSE_IMAGE_NAME);
        licenseImage.setFileContent(userRegister.get("licensePicture").toString());
        userRegisterRequest.addImage(licenseImage);

        UserImage insuranceImage = new UserImage();
        insuranceImage.setFileName(User.INSURANCE_IMAGE_NAME);
        insuranceImage.setFileContent(userRegister.get("insurancePicture").toString());
        userRegisterRequest.addImage(insuranceImage);

        UserCar userCar = new UserCar();
        userCar.setBrand(userRegister.get("carBrand").toString());
        userCar.setModel(userRegister.get("carModel").toString());
        userCar.setPlateNumber(userRegister.get("carPatent").toString());
        userRegisterRequest.setCar(userCar);
        UserService userService = new UserService(this);
        Call<LoginResponse> call = userService.register(userRegisterRequest);
        ProgressDialog dialog = new ProgressDialog(RegisterDriverCarActivity.this);
        dialog.setMessage("Espere un momento por favor");
        dialog.show();
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dialog.dismiss();
                if (response.code() == HttpURLConnection.HTTP_CONFLICT ||
                        response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    showMessage("Cuenta de facebook inválida. Debe tener más de 10 amigos para poder utilizarla.");
                    return;
                }
                LoginResponse loginResponse = response.body();
                AppSecurityManager.login(settingsSecurity, userRegisterRequest.getFbAccessToken(), userRegisterRequest.getFbId(), loginResponse.getToken(), loginResponse.getUser());
                if (loginResponse.getUser().getUserType().equals(User.USER_TYPE_DRIVER)) {
                    Intent intent = new Intent(mActivity, HomeDriverActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mActivity, HomeClientActivity.class);
                    startActivity(intent);
                }
                return;
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v("Register Error", t.getMessage());
                showMessage("Ha ocurrido un error. Intente luego.");
            }
        });
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("userRegister");
        editor.commit();
    }

    private Bitmap getBitmapFromFile(File file) {
        Bitmap bitmapReponse = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bitmapReponse = rotateBitmapIfNecessary(bitmapImage, file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapReponse;
    }

    private String getBase64FromFile(File file) {
        String base64File = "";
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            base64File = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return base64File;

    }
}
