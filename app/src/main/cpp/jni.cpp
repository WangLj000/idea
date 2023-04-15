/**
 * @Author WangLongjiang
 * @Date 2023/4/14 10:11
 * @Version 1.0
 **/

#include <jni.h>
#include <string>
#include <android/log.h>
#include "DiffUtils.h"

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

extern "C" JNIEXPORT jint JNICALL Java_com_example_overlay_idea_PatchUtils_genDiff(JNIEnv *env,
																jclass cls, jstring old, jstring news, jstring patch) {
	int argc = 4;
	char * argv[argc];
	argv[0] = "bsdiff";
	argv[1] = (char*) ((*env).GetStringUTFChars( old, 0));
	argv[2] = (char*) ((*env).GetStringUTFChars( news, 0));
	argv[3] = (char*) ((*env).GetStringUTFChars( patch, 0));

	printf("old apk = %s \n", argv[1]);
	printf("new apk = %s \n", argv[2]);
	printf("patch = %s \n", argv[3]);

	int ret = genpatch(argc, argv);

	printf("genDiff result = %d ", ret);

	(*env).ReleaseStringUTFChars( old, argv[1]);
	(*env).ReleaseStringUTFChars( news, argv[2]);
	(*env).ReleaseStringUTFChars( patch, argv[3]);

	return ret;
}

