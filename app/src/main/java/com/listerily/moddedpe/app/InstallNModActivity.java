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
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.listerily.mcdesign.app.DesignActivity;
import com.listerily.moddedpe.R;

import java.io.File;
public class InstallNModActivity extends DesignActivity
{
    private static final int UI_FILE_NOT_FOUND = 0;
    private static final int UI_LOADING = 1;
    private static final int UI_CONTENT = 2;
    private static final int UI_INSTALLING = 3;

    private int currentUIType = UI_LOADING;
    private File targetPackageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        targetPackageFile = getPackageFile();

        if (targetPackageFile == null)
            switchDialogUI(UI_FILE_NOT_FOUND);
        else
            switchDialogUI(UI_LOADING);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void switchDialogUI(int uiType)
    {
        currentUIType = uiType;

        switch (uiType)
        {
            case UI_LOADING:
                setContentView(R.layout.install_nmod_loading);
                break;
            case UI_FILE_NOT_FOUND:
                setContentView(R.layout.install_nmod_file_not_found);
                break;
        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed()
    {
        if (currentUIType == UI_CONTENT || currentUIType == UI_FILE_NOT_FOUND)
            super.onBackPressed();
    }

    private File getPackageFile()
        {
        Intent intent = getIntent();
        String path = intent.getExtras().getString("file");
        if(path == null)
            return null;
        return new File(path);
    }
}
