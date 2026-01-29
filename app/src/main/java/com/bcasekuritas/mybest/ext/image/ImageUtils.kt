package com.bcasekuritas.mybest.ext.image

object ImageUtils {

    /*fun getTmpFileUri(fileName: String, context: Context): Uri? {
        if (!context.isAvailable()) return null
        val tmpFile = File.createTempFile(fileName, getMimeType(CompressFormat.JPEG),
            context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", tmpFile)
    }

    fun loadImageWithPlaceholder(view: ImageView?, imagePath: String, context: Context) {
        if (!context.isAvailable()) return
        view?.let {
            Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_default_avatar)
                .into(view)
        }
    }

    fun loadImageWithPlaceHolderRound(view: ImageView?, imageUrl: String?, context: Context, @DimenRes radius: Int? = null) {
        if (!context.isAvailable()) return
        val multiTransformation =  MultiTransformation(FitCenter(), RoundedCorners(context.resources.getDimensionPixelSize(radius ?: 8)))
        view?.let {
            Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .placeholder(R.drawable.ic_default_avatar)
                .into(view)
        }
    }

    fun getMimeType(format: CompressFormat?): String {
        return when (format) {
            CompressFormat.JPEG -> ".jpeg"
            CompressFormat.PNG -> ".png"
            else -> ".png"
        }
    }*/

}