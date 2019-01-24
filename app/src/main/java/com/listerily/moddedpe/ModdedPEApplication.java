package com.listerily.moddedpe;

import android.app.Application;
import android.content.res.AssetManager;

import com.listerily.minecraftcore.android.MinecraftLauncher;

public class ModdedPEApplication extends Application
{
	private MinecraftLauncher launcherInstance = null;
	
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public AssetManager getAssets()
	{
		if(launcherInstance != null)
			return launcherInstance.getAssets();
		return super.getAssets();
	}

	public void setLauncher(MinecraftLauncher launcher)
	{
		launcherInstance = launcher;
	}

    public MinecraftLauncher getLauncher()
    {
        return launcherInstance;
    }
}
