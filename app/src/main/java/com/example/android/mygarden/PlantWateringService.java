package com.example.android.mygarden;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

/**
 * Created by feder on 08/04/2018.
 */

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER_PLANTS = "com.example.android.mygarden.action.water_plants";

    public PlantWateringService(){
        super(PlantWateringService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String action = intent.getAction();
            if (ACTION_WATER_PLANTS.equals(action)){
                handleActionWaterPlants();
            }
        }
    }

    private void handleActionWaterPlants(){
        ContentValues cv = new ContentValues();
        long timeNow = System.currentTimeMillis();
        cv.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);

        getContentResolver().update(
                PlantContract.PlantEntry.CONTENT_URI,
                cv,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME+">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)}
        );
    }
}
