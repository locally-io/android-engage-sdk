package io.locally.engagesdk.datamodels.campaign

import java.net.URL

class MediaVideo(val id: Int,
                 val filename: String,
                 val encodedFile: URL,
                 val videoThumb: URL,
                 val videoSthumb: URL,
                 val duration: Double,
                 val path: String,
                 val mimetype: String,
                 val filesize: Double,
                 val title: String,
                 val description: String,
                 val width: Double,
                 val height: Double,
                 val mediaStatus: String)