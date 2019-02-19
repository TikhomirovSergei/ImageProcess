package com.imageProcessor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;
import com.imageProcessor.Model.ItemBD;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    public static final String ROTATE = "Rotate";
    public static final String INVERT_COLORS = "Invert colors";
    public static final String MIRROR_IMAGE = "Mirror image";

    Bitmap loadImageFromUri(Context context, Uri uri) throws Exception {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));

            return Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    static Bitmap updateSaturation(Bitmap source, float settingSat) {
        Bitmap bitmapResult = Bitmap
                .createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(source, 0, 0, paint);

        return bitmapResult;
    }

    public static Bitmap flip(Bitmap src, boolean type) {
        Matrix matrix = new Matrix();

        if(type) //VERTICAL
            matrix.preScale(1.0f, -1.0f);
        else //HORIZONTAL
            matrix.preScale(-1.0f, 1.0f);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        storageDir.mkdirs();
        storageDir = new File(Environment.getExternalStorageDirectory() + File.separator + imageFileName + ".jpg");
        storageDir.createNewFile();
        return storageDir;
    }

    public void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    ArrayList<ItemBD> getBD(Cursor cursor) {
        ArrayList<ItemBD> list = new ArrayList<>();

        int length = cursor.getCount() - 1;
        int id = 0;
        for (int i = length; i >= 0; i--) {
            id++;
            cursor.moveToPosition(i);
            //int id = cursor.getInt(0);
            String operation = cursor.getString(1);
            byte[] image = cursor.getBlob(2);

            list.add(new ItemBD(operation, image, id));
        }

        return list;
    }
}
