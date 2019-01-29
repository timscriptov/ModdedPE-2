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
package com.listerily.minecraftcore.android;

import android.app.Application;
import android.content.res.AssetManager;

import com.listerily.minecraftcore.android.nmod.INModOptions;
import com.listerily.minecraftcore.android.nmod.IPatchListener;
import com.listerily.minecraftcore.android.nmod.exception.PatchException;
import com.listerily.minecraftcore.android.tools.AssetOverrideManager;
import com.listerily.minecraftcore.android.nmod.NModManager;
import com.listerily.minecraftcore.android.tools.MinecraftInfo;

public final class MinecraftLauncher
{
    private Application app;
    private ILauncherOptions options;
    private NModManager nmodManager;
    private AssetManager assets;
    private ILauncherListener listener;
    private MinecraftInfo minecraftInfo;

    public MinecraftLauncher(Application app, ILauncherOptions options, INModOptions nmodOptions, ILauncherListener listener)
    {
        this.options = options;
        this.app = app;
        this.listener = listener;
        this.minecraftInfo = new MinecraftInfo(app, options);
        this.nmodManager = new NModManager(app,nmodOptions);
    }

    public AssetManager getAssets()
    {
        if (assets == null)
            return app.getAssets();
        return assets;
    }

    public void launchGame() throws Exception
    {
        try
        {
            if (listener != null)
                listener.onLaunch();
            assets = AssetOverrideManager.newAssetManagerInstance();
            if (listener != null)
                listener.onPatchMinecraftLibraries();
            if (listener != null)
                listener.onPatchLibSubstrate();
            System.loadLibrary("substrate");
            if (listener != null)
                listener.onPatchLibFMod();
            System.load(minecraftInfo.getMinecraftPackageNativeLibraryDir() + "/libfmod.so");
            if (listener != null)
                listener.onPatchLibMinecraft();
            System.load(minecraftInfo.getMinecraftPackageNativeLibraryDir() + "/libminecraftpe.so");
            if (listener != null)
                listener.onPatchMinecraftLibrariesSucceed();
            if (listener != null)
                listener.onPatchMinecraftAssets();
            AssetOverrideManager.addAssetOverride(assets, minecraftInfo.getPackageResourcePath());
            if (listener != null)
                listener.onPatchMinecraftAssetsSucceed();
            if (listener != null)
                listener.onNativeInitialize();
            nativeInitialize(options.getMinecraftDataSavedPath());
            if (listener != null)
                listener.onNativeInitializeSucceed();
            if (listener != null)
                listener.onLaunchSucceed();
        }
        catch (Exception e)
        {
            if (listener != null)
                listener.onLaunchFailed(e);
            throw e;
        }
    }

    public void patchNMods(IPatchListener patchListener) throws PatchException
    {
        try
        {
            if (listener != null)
                listener.onPatchNMods();
            nmodManager.patchNMods(assets, patchListener);
            if (listener != null)
                listener.onPatchNModsSucceed();
        }
        catch (PatchException e)
        {
            if (listener != null)
                listener.onPatchNModsFailed(e);
            throw e;
        }
    }

    public native static void nativeInitialize(String path);
}
