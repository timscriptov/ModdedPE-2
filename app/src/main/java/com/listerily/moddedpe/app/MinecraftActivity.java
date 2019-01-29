/*
 * Copyright (C) 2018 - 2019 Тимашков Иван
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
