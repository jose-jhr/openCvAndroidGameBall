package com.ingenieriajhr.firstopencv.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ingenieriajhr.firstopencv.`interface`.SoccerResponse
import com.ingenieriajhr.soccer.SoccerGame
import org.opencv.core.Point


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