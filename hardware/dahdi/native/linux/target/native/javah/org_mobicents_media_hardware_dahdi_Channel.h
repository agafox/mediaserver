/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_mobicents_media_hardware_dahdi_Channel */

#ifndef _Included_org_mobicents_media_hardware_dahdi_Channel
#define _Included_org_mobicents_media_hardware_dahdi_Channel
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_mobicents_media_hardware_dahdi_Channel
 * Method:    openChannel
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_mobicents_media_hardware_dahdi_Channel_openChannel
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     org_mobicents_media_hardware_dahdi_Channel
 * Method:    readData
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_org_mobicents_media_hardware_dahdi_Channel_readData
  (JNIEnv *, jobject, jint, jbyteArray, jint);

/*
 * Class:     org_mobicents_media_hardware_dahdi_Channel
 * Method:    writeData
 * Signature: (I[BI)V
 */
JNIEXPORT void JNICALL Java_org_mobicents_media_hardware_dahdi_Channel_writeData
  (JNIEnv *, jobject, jint, jbyteArray, jint);

/*
 * Class:     org_mobicents_media_hardware_dahdi_Channel
 * Method:    doRegister
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_mobicents_media_hardware_dahdi_Channel_doRegister
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_mobicents_media_hardware_dahdi_Channel
 * Method:    doUnregister
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_mobicents_media_hardware_dahdi_Channel_doUnregister
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_mobicents_media_hardware_dahdi_Channel
 * Method:    closeChannel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_mobicents_media_hardware_dahdi_Channel_closeChannel
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif