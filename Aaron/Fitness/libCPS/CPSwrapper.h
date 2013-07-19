/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class CPSwrapper */

#ifndef _Included_CPSwrapper
#define _Included_CPSwrapper
#ifdef __cplusplus
extern "C" {
#endif
#undef CPSwrapper_MAXSIZE
#define CPSwrapper_MAXSIZE 4090L
/*
 * Class:     CPSwrapper
 * Method:    runCPS
 * Signature: ([D[D)V
 */
JNIEXPORT void JNICALL Java_CPSwrapper_runCPS
  (JNIEnv *, jobject, jdoubleArray, jdoubleArray);

/*
 * Class:     CPSwrapper
 * Method:    getFreq
 * Signature: ()[D
 */
JNIEXPORT jdoubleArray JNICALL Java_CPSwrapper_getFreq
  (JNIEnv *, jclass);

/*
 * Class:     CPSwrapper
 * Method:    getC
 * Signature: ()[D
 */
JNIEXPORT jdoubleArray JNICALL Java_CPSwrapper_getC
  (JNIEnv *, jclass);

/*
 * Class:     CPSwrapper
 * Method:    getEllip
 * Signature: ()[D
 */
JNIEXPORT jdoubleArray JNICALL Java_CPSwrapper_getEllip
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
