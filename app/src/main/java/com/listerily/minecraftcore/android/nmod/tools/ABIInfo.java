package com.listerily.minecraftcore.android.nmod.tools;

public class ABIInfo
{
	public static String getTargetABIType()
	{
		String property = System.getProperty("os.arch");
        return (property.equals("i686") || property.startsWith("x86")) ? "x86" : "armeabi-v7a";
	}
}
