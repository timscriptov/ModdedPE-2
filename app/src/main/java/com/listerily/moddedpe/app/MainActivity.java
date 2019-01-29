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

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.listerily.mcdesign.app.DesignActivity;
import com.listerily.minecraftcore.android.nmod.NModManager;
import com.listerily.minecraftcore.android.tools.MinecraftInfo;
import com.listerily.moddedpe.R;
import com.listerily.moddedpe.nmod.NModCloud;
import com.listerily.moddedpe.nmod.NModOptions;
import com.listerily.moddedpe.utils.UtilsSettings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends DesignActivity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private MinecraftInfo info;
    private ArrayList<NModCloud> cloudNMods = new ArrayList<>();
    private UIHandler handler = new UIHandler(this);
    private HomeAdapter homeAdapter;
    private ManageNModsAdapter manageNModsAdapter;

    private static final int REQUEST_CODE_PICK_FILE = 1;

    private static final int MSG_RECEIVED_LIST = 0;
    private static final int MSG_RECEIVED_ERROR = 1;
    private static final int MSG_LOADED_CLOUD_NMOD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        manageNModsAdapter = new ManageNModsAdapter(this, getLayoutInflater());
        homeAdapter = new HomeAdapter(this, getLayoutInflater());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                updateFab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        info = new MinecraftInfo(this, new UtilsSettings(this));

        updateFab(0);
        checkMinecraftWarnings();
        initializeSeverNMods();
    }

    private void initializeSeverNMods()
    {
        homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_TEXT_NMODS_TITLE));
        homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_NMOD_SEVER_CONNECING));
        updateSeverList();
    }

    private void checkMinecraftWarnings()
    {
        boolean isMinecraftInstalled = isGameInstalled();
        boolean isSupportedGameVersion = isSupportedGameVersion();
        if (!isMinecraftInstalled || !isSupportedGameVersion)
            homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_TEXT_WARNING_TITLE));
        if (!isMinecraftInstalled)
            homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_WARNING_GAME_NOT_FOUND));
        if (!isSupportedGameVersion)
            homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_WARNING_GAME_VERSION));
    }

    private void updateCloudNModsUI()
    {
        homeAdapter.remove(homeAdapter.dataList.size() - 1);
        for (NModCloud nmod : cloudNMods)
        {
            homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_NMOD_RECOMMENDED, nmod));
        }
    }

    private void updateConnectionFailedUI()
    {
        homeAdapter.remove(homeAdapter.dataList.size() - 1);
        homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_NMOD_SEVER_CONNECTION_FAILED));
    }

    private void updateSeverList()
    {
        homeAdapter.remove(homeAdapter.dataList.size() - 1);
        homeAdapter.add(new HomeAdapter.ViewData(HomeAdapter.TYPE_NMOD_SEVER_CONNECING));
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                try
                {
                    ArrayList<NModCloud> nmods = NModCloud.getCloudNModList(getCacheDir());
                    Message message = new Message();
                    message.what = MSG_RECEIVED_LIST;
                    message.obj = nmods;
                    handler.sendMessage(message);
                }
                catch (Exception e)
                {
                    handler.sendEmptyMessage(MSG_RECEIVED_ERROR);
                }
            }
        }.start();
    }

    private void updateFab(int section)
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (section == 0)
        {
            fab.setImageResource(R.drawable.hammer);
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    startGame(false);
                }
            });
        }
        if (section == 1)
        {
            fab.setImageResource(R.drawable.mcd_add_pack);
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    addNewNMod(false);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            boolean isAllGranted = true;

            for (int grant : grantResults)
            {
                if (grant != PackageManager.PERMISSION_GRANTED)
                {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted)
            {
                addNewNMod(true);
            }
            else
            {
                new AlertDialog.Builder(this).setTitle(R.string.main_dialog_permission_dined_title).setMessage(R.string.main_dialog_permission_dined_content).setPositiveButton(android.R.string.ok, null).show();
            }
        }
    }

    private void addNewNMod(boolean direct)
    {
        if (!direct && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.nmod_picker_file_title)), REQUEST_CODE_PICK_FILE);
        }
    }


    private void startGame(boolean direct)
    {
        if (!direct && !isSupportedGameVersion())
        {
            showDialogUnsupportedGameVersion();
        }
        else
        {
            //TODO start game
        }
    }

    private boolean isSupportedGameVersion()
    {
        return info.isSupportedMinecraftVersion(getResources().getStringArray(R.array.target_mcpe_versions));
    }

    private void showDialogUnsupportedGameVersion()
    {
        new AlertDialog.Builder(this).setTitle(R.string.main_dialog_unsupported_game_version_title).setMessage(getString(R.string.main_dialog_unsupported_game_version_content, getString(R.string.target_mcpe_version_info), info.getMinecraftVersionName())).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startGame(true);
            }
        }).setNegativeButton(android.R.string.cancel, null).show();
    }


    private boolean isGameInstalled()
    {
        return info.isMinecraftInstalled();
    }

    private void showDialogGameNotFound()
    {
        new AlertDialog.Builder(this).setTitle(R.string.main_dialog_game_not_found_title).setMessage(R.string.main_dialog_game_not_found_content).setPositiveButton(android.R.string.ok, null).show();
    }

    public static class UIHandler extends Handler
    {
        private WeakReference<MainActivity> activity;

        public UIHandler(MainActivity activity)
        {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            if (msg.what == MSG_RECEIVED_LIST)
            {
                activity.get().cloudNMods = (ArrayList<NModCloud>) msg.obj;
                activity.get().updateCloudNModsUI();
            }
            else if (msg.what == MSG_RECEIVED_ERROR)
            {
                activity.get().updateConnectionFailedUI();
            }
        }
    }

    public static class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>
    {
        static class ViewHolder extends RecyclerView.ViewHolder
        {
            ViewHolder(View view)
            {
                super(view);
            }
        }

        static class ViewData
        {
            int type;
            Object data;

            ViewData(int type)
            {
                this.type = type;
            }

            ViewData(int type, Object data)
            {
                this.type = type;
                this.data = data;
            }
        }

        private WeakReference<MainActivity> activity;
        private LayoutInflater inflater;
        private ArrayList<ViewData> dataList;

        private static final int TYPE_TEXT_WARNING_TITLE = 0;
        private static final int TYPE_TEXT_NMODS_TITLE = 1;
        private static final int TYPE_WARNING_GAME_NOT_FOUND = 2;
        private static final int TYPE_WARNING_GAME_VERSION = 3;
        private static final int TYPE_NMOD_SEVER_CONNECING = 4;
        private static final int TYPE_NMOD_SEVER_CONNECTION_FAILED = 5;
        private static final int TYPE_NMOD_RECOMMENDED = 6;

        public HomeAdapter(MainActivity activity, LayoutInflater inflater)
        {
            this.activity = new WeakReference<>(activity);
            this.inflater = inflater;
            this.dataList = new ArrayList<>();
        }

        public void add(int position, ViewData data)
        {
            dataList.add(position, data);
            notifyItemInserted(position);
        }

        public void add(ViewData data)
        {
            dataList.add(data);
            notifyItemInserted(dataList.size() - 1);
        }

        public void remove(int position)
        {
            dataList.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            if (getItemViewType(position) == TYPE_WARNING_GAME_VERSION)
            {
                CardView warningView = (CardView) holder.itemView;
                ((AppCompatImageView) warningView.findViewById(R.id.item_unsupported_game_image)).setImageResource(R.drawable.warning_unsupported_version);
                ((AppCompatTextView) warningView.findViewById(R.id.item_unsupported_game_title)).setText(R.string.main_item_unsupported_game_version);
                warningView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        activity.get().showDialogUnsupportedGameVersion();
                    }
                });
            }
            else if (getItemViewType(position) == TYPE_WARNING_GAME_NOT_FOUND)
            {
                CardView warningView = (CardView) holder.itemView;
                ((AppCompatImageView) warningView.findViewById(R.id.item_unsupported_game_image)).setImageResource(R.drawable.warning_game_not_found);
                ((AppCompatTextView) warningView.findViewById(R.id.item_unsupported_game_title)).setText(R.string.main_item_unsupported_game_not_found);
                warningView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        activity.get().showDialogGameNotFound();
                    }
                });
            }
            else if (getItemViewType(position) == TYPE_NMOD_SEVER_CONNECTION_FAILED)
            {
                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        activity.get().updateSeverList();
                    }
                });
            }
            else if (getItemViewType(position) == TYPE_NMOD_RECOMMENDED)
            {

                NModCloud nmod = (NModCloud) dataList.get(position).data;
                CardView warningView = (CardView) holder.itemView;
                ((AppCompatImageView) warningView.findViewById(R.id.main_item_nmod_icon)).setImageBitmap(nmod.getIcon());
                ((AppCompatTextView) warningView.findViewById(R.id.main_item_nmod_name)).setText(nmod.getManifest().name);
                if()
                ((AppCompatTextView) warningView.findViewById(R.id.main_item_nmod_summary)).setText(R.string.main_item_unsupported_game_not_found);
                warningView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //TODO install NMod
                    }
                });
            }
        }

        @Override
        public int getItemCount()
        {
            return dataList.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case TYPE_TEXT_WARNING_TITLE:
                    return new ViewHolder(inflater.inflate(R.layout.main_item_warning, parent, false));
                case TYPE_WARNING_GAME_NOT_FOUND:
                case TYPE_WARNING_GAME_VERSION:
                    return new ViewHolder(inflater.inflate(R.layout.main_item_unsupported_game, parent, false));
                case TYPE_TEXT_NMODS_TITLE:
                    return new ViewHolder(inflater.inflate(R.layout.main_item_nmod_recommended, parent, false));
                case TYPE_NMOD_RECOMMENDED:
                    return new ViewHolder(inflater.inflate(R.layout.main_item_nmod, parent, false));
                case TYPE_NMOD_SEVER_CONNECING:
                    return new ViewHolder(inflater.inflate(R.layout.main_item_connecting, parent, false));
                case TYPE_NMOD_SEVER_CONNECTION_FAILED:
                    return new ViewHolder(inflater.inflate(R.layout.main_item_connection_failed, parent, false));
            }
            return null;
        }

        @Override
        public int getItemViewType(int position)
        {
            return dataList.get(position).type;
        }
    }

    public static class ManageNModsAdapter extends RecyclerView.Adapter<ManageNModsAdapter.ViewHolder>
    {
        static class ViewHolder extends RecyclerView.ViewHolder
        {
            ViewHolder(View view)
            {
                super(view);
            }
        }

        private WeakReference<MainActivity> activity;
        private LayoutInflater inflater;
        private NModManager nmodManager;

        private static final int TYPE_TITLE_ENABLED = 0;
        private static final int TYPE_TITLE_DISABLED = 1;
        private static final int TYPE_ADD_NEW = 2;
        private static final int TYPE_ENABLED_NMOD = 3;
        private static final int TYPE_DISABLED_NMOD = 3;

        public ManageNModsAdapter(MainActivity activity, LayoutInflater inflater)
        {
            this.activity = new WeakReference<>(activity);
            this.inflater = inflater;
            this.nmodManager = new NModManager(activity, new NModOptions(activity));

            this.nmodManager.init();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {

        }

        @Override
        public int getItemCount()
        {
            int count = 1;
            if (!nmodManager.getEnabledNMods().isEmpty())
                ++count;
            if (!nmodManager.getDisabledNMods().isEmpty())
                ++count;
            return nmodManager.getInstalledNMods().size() + count;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            //TODO: create view holder
            return new ViewHolder(new View(activity.get()));
        }

        @Override
        public int getItemViewType(int position)
        {
            boolean enabledEmpty = nmodManager.getEnabledNMods().isEmpty();
            boolean disabledEmpty = nmodManager.getDisabledNMods().isEmpty();

            if (position == 0 && !enabledEmpty)
                return TYPE_TITLE_ENABLED;
            if (!enabledEmpty && position - 1 < nmodManager.getEnabledNMods().size())
                return TYPE_ENABLED_NMOD;
            if (!enabledEmpty && !disabledEmpty && position - 2 == nmodManager.getEnabledNMods().size())
                return TYPE_TITLE_DISABLED;
            if (enabledEmpty && !disabledEmpty && position - 1 == nmodManager.getEnabledNMods().size())
                return TYPE_TITLE_DISABLED;
            if (!enabledEmpty && !disabledEmpty && position - 3 == nmodManager.getEnabledNMods().size())
                return TYPE_ADD_NEW;
            if (enabledEmpty && !disabledEmpty && position - 2 == nmodManager.getEnabledNMods().size())
                return TYPE_ADD_NEW;
            return TYPE_DISABLED_NMOD;
        }
    }

    public static class PlaceholderFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment()
        {

        }

        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            if (section == 0)
            {
                RecyclerView rootView = (RecyclerView) inflater.inflate(R.layout.main_fragment, container, false);
                rootView.setLayoutManager(new LinearLayoutManager(getActivity()));
                rootView.setAdapter(((MainActivity) getActivity()).homeAdapter);
                return rootView;
            }
            else if (section == 1)
            {
                RecyclerView rootView = (RecyclerView) inflater.inflate(R.layout.main_fragment, container, false);
                rootView.setLayoutManager(new LinearLayoutManager(getActivity()));
                rootView.setAdapter(((MainActivity) getActivity()).manageNModsAdapter);
                return rootView;
            }
            return null;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount()
        {
            return 2;
        }
    }
}
