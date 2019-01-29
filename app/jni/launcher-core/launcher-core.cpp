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
#include <jni.h>
#include <android/native_activity.h>
#include <dlfcn.h>
#include <string>
#include "Substrate.h"

struct android_app;
inline std::string toString(JNIEnv* env, jstring j_str)
{
    //DO NOT RELEASE.
    const char * c_str = env->GetStringUTFChars(j_str, nullptr);
    std::string cpp_str = c_str;
    return cpp_str;
}

bool hook_isGameLicensed()
{
    return true;
}

//==================================================================================================
// Method Pointers
//==================================================================================================

void (*mOnCreateFunc)(ANativeActivity*,void*,size_t) = nullptr;
void (*mFinishFunc)(ANativeActivity*) = nullptr;
void (*mMainFunc)(struct android_app*) = nullptr;

std::string mMinecraftNativeLibPath;
std::string* mAndroidAppDataPath;

//==================================================================================================
// Launcher Core
//==================================================================================================

extern "C" JNIEXPORT void JNICALL Java_org_mcal_pesdk_nativeapi_NativeUtils_nativeSetDataDirectory(JNIEnv*env,jobject,jstring directory)
{
    *mAndroidAppDataPath = toString(env,directory);
}

extern "C" JNIEXPORT void JNICALL Java_net_listerily_minecraftcore_android_MinecraftLauncher_nativeInitialize(JNIEnv* env,jobject,jstring libPath)
{
    mMinecraftNativeLibPath = toString(env,libPath);

    void* imageMCPE = dlopen(mMinecraftNativeLibPath.c_str(),RTLD_LAZY);

    mOnCreateFunc = (void(*)(ANativeActivity*,void*,size_t)) dlsym(imageMCPE,"ANativeActivity_onCreate");
    mFinishFunc = (void(*)(ANativeActivity*)) dlsym(imageMCPE,"ANativeActivity_finish");
    mMainFunc = (void(*)(struct android_app*)) dlsym(imageMCPE,"android_main");

    mAndroidAppDataPath = ((std::string*)dlsym(imageMCPE,"_ZN19AppPlatform_android20ANDROID_APPDATA_PATHE"));
    void* isGameLicensed_ptr = dlsym(imageMCPE,"_ZNK15OfferRepository14isGameLicensedEv");
    MSHookFunction(isGameLicensed_ptr,(void*)&hook_isGameLicensed, nullptr);
    dlclose(imageMCPE);
}

//==================================================================================================
// NativeActivity Reflections
//==================================================================================================

extern "C" void android_main(struct android_app* state)
{
    if(mMainFunc)
        mMainFunc(state);
}

extern "C" void ANativeActivity_onCreate(ANativeActivity* activity, void* savedState, size_t savedStateSize)
{
    if(mOnCreateFunc)
        mOnCreateFunc(activity,savedState,savedStateSize);
}

extern "C" void ANativeActivity_finish(ANativeActivity* activity)
{
    if(mFinishFunc)
        mFinishFunc(activity);
}

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm,void*)
{

    return JNI_VERSION_1_6;
}
