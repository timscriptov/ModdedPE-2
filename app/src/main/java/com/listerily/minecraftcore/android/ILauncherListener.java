package com.listerily.minecraftcore.android;

import com.listerily.minecraftcore.android.nmod.exception.PatchException;

public interface ILauncherListener
{

    void onPatchMinecraftLibraries();
    void onPatchLibSubstrate();
    void onPatchLibFMod();
    void onPatchLibMinecraft();
    void onPatchMinecraftLibrariesSucceed();
    void onPatchMinecraftAssets();
    void onPatchMinecraftAssetsSucceed();
    void onNativeInitialize();
    void onNativeInitializeSucceed();
    void onLaunch();
    void onLaunchSucceed();
    void onLaunchFailed(Exception e);
    void onPatchNMods();
    void onPatchNModsSucceed();
    void onPatchNModsFailed(PatchException e);
}
