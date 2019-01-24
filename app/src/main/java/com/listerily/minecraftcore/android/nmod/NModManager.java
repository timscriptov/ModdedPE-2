package com.listerily.minecraftcore.android.nmod;

import android.content.Context;
import android.content.res.AssetManager;

import com.listerily.minecraftcore.android.nmod.exception.InstallException;
import com.listerily.minecraftcore.android.nmod.exception.PatchException;
import com.listerily.minecraftcore.android.nmod.instance.NMod;
import com.listerily.minecraftcore.android.nmod.patcher.NModPatcher;
import com.listerily.minecraftcore.android.nmod.tools.NModInstaller;
import com.listerily.minecraftcore.android.nmod.tools.NModFilePathManager;

import java.io.File;
import java.util.ArrayList;

public final class NModManager
{
    private ArrayList<NMod> mEnabledNMods;
    private ArrayList<NMod> mAllNMods;
    private ArrayList<NMod> mDisabledNMods;
    private INModOptions options;
    private NModInstaller extractor;

    public NModManager(Context context, INModOptions options)
    {
        this.options = options;
        this.extractor = new NModInstaller(context, options.getNModInstallationDir());

        mAllNMods = new ArrayList<>();
        mEnabledNMods = new ArrayList<>();
        mDisabledNMods = new ArrayList<>();
    }

    public void init()
    {
        loadNModList(options.getEnabledNMods(), true);
        loadNModList(options.getDisabledNMods(), false);
    }

    public ArrayList<NMod> getEnabledNMods()
    {
        return mEnabledNMods;
    }

    public ArrayList<NMod> getEnabledNModsOrder()
    {
        return mEnabledNMods;
    }

    public void setEnabledNModsOrder(ArrayList<NMod> order)
    {
        ArrayList<String> orderList = new ArrayList<>();
        for (NMod nmod : order)
            orderList.add(nmod.getPackageName());
        options.setNModOrder(orderList);
    }

    public ArrayList<NMod> getInstalledNMods()
    {
        return mAllNMods;
    }

    public boolean isInstalled(String pkgName)
    {
        for (NMod curr : mAllNMods)
        {
            if (curr.getPackageName().equals(pkgName))
                return true;
        }
        return false;
    }

    public boolean isEnabled(String pkgName)
    {
        for (NMod curr : mEnabledNMods)
        {
            if (curr.getPackageName().equals(pkgName))
                return true;
        }
        return false;
    }

    public boolean setEnabled(NMod nmod)
    {
        options.enableNMod(nmod.getPackageName());
        mEnabledNMods.add(nmod);
        mDisabledNMods.remove(nmod);
        return true;
    }

    public void setDisabled(NMod nmod)
    {
        options.disableNMod(nmod.getPackageName());
        mDisabledNMods.add(nmod);
        mEnabledNMods.remove(nmod);
    }

    public ArrayList<NMod> getDisabledNMods()
    {
        return mDisabledNMods;
    }

    public NMod installNMod(File file) throws InstallException
    {
        NMod newNMod = extractor.installNMod(file);
        loadNMod(newNMod, false);
        return newNMod;
    }

    public void uninstallNMod(NMod nmod)
    {
        mEnabledNMods.remove(nmod);
        mDisabledNMods.remove(nmod);
        mAllNMods.remove(nmod);
        options.uninstallNMod(nmod.getPackageName());
    }

    private void loadNModList(ArrayList<String> list, boolean enabled)
    {
        for (String packageName : list)
        {
            try
            {
                NModFilePathManager pathManager = new NModFilePathManager(options.getNModInstallationDir(), packageName);
                NMod nmod = new NMod(packageName, pathManager.getNModPath());
                loadNMod(nmod, enabled);
            }
            catch(Exception e)
            {
                //TODO load failed
                options.uninstallNMod(packageName);
            }
        }
    }

    private void loadNMod(NMod nmod, boolean enabled)
    {
        mAllNMods.add(nmod);
        if (enabled)
            mEnabledNMods.add(nmod);
        else
            mDisabledNMods.add(nmod);
    }


    public NMod getNModByPkgName(String pkgname)
    {
        for (NMod nmod : mAllNMods)
            if (nmod.getPackageName().equals(pkgname))
                return nmod;
        return null;
    }

    public void upOrderNMod(NMod nmod)
    {
        options.upOrderNMod(nmod.getPackageName());
        refreshEnabledOrderList();
    }

    public void downOrderNMod(NMod nmod)
    {
        options.downOrderNMod(nmod.getPackageName());
        refreshEnabledOrderList();
    }

    public AssetManager patchNMods(AssetManager originalAssets,IPatchListener patchListener) throws PatchException
    {
        NMod currNMod = null;
        try
        {
            for (NMod nmod : getEnabledNMods())
            {
                currNMod = nmod;

                if (patchListener != null)
                    patchListener.onPatch(nmod);
                if (patchListener != null)
                    patchListener.onPatchLibraries(nmod);
                NModPatcher.patchLibraries(nmod);
                if (patchListener != null)
                    patchListener.onPatchAssets(nmod);
                NModPatcher.patchAssets(nmod, originalAssets, options.getNModTempAssetsDir());
                if (patchListener != null)
                    patchListener.onPatchSucceed(nmod);
            }
            return originalAssets;
        }
        catch (PatchException patchE)
        {
            if (patchListener != null)
                patchListener.onPatchFailed(currNMod, patchE);
            throw patchE;
        }
    }

    private void refreshEnabledOrderList()
    {
        ArrayList<String> enabledList = options.getEnabledNModOrder();
        mEnabledNMods.clear();
        for (String pkgName : enabledList)
        {
            NMod nmod = getNModByPkgName(pkgName);
            if (nmod != null)
            {
                mEnabledNMods.add(nmod);
            }
        }
    }
}
