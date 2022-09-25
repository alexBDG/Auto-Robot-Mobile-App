package com.example.autorobot

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.autorobot.databinding.FragmentSecondBinding
import com.faizkhan.mjpegviewer.MjpegView
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.controlwear.virtual.joystick.android.JoystickView
import khttp.post
import kotlin.math.abs

/*
* A simple [Fragment] subclass as the second destination in the navigation.
*/
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private var viewer: MjpegView? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences(
            "ip_url_config",
            Context.MODE_PRIVATE
        )
        val ipNumberFirst = sharedPref?.getInt("ipNumberFirst", 192)
        val ipNumberSecond = sharedPref?.getInt("ipNumberSecond", 168)
        val ipNumberThird = sharedPref?.getInt("ipNumberThird", 1)
        val ipNumberFourth = sharedPref?.getInt("ipNumberFourth", 13)
        val portNumber = sharedPref?.getInt("portNumber", 9000)
        val url = sharedPref?.getString("url", "stream.mjpg")
        // Build this URL (for streaming)
        val streamUrl = "http://${ipNumberFirst}.${ipNumberSecond}.${ipNumberThird}.${ipNumberFourth}:${portNumber}/${url}"
        // Build the command URL
        val cmdUrl = "http://${ipNumberFirst}.${ipNumberSecond}.${ipNumberThird}.${ipNumberFourth}:9500"
//        val cmdUrl = "http://raspberry:9500/"

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        viewer = view.findViewById(R.id.mjpegid)
        viewer!!.isAdjustHeight = true
        viewer!!.mode1 = MjpegView.MODE_FIT_WIDTH
        viewer!!.setUrl(streamUrl)
        viewer!!.isRecycleBitmap1 = true
        viewer!!.startStream()

        val joystick = view.findViewById(R.id.joystickid) as JoystickView
        joystick.setOnMoveListener { angle, strength ->
            // do whatever you want
            binding.speedid.setText("Speed: $strength%")
            binding.angleid.setText("Angle: $angle°")
            postData(angle, strength, cmdUrl)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // when user leaves application
        viewer!!.stopStream()

        // disable
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // disable full screen
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun postData(angle: Int, strength: Int, cmdUrl: String) {
    println("angle: $angle - strength: $strength")

    var vector = arrayOf(0, 0)
    var speedRotation = 0.0
    if (strength>0) {
        when (angle) {
            in 0..90 -> {
                vector = arrayOf(1, -1)
                speedRotation = abs(angle - 90) /90.0
            }
            in 90..180 -> {
                vector = arrayOf(-1, -1)
                speedRotation = abs(angle - 90) /90.0
            }
            in 180..270 -> {
                vector = arrayOf(-1, 1)
                speedRotation = abs(angle - 270) /90.0
            }
            else -> {
                vector = arrayOf(1, 1)
                speedRotation = abs(angle - 270) /90.0
            }
        }
    }
    val values = mapOf(
        "vector" to vector,
        "speedVelocity" to strength/100.0,
        "speedRotation" to speedRotation
    )
    println("values: $values")

    GetMyIP().execute(cmdUrl, values)

}

class  GetMyIP : AsyncTask<Any, Any, Any>()
{

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: Any?) {
        val url = params[0].toString()
        var data = params[1]
        // Make your network call here and return result
        post(url = url, json = data)
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        // The data you have return from doInBackground will be received here.
        // So now you can parse the result.
    }
}
