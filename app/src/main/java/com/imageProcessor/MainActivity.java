package com.imageProcessor;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import com.imageProcessor.Model.BD;
import com.imageProcessor.Model.ItemBD;
import com.imageProcessor.Model.ItemDBListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 999;
    private Bitmap DEFAULT_IMAGE = null;
    private Uri outputFileUri;

    private ImageView imgMain = null;
    private Button btnRotate = null;
    private Button btnInvertColors = null;
    private Button btnMirrorImage = null;
    private GridView gridView = null;

    private static ArrayList<ItemBD> list;
    private static ItemDBListAdapter adapter = null;

    private static Utils utils = new Utils();
    private static BD bd = new BD();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bd.createBD(this);

        Drawable myDrawable = getResources().getDrawable(R.drawable.default_image);
        DEFAULT_IMAGE = ((BitmapDrawable) myDrawable).getBitmap();

        init();
        loadData();

        imgMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_CODE
                );
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!((BitmapDrawable) imgMain.getDrawable()).getBitmap().equals(DEFAULT_IMAGE)) {
                    Bitmap bitmap = ((BitmapDrawable) imgMain.getDrawable()).getBitmap();
                    Bitmap rotateBitmap = Utils.RotateBitmap(bitmap, -90);
                    imgMain.setImageBitmap(rotateBitmap);
                    bd.insertData(Utils.ROTATE, imgMain);
                    loadData();
                } else {
                    utils.showToast(MainActivity.this, getString(R.string.err_notSelectedImage));
                }
            }
        });

        btnInvertColors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!((BitmapDrawable) imgMain.getDrawable()).getBitmap().equals(DEFAULT_IMAGE)) {
                    Bitmap bitmap = ((BitmapDrawable) imgMain.getDrawable()).getBitmap();
                    Bitmap rotateBitmap = Utils.updateSaturation(bitmap, 0);
                    imgMain.setImageBitmap(rotateBitmap);
                    bd.insertData(Utils.INVERT_COLORS, imgMain);
                    loadData();
                } else {
                    utils.showToast(MainActivity.this, getString(R.string.err_notSelectedImage));
                }
            }
        });

        btnMirrorImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!((BitmapDrawable) imgMain.getDrawable()).getBitmap().equals(DEFAULT_IMAGE)) {
                    Bitmap bitmap = ((BitmapDrawable) imgMain.getDrawable()).getBitmap();
                    Bitmap rotateBitmap = Utils.flip(bitmap, false);
                    imgMain.setImageBitmap(rotateBitmap);
                    bd.insertData(Utils.MIRROR_IMAGE, imgMain);
                    loadData();
                } else {
                    utils.showToast(MainActivity.this, getString(R.string.err_notSelectedImage));
                }
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bitmap bitmap = ((BitmapDrawable) imgMain.getDrawable()).getBitmap();
        outState.putParcelable("bitmap", bitmap);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Bitmap bitmap = savedInstanceState.getParcelable("bitmap");
        imgMain.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDialog();
            } else {
                utils.showToast(this, getString(R.string.err_permissionDenied));
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                if (action == null) {
                    isCamera = false;
                } else {
                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }
            final Uri selectedImageUri;
            if (isCamera) {
                selectedImageUri = outputFileUri;
            } else {
                selectedImageUri = data.getData();
            }

            SetBitmap(selectedImageUri);
            /*try {
                imgMain.setImageBitmap(utils.loadImageFromUri(this, selectedImageUri));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        imgMain = findViewById(R.id.img_main);
        btnRotate = findViewById(R.id.btn_rotate);
        btnInvertColors = findViewById(R.id.btn_invertColors);
        btnMirrorImage = findViewById(R.id.btn_mirrorImage);
        gridView = findViewById(R.id.gw_Items);

        list = new ArrayList<>();
        adapter = new ItemDBListAdapter(this, R.layout.image_item, list, this, bd);
        gridView.setAdapter(adapter);
    }
    
    public void SetBitmap(Uri selectedImageUri) {
        try {
            imgMain.setImageBitmap(utils.loadImageFromUri(this, selectedImageUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        Cursor cursor = bd.getData();
        list.clear();

        ArrayList<ItemBD> new_list = utils.getBD(cursor);
        list.addAll(new_list);
        adapter.notifyDataSetChanged();
    }

    private void startDialog() {
        try {
            final File sdImageMainDirectory = utils.createImageFile();
            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                cameraIntents.add(intent);
            }

            // Filesystem.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.dialog_selectSource));

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, REQUEST_CODE);
        } catch (Exception ex) {
            utils.showToast(this, ex.toString());
        }
    }
}
