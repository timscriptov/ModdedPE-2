package com.listerily.minecraftcore.android.nmod.tools;

import android.content.Context;

import com.listerily.minecraftcore.android.nmod.instance.NMod;

import java.io.File;

public class NModFilePathManager
{
    private File baseDir;

    private static final String PATH_MANIFEST = "nmod_manifest.json";
    private static final String PATH_ICON = "icon.png";
    private static final String PATH_BANNER = "banner.png";
    private static final String PATH_NMOD = "base.nmod";
    private static final String PATH_DEX = "classes.dex";
    private static final String DIR_LIBS = "libs";

    public NModFilePathManager(File dir, String packageName)
    {
        this.baseDir = new File(dir, packageName);

        mkdirs();
    }

    public NModFilePathManager(File dir)
    {
        this.baseDir = dir;

        mkdirs();
    }

    public File getManifestPath()
    {
        return new File(baseDir, PATH_MANIFEST);
    }

    public File getIconPath()
    {
        return new File(baseDir, PATH_ICON);
    }

    public File getBannerPath()
    {
        return new File(baseDir, PATH_BANNER);
    }

    public File getDexPath()
    {
        return new File(baseDir, PATH_DEX);
    }

    public File getNModPath()
    {
        return new File(baseDir, PATH_NMOD);
    }

    public File getLibsDir()
    {
        return new File(baseDir, DIR_LIBS);
    }

    public File getBaseDir()
    {
        return baseDir;
    }

    private boolean mkdirs()
    {
        return baseDir.mkdirs();
    }
}
