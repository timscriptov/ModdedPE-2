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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.listerily.mcdesign.app.DesignActivity;
import com.listerily.moddedpe.R;
import com.listerily.minecraftcore.android.nmod.instance.NMod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class NModLoadFailActivity extends DesignActivity
{
	private static final String KEY_TYPE_STRING = "type_string";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_PACKAGE_NAME = "package_name";
	private static final String KEY_ICON_PATH = "icon_path";
	private static final String KEY_MC_DATA = "mc_data";

	private ArrayList<String> mPackageNames = new ArrayList<>();
	private ArrayList<String> mMessages = new ArrayList<>();
	private ArrayList<String> mTypeStrings = new ArrayList<>();
	private ArrayList<String> mIconPaths = new ArrayList<>();
	private Bundle mMCData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moddedpe_nmod_load_failed);

		mMessages = getIntent().getExtras().getStringArrayList(KEY_MESSAGE);
		mIconPaths = getIntent().getExtras().getStringArrayList(KEY_ICON_PATH);
		mTypeStrings = getIntent().getExtras().getStringArrayList(KEY_TYPE_STRING);
		mPackageNames = getIntent().getExtras().getStringArrayList(KEY_PACKAGE_NAME);
		mMCData = getIntent().getExtras().getBundle(KEY_MC_DATA);

		ListView errorListView = (ListView)findViewById(R.id.nmod_load_failed_list_view);
		errorListView.setAdapter(new ViewAdapter());
		
		findViewById(R.id.load_failed_next_button).setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					onNextClicked();
				}
				
			
		});
	}

	private class ViewAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mPackageNames.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return p1;
		}

		@Override
		public long getItemId(int p1)
		{
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			CardView view = (CardView)getLayoutInflater().inflate(R.layout.moddedpe_nmod_load_failed_item_card, null);
			AppCompatTextView packageNameTextView = (AppCompatTextView)view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_package_name);
			packageNameTextView.setText(mPackageNames.get(p1));
			AppCompatTextView errorMessageTextView = (AppCompatTextView)view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_message);
			errorMessageTextView.setText(getString(R.string.load_fail_msg, new Object[]{mTypeStrings.get(p1),mMessages.get(p1)}));
			AppCompatImageView imageViewIcon = (AppCompatImageView)view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_icon);
			try
			{
				if (mIconPaths.get(p1) != null)
					imageViewIcon.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(mIconPaths.get(p1))));
				else
					imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nmod_icon_default));
			}
			catch (FileNotFoundException e)
			{
				imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nmod_icon_default));
			}
			final int index = p1;
			view.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View p1)
					{
						new AlertDialog.Builder(NModLoadFailActivity.this).setTitle(R.string.load_fail_title).setMessage(getString(R.string.load_fail_msg, new Object[]{mTypeStrings.get(index),mMessages.get(index)})).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									p1.dismiss();
								}


							}).show();
					}
				});
			return view;
		}
	}

	private void onNextClicked()
	{
		Intent intent = new Intent(this, MinecraftActivity.class);
		intent.putExtras(mMCData);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed()
	{

	}

	public static void startThisActivity(Context context, ArrayList<NMod> nmods, Bundle data)
	{
		Intent intent=new Intent(context, NModLoadFailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		ArrayList<String> mPackageNames = new ArrayList<>();
		ArrayList<String> mMessages = new ArrayList<>();
		ArrayList<String> mTypeStrings = new ArrayList<>();
		ArrayList<String> mIconPaths = new ArrayList<>();
//		for (NMod nmod:nmods)
//		{
//			mPackageNames.add(nmod.getPackageName());
//			mMessages.add(nmod.getLoadException().getCause().toString());
//			mTypeStrings.add(nmod.getLoadException().toTypeString());
//			File iconPath = nmod.copyIconToData();
//			if (iconPath != null)
//				mIconPaths.add(iconPath.getAbsolutePath());
//			else
//				mIconPaths.add(null);
//		}
		bundle.putStringArrayList(KEY_MESSAGE, mMessages);
		bundle.putStringArrayList(KEY_ICON_PATH, mIconPaths);
		bundle.putStringArrayList(KEY_TYPE_STRING, mTypeStrings);
		bundle.putStringArrayList(KEY_PACKAGE_NAME, mPackageNames);
		bundle.putBundle(KEY_MC_DATA, data);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
}
