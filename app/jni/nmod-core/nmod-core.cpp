#include <jni.h>
#include <android/native_activity.h>
#include <dlfcn.h>
#include <cstring>
#include <string>

using namespace std;

inline string toString(JNIEnv *env, jstring j_str)
{
    return env->GetStringUTFChars(j_str, nullptr);
}

extern "C"
{
JNIEXPORT jbyteArray
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOverrideFile(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jstring path,
                                                                                  jstring asset_path,
                                                                                  jbyteArray content)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return nullptr;
    }
    char const* (*method)(char const*,char const*) = (char const* (*)(char const*,char const*)) dlsym(handle, "nmod_override_file");
    if (!method)
    {
        dlclose(handle);
        return nullptr;
    }
    char const* result = method(toString(env,asset_path).c_str(),(const char*)env->GetByteArrayElements(content,
                                                                                        nullptr));
    jbyteArray new_content = env->NewByteArray(strlen(result));
    env->SetByteArrayRegion(new_content,0,strlen(result),(const jbyte *)result);
    return new_content;
}

JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityCreate(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jstring path,
                                                                                  jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_create");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityStart(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring path,
                                                                                   jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_start");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityRestart(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring path,
                                                                                   jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_restart");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityStop(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring path,
                                                                                   jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_stop");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityPause(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring path,
                                                                                   jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_pause");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityResume(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring path,
                                                                                   jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_resume");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
JNIEXPORT jboolean
Java_net_listerily_minecraftcore_android_nmod_instance_NMod_nativeOnActivityDestroy(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring path,
                                                                                   jobject activity)
{
    string path_c = toString(env, path);
    void *handle = dlopen(path_c.c_str(), RTLD_LAZY);
    if (!handle)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    void (*method)(JNIEnv*,jobject) = (void (*)(JNIEnv*,jobject)) dlsym(handle, "nmod_on_activity_destroy");
    if (!method)
    {
        dlclose(handle);
        return JNI_FALSE;
    }
    method(env,activity);
    dlclose(handle);
    return JNI_TRUE;
}
}
