package com.example.akshay.akshay5;

/**
 * Created by AKSHAY on 25-01-2018.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;



public class DashboardActivity extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;
    public static EditText tweetText;
    Button CaptureImageFromCamera, UploadImageToServer, GoToProfile;
    ImageView ImageViewHolder;
    EditText imageName;
    ProgressDialog progressDialog;
    Intent intent;
    Bitmap bitmap;

    boolean check = true;

    String GetImageNameFromEditText;

    String GetTweetDataFromEditText;   //variable to store text which user want to tweet

    String ImageNameFieldOnServer = "image_name";

    String ImagePathFieldOnServer = "image_path";

    String ImageUploadPathOnSever = "https://arty-crafty-radiato.000webhostapp.com/AndroidApp/capture_img_upload_to_server.php";

    String username;

    //Fetching Tweets
    Button click;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        CaptureImageFromCamera = findViewById(R.id.button);
        ImageViewHolder = findViewById(R.id.imageView);
        UploadImageToServer = findViewById(R.id.button2);
        imageName = findViewById(R.id.editText);
        tweetText = findViewById(R.id.editText3);


        //twitter username fetching

        username = getIntent().getStringExtra("username");


        EnableRuntimePermissionToAccessCamera();
        OnClickButtonListener();

        CaptureImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 7);


            }
        });

        UploadImageToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = imageName.getText().toString();

                ImageUploadToServerFunction();

            }
        });

        //FT

        click = findViewById(R.id.button5);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FetchDataActivity process = new FetchDataActivity();
                process.execute();


            }
        });


    }


    public void OnClickButtonListener() {

        GoToProfile = findViewById(R.id.button4);
        GoToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.akshay.akshay5.ProfileActivity");
                intent.putExtra("username", username);
                startActivity(intent);
                // tweetText.setText("");
            }
        });

    }

    //FUNCTION ADDED RECENTLY
     private Uri createFileFromBitmap(Bitmap bitmap) throws IOException {
        String name = "image:";
        File f = new File(getCacheDir(), name + System.currentTimeMillis()+ ".jpg");
        f.createNewFile();

    //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

    //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return Uri.fromFile(f);
    }

    public void shareUsingTwitterNativeComposer(View view) {
        
        GetTweetDataFromEditText = tweetText.getText().toString();   //converting to string
     
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(DashboardActivity.this)
                .session(session)
                .text(GetTweetDataFromEditText)
                .hashtags("#twitter")
                .darkTheme()
                .createIntent();
        startActivity(intent);


    }

    // Start activity for result method to Set captured image on image view after click.
    // Uri filePath;
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri filePath;
        if (requestCode == 7 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageViewHolder.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 7 && resultCode == RESULT_OK) {
            filePath = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageViewHolder.setImageBitmap(imageBitmap);

        }
    }  */
    
     Uri filePath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageViewHolder.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
          if (requestCode == 7 && resultCode == RESULT_OK) {
            filePath = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageViewHolder.setImageBitmap(imageBitmap);


            // Create a file n then get Object of URI
            try {
                filePath=   createFileFromBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                filePath=null;
                Log.e(getClass().getName(), "onActivityResult: some problem occur to getting URI   "+e.getMessage());
            }
        }
    }


    /**
     * method to share picked/capture image with text using Twitter Native Composer
     * NOTE : For this you should authenticate user before sharing image as the builder required TwitterSession.
     * It does not depend on the Twitter for Android app being installed.
     *
     * @param view
     */
 /*   public void shareUsingTwitterNativeComposer(View view) {
        //check if user has picked/captured image or not
        if (filePath != null) {
            TwitterSession session = TwitterCore.getInstance().getSessionManager()
                    .getActiveSession();//get the active session
            if (session != null) {
                //if active session is not null start sharing image
                shareUsingNativeComposer(session);
            }  else {
                //if there is no active session then ask user to authenticate
                //authenticateUser();
                login();
            }
        } else {
            //if not then show dialog to pick/capture image
            Toast.makeText(this, "Please select image first to share.", Toast.LENGTH_SHORT).show();
            EnableRuntimePermissionToAccessCamera();
        }
    }
*/

    /**
     * method to share image using Twitter Native Kit composer
     *
     * @param session of the authenticated user
    put comment here!
    private void shareUsingNativeComposer(TwitterSession session) {
    Intent intent = new ComposerActivity.Builder(this)
    .session(session)//Set the TwitterSession of the User to Tweet
    .image(filePath)//Attach an image to the Tweet
    .text("This is Native Kit Composer Tweet!!")//Text to prefill in composer
    .hashtags("#android")//Hashtags to prefill in composer
    .createIntent();//finally create intent
    startActivity(intent);
    }   */

    /**
     * method call to authenticate user
     */



 /*  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (requestCode == 7 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();



            try {

                // Adding captured image in bitmap.
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // adding captured image in imageview.
                ImageViewHolder.setImageBitmap(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    } */

    // Requesting runtime permission to access camera.
    public void EnableRuntimePermissionToAccessCamera() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(DashboardActivity.this,
                Manifest.permission.CAMERA)) {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(DashboardActivity.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    // Upload captured image online on server function.
    public void ImageUploadToServerFunction() {


        // Locate the image in res > drawable-hdpi
        bitmap = ((BitmapDrawable) ImageViewHolder.getDrawable()).getBitmap();

        ByteArrayOutputStream byteArrayOutputStreamObject;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);


        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                // Showing progress dialog at image upload time.
                progressDialog = ProgressDialog.show(DashboardActivity.this, "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(DashboardActivity.this, string1, Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                ImageViewHolder.setImageResource(android.R.color.transparent);
                imageName.setText("");

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put(ImageNameFieldOnServer, GetImageNameFromEditText);

                HashMapParams.put(ImagePathFieldOnServer, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ImageUploadPathOnSever, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(DashboardActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(DashboardActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

}
