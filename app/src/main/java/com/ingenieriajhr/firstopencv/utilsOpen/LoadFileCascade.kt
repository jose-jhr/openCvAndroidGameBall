package com.ingenieriajhr.firstopencv.utilsOpen

import android.content.Context
import android.util.Log
import com.ingenieriajhr.firstopencv.R
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class LoadFileCascade(val context: Context) {

    fun load_cascade(resourceInt:Int,resourceString: String): CascadeClassifier? {
        try {
            val `is`: InputStream = context.getResources().openRawResource(resourceInt)
            val cascadeDir: File = context.getDir("cascade", Context.MODE_PRIVATE)
            val mCascadeFile = File(cascadeDir, "$resourceString")
            val os = FileOutputStream(mCascadeFile)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (`is`.read(buffer).also { bytesRead = it } != -1) {
                os.write(buffer, 0, bytesRead)
            }
            `is`.close()
            os.close()
            val face_cascade = CascadeClassifier(mCascadeFile.getAbsolutePath())
            if (face_cascade.empty()) {
                Log.v("MyActivity", "--(!)Error loading A\n")
                return null
            } else {
                Log.v(
                    "MyActivity",
                    "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath()
                )
                return face_cascade
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.v("MyActivity", "Failed to load cascade. Exception thrown: $e")
            return null
        }
    }
}