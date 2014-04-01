package com.mbrite.blobphoto.app;


import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mbrite.blobphoto.common.Constants;
import com.mbrite.blobphoto.connection.RestClient;

import java.io.*;

public class ImageUploadActivity extends Activity {
    private static final int PICK_IMAGE = 1;
    private ImageView imgView;
    private Button upload;
    private EditText caption;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    private String filePath;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        imgView = (ImageView) findViewById(R.id.ImageView);
        upload = (Button) findViewById(R.id.Upload);
        caption = (EditText) findViewById(R.id.Caption);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please select image", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = ProgressDialog.show(ImageUploadActivity.this, "Uploading",
                            "Please wait...", true);
                    try {
                        new ImageUploadTask().execute();
                    } catch (Exception ex) {
                        Toast.makeText(
                                ImageUploadActivity.this,
                                String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.imageupload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ic_menu_gallery:
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            PICK_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(
                            getApplicationContext(),
                            String.format("Error: %s", e.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    try {
                        // IO FILE Manager
                        String fileManagerString = selectedImageUri.getPath();

                        // MEDIA GALLERY
                        String selectedImagePath = getPath(selectedImageUri);

                        if (selectedImagePath != null) {
                            filePath = selectedImagePath;
                        } else if (fileManagerString != null) {
                            filePath = fileManagerString;
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Unknown path",
                                    Toast.LENGTH_LONG).show();
                            Log.e("Bitmap", "Unknown path");
                        }

                        if (filePath != null) {
                            decodeFile(filePath);
                        } else {
                            bitmap = null;
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Internal error",
                                Toast.LENGTH_LONG).show();
                        Log.e(e.getClass().getName(), e.getMessage(), e);
                    }
                }
                break;
            default:
        }
    }

    class ImageUploadTask extends AsyncTask <Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... unused) {
            try {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("file", new FileBody(new File(filePath)));
                entity.addPart("desc", new StringBody(caption.getText()
                        .toString()));
                HttpResponse response = RestClient.INSTANCE.post(ImageUploadActivity.this, Constants.UPLOAD, entity);
                return response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(ImageUploadActivity.this,
                        String.format("Error: %s", e.getLocalizedMessage()),
                        Toast.LENGTH_LONG).show();
            }

            return -1;
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (statusCode != 200) {
                Toast.makeText(getApplicationContext(),
                        String.format("Failed to upload photo. Status Code: %d", statusCode),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Photo uploaded successfully",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ImageUploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
            break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        imgView.setImageBitmap(bitmap);
    }
}
