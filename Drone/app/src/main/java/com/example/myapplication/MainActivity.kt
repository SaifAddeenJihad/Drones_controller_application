package com.example.myapplication;

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.R
import com.example.mydrone.CircleCanvas
import java.lang.Math.toRadians
import java.util.Random
import kotlin.math.*

class MainActivity : AppCompatActivity() {
    private val menuItems = listOf("Drone1", "Drone2", "Drone3", "Drone4")
    private lateinit var seekBarO: SeekBar
    private lateinit var textR: TextView
    private lateinit var seekBarR: SeekBar
    private lateinit var textO: TextView
    private lateinit var temperature: TextView
    private lateinit var layout: ConstraintLayout
    private lateinit var imageView1:ImageView
    private lateinit var imageView2:ImageView
    private lateinit var imageView3:ImageView
    private lateinit var imageView4:ImageView
    private var r :Float = 0.0f
    private var o :Float = 0.0f

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dropdownMenu: Spinner = findViewById(R.id.spinner)
        seekBarO = findViewById(R.id.BarO)
        seekBarR = findViewById(R.id.BarR)
        textO = findViewById(R.id.textO)
        textR = findViewById(R.id.textR)
        temperature = findViewById(R.id.temperature)
        imageView1 = findViewById(R.id.imageDrone1)
        imageView2 = findViewById(R.id.imageDrone2)
        imageView3 = findViewById(R.id.imageDrone3)
        imageView4 = findViewById(R.id.imageDrone4)
        layout=findViewById(R.id.layout)
        var drone = 1
        textO.text = "O$drone\n0"
        textR.text = "R$drone\n0"
        // Create an ArrayAdapter using the data and a default layout for the dropdown menu
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, menuItems)
        // Set the ArrayAdapter to the Spinner
        dropdownMenu.adapter = adapter
        dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                drone = position + 1
                val selectedItem = menuItems[position]
                val temp=getRandomTemp()
                textR.text = "R$drone\n$r"
                textO.text = "O$drone\n$o"
                //receiveTemperature(drone)
                temperature.text = "temp$drone\n$temp"
                Toast.makeText(
                    this@MainActivity,
                    "Selected item: $selectedItem",
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // This method is called when nothing is selected
            }
        }


        seekBarO.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int,fromUser: Boolean) {
                val temp=getRandomTemp()
                temperature.text = "temp$drone\n$temp"
                o =6* progress.toFloat()
                textO.text = "O$drone\n$progress"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(0f==o){
                    return
                }
                if(r!=0f ){
                    val animatorSet=animatedCircles()
                    animatorSet.start()
                }

            }
        })

        seekBarR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val temp=getRandomTemp()
                temperature.text = "temp$drone\n$temp"
                r = p1.toFloat()
                textR.text = "R$drone\n$r"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val animatedLines=animatedPaths()
                animatedLines.start()
            }

        })

    }


    private fun getRandomTemp(): Int {
        val random = Random()
        val minValue = 200
        val maxValue = 900
        return random.nextInt(maxValue - minValue + 1) + minValue
    }

    private fun animatedPaths():AnimatorSet{
        val animatorSet1=angularPath(imageView1,315f,r)
        val animatorSet2=angularPath(imageView2,225f,r)
        val animatorSet3=angularPath(imageView3,135f,r)
        val animatorSet4=angularPath(imageView4,45f,r)
        val animatorSet5=AnimatorSet()
        animatorSet5.playTogether(animatorSet1,animatorSet2,animatorSet3,animatorSet4)
        return animatorSet5
    }

    private fun angularPath(image:ImageView ,angle:Float,distance:Float):AnimatorSet {
        val angleInRadians = toRadians(angle.toDouble())
        val xComponent = (distance * cos(angleInRadians)).toFloat()
        val yComponent = (distance * sin(angleInRadians)).toFloat()
        val animatorX = ObjectAnimator.ofFloat(image, "translationX", xComponent)
        val animatorY = ObjectAnimator.ofFloat(image, "translationY", yComponent)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorX, animatorY)
        animatorSet.duration = 500 // Set the duration of the animation in milliseconds
        return animatorSet

    }


    private fun animatedCircles():AnimatorSet{
        val animator1=startCircularPathAnimation(imageView1,o,imageView1.x,imageView1.y,Path.Direction.CW)
        val animator2=startCircularPathAnimation(imageView2,o,imageView2.x,imageView2.y,Path.Direction.CW)
        val animator3 =startCircularPathAnimation(imageView3,o,imageView3.x,imageView3.y,Path.Direction.CW)
        val animator4 =startCircularPathAnimation(imageView4,o,imageView4.x,imageView4.y,Path.Direction.CW)
        val animatorSet=AnimatorSet()
        animatorSet.playTogether(animator1,animator2,animator3,animator4)
        return animatorSet
    }
    private fun startCircularPathAnimation(imageView:ImageView, radius: Float,
                                           X:Float,Y:Float,
                                           direction:Path.Direction): ObjectAnimator? {

        val centerX= X + (imageView.width/2) - radius
        val path = Path()
        path.addCircle(X- radius, Y,radius,direction)
        val animator=ObjectAnimator.ofFloat(imageView,View.X,View.Y,path)
        animator.duration = 2000

        val circleCanvas= CircleCanvas(this, centerX, Y + (imageView.height/2), radius)
        layout.addView(circleCanvas)
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                // Remove the custom canvas view once animation is finished
                layout.removeView(circleCanvas)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return animator
    }

    /*
        private fun sendROValues(drone: Int, R: Int, O: Int) {
            Thread {
                val serverAddress = "127.0.0.1" // Replace with your server IP address
                val serverPort = 12345 // Replace with your server port number

                val clientSocket = Socket(serverAddress, serverPort)

                // Create input and output streams for sending/receiving data
                val out = PrintWriter(clientSocket.getOutputStream(), true)
                val inStream = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

                // Prepare the data to send to the server
                val dataToSend = "R$drone=$R;O$drone=$O"

                // Send the data to the server
                out.println(dataToSend)

                // Close the streams and the socket
                out.close()
                inStream.close()
                clientSocket.close()
            }.start()
        }
        private fun receiveTemperature(drone: Int) {
            Thread {
                val serverAddress = "127.0.0.1" // Replace with your server IP address
                val serverPort = 12345 // Replace with your server port number

                val clientSocket = Socket(serverAddress, serverPort)

                // Create input and output streams for sending/receiving data
                val out = PrintWriter(clientSocket.getOutputStream(), true)
                val inStream = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

                // Prepare the data to send to the server
                val dataToSend = "temp$drone"

                // Send the data to the server
                out.println(dataToSend)

                // Receive data (temperature) from the server
                val temperatureValue = inStream.readLine()

                // Update UI with the server response
                runOnUiThread {
                    temperature.text = "temp$drone\n$temperatureValue"
                }

                // Close the streams and the socket
                out.close()
                inStream.close()
                clientSocket.close()
            }.start()
        }*/

}


