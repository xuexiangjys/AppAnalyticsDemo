#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "CMake-JNI" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

JNIEXPORT void JNICALL
Java_com_xuexiang_appanalyticsdemo_jni_NativeApi_testJavaCrash(JNIEnv *env, jobject instance) {

    //获取jclass
    jclass j_class = env->GetObjectClass(instance);
    //获取jfieldID
    jfieldID j_fid = env->GetFieldID(j_class, "method", "Ljava/lang/String666;");

    //获取java成员变量int值
    jstring j_stirng = static_cast<jstring>(env->GetObjectField(instance, j_fid));

    const char *value = env->GetStringUTFChars(j_stirng, 0);

    //释放
    env->ReleaseStringUTFChars(j_stirng, value);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_xuexiang_appanalyticsdemo_jni_NativeApi_testNativeCrash(JNIEnv *env, jobject instance) {

    const char *tmp = "正走向人生巅峰！";
    jstring j_tmp = env->NewStringUTF(tmp);

    jstring j_string = static_cast<jstring>(env->NewLocalRef(j_tmp));

    env->DeleteLocalRef(j_string);

    const char *value = env->GetStringUTFChars(j_string, 0);

}