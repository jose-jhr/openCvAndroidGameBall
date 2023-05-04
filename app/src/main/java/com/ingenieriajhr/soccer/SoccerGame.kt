package com.ingenieriajhr.soccer

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.ingenieriajhr.firstopencv.R


/**
 * @author JOSE HERNANDO HIDALGO RODRIGUEZ INGENIERIA JHR
 */
class SoccerGame {

    //1 init bitmap soccer ball
    private var bitMapSoccerBall:Bitmap? = null

    //pos and speed and bounce force
    var posX = 0f
    var posY = 0f
    var speedX = 0f;
    var gravity = 0.1f
    var speedY = 0f
    var bounceForce = -6f

    var widtView = 0
    var heightView = 0

    var widthBall = 0
    var heightBall = 0

    private var paint = Paint()

    var point = 0

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    /**
     * @param resource to access to resources mipmap
     * @param widthBall width ball
     * @param heightBall heidth ball
     *
     */
    fun getBitmapSrc(resource:Resources,widthBall:Int,heightBall:Int){
        //decode image to bitmap
        bitMapSoccerBall = BitmapFactory.decodeResource(resource, R.mipmap.football)
        //create scale bitmap
        bitMapSoccerBall = Bitmap.createScaledBitmap(bitMapSoccerBall!!,widthBall,heightBall,false)

        //calculate gravity with porcentage of diplay
        gravity = heightView*0.01f
        bounceForce *= gravity
        speedX = widtView*0.05f

        //ball width and height
        this.widthBall = widthBall
        this.heightBall = heightBall
    }

    /**
     * @param canvas draw ball
     * @param point1 pair contain two points
     * @param point2 pair contain two points
     */
    fun drawBall(canvas: Canvas, point1: Pair<Float,Float>, point2: Pair<Float,Float>){
        if (bitMapSoccerBall!=null){
            //change position ball
            changePosition(point1,point2)
            canvas.drawBitmap(bitMapSoccerBall!!,posX,posY,paint)
        }
    }

    /**
     * @param point1 pair contain two points
     * @param point2 pair contain two points
     */
    private fun changePosition(point1: Pair<Float, Float>, point2: Pair<Float, Float>) {
        //++speedy ++ gravity speedY
        posY += speedY
        speedY +=gravity
        //++ velX
        posX += speedX
        //if ball end display below reinit position
        if(posY>heightView){
            clearPosition()
        }
        //if ball > width display + widthBall if change values -1
        if (posX>widtView-widthBall){
            speedX*=-1
        }
        //if ball <0 if change values -1
        if (posX<0){
            speedX*=-1
        }

        //if ball touch points
        if (point1.first<posX+widthBall/2 && point2.first>posX+widthBall/2 && posY+heightBall>point1.second && posY<point1.second){
            speedY = bounceForce
            point++
        }

    }

    /**
     * clear position reinit game
     */
    private fun clearPosition() {
        posY = 0f
        speedY = 0f
        posX = 0f
        point = 0
    }


}