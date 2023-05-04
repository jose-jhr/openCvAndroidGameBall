package com.ingenieriajhr.firstopencv.detectFace

import android.content.Context
import android.graphics.Bitmap
import com.ingenieriajhr.firstopencv.R
import com.ingenieriajhr.firstopencv.`interface`.ReturnPos
import com.ingenieriajhr.firstopencv.utilsOpen.LoadFileCascade
import com.ingenieriajhr.firstopencv.utilsOpen.LoadFileCascadess
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class DetectFaceFromBitmap(val context: Context) {


    //return face cascade
    val loadFileCascade = LoadFileCascade(context)

    //interface ReturnPos
    lateinit var returnPos: ReturnPos

    fun initListener(returnPos: ReturnPos){
        this.returnPos = returnPos
    }

    fun recognizedFace(bitmap: Bitmap): Bitmap {
        //create object memory image
        val mat = Mat()

        //conver bitmap to Mat
        Utils.bitmapToMat(bitmap,mat)

        //convertimos el espacio de color MAT de BGR(predeterminado por openCV) a escala
        //de grises
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY)

        //load fileCacade
        val cascadeClassifier = loadFileCascade.load_cascade(R.raw.haarcascade_frontalface_default,"haarcascade_frontalface_default.xml")

        //rect
        val faces = MatOfRect()

        //detect face
        cascadeClassifier!!.detectMultiScale(mat,faces)

        //face array
        val rectArray = faces.toArray()

        //create object memory from data image
        val mat2 = Mat()

        //convert bitmap to mat2
        Utils.bitmapToMat(bitmap,mat2)

        for (rect in rectArray) {

            returnPos.returPos(rect.x,rect.y,rect.width,rect.height)
            Imgproc.rectangle(
                mat2,
                Point(rect.x.toDouble(), rect.y.toDouble()),
                Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                Scalar(0.0, 255.0, 0.0), // color del rectángulo (en BGR)
                2 // grosor del rectángulo
            )
        }


        Utils.matToBitmap(mat2, bitmap)

        return bitmap

    }


}