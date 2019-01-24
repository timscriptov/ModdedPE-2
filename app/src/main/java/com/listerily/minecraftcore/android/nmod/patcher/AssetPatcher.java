package com.listerily.minecraftcore.android.nmod.patcher;

import android.content.res.AssetManager;

import com.listerily.minecraftcore.android.nmod.exception.PatchException;
import com.listerily.minecraftcore.android.tools.AssetOverrideManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class AssetPatcher
{
    private File rootDir;
    private AssetManager rootAssets;
    private ArrayList<String> overrides;

    public AssetPatcher(AssetManager assets, File root)
    {
        rootDir = root;
        rootAssets = assets;
        overrides = new ArrayList<>();
    }

    public byte[] read(String relativePath) throws PatchException
    {
        try
        {
            InputStream input;
            File targetFile = new File(rootDir, relativePath);
            if (targetFile.exists())
                input = new FileInputStream(targetFile);
            else
                input = rootAssets.open(relativePath);
            byte[] buffer= new byte[input.available()];
            input.read(buffer);
            input.close();
            return buffer;
        }
        catch (IOException e)
        {
            throw new PatchException("File IO Failed.",e);
        }
    }

    public void write(String relativePath,byte[] content) throws PatchException
    {
        try
        {
            File targetFile = new File(rootDir,relativePath);
            targetFile.getParentFile().mkdirs();
            targetFile.createNewFile();

            FileOutputStream output = new FileOutputStream(targetFile);
            output.write(content);
            output.close();
            overrides.add(relativePath);
        }
        catch(IOException e)
        {
            throw new PatchException("File IO Failed.",e);
        }
    }

    public AssetManager generateAssets() throws PatchException
    {
        try
        {
            File targetFile = new File(rootDir.getParent(),"patched_assets.zip");
            targetFile.getParentFile().mkdirs();
            targetFile.createNewFile();

            ZipOutputStream output = new ZipOutputStream(new FileOutputStream(targetFile));
            for(String currPath:overrides)
            {
                File currFile = new File(rootDir,currPath);
                ZipEntry newEntry = new ZipEntry(currPath);
                FileInputStream input = new FileInputStream(currFile);
                byte[] buffer = new byte[input.available()];
                input.read(buffer);
                output.putNextEntry(newEntry);
                output.write(buffer);
                input.close();
            }
            output.putNextEntry(new ZipEntry("AndroidManifest.xml"));
            output.close();

            AssetOverrideManager.addAssetOverride(rootAssets,targetFile.getAbsolutePath());
            return rootAssets;
        }
        catch(IOException e)
        {
            throw new PatchException("File IO Filed.",e);
        }
        catch(Exception e)
        {
            throw new PatchException("Unknown exception.",e);
        }
    }
}
