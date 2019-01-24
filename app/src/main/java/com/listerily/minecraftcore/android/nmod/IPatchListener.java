package com.listerily.minecraftcore.android.nmod;

import com.listerily.minecraftcore.android.nmod.exception.PatchException;
import com.listerily.minecraftcore.android.nmod.instance.NMod;

public interface IPatchListener
{
    void onPatch(NMod nmod);
    void onPatchLibraries(NMod nmod);
    void onPatchAssets(NMod nmod);
    void onPatchSucceed(NMod nmod);
    void onPatchFailed(NMod nmod, PatchException cause);
}
