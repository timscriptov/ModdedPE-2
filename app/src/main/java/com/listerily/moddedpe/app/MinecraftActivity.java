package com.listerily.moddedpe.app;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.listerily.moddedpe.ModdedPEApplication;

public class MinecraftActivity extends com.mojang.minecraftpe.MainActivity
{
	@Override
	public AssetManager getAssets()
	{
		return null;//getPESdk().getGameManager().getAssets();
	}

	@Override
	public void onCreate(Bundle p1)
	{
		//getPESdk().getGameManager().onMinecraftActivityCreate(this,p1);
		super.onCreate(p1);
	}

	@Override
	protected void onDestroy()
	{
		//getPESdk().getGameManager().onMinecraftActivityFinish(this);
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && Build.VERSION.SDK_INT >= 19)
		{
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
}
