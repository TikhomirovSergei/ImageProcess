package com.imageProcessor.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.imageProcessor.MainActivity;
import com.imageProcessor.R;
import com.imageProcessor.Utils;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class ItemDBListAdapter extends BaseAdapter {
    private class ViewHolder {
        TextView txt_ItemId;
        TextView img_ItemOperation;
        ImageView img_ItemImage;
    }

    private Context context;
    private int layout;
    private ArrayList<ItemBD> itemsList;
    private Activity activity;
    private BD bd;

    public ItemDBListAdapter(Context context, int layout, ArrayList<ItemBD> itemsList, Activity activity, BD bd) {
        this.context = context;
        this.layout = layout;
        this.itemsList = itemsList;
        this.activity = activity;
        this.bd = bd;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txt_ItemId = (TextView) row.findViewById(R.id.txt_ItemId);
            holder.img_ItemOperation = (TextView) row.findViewById(R.id.img_ItemOperation);
            holder.img_ItemImage = (ImageView) row.findViewById(R.id.img_ItemImage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ItemBD imageItem = itemsList.get(position);

        holder.txt_ItemId.setText(String.valueOf(itemsList.size() - position - 1));

        final byte[] image = imageItem.getImage();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.img_ItemImage.setImageBitmap(bitmap);

        switch (imageItem.getOperation()) {
            case Utils.ROTATE:
                holder.img_ItemOperation.setText(context.getText(R.string.btn_rotate));
                break;
            case Utils.INVERT_COLORS:
                holder.img_ItemOperation.setText(context.getText(R.string.btn_invertColors));
                break;
            case Utils.MIRROR_IMAGE:
                holder.img_ItemOperation.setText(context.getText(R.string.btn_mirrorImage));
                break;
            default:
                holder.img_ItemOperation.setText(context.getText(R.string.undefinedOperation));
        }

        if ((itemsList.size() - position - 1) % 2 == 0) {
            row.setBackgroundColor(rgb(220,220,220));
        } else {
            row.setBackgroundColor(rgb(255,255,255));
        }

        final ViewHolder finalHolder = holder;
        final View finalRow = row;
        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                int id = Integer.valueOf((String) finalHolder.txt_ItemId.getText());
                showMenu(finalRow, id);
            }
        });

        return row;
    }

    @SuppressLint("ResourceType")
    private void showMenu(View view, final int id) {
        PopupMenu menu = new PopupMenu (context, view);
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
            @Override
            public boolean onMenuItemClick (MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.item_process: {
                        ItemBD imageItem = itemsList.get(itemsList.size() - id - 1);
                        final byte[] image = imageItem.getImage();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        ImageView imgMain = activity.findViewById(R.id.img_main);
                        imgMain.setImageBitmap(bitmap);
                        break;
                    }
                    case R.id.item_delete: {
                        bd.deleteData(id);
                        MainActivity.loadData();
                        break;
                    }
                    default: break;
                }
                return true;
            }
        });
        menu.inflate (R.layout.menu_layout);
        menu.show();
    }
}
