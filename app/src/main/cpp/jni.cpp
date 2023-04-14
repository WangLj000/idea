/**
 * @Author WangLongjiang
 * @Date 2023/4/14 10:11
 * @Version 1.0
 **/

#include <jni.h>
#include <string>
#include <android/log.h>
extern "C" {
#include "bspatch.h"
}
/*
 * Class:     com_cundong_utils_PatchUtils
 * Method:    patch
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
extern "C" JNIEXPORT jint Java_com_example_overlay_idea_PatchUtils_patch(JNIEnv *env,
		jobject obj, jstring old, jstring news, jstring patch) {

	char * ch[4];
	ch[0] = "bspatch";
	ch[1] = (char*) ((*env).GetStringUTFChars(old,NULL));
	ch[2] = (char*) ((*env).GetStringUTFChars(news, NULL));
	ch[3] = (char*) ((*env).GetStringUTFChars(patch, NULL));

	__android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "old = %s ", ch[1]);
	__android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "new = %s ", ch[2]);
	__android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "patch = %s ", ch[3]);

	int ret = applypatch(4, ch);

	__android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "applypatch result = %d ", ret);

	(*env).ReleaseStringUTFChars(old, ch[1]);
	(*env).ReleaseStringUTFChars(news, ch[2]);
	(*env).ReleaseStringUTFChars(patch, ch[3]);

	return ret;
}
