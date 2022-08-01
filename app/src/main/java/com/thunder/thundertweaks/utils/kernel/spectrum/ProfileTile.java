package com.thunder.thundertweaks.utils.kernel.spectrum;

import android.content.Context;
import android.util.SparseArray;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.thunder.thundertweaks.R;
import com.thunder.thundertweaks.utils.AppSettings;
import com.thunder.thundertweaks.utils.Utils;

/**
 * Created by Morogoku on 01/08/2017.
 * Modded by @nalas on 15/06/2019 - added ThundeRStormS card
 */

@TargetApi(Build.VERSION_CODES.N)
public class ProfileTile extends TileService {

    private String ENABLE;
 
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private boolean click = false;
    private boolean mIsSupported = Spectrum.supported();

    @Override
    public void onStartListening() {
        resetTileStatus();
    }

    @Override
    public void onClick() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile and set profile
        if (!mIsSupported){
            newLabel = "No Spectrum support";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_logo);
            newState = Tile.STATE_INACTIVE;
        } else {
            if (isActive && click) {
                newLabel = "Spectrum Gaming";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_game);
                newState = Tile.STATE_ACTIVE;
                click = false;
                Spectrum.setProfile(3);
                AppSettings.saveInt("spectrum_profile", 3, getApplicationContext());
            } else if (!isActive && click) {
                newLabel = "Spectrum Battery";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_battery);
                newState = Tile.STATE_ACTIVE;
                click = true;
                Spectrum.setProfile(2);
                AppSettings.saveInt("spectrum_profile", 2, getApplicationContext());
            } else if (isActive && !click) {
                newLabel = "Spectrum Performance";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_performance);
                newState = Tile.STATE_ACTIVE;
                click = true;
                Spectrum.setProfile(1);
                AppSettings.saveInt("spectrum_profile", 1, getApplicationContext());
            } else {
                newLabel = "Spectrum Balance";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_balanced);
                newState = Tile.STATE_ACTIVE;
                click = false;
                Spectrum.setProfile(0);
                AppSettings.saveInt("spectrum_profile", 0, getApplicationContext());
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }

    private boolean getServiceStatus() {

		boolean isActive = AppSettings.getBoolean(SERVICE_STATUS_FLAG, false, getApplicationContext());
        isActive = !isActive;

        AppSettings.saveBoolean(SERVICE_STATUS_FLAG, isActive, getApplicationContext());

        return isActive;
    }

    private void resetTileStatus() {
        int profile = Utils.strToInt(Spectrum.getProfile());
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
		int newState;

        // Update tile
        if (!mIsSupported){
            newLabel = "No Spectrum support";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_logo);
            newState = Tile.STATE_INACTIVE;
        } else {
            if (profile == 3) {
                newLabel = "Spectrum Gaming";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_game);
                newState = Tile.STATE_ACTIVE;
                click = false;
            } else if (profile == 2) {
                newLabel = "Spectrum Battery";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_battery);
                newState = Tile.STATE_ACTIVE;
                click = true;
            } else if (profile == 1) {
                newLabel = "Spectrum Performance";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_performance);
                newState = Tile.STATE_ACTIVE;
                click = true;
            } else if (profile == 0) {
                newLabel = "Spectrum Balance";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_balanced);
                newState = Tile.STATE_ACTIVE;
                click = false;
            } else {
                newLabel = "Spectrum Custom";
                newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_tile_logo);
                newState = Tile.STATE_ACTIVE;
                click = false;
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }
	
	// added for Apply on Boot
    // private void run(String command, String id, Context context) {
    //    Control.runSetting(command, ApplyOnBootFragment.SPECTRUM, id, context);
    //}
}
