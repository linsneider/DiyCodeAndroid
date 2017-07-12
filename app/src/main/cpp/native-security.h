#include <jni.h>

#ifndef DIYCODEANDROID_NATIVE_SECURITY_H
#define DIYCODEANDROID_NATIVE_SECURITY_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL
Java_com_sneider_diycode_utils_DiycodeUtils_getClientId(JNIEnv *env, jclass type);

JNIEXPORT jstring JNICALL
Java_com_sneider_diycode_utils_DiycodeUtils_getClientSecret(JNIEnv *env, jclass type);

#ifdef __cplusplus
}
#endif

#endif //DIYCODEANDROID_NATIVE_SECURITY_H
