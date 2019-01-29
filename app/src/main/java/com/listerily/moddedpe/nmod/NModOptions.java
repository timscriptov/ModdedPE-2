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

import android.content.Context;
import android.content.SharedPreferences;

import com.listerily.minecraftcore.android.nmod.INModOptions;

import java.io.File;
import java.util.ArrayList;

public class NModOptions implements INModOptions
{
	private Context mContext;
	private static final String TAG_SHARED_PREFERENCE = "nmod_data_list";
	private static final String TAG_ENABLED_LIST = "enabled_nmods_list";
	private static final String TAG_DISABLE_LIST = "disabled_nmods_list";

	public NModOptions(Context context)
	{
		mContext = context;
	}

	private SharedPreferences getSharedPreferences()
	{
		return mContext.getSharedPreferences(TAG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
	}

	private void addNewEnabled(String nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		ArrayList<String> enabledList=getEnabledNMods();
		ArrayList<String> disableList=getDisabledNMods();
		if (enabledList.indexOf(nmod) == -1)
			enabledList.add(nmod);
		disableList.remove(nmod);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
		editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList));
		editor.apply();
	}

	private void removeEnabled(String nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		ArrayList<String> enabledList=getEnabledNMods();
		ArrayList<String> disableList=getDisabledNMods();
		enabledList.remove(nmod);
		if (disableList.indexOf(nmod) == -1)
			disableList.add(nmod);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
		editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList));
		editor.apply();
	}

	public void upOrderNMod(String nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		ArrayList<String> enabledList=getEnabledNMods();
		int index=enabledList.indexOf(nmod);
		if (index == -1 || index == 0)
			return;
		int indexFront=index - 1;
		String nameFront=enabledList.get(indexFront);
		if (nameFront == null || nameFront.isEmpty())
			return;
		String nameSelf=nmod;
		enabledList.set(indexFront, nameSelf);
		enabledList.set(index, nameFront);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
		editor.apply();
	}

	public void downOrderNMod(String nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		ArrayList<String> enabledList=getEnabledNMods();
		int index=enabledList.indexOf(nmod);
		if (index == -1 || index == (enabledList.size() - 1))
			return;
		int indexBack=index + 1;
		String nameBack=enabledList.get(indexBack);
		if (nameBack == null || nameBack.isEmpty())
			return;
		String nameSelf=nmod;
		enabledList.set(indexBack, nameSelf);
		enabledList.set(index, nameBack);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
		editor.apply();
	}

	private static ArrayList<String> toArrayList(String str)
	{
		String[] mStr = str.split("/");
		ArrayList<String> list = new ArrayList<>();
		for (String strElement:mStr)
		{
			if (strElement != null && !strElement.isEmpty())
				list.add(strElement);
		}
		return list;
	}

	private static String fromArrayList(ArrayList<String> arrayList)
	{
		String str="";
		if (arrayList != null)
		{
			for (String mStr:arrayList)
			{
				str += mStr;
				str += "/";
			}
		}
		return str;
	}

	@Override
	public ArrayList<String> getEnabledNModOrder()
    {
		return getEnabledNMods();
	}

	@Override
	public ArrayList<String> getInstalledNMods() {
		ArrayList<String> ret = new ArrayList<>();
		ret.addAll(getEnabledNMods());
		ret.addAll(getDisabledNMods());
		return ret;
	}

	@Override
	public ArrayList<String> getEnabledNMods() {
        SharedPreferences preferences=getSharedPreferences();
        return toArrayList(preferences.getString(TAG_ENABLED_LIST, ""));
	}

	@Override
	public ArrayList<String> getDisabledNMods() {
        SharedPreferences preferences=getSharedPreferences();
        return toArrayList(preferences.getString(TAG_DISABLE_LIST, ""));
	}

	@Override
	public void setNModOrder(ArrayList<String> order)
	{

	}

	@Override
	public void uninstallNMod(String name) {
		SharedPreferences preferences=getSharedPreferences();
		ArrayList<String> enabledList=getEnabledNMods();
		ArrayList<String> disableList=getDisabledNMods();
		enabledList.remove(name);
		disableList.remove(name);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
		editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList));
		editor.apply();
	}

	@Override
	public void enableNMod(String nmod) {
		addNewEnabled(nmod);
	}

	@Override
	public void disableNMod(String nmod) {
		removeEnabled(nmod);
	}

	@Override
	public File getNModInstallationDir() {
		return mContext.getDir("nmod_files",0);
	}

    @Override
    public File getNModTempAssetsDir() {
        return mContext.getDir("nmod_assets_temp/nmod_assets",0);
    }
}
