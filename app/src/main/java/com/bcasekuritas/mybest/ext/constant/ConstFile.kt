package com.bcasekuritas.mybest.ext.constant

object ConstFile {
    /*fun pathBaseDir(context: Context): String {
        val path = if (BuildConfig.BUILD_TYPE == "release") {
            context.getExternalFilesDir("/Project/").toString()
        } else {
            context.getExternalFilesDir("/ProjectDev/").toString()
        }
        getBaseDir(context)
        return path
    }

    private fun getBaseDir(context: Context): File {
        val dir = if (BuildConfig.BUILD_TYPE == "release") {
            File(context.getExternalFilesDir("/Project/").toString())
        } else {
            File(context.getExternalFilesDir("/ProjectDev/").toString())
        }
        dir.mkdirs()
        return dir
    }

    *//**
     * Pass only [context] will return the directory
     * Pass [context] and [fileName] will return a new file path
     *//*
    fun getProfileFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "profile/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getTryoutFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "bundles/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getCarouselFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "carousels/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getSubmissionFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "submissions/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getResultFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "submissions_result/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getBackupFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "backup/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getModulesFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "modules/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getCacheFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "cache/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getFile(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "files/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }
    fun getImage(context: Context, fileName: String? = null): File {
        val dir = File(getBaseDir(context), "images/")
        dir.mkdirs()
        return if (fileName != null) {
            File(dir, fileName)
        } else {
            dir
        }
    }*/
}