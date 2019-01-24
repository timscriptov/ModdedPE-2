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
