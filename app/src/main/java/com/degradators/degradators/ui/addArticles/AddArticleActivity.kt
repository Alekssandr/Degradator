package com.degradators.degradators.ui.addArticles

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.degradators.degradators.R
import com.degradators.degradators.databinding.ActivityAddArticleBinding
import com.degradators.degradators.di.common.ViewModelFactory
import com.degradators.degradators.model.Block
import com.degradators.degradators.ui.addArticles.components.TYPE_IMAGE
import com.degradators.degradators.ui.addArticles.components.TYPE_TEXT
import com.degradators.degradators.ui.addArticles.components.TYPE_VIDEO
import com.degradators.degradators.ui.addArticles.model.ArticleItem
import com.degradators.degradators.ui.addArticles.viewModel.AddArticle
import com.degradators.degradators.ui.addArticles.viewModel.AddArticleViewModel
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_article.*
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

const val REQUEST_VIDEO_CAPTURE = 123

class AddArticleActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory<AddArticleViewModel>

    val viewmodel: AddArticleViewModel by viewModels { factory }

    private var mUri: Uri? = null

    //Our constants
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    var content: MutableList<Block> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding: ActivityAddArticleBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_article)
        binding.run {
            this.addArticleViewModel = viewmodel
            lifecycleOwner = this@AddArticleActivity
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val fabMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        val sheet: CircularRevealCardView = findViewById(R.id.sheet)
        val scrim: View = findViewById(R.id.scrim)

        ViewCompat.setOnApplyWindowInsetsListener(add_article_layout) { _, insets ->
            fabText.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                leftMargin = fabMargin + insets.systemWindowInsetLeft
                rightMargin = fabMargin + insets.systemWindowInsetRight
                bottomMargin = fabMargin + insets.systemWindowInsetBottom
            }
            sheet.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                leftMargin = fabMargin + insets.systemWindowInsetLeft
                rightMargin = fabMargin + insets.systemWindowInsetRight
                bottomMargin = fabMargin + insets.systemWindowInsetBottom
            }
            insets
        }


        scrim.setOnClickListener {
            // Shrink the menu sheet back into the FAB.
            fabText.isExpanded = false
        }

        fabText.setOnClickListener {
            checkPermission()
        }

        publicArticle.setOnClickListener {
            viewmodel.addArticle(editText.text.toString(), container.getArticleList())
        }

        viewmodel.closeScreen.observe(this, Observer {
            finish()
        })

        observeEvent()
    }

    fun addText(){
        container.setArticleItem(
                ArticleItem(
                    TYPE_TEXT,
                    "",
                    action = { removeArticleItem(it) }
                ))
    }

    fun observeEvent(){
        viewmodel.addArticleEvent.observe(this, Observer {
            startEvent(it)
        })
    }

    fun startEvent(addArticle:AddArticle){
        when(addArticle){
            AddArticle.WriteComment -> addText()
            AddArticle.MakePhoto -> makePhoto()
            AddArticle.MakeVideo -> makeVideo()
            AddArticle.GetVideoFromFolder ->  openGalleryForVideo()
            AddArticle.GetImageFromGallery ->  openGallery()
        }
        fabText.isExpanded = false
    }

    private fun removeArticleItem(articleItem: Int) {
        container.removeArticleItem(articleItem)
    }

    private fun addVideo(videoUri: Uri) {
        container.setArticleItem(
            ArticleItem(
                TYPE_VIDEO,
                "",
                videoUri = videoUri,
                action = { removeArticleItem(it) }
            ))
    }

    private fun addImage(bitmap: Bitmap, path: String = "") {
        val newBitmap = compressBitmap(bitmap, 0)
        container.setArticleItem(
            ArticleItem(
                TYPE_IMAGE,
                "",
                newBitmap,
                path,
                action = { removeArticleItem(it) }
            ))
    }


    private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)

        val byteArray = stream.toByteArray()

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun openGalleryForVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_CAPTURE)
    }


    private fun createVideoFile(): File {
        val fileName = "MyVideo"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            fileName,
            "mp4",
            storageDir
        )
    }

    lateinit var videoUri: Uri
    private fun recordVideo() {
        val videoFile = createVideoFile()
        videoUri = FileProvider.getUriForFile(
            this, "com.degradators.degradators.fileprovider",
            videoFile
        )
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
    }


    private fun makeVideo() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }


    private fun checkPermission() {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            //Requests permissions to be granted to this application at runtime
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )
        } else {
            fabText.isExpanded = true
        }
    }

    private fun makePhoto() {
        val capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                this, "com.degradators.degradators.fileprovider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            addImage(bitmap, imagePath)
        }
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion
                )
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(
                        "content://downloads/public_downloads"
                    ), java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        renderImage(imagePath)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>
        , grantedResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when (requestCode) {
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    fabText.isExpanded = true
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
//                Log.d("Test111", )

            if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(mUri!!)
                    )
                    mUri!!.path?.let { addImage(bitmap, it) }
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    handleImageOnKitkat(data)
                }
            REQUEST_VIDEO_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val videoUri: Uri? = data?.data
                    videoUri?.let { addVideo(it) }
                }
            }

        }
    }

}
