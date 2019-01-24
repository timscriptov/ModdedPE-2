package com.listerily.moddedpe.app;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.listerily.mcdesign.app.DesignActivity;
import com.listerily.minecraftcore.android.nmod.NModManager;
import com.listerily.minecraftcore.android.nmod.instance.NMod;
import com.listerily.moddedpe.R;
import com.listerily.moddedpe.nmod.NModOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ManageNModActivity extends DesignActivity
{
    private ListView mListView;
    private UIHandler mUIHandler = new UIHandler(this);
    private NModManager mNModManager;

    private static final int MSG_MANAGER_INITIALIZED = 1;
    private static final int MSG_SHOW_SUCCEED_DIALOG = 3;
    private static final int MSG_SHOW_REPLACED_DIALOG = 4;
    private static final int MSG_SHOW_FAILED_DIALOG = 5;
    private static final int MSG_REFRESH_NMOD_DATA = 6;

    private static final int REQUEST_CODE_PICK_FILE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_manage_nmod);

        mListView = (ListView) findViewById(R.id.moddedpe_manage_nmod_list_view);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        findViewById(R.id.moddedpe_manage_nmod_loading).setVisibility(View.VISIBLE);
        findViewById(R.id.moddedpe_manage_nmod_layout_nmods).setVisibility(View.GONE);
        findViewById(R.id.moddedpe_manage_nmod_layout_no_found).setVisibility(View.GONE);

        mNModManager = new NModManager(this,new NModOptions(this));
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                mNModManager.init();

                mUIHandler.sendEmptyMessage(MSG_MANAGER_INITIALIZED);
            }
        }.start();
    }

    private void updateListView()
    {
        NModListAdapter adapterList = new NModListAdapter();
        mListView.setAdapter(adapterList);
    }

    private static class UIHandler extends Handler
    {
        WeakReference<ManageNModActivity> activity = null;
        UIHandler(ManageNModActivity activity)
        {
            this.activity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case MSG_MANAGER_INITIALIZED:
                    activity.get().findViewById(R.id.moddedpe_manage_nmod_loading).setVisibility(View.GONE);
                    if (activity.get().mNModManager.getInstalledNMods().isEmpty())
                    {
                        activity.get().findViewById(R.id.moddedpe_manage_nmod_layout_nmods).setVisibility(View.GONE);
                        activity.get().findViewById(R.id.moddedpe_manage_nmod_layout_no_found).setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        activity.get().findViewById(R.id.moddedpe_manage_nmod_layout_nmods).setVisibility(View.VISIBLE);
                        activity.get().findViewById(R.id.moddedpe_manage_nmod_layout_no_found).setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_PICK_FILE)
        {
            if(resultCode == Activity.RESULT_OK && data.getData() != null)
            {
                Intent intent = new Intent(this,InstallNModActivity.class);
                Bundle extra = new Bundle();
                extra.putString("file",data.getData().toString());
                intent.putExtras(extra);
                startActivity(intent);
            }
        }
//
//        if (resultCode == Activity.RESULT_OK)
//        {
//            if (requestCode == NModPackagePickerActivity.REQUEST_PICK_PACKAGE)
//            {
//                //picked from package
//                onPickedNModFromPackage(data.getExtras().getString(NModPackagePickerActivity.TAG_PACKAGE_NAME));
//            }
//            else if (requestCode == NModFilePickerActivity.REQUEST_PICK_FILE)
//            {
//                //picked from storage
//                onPickedNModFromStorage(data.getExtras().getString(NModFilePickerActivity.TAG_FILE_PATH));
//            }
//
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pickNewNMod()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/" + getPackageName());
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult( Intent.createChooser(intent, getString(R.string.nmod_picker_file_title)), REQUEST_CODE_PICK_FILE);
    }


//    public void showPickNModFailedDialog(ExtractFailedException archiveFailedException)
//    {
//        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this).setTitle(R.string.nmod_import_failed).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
//        {
//
//            @Override
//            public void onClick(DialogInterface p1, int p2)
//            {
//                p1.dismiss();
//            }
//
//
//        });
//        switch (archiveFailedException.getType())
//        {
//            case ExtractFailedException.TYPE_DECODE_FAILED:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_decode);
//                break;
//            case ExtractFailedException.TYPE_UNEQUAL_PACKAGE_NAME:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_inequal_package_name);
//                break;
//            case ExtractFailedException.TYPE_INVALID_PACKAGE_NAME:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_invalid_package_name);
//                break;
//            case ExtractFailedException.TYPE_IO_EXCEPTION:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_io_exception);
//                break;
//            case ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_manifest_json_syntax_error);
//                break;
//            case ExtractFailedException.TYPE_NO_MANIFEST:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_no_manifest);
//                break;
//            case ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_no_package_name);
//                break;
//            case ExtractFailedException.TYPE_REDUNDANT_MANIFEST:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_no_package_name);
//                break;
//            default:
//                alertBuilder.setMessage(R.string.nmod_import_failed_message_unexpected);
//                break;
//        }
//        if (archiveFailedException.getCause() != null)
//        {
//            final ExtractFailedException fArvhiveFailedException = archiveFailedException;
//            alertBuilder.setNegativeButton(R.string.nmod_import_failed_button_full_info, new DialogInterface.OnClickListener()
//            {
//
//                @Override
//                public void onClick(DialogInterface p1, int p2)
//                {
//                    p1.dismiss();
//                    new AlertDialog.Builder(ManageNModActivity.this).setTitle(R.string.nmod_import_failed_full_info_title).setMessage(ManageNModActivity.this.getResources().getString(R.string.nmod_import_failed_full_info_message, new Object[]{fArvhiveFailedException.toTypeString(),fArvhiveFailedException.getCause().toString()})).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
//                    {
//
//                        @Override
//                        public void onClick(DialogInterface p1_, int p2)
//                        {
//                            p1_.dismiss();
//                        }
//
//
//                    }).show();
//                }
//
//
//            });
//        }
//        alertBuilder.show();
//    }
//
//    private static class NModProcesserHandler extends Handler
//    {
//        WeakReference<ManageNModActivity> activity;
//        NModProcesserHandler(ManageNModActivity activity)
//        {
//            this.activity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg)
//        {
//            super.handleMessage(msg);
//            switch (msg.what)
//            {
//                case MSG_SHOW_PROGRESS_DIALOG:
//                    activity.get().mProcessingDialog = new AlertDialog.Builder(activity.get()).setTitle(R.string.nmod_importing_title).setView(R.layout.moddedpe_manage_nmod_progress_dialog_view).setCancelable(false).show();
//                    break;
//                case MSG_HIDE_PROGRESS_DIALOG:
//                    if (activity.get().mProcessingDialog != null)
//                        activity.get().mProcessingDialog.hide();
//                    activity.get().mProcessingDialog = null;
//                    break;
//                case MSG_SHOW_SUCCEED_DIALOG:
//
//                    new AlertDialog.Builder(activity.get()).setTitle(R.string.nmod_import_succeed_title).setMessage(R.string.nmod_import_succeed_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
//                    {
//
//                        @Override
//                        public void onClick(DialogInterface p1, int p2)
//                        {
//                            p1.dismiss();
//                        }
//
//
//                    }).show();
//                    break;
//                case MSG_SHOW_REPLACED_DIALOG:
//                    new AlertDialog.Builder(activity.get()).setTitle(R.string.nmod_import_replaced_title).setMessage(R.string.nmod_import_replaced_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
//                    {
//
//                        @Override
//                        public void onClick(DialogInterface p1, int p2)
//                        {
//                            p1.dismiss();
//                        }
//
//
//                    }).show();
//                    break;
//                case MSG_SHOW_FAILED_DIALOG:
//                    activity.get().showPickNModFailedDialog((ExtractFailedException)msg.obj);
//                    break;
//                case MSG_REFRESH_NMOD_DATA:
//                    activity.get().refreshNModDatas();
//                    break;
//            }
//        }
//    }
//
//    private void showBugDialog(NMod nmod)
//    {
//        if(!nmod.isValidPack())
//            return;
//        new AlertDialog.Builder(this).setTitle(R.string.load_fail_title).setMessage(getString(R.string.load_fail_msg,new String[]{nmod.getLoadException().toTypeString(),nmod.getLoadException().getCause().toString()})).setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener()
//        {
//
//            @Override
//            public void onClick(DialogInterface p1, int p2)
//            {
//                p1.dismiss();
//            }
//
//
//        }).show();
//    }

    private class NModListAdapter extends BaseAdapter
    {
        private ArrayList<NMod> mImportedEnabledNMods = new ArrayList<>();
        private ArrayList<NMod> mImportedDisabledNMods = new ArrayList<>();

        NModListAdapter()
        {
            mImportedEnabledNMods.addAll(mNModManager.getEnabledNMods());
            mImportedDisabledNMods.addAll(mNModManager.getDisabledNMods());
        }

        @Override
        public int getCount()
        {
            int count = mImportedEnabledNMods.size() + mImportedDisabledNMods.size() + 2;
            if (mImportedEnabledNMods.size() > 0)
                ++count;
            return count;
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            boolean shouldShowEnabledList = mImportedEnabledNMods.size() > 0 && (position  < mImportedEnabledNMods.size() + 1);
            if (shouldShowEnabledList)
            {
                if (position == 0)
                {
                    return createCutlineView(R.string.nmod_enabled_title);
                }
                else
                {
                    int nmodIndex = position - 1;
                    return createEnabledNModView(mImportedEnabledNMods.get(nmodIndex));
                }
            }
            int disableStartPosition = mImportedEnabledNMods.size() > 0 ? mImportedEnabledNMods.size() + 1: 0;
            if (position == disableStartPosition)
            {
                return createCutlineView(R.string.nmod_disabled_title);
            }
            int itemInListPosition = position - 1 - disableStartPosition;
            if (itemInListPosition >= 0 && itemInListPosition < mImportedDisabledNMods.size())
            {
                return createDisabledNModView(mImportedDisabledNMods.get(itemInListPosition));
            }
            return createAddNewView();
        }

    }

    private View createCutlineView(int textResId)
    {
        View convertView = LayoutInflater.from(this).inflate(R.layout.moddedpe_ui_cutline, null);
        AppCompatTextView textTitle = (AppCompatTextView) convertView.findViewById(R.id.moddedpe_cutline_textview);
        textTitle.setText(textResId);
        return convertView;
    }

    private View createAddNewView()
    {
        final View convertView = LayoutInflater.from(this).inflate(R.layout.moddedpe_nmod_item_new, null);
        convertView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                onAddNewNMod(convertView);
            }


        });
        return convertView;
    }

    private View createDisabledNModView(NMod nmod_)
    {
        final NMod nmod = nmod_;
        View convertView;
        convertView = LayoutInflater.from(this).inflate(R.layout.moddedpe_nmod_item_disabled, null);
        AppCompatTextView textTitle = (AppCompatTextView) convertView.findViewById(R.id.nmod_disabled_item_card_view_text_name);
        textTitle.setText(nmod.getName());
        AppCompatTextView textPkgTitle = (AppCompatTextView) convertView.findViewById(R.id.nmod_disabled_item_card_view_text_package_name);
        textPkgTitle.setText(nmod.getPackageName());
        AppCompatImageView imageIcon = (AppCompatImageView) convertView.findViewById(R.id.nmod_disabled_item_card_view_image_view);
        Bitmap nmodIcon = nmod.getIcon();
        if (nmodIcon == null)
            nmodIcon = BitmapFactory.decodeResource(getResources(), R.drawable.nmod_icon_default);
        imageIcon.setImageBitmap(nmodIcon);
        AppCompatImageButton addButton = (AppCompatImageButton) convertView.findViewById(R.id.nmod_disabled_add);
        addButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                mNModManager.setEnabled(nmod);
                updateListView();
            }


        });
        AppCompatImageButton deleteButton = (AppCompatImageButton) convertView.findViewById(R.id.nmod_disabled_delete);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                new AlertDialog.Builder(ManageNModActivity.this).setTitle(R.string.nmod_delete_title).setMessage(R.string.nmod_delete_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        mNModManager.setEnabled(nmod);
                        updateListView();
                        p1.dismiss();
                    }


                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        p1.dismiss();
                    }


                }).show();
            }


        });
        convertView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                NModDescriptionActivity.startThisActivity(ManageNModActivity.this, nmod);
            }


        });
        return convertView;
    }

    private View createEnabledNModView(NMod nmod_)
    {
        final NMod nmod = nmod_;
        View convertView;
        convertView = LayoutInflater.from(this).inflate(R.layout.moddedpe_nmod_item_active, null);
        AppCompatTextView textTitle = (AppCompatTextView) convertView.findViewById(R.id.nmod_enabled_item_card_view_text_name);
        textTitle.setText(nmod.getName());
        AppCompatTextView textPkgTitle = (AppCompatTextView) convertView.findViewById(R.id.nmod_enabled_item_card_view_text_package_name);
        textPkgTitle.setText(nmod.getPackageName());
        AppCompatImageView imageIcon = (AppCompatImageView) convertView.findViewById(R.id.nmod_enabled_item_card_view_image_view);
        Bitmap nmodIcon = nmod.getIcon();
        if (nmodIcon == null)
            nmodIcon = BitmapFactory.decodeResource(getResources(), R.drawable.nmod_icon_default);
        imageIcon.setImageBitmap(nmodIcon);
        AppCompatImageButton minusButton = (AppCompatImageButton) convertView.findViewById(R.id.nmod_enabled_minus);
        minusButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                mNModManager.setEnabled(nmod);
                updateListView();
            }


        });
        AppCompatImageButton downButton = (AppCompatImageButton) convertView.findViewById(R.id.nmod_enabled_arrow_down);
        downButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                mNModManager.downOrderNMod(nmod);
                updateListView();
            }


        });
        AppCompatImageButton upButton = (AppCompatImageButton) convertView.findViewById(R.id.nmod_enabled_arrow_up);
        upButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                mNModManager.upOrderNMod(nmod);
                updateListView();
            }


        });
        convertView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View p1)
            {
                NModDescriptionActivity.startThisActivity(ManageNModActivity.this, nmod);
            }


        });
        return convertView;
    }

    public void onAddNewNMod(View view)
    {
        if(checkPermissions())
            pickNewNMod();
    }

    private boolean checkPermissions()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
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
                pickNewNMod();
            }
            else
            {
                showPermissionDinedDialog();
            }
        }
    }

    private void showPermissionDinedDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_grant_failed_title);
        builder.setMessage(R.string.permission_grant_failed_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + ManageNModActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }


}
