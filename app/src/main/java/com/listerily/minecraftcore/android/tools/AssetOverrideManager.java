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
package com.listerily.minecraftcore.android.tools;

import android.content.res.AssetManager;

import java.lang.reflect.Method;

public class AssetOverrideManager
{
	private AssetManager assets;

	public void addAssetOverride(String packageResourcePath) throws Exception
	{
		addAssetOverride(this.assets,packageResourcePath);
	}
	
	public static void addAssetOverride(AssetManager mgr,String packageResourcePath) throws Exception
	{
		Method method = AssetManager.class.getMethod("addAssetPath", String.class);
		method.invoke(mgr, packageResourcePath);
	}

	public static AssetManager newAssetManagerInstance() throws Exception
	{
		return AssetManager.class.newInstance();
	}

	public AssetManager getAssetManager()
	{
		return assets;
	}

	public AssetOverrideManager(AssetManager originalAssets)
	{
		this.assets = originalAssets;
	}

	public AssetOverrideManager() throws Exception
	{
		this.assets = AssetManager.class.newInstance();
	}

}
