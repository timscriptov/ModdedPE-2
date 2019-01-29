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
package com.listerily.minecraftcore.android.nmod.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.listerily.minecraftcore.android.nmod.exception.InstallException;
import com.listerily.minecraftcore.android.nmod.instance.NMod;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class NModInstaller
{
    private Context mContext;
    private File nmodDir;

    public NModInstaller(Context context, File dir)
    {
        this.mContext = context;
        this.nmodDir = dir;
    }

    public NMod installNMod(File path) throws InstallException
    {
        NMod.NModInfo info = archiveInfoFromZipped(path);
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path.getAbsolutePath(), PackageManager.GET_CONFIGURATIONS);
        NModFilePathManager pathManager = new NModFilePathManager(nmodDir, info.package_name);

        //Start Installation
        try
        {
            //STEP1:Copy full package and create AndroidManifest.xml
            {
                ZipFile zipFile = new ZipFile(path);
                ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
                nmodDir.mkdirs();
                pathManager.getNModPath().getParentFile().mkdirs();
                pathManager.getNModPath().createNewFile();
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(pathManager.getNModPath()));
                ZipEntry entry;
                while ((entry = zipInput.getNextEntry()) != null)
                {
                    if (!entry.isDirectory())
                    {
                        zipOutputStream.putNextEntry(entry);
                        InputStream from = zipFile.getInputStream(entry);
                        int byteRead = -1;
                        byte[] buffer = new byte[1024];
                        while ((byteRead = from.read(buffer)) != -1)
                        {
                            zipOutputStream.write(buffer, 0, byteRead);
                        }
                        from.close();
                        zipOutputStream.closeEntry();
                    }
                }
                ZipEntry entryManifest = new ZipEntry("AndroidManifest.xml");
                zipOutputStream.putNextEntry(entryManifest);
                zipOutputStream.closeEntry();
                zipOutputStream.flush();
                zipOutputStream.close();
                zipInput.close();
            }
            //STEP2:Copy Libs Dir
            {
                ZipFile zipFile = new ZipFile(path);
                ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
                ZipEntry entry;
                while ((entry = zipInput.getNextEntry()) != null)
                {
                    if (entry.getName().startsWith("libs/" + ABIInfo.getTargetABIType()))
                    {
                        InputStream from = zipFile.getInputStream(entry);
                        File newFile = new File(pathManager.getBaseDir(), entry.getName());
                        newFile.getParentFile().mkdirs();
                        newFile.createNewFile();
                        FileOutputStream output = new FileOutputStream(newFile);
                        int byteRead = -1;
                        byte[] buffer = new byte[1024];
                        while ((byteRead = from.read(buffer)) != -1)
                        {
                            output.write(buffer, 0, byteRead);
                        }
                        from.close();
                        output.close();
                    }
                }
            }
            //STEP3:Copy Icon
            {
                Bitmap icon = getIcon(path);
                if (icon != null)
                {
                    FileOutputStream output = new FileOutputStream(pathManager.getIconPath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    icon.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    output.write(byteArrayOutputStream.toByteArray());
                    output.close();
                }

            }
            //STEP4:Copy DEX
            {
                ZipFile zipFile = new ZipFile(path);
                ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
                ZipEntry entry;
                while ((entry = zipInput.getNextEntry()) != null)
                {
                    if (entry.getName().equals("classes.dex"))
                    {
                        InputStream from = zipFile.getInputStream(entry);
                        pathManager.getDexPath().getParentFile().mkdirs();
                        pathManager.getDexPath().createNewFile();
                        FileOutputStream output = new FileOutputStream(pathManager.getDexPath());
                        int byteRead = -1;
                        byte[] buffer = new byte[1024];
                        while ((byteRead = from.read(buffer)) != -1)
                        {
                            output.write(buffer, 0, byteRead);
                        }
                        from.close();
                        output.close();
                    }
                }
            }
            //STEP5:Copy Banner
            {
                Bitmap banner = getBannerImage(path);
                if (banner != null)
                {
                    FileOutputStream output = new FileOutputStream(pathManager.getIconPath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    banner.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    output.write(byteArrayOutputStream.toByteArray());
                    output.close();
                }
            }
            //STEP6: Create manifest.json
            {
                FileOutputStream output = new FileOutputStream(pathManager.getManifestPath());
                output.write(new Gson().toJson(info).getBytes());
                output.close();
            }
            return new NMod(info.package_name, pathManager.getBaseDir());
        }
        catch (IOException ioe)
        {
            throw new InstallException("File IO Failed.", ioe);
        }
        catch(Exception e)
        {
            throw new InstallException("Unknown exception.",e);
        }
    }

    public NMod.NModInfo archiveInfoFromZipped(File filePath) throws InstallException
    {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath.getAbsolutePath(), PackageManager.GET_CONFIGURATIONS);
        NMod.NModInfo info;

        try
        {
            ZipFile zipFile = new ZipFile(filePath);
            ZipEntry manifest1 = zipFile.getEntry(NMod.MANIFEST_NAME);
            ZipEntry manifest2 = zipFile.getEntry("assets" + File.separator + NMod.MANIFEST_NAME);

            if (manifest1 != null && manifest2 != null)
                throw new InstallException("Redefined nmod_manifest.json at /nmod_manifest.json and /assets/nmod_manifest.json");
            else if (manifest1 == null && manifest2 == null && packageInfo == null)
                throw new InstallException("Undefined nmod_manifest.json");

            InputStream input = zipFile.getInputStream(manifest1 == null ? manifest2 : manifest1);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            info = new Gson().fromJson(new String(buffer), NMod.NModInfo.class);

            if (info.package_name == null && packageInfo != null)
            {
                info.package_name = packageInfo.packageName;
            }
            else if (info.package_name != null && packageInfo != null && info.package_name.equals(packageInfo.packageName))
            {
                throw new InstallException("Unequal package name found in AndroidManifest.xml and nmod_manifest.json.");
            }

            if (zipFile.getEntry("lib/" + ABIInfo.getTargetABIType()) == null)
            {
                throw new InstallException("NMod uses an unsupported CPU ABI type.");
            }

            if (info.native_libs == null)
            {
                ArrayList<String> libsFound = new ArrayList<>();
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements())
                {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().startsWith("lib/" + ABIInfo.getTargetABIType()))
                        ;
                    {
                        libsFound.add(entry.getName().substring(entry.getName().lastIndexOf('/') + 1));
                    }
                }
                info.native_libs = (String[]) libsFound.toArray();
            }
            else
            {
                for (String name : info.native_libs)
                {
                    String path = "libs/" + ABIInfo.getTargetABIType() + "/" + name;
                    ZipEntry entry = zipFile.getEntry(path);
                    if (entry == null)
                        throw new InstallException("Native library " + path + " not found.");
                }
            }

            if (packageInfo != null && info.version_code == -1)
                info.version_code = packageInfo.versionCode;
            if (packageInfo != null && info.version_name == null)
                info.version_name = packageInfo.versionName;
            return info;
        }
        catch (IOException e)
        {
            throw new InstallException("File IO Failed.", e);
        }
    }

    public void uninstallNMod(File packageDir)
    {
        Stack<File> fileTree = new Stack<>();
        fileTree.add(packageDir);
        while (!fileTree.empty())
        {
            File curr = fileTree.peek();
            if (curr.listFiles().length > 0)
                for (File file : curr.listFiles())
                {
                    if (file.isDirectory())
                        fileTree.add(file);
                    else
                        file.delete();
                }
            else
            {
                curr.delete();
                fileTree.pop();
            }
        }
    }


    public Bitmap getBannerImage(File path) throws InstallException
    {
        try
        {
            ZipFile zipFile = new ZipFile(path.getAbsolutePath());
            ZipEntry banner1 = zipFile.getEntry("banner.png");
            ZipEntry banner2 = zipFile.getEntry("assets/banner.png");
            if (banner1 != null || banner2 != null)
            {
                if (banner1 != null && banner2 != null)
                    throw new InstallException("Redefined banner.png in /assets/banner.png and /banner.png");
                ZipEntry entry = banner1 == null ? banner2 : banner1;
                InputStream input = zipFile.getInputStream(entry);
                Bitmap content = BitmapFactory.decodeStream(input);
                if (content == null)
                    throw new InstallException("Banner decode failed:" + entry.getName());
                return content;
            }
            return null;
        }
        catch (IOException e)
        {
            throw new InstallException("Banner decode failed:", e);
        }
    }

    public Bitmap getIcon(File path) throws InstallException
    {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path.getAbsolutePath(), PackageManager.GET_CONFIGURATIONS);

        try
        {
            ZipFile zipFile = new ZipFile(path.getAbsolutePath());
            ZipEntry icon1 = zipFile.getEntry("icon.png");
            ZipEntry icon2 = zipFile.getEntry("assets/icon.png");
            if (icon1 != null || icon2 != null)
            {
                if (icon1 != null && icon2 != null)
                    throw new InstallException("Redefined icon.png in /assets/icon.png and /icon.png");
                ZipEntry entry = icon1 == null ? icon2 : icon1;
                InputStream input = zipFile.getInputStream(entry);
                Bitmap content = BitmapFactory.decodeStream(input);
                if (content == null)
                    throw new InstallException("Icon decode failed:" + entry.getName());
                return content;
            }
            else if (packageInfo != null)
            {
                Drawable icon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
                Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                icon.draw(canvas);
                return bitmap;
            }
            return null;
        }
        catch (IOException e)
        {
            throw new InstallException("File IO Failed.", e);
        }
    }

}
