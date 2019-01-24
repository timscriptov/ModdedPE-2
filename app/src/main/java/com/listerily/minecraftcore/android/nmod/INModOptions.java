package com.listerily.minecraftcore.android.nmod;

import java.io.File;
import java.util.ArrayList;

public interface INModOptions
{
    ArrayList<String> getEnabledNModOrder();
    ArrayList<String> getInstalledNMods();
    ArrayList<String> getEnabledNMods();
    ArrayList<String> getDisabledNMods();

    void setNModOrder(ArrayList<String> order);
    void uninstallNMod(String nmod);
    void enableNMod(String nmod);
    void disableNMod(String nmod);
    void upOrderNMod(String nmod);
    void downOrderNMod(String nmod);

    File getNModInstallationDir();
    File getNModTempAssetsDir();
}
