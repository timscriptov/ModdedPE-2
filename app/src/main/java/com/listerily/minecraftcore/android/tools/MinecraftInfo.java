package com.listerily.minecraftcore.android.tools;

import android.content.Context;
import android.content.pm.PackageManager;

import com.listerily.minecraftcore.android.ILauncherOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftInfo
{
	public static String MC_DEFAULT_PACKAGE_NAME = "com.mojang.minecraftpe";

	private Context mContext;
	private Context mMCContext;

	public boolean isSupportedMinecraftVersion(String[] versions)
	{
	    if(!isMinecraftInstalled())
	        return false;
		String mcpeVersionName = getMinecraftVersionName();
		if (mcpeVersionName == null)
			return false;
		for (String nameItem : versions)
		{
			Pattern pattern = Pattern.compile(nameItem);
			Matcher matcher = pattern.matcher(mcpeVersionName);
			if (matcher.find())
				return true;
		}
		return false;
	}

	public String getMinecraftVersionName()
	{
		if (getMinecraftPackageContext() == null)
			return null;
		try
		{
			return mContext.getPackageManager().getPackageInfo(getMinecraftPackageContext().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		return null;
	}

	public String getMinecraftPackageNativeLibraryDir()
	{
		return mMCContext.getApplicationInfo().nativeLibraryDir;
	}

	public Context getMinecraftPackageContext()
	{
		return mMCContext;
	}

	public boolean isMinecraftInstalled()
	{
		return getMinecraftPackageContext() != null;
	}

	public String getPackageResourcePath()
    {
        return mMCContext.getPackageResourcePath();
    }

	public MinecraftInfo(Context context, ILauncherOptions options)
	{
		this.mContext = context;

		String mMinecraftPackageName = MC_DEFAULT_PACKAGE_NAME;
		if (options.getMinecraftPackageName() != null)
			mMinecraftPackageName = options.getMinecraftPackageName();

		try
		{
			mMCContext = context.createPackageContext(mMinecraftPackageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
		}
		catch (PackageManager.NameNotFoundException e)
		{}
	}
}
