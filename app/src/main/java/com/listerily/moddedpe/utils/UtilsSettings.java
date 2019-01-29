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
package com.listerily.moddedpe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.listerily.minecraftcore.android.ILauncherOptions;
import com.listerily.minecraftcore.android.tools.MinecraftInfo;

public class UtilsSettings implements ILauncherOptions
{
	private Context mContext;
	private final static String TAG_SETTINGS = "moddedpe_settings";
	private final static String TAG_SAFE_MODE = "safe_mode";
	private final static String TAG_DATA_SAVED_PATH = "data_saved_path";
	private final static String TAG_PKG_NAME = "pkg_name";
	private final static String TAG_LANGUAGE = "language_type";
	private final static String TAG_OPEN_GAME_FAILED = "open_game_failed_msg";

	public UtilsSettings(Context context)
	{
		this.mContext = context;
	}

	public void setSafeMode(boolean z)
	{
		SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putBoolean(TAG_SAFE_MODE, z);
		editor.apply();
	}

	public boolean isSafeMode()
	{
		return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getBoolean(TAG_SAFE_MODE, false);
	}

	
	public void setLanguageType(int z)
	{
		SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putInt(TAG_LANGUAGE, z);
		editor.apply();
	}

	@Override
	public boolean isLauncherSafeMode()
	{
		return false;
	}
	
	@Override
	public String getMinecraftDataSavedPath()
	{
		return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_DATA_SAVED_PATH, mContext.getDir("minecraft_app_data",0).getAbsolutePath());
	}

	@Override
	public String getMinecraftPackageName()
	{
		return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_PKG_NAME, MinecraftInfo.MC_DEFAULT_PACKAGE_NAME);
	}
	
	public void setDataSavedPath(String z)
	{
		SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putString(TAG_DATA_SAVED_PATH, z);
		editor.apply();
	}
	
	public void setMinecraftPackageName(String z)
	{
		SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putString(TAG_PKG_NAME, z);
		editor.apply();
	}

	public void setOpenGameFailed(String z)
	{
		SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
		editor.putString(TAG_OPEN_GAME_FAILED, z);
		editor.apply();
	}

	public String getOpenGameFailed()
	{
		return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_OPEN_GAME_FAILED, null);
	}
}
