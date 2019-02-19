package com.imageProcessor.Model;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import com.imageProcessor.Utils;

import java.util.ArrayList;

public class BD {
    private static String CREATE_BD = "CREATE TABLE IF NOT EXISTS IMAGES(Id INTEGER PRIMARY KEY AUTOINCREMENT, operation VARCHAR, image BLOB)";
    private static String LOAD_BD = "SELECT * FROM IMAGES";
    private static String SELECT_IDS = "SELECT id FROM IMAGES";

    private static SQLiteHelper sqLiteHelper;

    public void createBD(Context context) {
        sqLiteHelper = new SQLiteHelper(context, "ImagesDB.sqlite", null, 1);
        sqLiteHelper.queryData(CREATE_BD);
    }

    public void insertData(String operation, ImageView view) {
        sqLiteHelper.insertData(operation, Utils.imageViewToByte(view));
    }

    public Cursor getData() {
        return sqLiteHelper.getData(LOAD_BD);
    }

    public void deleteData(int id) {
        Cursor c = sqLiteHelper.getData(SELECT_IDS);
        ArrayList<Integer> arrID = new ArrayList<Integer>();
        while (c.moveToNext()) {
            arrID.add(c.getInt(0));
        }
        sqLiteHelper.deleteData(arrID.get(id));
    }
}
