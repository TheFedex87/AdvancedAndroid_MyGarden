package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.PlantDetailActivity;
import com.example.android.mygarden.utils.PlantUtils;

/**
 * Created by feder on 08/04/2018.
 */

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewFactory(this.getApplicationContext());
    }
}

class GridRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context context;
    private Cursor cursor;

    public GridRemoteViewFactory(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) cursor.close();
        cursor = context.getContentResolver().query(
                PlantContract.PlantEntry.CONTENT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME);

    }

    @Override
    public void onDestroy() {
        if (cursor != null)
            cursor.close();
    }

    @Override
    public int getCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (cursor == null || cursor.getCount() == 0) return null;
        cursor.moveToPosition(i);

        long plantId = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry._ID));
        int plantType = cursor.getInt(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE));
        long createdAt = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME));
        long wateredAt = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();

        int imgRes = PlantUtils.getPlantImageRes(context, timeNow - createdAt, timeNow - wateredAt, plantType);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);

        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        Intent fillIntent = new Intent(context, PlantDetailActivity.class);
        fillIntent.putExtras(bundle);
        views.setOnClickFillInIntent(R.id.widget_plant_image, fillIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
