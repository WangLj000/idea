# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html
#y:\nfs\tr069\AnQ20200508\out\soong\.intermediates\external\boringssl\libcrypto\android_arm_armv7-a-neon_cortex-a9_core_shared\unstripped\
# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("idea")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#set(CMAKE_ANDROID_STL_TYPE "c++_static")

add_library( # Sets the name of the library.
        idea
        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        jni.cpp
        bspatch.c
        bzip2 DiffUtils.c )


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        idea
        log
        android )