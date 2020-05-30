package com.degradators.degradators.ui.addArticles

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.children
import com.degradators.degradators.R
import com.degradators.degradators.model.Block
import com.degradators.degradators.model.BlockText
import kotlinx.android.synthetic.main.activity_add_article.*
import java.io.File


class AddArticleActivity : AppCompatActivity() {

    //Our variables
    private var mImageView: ImageView? = null
    private var mUri: Uri? = null

    //Our widgets
    private lateinit var btnCapture: Button
    private lateinit var btnChoose: Button

    //Our constants
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    var index = 0
    var content: MutableList<Block> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 10, 0, 0)

        fabText.setOnClickListener {
            val pair = createParentLayout()
            val button = pair.first
            val parent = pair.second
            val editText = EditText(this)
            params.weight = 0.3f
            editText.layoutParams = params
            editText.minLines = 3
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
            parent.addView(editText)
            parent.addView(button)
            container.addView(parent)



            removeBtnClick(button)
        }

        fabImage.setOnClickListener {
            checkPermission()
            val pair = createParentLayout()
            val button = pair.first
            val parent = pair.second
            mImageView = ImageView(this)
            params.weight = 0.3f
            mImageView!!.layoutParams = params
            mImageView!!.adjustViewBounds = true
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
            parent.addView(mImageView)
            parent.addView(button)
            container.addView(parent)
            removeBtnClick(button)
        }

        fabPhoto.setOnClickListener {
            makePhoto()

            val pair = createParentLayout()
            val button = pair.first
            val parent = pair.second

            mImageView = ImageView(this)
            mImageView!!.layoutParams = params
            mImageView!!.adjustViewBounds = true
            parent.addView(mImageView)
            parent.addView(button)
            container.addView(parent)
            removeBtnClick(button)
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        publicArticle.setOnClickListener {
            container.children.forEach {
               val child =  (it as ViewGroup).getChildAt(0)
                if (child is EditText) {
                    content.add(BlockText(child.text.toString(), "text"))
                }

            }
//

        }
    }

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()


    private fun createParentLayout(): Pair<ImageButton, LinearLayout> {
        val parent = LinearLayout(this)
        parent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        parent.orientation = LinearLayout.HORIZONTAL

        val button = ImageButton(this)
        val btn = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        btn.width = 120
        btn.height = 110
        button.background = ContextCompat.getDrawable(this, R.drawable.style_circular_button)
        button.setImageResource(R.drawable.ic_delete_white_18dp)
        btn.setMargins(50, 0, 0, 0)

        button.layoutParams = btn
        return Pair(button, parent)
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
            openGallery()
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

    private fun removeBtnClick(button: ImageButton) {
        button.setOnClickListener {
            if (container.childCount == 1) {
                container.removeAllViews()
            } else {
                container.removeView((it.parent as View))
            }
        }
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
            mImageView?.setImageBitmap(bitmap)
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
                    openGallery()
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
                    mImageView!!.setImageBitmap(bitmap)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    handleImageOnKitkat(data)
                }
        }
    }
}
