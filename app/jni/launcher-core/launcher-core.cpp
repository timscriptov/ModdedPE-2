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