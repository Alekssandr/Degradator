package com.degradators.degradators.ui.utils


import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.degradators.degradators.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily


fun ImageView.loadImage(url: String, background: Int){
	Glide.with(context)
		.load(url)
		.apply(
			RequestOptions()
				.placeholder(background).fitCenter().override(600, 1200)
		)
		.into(this)
}

fun ShapeableImageView.roundedCorner() =
		this.shapeAppearanceModel
			.toBuilder()
			.setAllCorners(
				CornerFamily.ROUNDED,
				this.resources.getDimension(R.dimen.image_corner_radius)
			)
			.build()
