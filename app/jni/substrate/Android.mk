LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := substrate
LOCAL_SRC_FILES := armeabi-v7a/libsubstrate.so
ifeq ($(TARGET_ARCH_ABI),x86)
    LOCAL_SRC_FILES := x86/libsubstrate.so
endif
include $(PREBUILT_SHARED_LIBRARY)