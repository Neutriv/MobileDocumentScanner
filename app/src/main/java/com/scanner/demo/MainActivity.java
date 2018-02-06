package com.scanner.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
//We need the library below to write the final PDF file which has our image converted to PDF
//The image class which will hold the bitmap image
//This class is required to read image into Image object


public class MainActivity extends AppCompatActivity {




    private static final int REQUEST_CODE = 99;
    private Button scanButton;
    private Button cameraButton;
    private Button mediaButton;
    private ImageView scannedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1235435645);
            return;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1235);
            return;
        }
        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA)) {
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    125);
            return;
        }

        init();
    }

    private void init() {
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());
        cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        mediaButton = findViewById(R.id.mediaButton);
        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
        scannedImageView = findViewById(R.id.scannedImage);
    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    protected void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                scannedImageView.setImageBitmap(bitmap);


                /*ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                scannedImageView.setColorFilter(filter);
                */

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/scanSample");
                myDir.mkdirs();
                String fname = "Image-" + "test" + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                Log.i("LOAD", root + fname);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //Create Document Object
                    Document convertBmpToPdf = new Document();
                    Log.e("try", "new document");
                    //Create PdfWriter for Document to hold physical file
                    //PdfWriter.getInstance(convertBmpToPdf, new FileOutputStream(db_path));
                    PdfWriter.getInstance(convertBmpToPdf, new FileOutputStream(root + "/scanSample" + "/aaaaa.pdf"));
                    Log.e("try", "pdf write");
                    convertBmpToPdf.open();
                    Log.e("try", "convert bmp to pdf open");
                    //Get the Bitmap image to Convert to PDF
                    //getImage is a static method, does not require object
                    Image image = Image.getInstance(root + "/scanSample/" + fname);


                    float scaler = ((convertBmpToPdf.getPageSize().getWidth() - convertBmpToPdf.leftMargin()
                            - convertBmpToPdf.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    //img.setAlignment(Image.LEFT| Image.TEXTWRAP);

                    Log.e("try", "get image");
                    //Add image to Document
                    convertBmpToPdf.add(image);
                    Log.e("try", "z convert bpm to pdf");
                    //Close Document
                    convertBmpToPdf.close();
                    System.out.println("Successfully Converted BMP to PDF in iText");
                    Log.e("try", "zakonczenie");


                } catch (Exception i1) {
                    i1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
