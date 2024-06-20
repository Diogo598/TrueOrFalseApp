package com.example.trueorfalseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trueorfalseapp.ui.theme.AnimationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay


class Animation:ComponentActivity() {
    var currentlySelected=UnlocksManager.currentlyUnlocked
    var targetColor= Color.Gray
    var endScreenText="You unlocked a new Avatar!"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseTheRightColor()
        Log.d("Animation","Nothing Changed because $currentlySelected")
        setContent {
            AnimationTheme {
                AnimationTheme {
                    AnimatedCircleToBox()
                }
            }
        }
    }
    //C=Grün,UC=Blau,R=Lila,S=GELB
    fun chooseTheRightColor(){
        if (currentlySelected.equals("d")){
            targetColor=Color.Gray
            endScreenText="You got a duplicate :(\nYou will get some coins back"
        }
        if (currentlySelected.equals("c")){
            targetColor=Color.Green
        }
        if (currentlySelected.equals("uc")){
            targetColor=Color.Blue
        }
        if (currentlySelected.equals("r1")){
            targetColor=Color.Magenta
        }
        if(currentlySelected.equals("r2")){
            targetColor=Color.Magenta
            endScreenText="You unlocked a new title !"
        }
        if (currentlySelected.equals("s")){
            targetColor=Color(0xFFDAA520)
            endScreenText="You unlocked something special! Check out your Profile"

        }
    }
    @Composable
    fun AnimatedCircleToBox() {
        var isAnimating by remember { mutableStateOf(false) }
        var currentColor by remember { mutableStateOf(Color.Gray) }
        var colorChanges by remember { mutableStateOf(0) }
        var isCircle by remember { mutableStateOf(true) }
        var targetSize by remember { mutableStateOf(50.dp) }
        var finalAnimationCompleted by remember{ mutableStateOf(false) }
        val context= LocalContext.current



        LaunchedEffect(isAnimating) {
            if (isAnimating) {
                while (colorChanges < 8) {
                    targetSize = if (targetSize == 200.dp) 50.dp else 200.dp
                    delay(1000) // Allow some time for the animation to complete
                    colorChanges++
                    currentColor = when (colorChanges % 4) {
                        0 -> Color.Gray
                        1 -> Color.Green
                        2 -> Color.Blue
                        3 -> Color.Magenta
                        else -> Color.Gray
                    }
                    if (colorChanges==8){
                        isCircle = false
                        Log.d("targetColor","targetColor is $targetColor")
                }
            }
                if(!finalAnimationCompleted) {
                    currentColor = targetColor
                    targetSize = if (targetSize == 200.dp) 50.dp else 200.dp
                    delay(1000)
                    targetSize=200.dp
                    finalAnimationCompleted=true
                }
        }else {
                colorChanges = 0
                currentColor = Color.Gray
                isCircle = true
                targetSize=50.dp
                finalAnimationCompleted=false
            }
        }
        val size by animateDpAsState(
            targetValue=targetSize,
            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow),
            label = "",

        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!isAnimating && !finalAnimationCompleted) {
                Button(
                    onClick = { isAnimating = !isAnimating },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(text = "Start")
                }
            }
            if (finalAnimationCompleted) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = endScreenText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = {
                        val intent= Intent(context,ShopActivity::class.java)
                        startActivity(intent) }) {
                        Text(text = "Awesome!")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(
                            color = currentColor,
                            shape = if (isCircle) CircleShape else RoundedCornerShape(0.dp)
                        )
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AnimatedCircleToBoxPreview() {
        AnimationTheme {
            AnimatedCircleToBox()
        }
    }
}