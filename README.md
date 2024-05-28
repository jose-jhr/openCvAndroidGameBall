# openCvAndroidGameBall


```kotlin

 buildFeatures{
        viewBinding = true
    }

```

```kotlin
//Implementation CameraJhr
    implementation 'com.github.jose-jhr:Library-CameraX:1.0.8'

    // If you want to additionally use the CameraX View class
    implementation "androidx.camera:camera-view:1.0.0-alpha21"
    
    //openCv
    implementation 'com.github.jose-jhr:openCvAndroidGameBall:1.0.2'


```




MainActivity

```kotlin

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
        cameraJhr.addlistenerBitmap(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    //val newBitmap = binding.cameraPreview.bitmap
                    //val newBitmap2 = bitmap.rotate(180f)

                    val newBitmap = detectFaceFromBitmap.recognizedFace(bitmap)
                    if (!factorCalculate){
                        binding.overlayView.calculateScaleFactor(bitmap.width,bitmap.height)
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
        cameraJhr.start(0,0,binding.cameraPreview,true,false,true)
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


```

File xml

```xml

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.camera.view.PreviewView
        android:layout_width="match_parent"
        android:id="@+id/camera_preview"
        android:layout_height="match_parent"
        >
    </androidx.camera.view.PreviewView>

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/imgBitMap"
        android:scaleType="fitXY"
        >
    </ImageView>

    <com.ingenieriajhr.firstopencv.overlay.OverlayDraw
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/overlayView"
        >
    </com.ingenieriajhr.firstopencv.overlay.OverlayDraw>


    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:text="0"
        android:textColor="@color/red"
        android:textSize="30sp"
        android:layout_marginEnd="10dp"
        android:id="@+id/pointsGame"
        >
    </TextView>



</RelativeLayout>


```

file change xml 
[haarcascade_frontalface_default.zip](https://github.com/jose-jhr/openCvAndroidGameBall/files/11402512/haarcascade_frontalface_default.zip)

link download
https://cienciayculturacreativa.com/openCv/xmls/haarcascade_frontalface_default.xml


DetectFaceFromBitmap

```kotlin

/**
 * @author JOSE HIDALGO RODRIGUEZ INGENIERIA JHR
 */
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


```


Draw points and ball


```kotlin

/**
 * @author JOSE HIDALGO RODRIGUEZ INGENIERIA JHR
 */

class OverlayDraw(context: Context,attrs: AttributeSet):View(context,attrs) {


    //1
    var paint: Paint = Paint()

    var posX = 0f
    var posY = 0f

    var posX2 = 0f
    var posY2 = 0f

    var scaleFactorWidth:Float = 0f
    var scaleFactorHeigth:Float = 0f

    var imageWidth = 0f
    var imageHeight = 0f

    //object soccer game
    var soccerGame = SoccerGame()

    var point1X: Float = 0.0f
    var point1Y: Float = 0.0f
    var point2X: Float = 0.0f
    var point2Y: Float = 0.0f

    var point1 = Pair(0f,0f)
    var point2 = Pair(0f,0f)

    var widthBall = 0f
    var heightBall = 0f



    //interface response
    lateinit var soccerResponse: SoccerResponse

    //2) init paint color and stroke lines and points
    init {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.setColor(Color.RED)
    }

    //init method listener
    fun responseSoccer(soccerResponse: SoccerResponse){
        this.soccerResponse = soccerResponse
    }



    /**
     * 3
     * @param x First pos1 posX
     * @param y Second pos1 posy
     * @param posX2 First pos2 posX
     * @param posY2 Second pos2 posY
     */
    fun posXY(x: Int, y: Int, posX2: Int, posY2: Int){
        //init var locale
        posX = x.toFloat()
        posY = y.toFloat()

        this.posX2 = posX2.toFloat()
        this.posY2 = posY2.toFloat()

        //scale Factor points, 480->1080
        point1X = posX*scaleFactorWidth
        point1Y = posY*scaleFactorHeigth
        point2X = (posX+posX2)*scaleFactorWidth
        point2Y = (posY+point2Y)*scaleFactorHeigth

        //init pair with method copy,this replace value former firts and second
        point1 = point1.copy(first = point1X).copy(second = point1Y)
        point2 = point2.copy(first = point2X).copy(second = point1Y)

        //response points according to touch ball
        soccerResponse.point(soccerGame.point)

        //redraw
        invalidate()
    }


    /**4
     * calculata factor for image calculate colision
     * @param imageWidth width bitmap
     * @param imageHeight heighr bitmap
     */
    fun calculateScaleFactor(imageWidth: Int, imageHeight: Int){
        //scale factor calculate with width(width view) / imageWidth is size width bitmap
        scaleFactorWidth = width/imageWidth.toFloat()
        scaleFactorHeigth = height/imageHeight.toFloat()

        //init var width and height local
        this.imageWidth = imageWidth.toFloat()
        this.imageHeight = imageHeight.toFloat()

        //width and height of ball
        this.widthBall = width*0.3f
        this.heightBall = height*0.15f

        //pass to parameter width and height to soccerGame object
        soccerGame.widtView = width
        soccerGame.heightView = height
        //init bitmap in object soccergame
        soccerGame.getBitmapSrc(resource = resources, widthBall.toInt(), heightBall.toInt())
    }


    /**
     * 5
     * draw views ball and lines, points
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw points
        canvas!!.drawCircle(point1X,point1Y,20f,paint)
        canvas!!.drawCircle(point2X,point1Y,20f,paint)
        //draw ball with soccerGame object
        soccerGame.drawBall(canvas,point1,point2)

    }


}

```

InterfaceReturnPos

```kotlin

interface ReturnPos {

    fun returPos(posX:Int,posY:Int,posX2:Int,posY2: Int)

}

```

Interface SoccerResponse

```kotlin

interface SoccerResponse {

    fun point(point:Int)

}

```

![image](https://user-images.githubusercontent.com/66834393/236107050-78ace956-55e0-4196-9f1b-0d56753ade28.png)




//POSSIBILITIES ERRORS
![image](https://user-images.githubusercontent.com/66834393/236109250-470e0527-41e3-495c-bafc-738c3fe90821.png)
![image](https://user-images.githubusercontent.com/66834393/236109258-1149ec6d-4c26-476e-ae0f-211118971884.png)
![image](https://user-images.githubusercontent.com/66834393/236109280-ac85195f-c9ba-483a-93ab-22a4481a55f3.png)




