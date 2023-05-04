package com.ingenieriajhr.firstopencv

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.ingenieriajhr.firstopencv.databinding.ActivityMainBinding
import com.ingenieriajhr.firstopencv.detectFace.DetectFaceFromBitmap
import com.ingenieriajhr.firstopencv.`interface`.ReturnPos
import com.ingenieriajhr.firstopencv.`interface`.SoccerResponse
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity(),ReturnPos,SoccerResponse {

    lateinit var binding:ActivityMainBinding
    lateinit var cameraJhr: CameraJhr
    lateinit var detectFaceFromBitmap: DetectFaceFromBitmap
    //question calculate Scale Factor
    var factorCalculate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this,R.color.black)

        //init cameraJhr
        cameraJhr = CameraJhr(this)

        if(OpenCVLoader.initDebug()) Log.d("OPEN2023CV","SUCESS")
        else Log.d("OPEN2023CV","ERROR")

        detectFaceFromBitmap = DetectFaceFromBitmap(this)
        detectFaceFromBitmap.initListener(this)

        binding.overlayView.responseSoccer(this)
    }

    /**
     * focus windows Mainactivity
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cameraJhr.allpermissionsGranted() && !cameraJhr.ifStartCamera){
            startCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }

    /**
     * start configuration camera X
     */
    private fun startCameraJhr() {
        cameraJhr.addlistenerResponse(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    //val newBitmap = binding.cameraPreview.bitmap
                    val newBitmap2 = bitmap.rotate(180f)
                    val newBitmap = detectFaceFromBitmap.recognizedFace(newBitmap2)
                    if (!factorCalculate){
                        binding.overlayView.calculateScaleFactor(newBitmap2.width,newBitmap2.height)
                        factorCalculate = true
                    }
                    runOnUiThread {
                        binding.imgBitMap.setImageBitmap(newBitmap)
                    }

                }
            }
        })
        cameraJhr.initBitmap()
        //selector camera LENS_FACING_FRONT = 0;    LENS_FACING_BACK = 1;
        //aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1;
        cameraJhr.start(0,0,binding.cameraPreview,false)
    }

    /**
     * @return bitmap rotate degrees
     */
    fun Bitmap.rotate(degrees:Float) = Bitmap.createBitmap(this,0,0,width,height,
        Matrix().apply { postRotate(degrees) },true)

    /**
     * return pos X and Y Points1 and Points2
     */
    override fun returPos(posX: Int, posY: Int, posX2: Int, posY2: Int) {
        binding.overlayView.posXY(posX,posY,posX2,posY2)
    }

    /**
     * change text in pointsGame TextView
     */
    override fun point(point: Int) {
        runOnUiThread {
            binding.pointsGame.text = point.toString()
        }
    }


}