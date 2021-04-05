package com.delzor.zb.csakbusz

import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

object Data {

    enum class BUSCOLOR {
        YELLOW,
        ORANGE;

        fun getNext(): BUSCOLOR {
            return values()[(ordinal + 1) % values().size]
        }
    }

    enum class RESP {
        SUCCESSFUL,
        NODATA,
        ERROR
    }

    enum class BULLETPOS{
        FIRST,
        LAST,
        MID
    }

    fun KOLORS(num: Int): String {
        return when (num) {
            0 -> "#08E80C"//zöld
            1 -> "#FF6659"//piros
            2 -> "#E86C96"
            3 -> "#3C3DE8"
            4 -> "#E8876C"
            5 -> "#7545E8"//lila
            6 -> "#839e22"
            7 -> "#FF64E2"
            8 -> "#E8B86C"
            9 -> "#B0B863"
            10 -> "#BEE8C8"
            11 -> "#6F63B8"
            12 -> "#B8B41D"
            13 -> "#1DB884"
            else -> "#B87825"
        }


    }


    var onlinebusList: MutableList<String> = mutableListOf()
    var busStopList: MutableList<BusStop> = mutableListOf()
    var allLineList: MutableList<SimpleLine> = mutableListOf()
    var currStopSpots = mutableListOf<StopSpot>()
    var nearStopSpots = mutableListOf<StopSpot>()
    var selectedSubLine = Subline()
    val client = OkHttpClient()
    var _testStopSpot = "100607"
    var _testStopSpotName = "Kiskondás étterem"
    var _testLineNum = "24"
    var numOfDownloadedPages = 0
    val ZEG = LatLng(46.84, 16.84389)
    var timer = Timer()

    val infohtml = "<b>zalaegerszeg.enykk.hu mobil változata.</b><p>Az alkalmazás használatához állandó internetkapcsolatra van szükség. Az adatok pontosságáért felelősséget nem vállalok, pontosan ugyan azok az adatok jelennek meg az alkalmazásban, mint a weboldalon. Ebből kifolyólag a 6200, 6205, 6235-ös járatoknál az \"Útvonal\" fülön az útvonal hibásan vagy nem jelenik meg, mivel ez a weboldalas verzióban sem működik.</p><h3>Funkciók:</h3><p> &emsp;&#9679; Összes és közeli megálló kilistázása<br/> &emsp;&#9679; Online buszok mutatása a térképen<br/> &emsp;&#9679; Vonalak/Járatok listája</p><h3>Megállók:</h3><p> &emsp;&#9679; Megálló választása listából, majd pontosítás térkép segítségével<br/> &emsp;&#9679; Megálló keresés a listában<br/> &emsp;&#9679; <b>Érkezések:</b> Következő órában érkező busz(ok) kilistázása<br/> &emsp;&#9679; <b>Menetrendek:</b> Melyik járat mikor érkezik a megállóba a beállított dátumon<br/> &emsp;&#9679; <b>Vonalak:</b> Az adott megállót érintő vonalak/járatok listája</p><h3>Online buszok:</h3><p> &emsp;&#9679; A kiválasztott járat buszát/buszait megjeleníti a térképen<br/> &emsp;&#9679; Automatikusan frissülő<br/> &emsp;&#9679; A busz(ok) listázása útvonallal<br/></p><h3>Vonalak/Járatok:</h3><p> &emsp;&#9679; Ezzel a funkcióval csak az aznapi járatok jelennek meg!<br/> &emsp;&#9679; Járatok kilistázása, útvonallal együtt<br/> &emsp;&#9679; Választás után pontosítás, listából:<br/> &emsp;&emsp;&#9675; Az \"Útvonal\" fül segítségével ellenőrízhetjük, hogy jól választottunk-e, ha nem visszatérhetünk a \"Lista\" fülre és választhatunk másikat<br/> &emsp;&emsp;&#9675; Az \"Útvonal\" fülön láthatjuk, hogy a járat amelyet a \"Lista\" fülön pontosítottunk, milyen útvonalon közlekedik és milyen megállókat érint<br/> &emsp;&emsp;&#9675; A \"Menetidő\" fülön láthatjuk, hogy a kiválasztott járat mikor indul, illetve egy lista mutatja az érintett megállókat és a menetidőt percben.</p><p> Köszönöm,<br/> Fejlesztő</p>"

    /**FETCH FUNCTIONS*/
    fun fetchOnlineBus(callback: (Data.RESP) -> Unit) {

        val url = "http://webgyor.kvrt.hu/csaoajax/sql-cs.php?vonal_irany=ZE001:O,V;ZE01A:O,V;ZE01E:O,V;ZE01U:O,V;ZE01Y:O,V;ZE002:O,V;ZE02A:O,V;ZE02Y:O,V;ZE003:O,V;ZE03C:O,V;ZE03Y:O,V;ZE004:O,V;ZE04A:O,V;ZE04E:O,V;ZE04U:O,V;ZE04Y:O,V;ZE005:O,V;ZE05C:O,V;ZE05U:O,V;ZE05Y:O,V;ZE008:O,V;ZE08C:O,V;ZE08E:O,V;ZE009:O,V;ZE09E:O,V;ZE09U:O,V;ZE010:O,V;ZE10C:O,V;ZE10E:O,V;ZE10Y:O,V;ZE011:O,V;ZE11A:O,V;ZE11Y:O,V;ZE024:O,V;ZE026:O,V;ZE030:O,V;ZE30A:O,V;ZE30C:O,V;ZE041:O,V;ZE044:O,V;ZE048:O,V;ZE069:O,V;ZE0C1:O,V;ZE0C2:O,V;ZE0C4:O,V;ZE0C5:O,V"
        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                //process data

                val resp = response!!.body()!!.string().toString()

                if(resp.length < 2){
                    callback(Data.RESP.NODATA)
                }else{
                    val lines: MutableList<String> = resp.split("<br/>").toMutableList()

                    lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (item in lines) {
                        val busid = item.split("|").first()
                        val bus = Utils.removeZeroes(busid.removePrefix("ZE"))

                        //println(busz)

                        if (!Data.onlinebusList.contains(bus)) Data.onlinebusList.add(bus)

                    }
                    callback(Data.RESP.SUCCESSFUL)
                }
            }
        }
        )
    }

    fun fetchBusStopORline(findLines: Boolean = false, callback: () -> Unit) {

        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=keres|%20"

        val request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
                //Log.d("H_REQ", "failure, error: " + e!!.message)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var resp = response!!.body()!!.string().toString()
                //Log.d("H_REQ", "req ok resp:" + resp)
                resp = Jsoup.parse(resp).text()

                val lines: MutableList<String> = resp.split("[]").toMutableList()

                //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                for (line in lines) {
                    val items = line.split("|")
                    if(findLines){
                        if (items[0] == "vonal") {
                            val curr = SimpleLine(
                                    items[2], //szám
                                    items[3]  //név
                            )
                            allLineList.add(curr)
                        }
                    }else{
                        if (items[0] == "megallo") {
                            val curr = BusStop(
                                    items[1],
                                    items[2]
                            )
                            Data.busStopList.add(curr)
                        }
                    }

                }
                callback()
            }
        }
        )

    }

    fun fetchStopSpot(busStopID: String, callback: () -> Unit) {

        Data.currStopSpots = mutableListOf()

        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=mod_megallo|" + busStopID

        val request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()

                val lines: MutableList<String> = resp.split("\n").toMutableList()

                lines.removeAt(0) // Első sor tartalmazza a szülő osztály adatait

                for (line in lines) {
                    val items = line.split("|")
                    val curr = StopSpot(
                            items[1].trim(),
                            items[2].trim(),
                            items[4],
                            items[3]
                    )
                    currStopSpots.add(curr)
                }
                callback()
            }
        }
        )

    }

    fun fetchNearStopSpot(lat: String, lng: String, callback: (Data.RESP) -> Unit) {
        Data.nearStopSpots = mutableListOf()

        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=gmaps|$lng|$lat"

        val request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                if(resp.length < 2){
                    callback(Data.RESP.NODATA)
                }else{
                    val lines: MutableList<String> = resp.split("\n").toMutableList()
                    //lines.removeAt(0) // Első sor tartalmazza a szülő osztály adatait
                    var name = "asd"
                    for (line in lines) {
                        val items = line.split("|")
                        if (items[0] == "foldhely") {
                            name = items[2]
                        } else if (items[0] == "kocsiall") {
                            val curr = StopSpot(
                                    items[1].trim(),
                                    items[2].trim(),
                                    items[4],
                                    items[3],
                                    name
                            )
                            nearStopSpots.add(curr)
                        }
                    }
                    callback(Data.RESP.SUCCESSFUL)
                }
            }
        }
        )

    }

    fun fetchLineData(lineID: Int, callback: (messages : MutableList<String>) -> Unit) {
        selectedSubLine.id = lineID
        val messages = mutableListOf<String>()
        fetchPathTime {
            when(it){
                Data.RESP.NODATA -> messages.add("NO_PATHTIME")
                Data.RESP.ERROR -> messages.add("ERR_PATHTIME")
                Data.RESP.SUCCESSFUL -> {}
            }
            fetchStartTimes{
                when(it){
                    Data.RESP.NODATA -> messages.add("NO_STARTTIMES")
                    Data.RESP.ERROR -> messages.add("ERR_STARTTIMES")
                    Data.RESP.SUCCESSFUL -> {}
                }
                fetchLineStopSpots{
                    when(it){
                        Data.RESP.NODATA -> messages.add("NO_STOPSPOTS")
                        Data.RESP.ERROR -> messages.add("ERR_STOPSPOTS")
                        Data.RESP.SUCCESSFUL -> {}
                    }
                    fetchLinePath {
                        when(it){
                            Data.RESP.NODATA -> messages.add("NO_LINEPATH")
                            Data.RESP.ERROR -> messages.add("ERR_LINEPATH")
                            Data.RESP.SUCCESSFUL -> {}
                        }
                        callback(messages)
                    }
                }
            }
        }
    }
    /**Sub fetch functions for fetchline data*/
    fun fetchPathTime(cb1: (Data.RESP) -> Unit) {
        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=nyomvonal2|${selectedSubLine.id}|0"

        val request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
                cb1(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                //println("resp" + resp)

                val pathTime = mutableListOf<Int>()

                if (resp.length < 2) {
                    cb1(Data.RESP.NODATA)
                } else {
                    val lines: MutableList<String> = resp.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (line in lines) {
                        val items = line.split("|")
                        val curr = items[4].toInt()
                        pathTime.add(curr)
                    }
                    selectedSubLine.pathtime = pathTime
                    cb1(Data.RESP.SUCCESSFUL)

                }
            }
        }
        )

    }
    fun fetchStartTimes(cb2: (Data.RESP) -> Unit){
        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=jaratok|${selectedSubLine.id}"

        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
                cb2(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                //println("resp" + resp)

                var startTimes = mutableListOf<Pair<Int,Int>>()

                if (resp.length < 2) {
                    cb2(Data.RESP.NODATA)
                } else {
                    var lines: MutableList<String> = resp!!.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (line in lines) {
                        var items = line.split("|")
                        var curr = Pair(items[2].toInt(),items[3].toInt())
                        startTimes.add(curr)
                    }
                    selectedSubLine.startTimes = startTimes
                    cb2(Data.RESP.SUCCESSFUL)

                }
            }
        }
        )
    }
    fun fetchLineStopSpots(cb3:(Data.RESP) -> Unit){
        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=online_terkep2a|${selectedSubLine.id}"

        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
                cb3(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                //println("resp" + resp)

                var stopSpots = mutableListOf<StopSpot>()

                if (resp.length < 2) {
                    cb3(Data.RESP.NODATA)
                } else {
                    var lines: MutableList<String> = resp!!.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (line in lines) {
                        var items = line.split("|")
                        var curr = StopSpot(
                                items[4],
                                items[0],
                                items[2],
                                items[1],
                                items[3]
                        )
                        stopSpots.add(curr)
                    }
                    selectedSubLine.stopSpots = stopSpots
                    cb3(Data.RESP.SUCCESSFUL)

                }
            }
        }
        )
    }
    fun fetchLinePath(cb4: (Data.RESP) -> Unit){
        val url = "http://zalaegerszeg.enykk.hu/php/sql.php?id=online_terkep2b|${selectedSubLine.id}"

        var request = Request.Builder()
                .url(url)
                .build()

        Data.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //println(e)
                cb4(Data.RESP.ERROR)
            }

            override fun onResponse(call: Call?, response: Response?) {

                var resp = response!!.body()!!.string().toString()
                //resp = Jsoup.parse(resp).text()
                //println("resp" + resp)

                var path = mutableListOf<LatLng>()

                if (resp.length < 2) {
                    cb4(Data.RESP.NODATA)
                } else {
                    var lines: MutableList<String> = resp!!.split("[]").toMutableList()

                    //lines.removeAt(0) // Első sor tartalmazza a mezőneveket

                    for (line in lines) {
                        var items = line.split("|")
                        var curr = LatLng(items[1].toDouble(), items[0].toDouble()
                        )
                        path.add(curr)
                    }
                    selectedSubLine.path = path
                    cb4(Data.RESP.SUCCESSFUL)

                }
            }
        }
        )
    }
}
