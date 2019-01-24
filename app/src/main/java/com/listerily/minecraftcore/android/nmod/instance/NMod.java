package com.listerily.minecraftcore.android.nmod.instance;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.minecraftpe.MainActivity;

import com.listerily.minecraftcore.android.nmod.exception.NModWarning;
import com.listerily.minecraftcore.android.nmod.exception.PatchException;
import com.listerily.minecraftcore.android.nmod.patcher.AssetPatcher;
import com.listerily.minecraftcore.android.nmod.tools.ABIInfo;
import com.listerily.minecraftcore.android.nmod.tools.JSONMerger;
import com.listerily.minecraftcore.android.nmod.tools.NModFilePathManager;
import com.listerily.minecraftcore.android.tools.AssetOverrideManager;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NMod
{
    private NModInfo mInfo;
    private ArrayList<NModWarning> mWarnings = new ArrayList<>();
    private Bitmap mIcon;
    private Bitmap mBannerImage;
    private String mPackageName;
    private File mInstallationPath;
    private AssetOverrideManager mAssets;

    public static final String MANIFEST_NAME = "nmod_manifest.json";

    //==============================================================================================
    // Initialize Methods
    //==============================================================================================
    public NMod(String packageName, File installlationPath) throws Exception
    {
        mPackageName = packageName;
        mInstallationPath = installlationPath;

        createInfo();
        createBanner();
        createIcon();
        createAssets();
    }

    private void createIcon() throws Exception
    {
        NModFilePathManager pathManager = new NModFilePathManager(getInstallationDir());
        File iconFile = pathManager.getIconPath();
        if(!iconFile.exists())
            return;
        FileInputStream input = new FileInputStream(iconFile);
        mIcon = BitmapFactory.decodeStream(input);
        input.close();
    }

    private void createInfo() throws Exception
    {
        NModFilePathManager pathManager = new NModFilePathManager(getInstallationDir());
        FileInputStream input = new FileInputStream(pathManager.getManifestPath());
        byte[] buffer = new byte[input.available()];
        input.read(buffer);
        String tmp = new String(buffer);
        Gson gson = new Gson();
        mInfo = gson.fromJson(tmp, NModInfo.class);

    }

    private void createBanner() throws Exception
    {
        NModFilePathManager pathManager = new NModFilePathManager(getInstallationDir());
        File bannerFile = pathManager.getBannerPath();
        if(!bannerFile.exists())
            return;
        InputStream is = new FileInputStream(bannerFile);
        mBannerImage = BitmapFactory.decodeStream(is);
        is.close();
    }

    private void createAssets() throws Exception
    {
        mAssets = new AssetOverrideManager();
        mAssets.addAssetOverride(getPackageResourcePath().getAbsolutePath());
    }
    //==============================================================================================
    // Getters
    //==============================================================================================

    public File getPackageResourcePath()
    {
        NModFilePathManager pathManager = new NModFilePathManager(getInstallationDir());
        return pathManager.getNModPath();
    }

    public File getInstallationDir()
    {
        return mInstallationPath;
    }

    public AssetManager getAssets()
    {
        return mAssets.getAssetManager();
    }

    public final String getPackageName()
    {
        return mPackageName;
    }

    public final String getName()
    {
        if (mInfo.name != null)
            return mInfo.name;
        return mInfo.package_name;
    }

    public final Bitmap getBannerImage()
    {
        return mBannerImage;
    }

    public final Bitmap getIcon()
    {
        return mIcon;
    }

    public final String getDescription()
    {
        return mInfo.description;
    }

    public final String getAuthor()
    {
        return mInfo.author;
    }

    public final String getVersionName()
    {
        return mInfo.version_name;
    }

    public final ArrayList<NModWarning> getWarnings()
    {
        return new ArrayList<>(mWarnings);
    }

    public String getChangeLog()
    {
        return mInfo.change_log;
    }

    public NMod.NModInfo getInfo()
    {
        return mInfo;
    }
    //==============================================================================================
    // Operations
    //==============================================================================================

    private byte[] getAssetContent(String path)
    {
        try
        {
            InputStream input = getAssets().open(path);
            byte[] content = new byte[input.available()];
            input.read(content);
            input.close();
            return content;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void doJsonOverrides(AssetPatcher patcher) throws PatchException
    {
        if (mInfo.json_overrides != null)
        {
            for (JsonOverrideBean jsonOverrideBean : mInfo.json_overrides)
            {
                try
                {
                    byte[] contentOverrider = getAssetContent(jsonOverrideBean.path);
                    byte[] contentOverridee = patcher.read(jsonOverrideBean.path);
                    byte[] contentNew;
                    if (jsonOverrideBean.mode.equals(JsonOverrideBean.MODE_MERGE))
                        contentNew = new JSONMerger(new String(contentOverridee), new String(contentOverrider)).merge().getBytes();
                    else if (jsonOverrideBean.mode.equals(JsonOverrideBean.MODE_REPLACE))
                        contentNew = contentOverrider;
                    else
                        throw new PatchException("Json override failed:undefined override mode " + jsonOverrideBean.mode + ".");
                    patcher.write(jsonOverrideBean.path, contentNew);
                }
                catch (JSONException e)
                {
                    throw new PatchException("Json override failed:json syntax error.", e);
                }
            }
        }
    }

    public void doTextOverrides(AssetPatcher patcher) throws PatchException
    {
        if (mInfo.text_overrides != null)
        {
            for (TextOverrideBean textOverrideBean : mInfo.text_overrides)
            {
                byte[] contentOverrider = getAssetContent(textOverrideBean.path);
                byte[] contentOverridee = patcher.read(textOverrideBean.path);
                byte[] contentNew;
                if (textOverrideBean.mode.equals(TextOverrideBean.MODE_APPEND))
                    contentNew = (new String(contentOverridee) + new String(contentOverrider)).getBytes();
                else if (textOverrideBean.mode.equals(TextOverrideBean.MODE_REPLACE))
                    contentNew = contentOverrider;
                else if (textOverrideBean.mode.equals(TextOverrideBean.MODE_PREPEND))
                    contentNew = (new String(contentOverrider) + new String(contentOverridee)).getBytes();
                else
                    throw new PatchException("Text override failed:undefined override mode " + textOverrideBean.mode + ".");
                patcher.write(textOverrideBean.path, contentNew);
            }
        }
    }

    public void doFileOverrides(AssetPatcher patcher) throws PatchException
    {
        if (mInfo.file_overrides != null && mInfo.native_libs != null)
        {
            for (String path : mInfo.file_overrides)
            {
                byte[] contentNew = patcher.read(path);
                File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
                File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
                for (String lib : mInfo.native_libs)
                {
                    File target = new File(targetDir, lib);
                    contentNew = nativeOverrideFile(target.getAbsolutePath(), path, contentNew);

                    if (contentNew == null)
                        throw new PatchException("File override returns null by " + target.getName() + ".");
                }

                patcher.write(path, contentNew);
            }
        }
    }

    public void examineForWarnings()
    {

    }

    public void addNewWarning(NModWarning warning)
    {
        mWarnings.add(warning);
    }

    public void loadNativeLibraries() throws PatchException
    {
        try
        {
            File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
            File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
            if (mInfo.native_libs != null)
            {
                for (String lib : mInfo.native_libs)
                {
                    File target = new File(targetDir, lib);
                    System.load(target.getAbsolutePath());
                }
            }
        }
        catch (UnsatisfiedLinkError error)
        {
            throw new PatchException("Unable to load native library.", error);
        }
    }

    public void onActivityCreate(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityCreate(target.getAbsolutePath(), activity);
            }
        }
    }

    public void onActivityStop(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityStop(target.getAbsolutePath(), activity);
            }
        }
    }

    public void onActivityResume(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityResume(target.getAbsolutePath(), activity);
            }
        }
    }

    public void onActivityRestart(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityRestart(target.getAbsolutePath(), activity);
            }
        }
    }

    public void onActivityStart(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityStart(target.getAbsolutePath(), activity);
            }
        }
    }

    public void onActivityPause(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityPause(target.getAbsolutePath(), activity);
            }
        }
    }

    public void onActivityDestroy(MainActivity activity)
    {
        File libsDir = new NModFilePathManager(mInstallationPath).getLibsDir();
        File targetDir = new File(libsDir, ABIInfo.getTargetABIType());
        if (mInfo.native_libs != null)
        {
            for (String lib : mInfo.native_libs)
            {
                File target = new File(targetDir, lib);
                nativeOnActivityDestroy(target.getAbsolutePath(), activity);
            }
        }
    }

    @Override
    public final boolean equals(Object obj)
    {
        return obj instanceof NMod && getPackageName().equals(((NMod) obj).getPackageName());
    }

    //==============================================================================================
    // Native Operations
    //==============================================================================================

    private static native byte[] nativeOverrideFile(String path, String assetPath, byte[] content);

    private static native boolean nativeOnActivityCreate(String path, MainActivity activity);

    private static native boolean nativeOnActivityDestroy(String path, MainActivity activity);

    private static native boolean nativeOnActivityStart(String path, MainActivity activity);

    private static native boolean nativeOnActivityPause(String path, MainActivity activity);

    private static native boolean nativeOnActivityRestart(String path, MainActivity activity);

    private static native boolean nativeOnActivityResume(String path, MainActivity activity);

    private static native boolean nativeOnActivityStop(String path, MainActivity activity);

    //==============================================================================================
    // Classes For Json
    //==============================================================================================
    public static final class TextOverrideBean
    {
        public String path = null;
        public String mode = MODE_REPLACE;

        public static final String MODE_REPLACE = "replace";
        public static final String MODE_APPEND = "append";
        public static final String MODE_PREPEND = "prepend";
    }

    public static final class JsonOverrideBean
    {
        public String path = null;
        public String mode = MODE_REPLACE;

        public static final String MODE_REPLACE = "replace";
        public static final String MODE_MERGE = "merge";
    }

    public static class NModInfo
    {
        public TextOverrideBean[] text_overrides = null;
        public JsonOverrideBean[] json_overrides = null;
        public String[] file_overrides = null;
        public String[] native_libs = null;
        public int version_code = -1;
        public String name = null;
        public String package_name = null;
        public String description = null;
        public String author = null;
        public String version_name = null;
        public String change_log = null;
    }
}
