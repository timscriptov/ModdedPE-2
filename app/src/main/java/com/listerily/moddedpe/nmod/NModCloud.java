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
package com.listerily.moddedpe.nmod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.listerily.minecraftcore.android.nmod.instance.NMod;
import com.listerily.minecraftcore.android.nmod.tools.NModFilePathManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NModCloud
{
    private String pkgname;
    private String manifestURL;
    private String bannerURL;
    private String iconURL;
    private String dataURL;
    private Bitmap icon;
    private NMod.NModInfo manifest;
    private NModFilePathManager pathManager;

    private NModCloud(String pkgname, String manifestURL, String bannerURL, String iconURL, String dataURL, NModFilePathManager filePathManager)
    {
        this.pkgname = pkgname;
        this.manifestURL = manifestURL;
        this.bannerURL = bannerURL;
        this.iconURL = iconURL;
        this.dataURL = dataURL;
        this.pathManager = filePathManager;
    }

    public static ArrayList<NModCloud> getCloudNModList(File cacheDir) throws MalformedURLException, IOException, JSONException,IllegalStateException
    {
        URL url = new URL("https://raw.githubusercontent.com/TimScriptov/TimScriptov.github.io/master/nmod_server/list.json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        if (conn.getResponseCode() != 200)
            throw new IllegalStateException("Response code returns " + conn.getResponseCode());

        BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String data = "";
        String str;
        while ((str = dis.readLine()) != null)
        {
            data = data + str + "\n";
        }
        dis.close();
        JSONObject list = new JSONObject(data);
        JSONArray listdata = list.getJSONArray("data");
        ArrayList<NModCloud> cloudnmod = new ArrayList<>();
        for (int i = 0; i < listdata.length(); i++)
        {
            try
            {
                JSONObject obj = listdata.getJSONObject(i);
                NModFilePathManager filePathManager = new NModFilePathManager(cacheDir,obj.getString("package_name"));
                NModCloud nmod = new NModCloud(obj.getString("package_name"), obj.getString("manifest_url"), obj.getString("banner_url"), obj.getString("icon_url"), obj.getString("package_url"), filePathManager);
                nmod.icon = nmod.createIcon();
                nmod.manifest = nmod.createManifest();
                cloudnmod.add(nmod);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return cloudnmod;
    }

    public String getPackageName()
    {
        return pkgname;
    }

    private NMod.NModInfo createManifest() throws IOException, IllegalStateException,JsonSyntaxException
    {
        URL url = new URL(manifestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        if (conn.getResponseCode() != 200)
            throw new IllegalStateException("Response Code returns " + conn.getResponseCode());
        BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String data = "";
        String str;
        while ((str = dis.readLine()) != null)
        {
            data = data + str + "\n";
        }
        dis.close();
        NMod.NModInfo info = new Gson().fromJson(data, NMod.NModInfo.class);
        if(info == null)
            throw new IllegalStateException("Info defines as null.");
        if(info.name == null)
            throw new IllegalStateException("Info.name defines as null.");
        return info;
    }

    public Bitmap getBanner() throws MalformedURLException, IOException, IllegalStateException
    {
        File bannerCache = pathManager.getBannerPath();
        if (bannerCache.exists())
            return BitmapFactory.decodeFile(bannerCache.getAbsolutePath());
        createBanner();
        return getBanner();
    }

    public NMod.NModInfo getManifest()
    {
        return manifest;
    }

    public Bitmap getIcon()
    {
        return icon;
    }

    public File download() throws IOException, IllegalStateException
    {
        File nmodFile = pathManager.getNModPath();
        FileOutputStream fos = new FileOutputStream(nmodFile);
        URL url = new URL(dataURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        if (conn.getResponseCode() != 200)
            throw new IllegalStateException("Response Code returns " + conn.getResponseCode());
        InputStream is = conn.getInputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = is.read(buffer)) != -1)
        {
            fos.write(buffer, 0, len);
        }
        is.close();
        fos.close();
        return nmodFile;
    }

    private void createBanner() throws MalformedURLException, IOException, IllegalStateException
    {
        File bannerCache = pathManager.getBannerPath();
        bannerCache.getParentFile().mkdirs();
        bannerCache.createNewFile();
        URL url = new URL(bannerURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        if (conn.getResponseCode() != 200)
            throw new IllegalStateException("Response Code returns " + conn.getResponseCode());
        FileOutputStream output = new FileOutputStream(bannerCache);
        InputStream input = conn.getInputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, len);
        }
        input.close();
        output.close();
    }

    private Bitmap createIcon() throws MalformedURLException, IOException, IllegalStateException
    {
        URL url = new URL(iconURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        if (conn.getResponseCode() != 200)
            throw new IllegalStateException("Response Code returns " + conn.getResponseCode());
        return BitmapFactory.decodeStream(conn.getInputStream());
    }
}
