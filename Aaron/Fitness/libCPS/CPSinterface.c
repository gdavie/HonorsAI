// CCode with documentation
#include <time.h>
#include <stdio.h>
#include "CPSwrapper.h"  // Required header for JNI

// FORTRAN routines have to be prototyped as extern, and parameters are
// passed by reference.  Note also that for g77 the function name in C by
// default must be prefixed by a "_".
extern void sprep_(jdouble *,double []);
extern void sdisp_();
extern void sregn_();
//extern double* printvals_(double []);
extern double* getfreq_(double []);
extern void getc_(double []);
extern void getellip_(double []);
extern main_();

const int NPERIOD = 4090;

// When calling C code from Java, main() must be replaced by a declaration
// similar to below, where the function name is given by "Java_" + the name
// of the class in the Java code that calls this C code, in this case
// "JavaCode", + "_" + the name of this C function called from Java, in this
// case "sumsquaredc".  This is followed by at least two parameters as below,
// plus possibly more if more are required.

JNIEXPORT void JNICALL Java_CPSwrapper_runCPS(JNIEnv *env,
                       jobject obj, jdoubleArray frq, jdoubleArray mdl) {

//  Data from any additional parameters are passed via special pointers as
//  shown here.
    jsize n = (*env)->GetArrayLength(env, frq);
    jdouble *freqArray = (*env)->GetDoubleArrayElements(env, frq, 0);
    jsize n2 = (*env)->GetArrayLength(env, mdl);
    jdouble *mdlArray = (*env)->GetDoubleArrayElements(env, mdl, 0);
   
    // We are calling the component parts separately here.
    // Passing by ref may be more robust that common variables.
    
  //  clock_t start = clock();
    sprep_(freqArray, mdlArray);
 //   clock_t end = clock();
 //   double div = CLOCKS_PER_SEC;
  //  printf("SPREP: %.4f seconds\n", (double)(end - start) / div);
    
 //   start = clock();
     sdisp_();
 //   end = clock();
 //   printf("SDISP: %.4f seconds\n", (double)(end - start) / div);
    
        
 //   start = clock();
    sregn_();
 //   end = clock();
 //   printf("SREGN: %.4f seconds\n", (double)(end - start) / div);
    


 

    //Release the variables
    (*env)->ReleaseDoubleArrayElements(env, frq, freqArray, 0);
    (*env)->ReleaseDoubleArrayElements(env, mdl, mdlArray, 0);
}

/**
The following three functions are to return the results to the java callee.
They represent the three columns in the ASC file we have replaced.
**/
JNIEXPORT jdoubleArray JNICALL Java_CPSwrapper_getFreq(JNIEnv *env,
                       jobject obj) {
	int i;
	jdouble  freq[NPERIOD];
	getfreq_(freq);
	//Copy back to java
	jdoubleArray outJNIArray = (*env)->NewDoubleArray(env, NPERIOD);  // allocate
	if (NULL == outJNIArray) return NULL;
	(*env)->SetDoubleArrayRegion(env, outJNIArray, 0 , NPERIOD, freq);  // copy
	return outJNIArray;
}

JNIEXPORT jdoubleArray JNICALL Java_CPSwrapper_getC(JNIEnv *env,
                       jobject obj) {
	jdouble  c[NPERIOD];
	getc_(c);
	//Copy back to java
	jdoubleArray outJNIArray = (*env)->NewDoubleArray(env, NPERIOD);  // allocate
	if (NULL == outJNIArray) return NULL;
	(*env)->SetDoubleArrayRegion(env, outJNIArray, 0 , NPERIOD, c);  // copy
	return outJNIArray;
}

JNIEXPORT jdoubleArray JNICALL Java_CPSwrapper_getEllip(JNIEnv *env,
                       jobject obj) {
	jdouble  ellip[NPERIOD];
	getellip_(ellip);
	//Copy back to java
	jdoubleArray outJNIArray = (*env)->NewDoubleArray(env, NPERIOD);  // allocate
	if (NULL == outJNIArray) return NULL;
	(*env)->SetDoubleArrayRegion(env, outJNIArray, 0 , NPERIOD, ellip);  // copy
	return outJNIArray;
}
