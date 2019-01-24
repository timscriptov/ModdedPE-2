package com.listerily.minecraftcore.android.nmod.patcher;

import android.content.res.AssetManager;

import com.listerily.minecraftcore.android.nmod.NModManager;
import com.listerily.minecraftcore.android.nmod.exception.PatchException;
import com.listerily.minecraftcore.android.nmod.instance.NMod;

import java.io.File;

public final class NModPatcher
{
    public static void patchLibraries(NMod target) throws PatchException
    {
        target.loadNativeLibraries();
    }

    public static AssetManager patchAssets(NMod target,AssetManager origin,File dir) throws PatchException
    {
        AssetPatcher patcher = new AssetPatcher(origin,dir);
        target.doJsonOverrides(patcher);
        target.doTextOverrides(patcher);
        target.doFileOverrides(patcher);
        return patcher.generateAssets();
    }
}
