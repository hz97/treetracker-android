package org.greenstand.android.TreeTracker.fragments


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tree_preview.view.*
import kotlinx.coroutines.*
import org.greenstand.android.TreeTracker.R
import org.greenstand.android.TreeTracker.activities.MainActivity
import org.greenstand.android.TreeTracker.database.TreeTrackerDAO
import org.greenstand.android.TreeTracker.managers.UserLocationManager
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.IOException
import java.util.*

class TreePreviewFragment : Fragment() {

    private val userLocationManager: UserLocationManager by inject()
    private val dao: TreeTrackerDAO by inject()
    private var mImageView: ImageView? = null
    private var mCurrentPhotoPath: String? = null
    private var treeIdStr: String = ""

    private val args: TreePreviewFragmentArgs by navArgs()

     private val appDatabase: TreeTrackerDAO by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_tree_preview, container, false)

        activity?.toolbarTitle?.setText(R.string.tree_preview)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        treeIdStr = args.treeId

        mImageView = v.fragmentTreePreviewImage

        runBlocking {

            val tree = withContext(Dispatchers.IO) { dao.getTreeCaptureById(treeIdStr.toLong()) }

            tree.let {

                mCurrentPhotoPath = it.localPhotoPath

                val noImage = v.fragmentTreePreviewNoImage

                if (mCurrentPhotoPath != null) {
                    setPic()

                    noImage.visibility = View.INVISIBLE
                } else {
                    noImage.visibility = View.VISIBLE
                }

                MainActivity.currentTreeLocation = Location("")
                MainActivity.currentTreeLocation!!.latitude = it.latitude
                MainActivity.currentTreeLocation!!.longitude = it.longitude

                // No GPS accuracy info from new api.
                //			MainActivity.currentTreeLocation.setAccuracy(Float.parseFloat(photoCursor.getString(photoCursor.getColumnIndex("accuracy"))));

                val results = floatArrayOf(0f, 0f, 0f)
                if (userLocationManager.currentLocation != null) {
                    Location.distanceBetween(
                        userLocationManager.currentLocation!!.latitude,
                        userLocationManager.currentLocation!!.longitude,
                        tree.latitude,
                        tree.longitude,
                        results
                    )
                }

                val distanceTxt = v.fragmentTreePreviewDistance
                val distanceTxtString =
                    Integer.toString(Math.round(results[0])) + " " + resources.getString(R.string.meters)
                distanceTxt.text = distanceTxtString

                val accuracyTxt = v.fragmentTreePreviewGpsAccuracy
                val treeAccuracy = it.accuracy
                val accuracyTxtString = treeAccuracy.toString() + " " + resources.getString(R.string.meters)
                accuracyTxt.text = accuracyTxtString


                val createdTxt = v.fragmentTreePreviewCreated
                createdTxt.text = Date(it.createAt).toLocaleString()


                val tree = GlobalScope.async {
                    return@async appDatabase.getTreeCaptureById(treeIdStr.toLong())
                }.await()

                val notesTxt = v.fragmentTreePreviewNotes

                notesTxt.text = tree.noteContent
            }
        }


        return v
    }

    private fun setPic() {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the image */
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val imageWidth = bmOptions.outWidth
        // Calculate your sampleSize based on the requiredWidth and
        // originalWidth
        // For e.g you want the width to stay consistent at 500dp
        val requiredWidth = (500 * resources.displayMetrics.density).toInt()

        var sampleSize = Math.ceil((imageWidth.toFloat() / requiredWidth.toFloat()).toDouble()).toInt()

        // If the original image is smaller than required, don't sample
        if (sampleSize < 1) {
            sampleSize = 1
        }

        bmOptions.inSampleSize = sampleSize
        bmOptions.inPurgeable = true
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565
        bmOptions.inJustDecodeBounds = false

        /* Decode the JPEG file into a Bitmap */
        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(mCurrentPhotoPath!!)

            val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation = if (orientString != null)
                Integer.parseInt(orientString)
            else
                ExifInterface.ORIENTATION_NORMAL
            var rotationAngle = 0
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotationAngle = 90
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotationAngle = 180
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotationAngle = 270

            Timber.d("rotationAngle " + Integer.toString(rotationAngle))

            val matrix = Matrix()
            matrix.setRotate(
                rotationAngle.toFloat(), bitmap.width.toFloat() / 2,
                bitmap.height.toFloat() / 2
            )
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0,
                bmOptions.outWidth, bmOptions.outHeight, matrix, true
            )
            /* Associate the Bitmap to the ImageView */
            mImageView!!.setImageBitmap(rotatedBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
            mImageView!!.setImageBitmap(bitmap)
        }

        mImageView!!.visibility = View.VISIBLE
    }


}//some overrides and settings go here
