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
package com.listerily.moddedpe.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;

import com.listerily.mcdesign.app.DesignActivity;
import com.listerily.moddedpe.R;
import com.listerily.minecraftcore.android.nmod.instance.NMod;

import java.io.File;
import java.io.FileInputStream;

public class NModDescriptionActivity extends DesignActivity
{
	public final static String TAG_PACKAGE_NAME="nmod_package_name";
	public final static String TAG_NAME="nmod_name";
	public final static String TAG_AUTHOR="author";
	public final static String TAG_VERSION_NAME="version_name";
	public final static String TAG_DESCRIPTION="description";
	public final static String TAG_ICON_PATH="icon_path";
	public final static String TAG_CHANGE_LOG="change_log";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moddedpe_nmod_description);

		String nmodPackageName = getIntent().getExtras().getString(TAG_PACKAGE_NAME);
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.nmod_icon_default);
		try
		{
			String iconpath = getIntent().getExtras().getString(TAG_ICON_PATH);
			FileInputStream fileInput = new FileInputStream(iconpath);
			icon = BitmapFactory.decodeStream(fileInput);
		}
		catch (Throwable e)
		{}

		String description = getIntent().getExtras().getString(TAG_DESCRIPTION);
		String name = getIntent().getExtras().getString(TAG_NAME);
		String version_name = getIntent().getExtras().getString(TAG_VERSION_NAME);
		String author = getIntent().getExtras().getString(TAG_AUTHOR);
		String change_log = getIntent().getExtras().getString(TAG_CHANGE_LOG);

		setTitle(name);

		AppCompatImageView iconImage=(AppCompatImageView)findViewById(R.id.moddedpenmoddescriptionImageViewIcon);
		iconImage.setImageBitmap(icon);

		AppCompatTextView textViewName=(AppCompatTextView)findViewById(R.id.moddedpenmoddescriptionTextViewNModName);
		textViewName.setText(name);
		AppCompatTextView textViewPackageName=(AppCompatTextView)findViewById(R.id.moddedpenmoddescriptionTextViewNModPackageName);
		textViewPackageName.setText(nmodPackageName);
		AppCompatTextView textViewDescription=(AppCompatTextView)findViewById(R.id.moddedpenmoddescriptionTextViewDescription);
		textViewDescription.setText(description == null?getString(R.string.nmod_description_unknow):description);
		AppCompatTextView textViewAuthor=(AppCompatTextView)findViewById(R.id.moddedpenmoddescriptionTextViewAuthor);
		textViewAuthor.setText(author == null?getString(R.string.nmod_description_unknow):author);
		AppCompatTextView textViewVersionName=(AppCompatTextView)findViewById(R.id.moddedpenmoddescriptionTextViewVersionName);
		textViewVersionName.setText(version_name == null?getString(R.string.nmod_description_unknow):version_name);
		AppCompatTextView textViewWhatsNew=(AppCompatTextView)findViewById(R.id.moddedpenmoddescriptionTextViewWhatsNew);
		textViewWhatsNew.setText(change_log == null?getString(R.string.nmod_description_unknow):change_log);
	}

	public static void startThisActivity(Context context, NMod nmod)
	{
		Intent intent=new Intent(context, NModDescriptionActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString(TAG_PACKAGE_NAME, nmod.getPackageName());
		bundle.putString(TAG_NAME, nmod.getName());
		bundle.putString(TAG_DESCRIPTION, nmod.getDescription());
		bundle.putString(TAG_AUTHOR, nmod.getAuthor());
		bundle.putString(TAG_VERSION_NAME, nmod.getVersionName());
		bundle.putString(TAG_CHANGE_LOG, nmod.getChangeLog());
		File iconPath = null;//nmod.copyIconToData();
		if (iconPath != null)
			bundle.putString(TAG_ICON_PATH, iconPath.getAbsolutePath());
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
}
