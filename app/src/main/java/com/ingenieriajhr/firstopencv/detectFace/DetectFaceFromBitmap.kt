package com.ingenieriajhr.firstopencv.detectFace

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ingenieriajhr.firstopencv.R
import com.ingenieriajhr.firstopencv.`interface`.ReturnPos
import com.ingenieriajhr.firstopencv.overlay.OverlayDraw
import com.ingenieriajhr.firstopencv.utilsOpen.LoadFileCascade
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class DetectFaceFromBitmap(val context: Context) {


    //return face cascade
    val loadFileCascade = LoadFileCascade(context)

    lateinit var returnPos: ReturnPos

    var factorConvertion = 0


    fun initListener(returnPos: ReturnPos){
        this.returnPos = returnPos
    }



    fun recognizedFace(bitmap: Bitmap): Bitmap {

        val bitmapNew = bitmap

        val mat = Mat()

        Utils.bitmapToMat(bitmap,mat)

        //convertimos el espacio de color MAT de BGR(predeterminado por openCV) a escala
        //de grises
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY)

        val cascadeClassifier = loadFileCascade.load_cascade(R.raw.haarcascade_frontalface_default,"haarcascade_frontalface_default.xml")

        val faces = MatOfRect()

        cascadeClassifier!!.detectMultiScale(mat,faces)

        val rectArray = faces.toArray()

        val mat2 = Mat()
        Utils.bitmapToMat(bitmapNew,mat2)

        for (rect in rectArray) {
            returnPos.returPos(rect.x,rect.y,rect.width,rect.height)
            Log.d("carax", rect.x.toString())
            Log.d("caray", rect.y.toString())
            Imgproc.rectangle(
                mat2,
                Point(rect.x.toDouble(), rect.y.toDouble()),
                Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                Scalar(0.0, 255.0, 0.0), // color del rectángulo (en BGR)
                2 // grosor del rectángulo
            )
        }


        Utils.matToBitmap(mat2, bitmapNew)

        return bitmapNew

    }


}