package com.example.alchemist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.alchemist.ui.theme.AlchemistTheme
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlchemistTheme {
               Alchemist()
            }
        }
    }
}

//список для всех зелий
var listBottle =
    mutableStateListOf(
        Bottle(50f,100f, Color.Blue,R.drawable.blue),
        Bottle(200f,100f, Color.Black, R.drawable.black),
        Bottle(350f,100f, Color.Red, R.drawable.red),
        Bottle(500f,100f, Color.Yellow, R.drawable.yellow),
        Bottle(650f,100f, Color.Green, R.drawable.green)
    )



//список для зелий которые вылили в котел
var listBottlesInBoiler = mutableListOf<Bottle>()

//дата класс для зелья (координаты и цвет)
data class Bottle(val offsetX:Float,val offsetY: Float, val color: Color,val img:Int)
//у каждого зелья одинаковая логика
// если зелье попало в котел,  то оно удаляется из списка доступных зелий и добавляется в список с использованными зельями
@Composable
private fun DragBottle( bottle: Bottle, index:Int){
    var offsetX by remember { mutableStateOf(bottle.offsetX) }
    var offsetY by remember { mutableStateOf(bottle.offsetY) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current
    Box(
        Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            //.background(bottle.color)
            .size(40.dp, 70.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    //движение
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    //когда отпустили, проверка на координаты котла
                    onDragEnd = {
                        if (offsetY >= (with(density) { screenHeight.toPx() }) * 2 / 3 &&
                            offsetX >= with(density) { 25.dp.toPx() } && offsetX <= with(density) { (screenWidth - 25.dp).toPx() }
                        ) {
                            listBottlesInBoiler.add(bottle)
                            //listBottle.remove(bottle)
                            Log.d(
                                "BOTTLE",
                                listBottle
                                    .toList()
                                    .toString()
                            )
                            Log.d("BOTTLE", listBottlesInBoiler.toString())
                        } else {
                            offsetX = bottle.offsetX
                            offsetY = bottle.offsetY
                        }
                    }
                )
            }
    ){
        Image(painter = painterResource(id = bottle.img), contentDescription = null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }

}


@Composable
private fun Alchemist(){
    var test = remember {
        mutableStateListOf<Bottle>()
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current
    Box(modifier = Modifier.fillMaxSize()){
        listBottle.forEachIndexed() { index, bottle -> DragBottle(bottle, index) }


        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    if(listBottlesInBoiler.size==2){
                        if(listBottlesInBoiler.any { it.color == Color.Red } && listBottlesInBoiler.any { it.color == Color.Blue }){

                            listBottle.add(Bottle(800f,100f, Color.Magenta, R.drawable.magenta))

                        } else if(listBottlesInBoiler.any { it.color == Color.Yellow } && listBottlesInBoiler.any { it.color == Color.Blue }){

                            listBottle.add(Bottle(50f,250f, Color.Cyan, R.drawable.cyan))

                        } else if(listBottlesInBoiler.any { it.color == Color.Red } && listBottlesInBoiler.any { it.color == Color.Green }){

                            listBottle.add(Bottle(200f,250f, Color.DarkGray, R.drawable.dark_gray))

                        }else if(listBottlesInBoiler.any { it.color == Color.Green } && listBottlesInBoiler.any { it.color == Color.Yellow }){

                            listBottle.add(Bottle(350f,250f, Color.White, R.drawable.white))

                        }else if(listBottlesInBoiler.any { it.color == Color.Black } ){

                            listBottle.add(Bottle(500f,250f, Color.Black, R.drawable.black))

                        } else{
                            listBottle.add(Bottle(50f,400f, Color.Black, R.drawable.black))
                        }
                    }else{
                        if(listBottlesInBoiler.size!=0)
                            listBottle.add(Bottle(650f,250f, Color.Black, R.drawable.black))
                    }

                    listBottlesInBoiler.clear()
                    Log.d("BOTTLE2", listBottle.toString())
                    Log.d("BOTTLE2", listBottlesInBoiler.toString())
                }) {
                        Text("MIX")
                }
                Box(modifier = Modifier
                    .height(screenHeight / 3)
                    .width(screenWidth - 50.dp)
                    .background(Color.DarkGray)
                    .align(Alignment.CenterHorizontally)
                ){
                    Image(painter = painterResource(id = R.drawable.boiler), contentDescription = null,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
            }

        }
    }

}