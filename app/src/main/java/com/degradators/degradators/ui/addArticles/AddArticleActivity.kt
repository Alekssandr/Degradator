package com.degradators.degradators.ui.addArticles

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.degradators.degradators.R
import com.degradators.degradators.databinding.ActivityAddArticleBinding
import com.degradators.degradators.di.common.ViewModelFactory
import com.degradators.degradators.model.Block
import com.degradators.degradators.ui.addArticles.components.TYPE_IMAGE
import com.degradators.degradators.ui.addArticles.components.TYPE_TEXT
import com.degradators.degradators.ui.addArticles.components.TYPE_VIDEO
import com.degradators.degradators.ui.addArticles.model.AddArticleActionMain
import com.degradators.degradators.ui.addArticles.model.ArticleItem
import com.degradators.degradators.ui.addArticles.viewModel.AddArticleViewModel
import com.zolad.videoslimmer.VideoSlimmer
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_article.*
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

const val REQUEST_VIDEO_CAPTURE = 123

//photo enormous size
class AddArticleActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory<AddArticleViewModel>

    val viewmodel: AddArticleViewModel by viewModels { factory }

    private var mUri: Uri? = null

    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    var content: MutableList<Block> = mutableListOf()
    var capturedVideo: File? = null



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
        val scrim: View = findViewById(R.id.scrim)


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
            deleteVideo()
            finish()
        })

        viewmodel.apply {
            clickItem.value = { event ->
                when (event) {
                    AddArticleActionMain.AddArticle.WriteComment -> addText()
                    AddArticleActionMain.AddArticle.MakePhoto -> makePhoto()
                    AddArticleActionMain.AddArticle.MakeVideo -> makeVideo()
                    AddArticleActionMain.AddArticle.GetVideoFromFolder -> openGalleryForVideo()
                    AddArticleActionMain.AddArticle.GetImageFromGallery -> openGallery()
                }
                fabText.isExpanded = false
            }
        }

//        observeEvent()
    }

    fun addText() {
        container.setArticleItem(
            ArticleItem(
                TYPE_TEXT,
                "",
                action = { removeArticleItem(it) }
            ))
    }

    private fun removeArticleItem(articleItem: Int) {
        container.removeArticleItem(articleItem)
    }

    private fun addVideo(videoPath: String) {
        container.setArticleItem(
            ArticleItem(
                TYPE_VIDEO,
                "",
                videoPath = videoPath,
                action = { removeArticleItem(it) }
            ))
    }

    private fun addImage(bitmap: Bitmap, path: String = "") {
        val newBitmap =
            BitmapRotation.bitmapRotate(bitmapScale(bitmap), capturedImage?.absolutePath ?: path)
        container.setArticleItem(
            ArticleItem(
                TYPE_IMAGE,
                "",
                newBitmap,
                capturedImage?.absolutePath ?: path,
                action = { removeArticleItem(it) }
            ))
    }

    private fun bitmapScale(bitmap: Bitmap): Bitmap {
        val screenWidth = DeviceDimensionsHelper.getDisplayWidth(this)
        return BitmapScaler.scaleToFitWidth(bitmap, screenWidth)
    }


//    private fun compressBitmap(bitmap: Bitmap, quality: Int) : Bitmap {
//        val stream = ByteArrayOutputStream()
//
//        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
//
//        val byteArray = stream.toByteArray()
//
//        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//    }

    private fun openGalleryForVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"),
            REQUEST_VIDEO_CAPTURE
        )
    }


    private fun createVideoFile(): File {
        val fileName = "MyVideo"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            fileName,
            ".mp4",
            storageDir
        )
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

    var capturedImage: File? = null

    private fun makePhoto() {
        capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
        if (capturedImage!!.exists()) {
            capturedImage!!.delete()
        }
        capturedImage!!.createNewFile()
        mUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                this, "com.degradators.degradators.fileprovider",
                capturedImage!!
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
                    data?.data?.let {
                        compress(it)
                    }
                }
            }

        }
    }

    fun compress(uri: Uri) {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = managedQuery(uri, projection, null, null, null)
        val pathNew = if (cursor != null) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else ""
        val pathDestDefault = pathNew.dropLast(4) + "packdim_temp" + ".mp4"
        val retriever = MediaMetadataRetriever()//1920 1080
        retriever.setDataSource(pathNew)
        var width: Int =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
        var height: Int =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        val metaRotation = retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION)
        retriever.release()
        val pair = getNewVideoDimension(width, height)
        if(metaRotation.toInt() > 0){
            width = pair.second
            height = pair.first
        } else {
            width = pair.first
            height = pair.second
        }

        VideoSlimmer.convertVideo(
            pathNew,
            pathDestDefault,
            width,
            height,
            width/2 * height/2 * 5,
            object : VideoSlimmer.ProgressListener {
                override fun onFinish(result: Boolean) {
                                        addVideo(pathDestDefault)
                    progressText.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    capturedVideo = File(pathDestDefault)
                }

                override fun onProgress(progress: Float) {

                    progressText.text = "Compress video: ${progress.toInt()}%"
                        progressBar.progress = progress.toInt()
                }

                override fun onStart() {
                    progressBar.visibility = View.VISIBLE
                    progressText.visibility = View.VISIBLE
                }
            })
    }

    fun getNewVideoDimension(width: Int, height: Int): Pair<Int, Int> {
        var newWidth = 0
        var newHeight = 0
        when {
            width >= 1920 || height >= 1920 -> {
                newWidth = (((width * 0.5) / 25).roundToInt() * 16)
                newHeight = (((height * 0.5) / 25f).roundToInt() * 16)
            }
            width >= 1280 || height >= 1280 -> {
                newWidth = (((width * 0.75) / 25).roundToInt() * 16)
                newHeight = (((height * 0.75) / 25).roundToInt() * 16)
            }
            width >= 960 || height >= 960 -> {
                newWidth = (((640.0 * 0.95) / 25).roundToInt() * 16)
                newHeight = (((360.0 * 0.95) / 16).roundToInt() * 16)
            }
            width >= 650 || height >= 650 -> {
                newWidth = (((width * 0.95) / 25).roundToInt() * 16)
                newHeight = (((height * 0.95) / 25).roundToInt() * 16)
            }
            else -> {
                newWidth = (((width * 0.9) / 15).roundToInt() * 16)
                newHeight = (((height * 0.9) / 15).roundToInt() * 16)
            }
        }
        return Pair(newWidth,newHeight)
    }

    fun deleteVideo(){
        capturedVideo?.delete()
    }
}
