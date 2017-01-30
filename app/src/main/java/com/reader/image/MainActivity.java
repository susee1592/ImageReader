package com.reader.image;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
/**
 * Created by suseendran on 30/1/17.
 */
public class MainActivity extends AppCompatActivity {
    private final int TAKE_PICTURE = 0;
    private final int SELECT_FILE = 1;
    String lang = "eng";
    Button gallery;
    Button camera;
    MenuItem menu_lang;

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ImageReader");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg");

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = (Button) findViewById(R.id.gallery);
        camera = (Button) findViewById(R.id.camera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImageFromSdCard();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImageFromCamera();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lang_menu, menu);

        menu_lang = menu.findItem(R.id.menu_lang);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_menu1) {
            lang = "eng";
            menu_lang.setTitle("eng");
            return true;
        }
        if (id == R.id.action_menu2) {
            lang = "tam";
            menu_lang.setTitle("tam");
            return true;
        }
        if (id == R.id.action_menu3) {
            lang = "hin";
            menu_lang.setTitle("hin");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void captureImageFromSdCard() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, SELECT_FILE);
    }

    public void captureImageFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        String imageFilePath = null;
        Intent results = new Intent(this, OcrActivity.class);


        switch (requestCode) {
            case TAKE_PICTURE:
                imageFilePath = getOutputMediaFileUri().getPath();

                results.putExtra("IMAGE_PATH", imageFilePath);
                results.putExtra("LANG", lang);
                startActivity(results);
                break;
            case SELECT_FILE: {
                Uri imageUri = data.getData();

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cur = managedQuery(imageUri, projection, null, null, null);
                cur.moveToFirst();
                imageFilePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));

                results.putExtra("IMAGE_PATH", imageFilePath);
                results.putExtra("LANG", lang);
                startActivity(results);
                break;
            }
        }
    }
}
