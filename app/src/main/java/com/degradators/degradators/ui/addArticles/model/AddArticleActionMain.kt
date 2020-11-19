package com.degradators.degradators.ui.addArticles.model

import com.degradators.degradators.R
import javax.inject.Inject

class AddArticleActionMain @Inject constructor() {
    val articlesAction: MutableList<AddArticleAction> =
        mutableListOf(
            AddArticleAction(
                AddArticle.WriteComment,
                R.string.write_comment,
                R.drawable.ic_create_black_18dp
            ),
            AddArticleAction(
                AddArticle.MakePhoto,
                R.string.make_photo,
                R.drawable.ic_photo_camera_black_18dp
            ),
            AddArticleAction(
                AddArticle.MakeVideo,
                R.string.make_video,
                R.drawable.ic_videocam_black_18dp
            ),
            AddArticleAction(
                AddArticle.GetVideoFromFolder,
                R.string.get_video_from_folder,
                R.drawable.ic_movie_creation_black_18dp
            ),
            AddArticleAction(
                AddArticle.GetImageFromGallery,
                R.string.get_image_from_gallery,
                R.drawable.ic_insert_photo_black_18dp
            )
        )

    sealed class AddArticle {
        object WriteComment : AddArticle()
        object MakePhoto : AddArticle()
        object MakeVideo : AddArticle()
        object GetVideoFromFolder : AddArticle()
        object GetImageFromGallery : AddArticle()
    }
}