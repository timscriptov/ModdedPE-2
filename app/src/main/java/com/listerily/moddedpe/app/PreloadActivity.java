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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.widget.LinearLayout;

import com.listerily.mcdesign.app.DesignActivity;
import com.listerily.moddedpe.R;
import com.listerily.moddedpe.utils.UtilsSettings;

import com.listerily.minecraftcore.android.nmod.instance.NMod;

import java.util.ArrayList;
import java.util.Random;


public class PreloadActivity extends DesignActivity
{
//	private PreloadUIHandler mPreloadUIHandler = new PreloadUIHandler();
//	private LinearLayout mPreloadingMessageLayout;
//	private final static int MSG_START_MINECRAFT = 1;
//	private final static int MSG_WRITE_TEXT = 2;
//	private final static int MSG_ERROR = 3;
//	private final static int MSG_START_NMOD_LOADING_FAILED = 4;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.moddedpe_preloading);
//
//		AppCompatTextView tipsText = (AppCompatTextView)findViewById(R.id.moddedpe_preloading_text);
//		String[] tipsArray = getResources().getStringArray(R.array.preloading_tips_text);
//		tipsText.setText(tipsArray[new Random().nextInt(tipsArray.length)]);
//
//		mPreloadingMessageLayout = (LinearLayout)findViewById(R.id.moddedpe_preloading_texts_adapted_layput);
//
//		new PreloadThread().start();
//	}
//
//	private class PreloadThread extends Thread
//	{
//		private ArrayList<NMod> mFailedNMods = new ArrayList<>();
//		@Override
//		public void run()
//		{
//			super.run();
//
//			try
//			{
//				new Preloader(getPESdk(), null, new Preloader.PreloadListener()
//					{
//						@Override
//						public void onStart()
//						{
//							writeNewText(getString(R.string.preloading_initing));
//							if (getPESdk().getLauncherOptions().isSafeMode())
//								writeNewText(getString(R.string.preloading_initing_info_safe_mode, new Object[]{getPESdk().getMinecraftInfo().getMinecraftVersionName()}));
//							else
//								writeNewText(getString(R.string.preloading_initing_info, new Object[]{getPESdk().getNModAPI().getVersionName(),getPESdk().getMinecraftInfo().getMinecraftVersionName()}));
//							try
//							{
//								Thread.sleep(1500);
//							}
//							catch (InterruptedException e)
//							{
//								e.printStackTrace();
//							}
//						}
//
//						@Override
//						public void onLoadNativeLibs()
//						{
//							writeNewText(getString(R.string.preloading_initing_loading_libs));
//						}
//
//						@Override
//						public void onLoadSubstrateLib()
//						{
//							writeNewText(getString(R.string.preloading_loading_lib_substrate));
//						}
//
//						@Override
//						public void onLoadFModLib()
//						{
//							writeNewText(getString(R.string.preloading_loading_lib_fmod));
//						}
//
//						@Override
//						public void onLoadMinecraftPELib()
//						{
//							writeNewText(getString(R.string.preloading_loading_lib_minecraftpe));
//						}
//
//						@Override
//						public void onLoadPESdkLib()
//						{
//							writeNewText(getString(R.string.preloading_loading_lib_pesdk));
//						}
//
//						@Override
//						public void onFinishedLoadingNativeLibs()
//						{
//							writeNewText(getString(R.string.preloading_initing_loading_libs_done));
//						}
//
//						@Override
//						public void onStartLoadingAllNMods()
//						{
//							writeNewText(getString(R.string.preloading_nmod_start_loading));
//						}
//
//						@Override
//						public void onFinishedLoadingAllNMods()
//						{
//							writeNewText(getString(R.string.preloading_nmod_finish_loading));
//						}
//
//						@Override
//						public void onNModLoaded(NMod nmod)
//						{
//							writeNewText(getString(R.string.preloading_nmod_loaded, new Object[]{nmod.getPackageName()}));
//						}
//
//						@Override
//						public void onFailedLoadingNMod(NMod nmod)
//						{
//							writeNewText(getString(R.string.preloading_nmod_loaded_failed, new Object[]{nmod.getPackageName()}));
//							mFailedNMods.add(nmod);
//						}
//
//						@Override
//						public void onFinish(Bundle bundle)
//						{
//							if (mFailedNMods.isEmpty())
//							{
//								writeNewText(getString(R.string.preloading_finished));
//								try
//								{
//									Thread.sleep(1500);
//								}
//								catch (InterruptedException e)
//								{
//									e.printStackTrace();
//								}
//								Message message = new Message();
//								message.what = MSG_START_MINECRAFT;
//								message.setData(bundle);
//								mPreloadUIHandler.sendMessage(message);
//							}
//							else
//							{
//								Message message = new Message();
//								message.what = MSG_START_NMOD_LOADING_FAILED;
//								message.obj = mFailedNMods;
//								message.setData(bundle);
//								mPreloadUIHandler.sendMessage(message);
//							}
//						}
//
//					}).preload(PreloadActivity.this);
//			}
//			catch (PreloadException e)
//			{
//				Message message = new Message();
//				message.what = MSG_ERROR;
//				message.obj = e;
//				mPreloadUIHandler.sendMessage(message);
//			}
//		}
//	}
//
//	@Override
//	public void onBackPressed()
//	{
//
//	}
//
//	private void writeNewText(String text)
//	{
//		Message message = new Message();
//		message.obj = text;
//		message.what = MSG_WRITE_TEXT;
//		mPreloadUIHandler.sendMessage(message);
//	}
//
//	private class PreloadUIHandler extends Handler
//	{
//
//		@Override
//		public void handleMessage(Message msg)
//		{
//			super.handleMessage(msg);
//
//			if (msg.what == MSG_WRITE_TEXT)
//			{
//				AppCompatTextView textView = (AppCompatTextView)getLayoutInflater().inflate(R.layout.moddedpe_ui_text_small, null);
//				textView.setText((CharSequence)msg.obj);
//				mPreloadingMessageLayout.addView(textView);
//			}
//			else if (msg.what == MSG_START_MINECRAFT)
//			{
//				Intent intent = new Intent(PreloadActivity.this, MinecraftActivity.class);
//				intent.putExtras(msg.getData());
//				startActivity(intent);
//				finish();
//			}
//			else if (msg.what == MSG_ERROR)
//			{
//				PreloadException preloadException = (PreloadException)msg.obj;
//				new UtilsSettings(PreloadActivity.this).setOpenGameFailed(preloadException.toString());
//				Intent intent = new Intent(PreloadActivity.this,SplashesActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				finish();
//			}
//			else if (msg.what == MSG_START_NMOD_LOADING_FAILED)
//			{
//				NModLoadFailActivity.startThisActivity(PreloadActivity.this, (ArrayList<NMod>)msg.obj,msg.getData());
//				finish();
//			}
//		}
//	}
}
