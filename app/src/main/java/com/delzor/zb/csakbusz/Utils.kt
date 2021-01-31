package com.delzor.zb.csakbusz

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.ConnectivityManager
import android.text.Html
import android.text.Spanned


object Utils{

    /**GRAPHICS RELATED FUNCTIONS*/

    //fixed: 2021-01-31
    fun writeToBusIcon(c: Context, drawableId: Int, text: String): Bitmap {

        val bm = BitmapFactory.decodeResource(c.resources, drawableId).copy(Bitmap.Config.ARGB_8888, true)

        val tl = text.length //we use this property too many times

        //for paiting the number of the busline
        val paintText = Paint().apply{
            style = Paint.Style.FILL
            color = Color.YELLOW
            strokeWidth = 10f
            textSize = if(tl > 2) 20f else 30f
        }
        //for painting the black box behind the text
        val paintTextBackground = Paint().apply{
            style = Paint.Style.FILL
            color = Color.BLACK
        }

        //size of black text table (longer texts need wider table)
        val tableRight = if (tl > 1) 68f else 60f
        val tableWidthOffset = if(tl > 1) 20f else 12f

        val r = RectF((bm.width / 2 - tableWidthOffset).toFloat(), 0f, tableRight, 30f)

        //draw the new things to the input Bitmap
        val canvas = Canvas(bm).also {
            it.drawRoundRect(r, 8f, 8f, paintTextBackground)
            it.drawText(text, bm.width / 2f - if (tl > 1) 18f else 9f, if (tl == 3) 22f else 26f, paintText)
        }

        return bm
    }
    fun writeToBusStopIcon(c: Context, drawableId: Int, text: String): Bitmap {

        val bm = BitmapFactory.decodeResource(c.resources, drawableId).copy(Bitmap.Config.ARGB_8888, true)
        //SZÖVEG
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        if (text.length > 2) {
            paint.textSize = 20f
        } else {
            paint.textSize = 30f
        }

        paint.strokeWidth = 10f
        //SZÖVEGDOBOZ
        val paint1 = Paint()
        paint1.style = Paint.Style.FILL
        paint1.color = Color.parseColor("#0c5ca7")
        val r = RectF((bm.width - 34).toFloat(), bm.height.toFloat()-30, bm.width.toFloat(), bm.height.toFloat())

        val canvas = Canvas(bm)
        canvas.drawRoundRect(r,8f,8f, paint1)
        /*
        if (text.length == 2) { // ha ketjegyu
            canvas.drawText(text, bm.width - 33f, bm.height-5f, paint)
        }else{ // ha csak egyjegyu
            canvas.drawText(text, bm.width-26f,bm.height-5f, paint)
        }*/
        val l = text.length
        canvas.drawText(text, bm.width - if(l == 2) 33f else 26f, bm.height-5f, paint)


        return bm
    }
    fun circularBG(c: Context, color: Int): Bitmap {
        val image = BitmapFactory.decodeResource(c.resources, R.drawable.bus_marker).copy(Bitmap.Config.ARGB_8888, false)
        val multiplier = 1.5f
        val unit = image.width*multiplier
        val backgrund = Bitmap.createBitmap(unit.toInt(),unit.toInt(), Bitmap.Config.ARGB_8888 )
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = color
        paint.flags = Paint.ANTI_ALIAS_FLAG

        val canvas = Canvas(backgrund)
        canvas.drawCircle(unit/2f,unit/2f,unit/2f,paint)
        val imgStart = (unit-unit/multiplier)/2
        canvas.drawBitmap(image,imgStart,imgStart,paint)


        return backgrund
    }
    fun bulletIcon(c: Context, bulletpos: Data.BULLETPOS): Bitmap{
        val unit = 80
        val backgrund = Bitmap.createBitmap(unit/2,unit, Bitmap.Config.ARGB_8888 )
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor(Data.selectedSubLine.pathColor?:"#0c5ca7")
        paint.flags = Paint.ANTI_ALIAS_FLAG

        val canvas = Canvas(backgrund)
        val rad = unit/4f
        canvas.drawCircle(unit/4f,unit/2f,rad,paint)

        val rect = when(bulletpos){ //left top right bot
            Data.BULLETPOS.FIRST -> Rect((3*rad/4).toInt(),unit/2,(rad+rad/4).toInt(),unit)
            Data.BULLETPOS.LAST -> Rect((3*rad/4).toInt(),0,(rad+rad/4).toInt(),unit/2)
            else -> Rect((3*rad/4).toInt(),0,(rad+rad/4).toInt(),unit)
        }

        canvas.drawRect(rect,paint)


        return backgrund
    }

    /**STRING RELATED FUNCTIONS*/
    fun removeZeroes(num: String) : String{
        //remove zeroes before the nums. ex.: 0024 -> 24
        var num1 = num
        var startNum: Int = 0
        var fin = num1.toCharArray()

        for (x in fin){
            if(x == '0'){
                startNum++
            }else{
                break
            }
        }
        val ret = num1.substring(startNum,num.length)
        return ret
    }
    fun addZeroes(num: String) : String {
        //pl 4
        val numZeroes = 3 - num.length
        var fin = ""
        for(i in 1..numZeroes){
            fin += "0"
        }
        fin+=num

        return fin
    }
    fun dateFormat(year:Int, month: Int, day: Int) = year.toString() +"-"+ String.format("%02d",month+1) +"-"+ String.format("%02d",day)
    fun setTextHTML(html: String): Spanned
    {
        val result: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
        return result
    }

    /**GOOGLE MAPS FUNCTIONS*/
    fun midPoint(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Pair<Double,Double>{
        var lat1 = lat1
        var lon1 = lon1
        var lat2 = lat2

        val dLon = Math.toRadians(lon2 - lon1)

        //convert to radians
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)
        lon1 = Math.toRadians(lon1)

        val Bx = Math.cos(lat2) * Math.cos(dLon)
        val By = Math.cos(lat2) * Math.sin(dLon)
        val lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By))
        val lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx)

        //print out in degrees
        return Pair(Math.toDegrees(lat3),Math.toDegrees(lon3))
    }

    /**SEARCH IN LIST FUNCTIONS*/
    fun stopSpotNObyID(id: String, itemList: MutableList<StopSpot>)
         = itemList.filter{
            it.id == id
        }.first().no
    fun searchStopByName(name : String) = Data.busStopList.filter{ it.name.toLowerCase().contains(name.toLowerCase()) }
    fun nearStopSpotNameByID(id: String)
            = Data.nearStopSpots.filter{
                it.id == id
                }.first().name

    /**VAN-E NET FUNFC */
    fun isOnline(act : Activity): Boolean {
        val cm = act.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

}