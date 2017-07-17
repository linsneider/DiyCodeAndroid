#include <android/log.h>
#include <string>
#include "native-security.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "security", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "security", __VA_ARGS__))

static int verifySign(JNIEnv *env);

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) & env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }
    if (verifySign(env) == JNI_OK) {
        return JNI_VERSION_1_4;
    }
//    LOGE("签名不一致!");
    return JNI_ERR;
}

static jobject getApplication(JNIEnv *env) {
    jobject application = NULL;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != NULL) {
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != NULL) {
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        } else {
//            LOGE("Cannot find method: currentApplication() in ActivityThread.");
        }
        env->DeleteLocalRef(activity_thread_clz);
    } else {
//        LOGE("Cannot find class: android.app.ActivityThread");
    }
    return application;
}

static const char *SIGN = "308203433082022ba003020102020461a270b8300d06092a864886f70d01010b05003051310b3009060355040613023836310b3009060355040813024744310b300906035504071302475a310c300a060355040a13036c696e310c300a060355040b13036c696e310c300a060355040313036c696e3020170d3137303730353038333133325a180f32303837303631383038333133325a3051310b3009060355040613023836310b3009060355040813024744310b300906035504071302475a310c300a060355040a13036c696e310c300a060355040b13036c696e310c300a060355040313036c696e30820122300d06092a864886f70d01010105000382010f003082010a02820101009211df22caa433ef64b496c99dc69e599a31e7ce6e1eb46d44adac164c85aee827a23a9553dceabf3c06f887cdbfa03d535d305e97b8dcfa3231bcfd56ff211854517c72a76828e8d3c395812baef93605421549a5dd001c1076dafb4c62955a83170b2ecdf93c3f2b41b05318cae21735a748c44756e4c162be8a067e39709299e4a7f88330299a7e8e45749a4df2448eb29935f29375f692fb8f11076bc556b3cad161d2e552e05aba2e20df40eaa1f712038c2f57e55b182417c91f84cd1c615fe9784ce3a0cfcb1002bae12267ea0fab8d6177a9c2b6dabe40bbd79377e33efe50054a35afac9db46b6e61204d1e599abf8a6df6f1b0e41370babe4442b10203010001a321301f301d0603551d0e0416041459b16d9c34ba4e6968911eed8dd4de0be5ed642d300d06092a864886f70d01010b05000382010100868f4f9b5aad3eca8cf5e770856547713741c8253764cc6987bdb0452777130eaa4a5c2d213fff19309e95173ef17f8940f88b931f0763df3405e993578ce362e334386773831d521aec1c7aaaf5b0a97aa1c375ef88c0760d69b01cc410767b418d7fba72cf31e06030fe22fa2aae2006f009cbf6bc889d20b3b3e265df867d132263c871fde503ca798664ee07d88dad8070edac4d0ebfaadb73ce211142ded9cfbf462f13056bba797fdfcf0bb9f9ad86351c3e6148bac556df4af765cfd1a75692eaa1cb163e867241750872cb00ae9767699bfd1faa7eb9fea95ee613cdd69436744d2f0bcb4821f8c4a8ae0e85cf588de26670f6764dfecb895185dce4";
//static const char *SIGN = "308201e53082014ea0030201020204526fab57300d06092a864886f70d01010505003037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f6964204465627567301e170d3133313032393132333433315a170d3433313032323132333433315a3037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f696420446562756730819f300d06092a864886f70d010101050003818d0030818902818100b32d73ba222892ca5baed1f27a53df8d5e93bc744b6d2315fc7464334a38d06670bfe6c441a69c206bdb79e1171fc82b668cbb6dcd9ad71c850d73a0acd5b2c869370b99ed0d8ccc5f374122bb635df2f88ad801d17bc89b7544825db76fa06342051b3ab269190bd04f4ef3233da5ce3d30366a96b166e060bacd7ac0a964ff0203010001300d06092a864886f70d010105050003818100ab85ff589214f7909c331f415f90699ec88e78b099d1f0014b171b96a9a7af57a5b60473d8d3430bef6197eaf44352d3d8b1dd4535c144d1ede17746cdeed8d8a90fd0c4e3f90e1da681dfd0d58406bff7b5f45ec789a2fb8e6529a55601b911c80b5e0bf1fcd9beb224b6b1b21cff1c70f5e474b78b501a5fccc931d64d2d6e";

static int verifySign(JNIEnv *env) {
    // Application object
    jobject application = getApplication(env);
    if (application == NULL) {
        return JNI_ERR;
    }
    // Context(ContextWrapper) class
    jclass context_clz = env->GetObjectClass(application);
    // getPackageManager()
    jmethodID getPackageManager = env->GetMethodID(context_clz, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    // android.content.pm.PackageManager object
    jobject package_manager = env->CallObjectMethod(application, getPackageManager);
    // PackageManager class
    jclass package_manager_clz = env->GetObjectClass(package_manager);
    // getPackageInfo()
    jmethodID getPackageInfo = env->GetMethodID(package_manager_clz, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    // context.getPackageName()
    jmethodID getPackageName = env->GetMethodID(context_clz, "getPackageName", "()Ljava/lang/String;");
    // call getPackageName() and cast from jobject to jstring
    jstring package_name = (jstring) (env->CallObjectMethod(application, getPackageName));
    // PackageInfo object
    jobject package_info = env->CallObjectMethod(package_manager, getPackageInfo, package_name, 64);
    // class PackageInfo
    jclass package_info_clz = env->GetObjectClass(package_info);
    // field signatures
    jfieldID signatures_field = env->GetFieldID(package_info_clz, "signatures", "[Landroid/content/pm/Signature;");
    jobject signatures = env->GetObjectField(package_info, signatures_field);
    jobjectArray signatures_array = (jobjectArray) signatures;
    jobject signature0 = env->GetObjectArrayElement(signatures_array, 0);
    jclass signature_clz = env->GetObjectClass(signature0);
    jmethodID toCharsString = env->GetMethodID(signature_clz, "toCharsString", "()Ljava/lang/String;");
    // call toCharsString()
    jstring signature_str = (jstring) (env->CallObjectMethod(signature0, toCharsString));
    // release
    env->DeleteLocalRef(application);
    env->DeleteLocalRef(context_clz);
    env->DeleteLocalRef(package_manager);
    env->DeleteLocalRef(package_manager_clz);
    env->DeleteLocalRef(package_name);
    env->DeleteLocalRef(package_info);
    env->DeleteLocalRef(package_info_clz);
    env->DeleteLocalRef(signatures);
    env->DeleteLocalRef(signature0);
    env->DeleteLocalRef(signature_clz);
    const char *sign = env->GetStringUTFChars(signature_str, NULL);
    if (sign == NULL) {
//        LOGE("分配内存失败");
        return JNI_ERR;
    }
//    LOGI("读取的签名为：%s", sign);
//    LOGI("预置的签名为：%s", SIGN);
    int result = strcmp(sign, SIGN);
//    LOGI("########### i = %d", result);
    // 使用之后要释放这段内存
    env->ReleaseStringUTFChars(signature_str, sign);
    env->DeleteLocalRef(signature_str);
    if (result == 0) { // 签名一致
//        LOGE("签名一致!");
        return JNI_OK;
    }
    return JNI_ERR;
}

jstring Java_com_sneider_diycode_utils_DiycodeUtils_getClientId(JNIEnv *env, jclass type) {
    return env->NewStringUTF("6840f794");
}

jstring Java_com_sneider_diycode_utils_DiycodeUtils_getClientSecret(JNIEnv *env, jclass type) {
    return env->NewStringUTF("987665b88358a0f5aa7c3f4103ffb7bfa3a050a2b3e8ca5d816f66116fc8b92a");
}