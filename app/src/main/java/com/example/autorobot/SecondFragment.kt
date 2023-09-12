package com.example.autorobot

//import com.faizkhan.mjpegviewer. //MjpegView

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.autorobot.databinding.FragmentSecondBinding
import com.example.autorobot.mjpegviewer.MjpegView
import io.github.controlwear.virtual.joystick.android.JoystickView
import khttp.get
import khttp.post
import khttp.responses.Response
import kotlin.math.abs


/*
* A simple [Fragment] subclass as the second destination in the navigation.
*/
class SecondFragment : Fragment() {
    private val tag = "SecondFragment"

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
//        activity?.window?.insetsController.hide(WindowInsets.Type.statusBars())
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
        val hostName = "${ipNumberFirst}.${ipNumberSecond}.${ipNumberThird}.${ipNumberFourth}"
        val indexUrl = "http://${hostName}:${portNumber}/index.html"
        val streamUrl = "http://${hostName}:${portNumber}/${url}"
        // Build the command URL
        val cmdUrl = "http://${hostName}:9500"

        val testUrl = AsyncHTTPTest(streamUrl, indexUrl)
        viewer = view.findViewById(R.id.mjpegid)
        testUrl.execute(viewer)

        binding.buttonSecond.setOnClickListener {
            println("[INFO] stopStream $viewer")
            viewer?.stopStream()
            println("[INFO] stopStream $viewer")
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

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
//        streamer!!.cancel(true)

        // disable
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // disable full screen
//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}


fun postData(angle: Int, strength: Int, cmdUrl: String) {
    val tag = "postData"
    Log.v(tag, "angle: $angle - strength: $strength")

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
    Log.v(tag, "values: $values")

    AsyncHTTP().execute(cmdUrl, values)

}


class  AsyncHTTP : AsyncTask<Any, Any, Any>()
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


class AsyncHTTPTest(streamUrl: String, indexUrl: String) : AsyncTask<MjpegView, Any, Response>()
{
    private val tag = "AsyncHTTPTest"
    private var isRunning = false
    var streamUrl = streamUrl
    var indexUrl = indexUrl

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: MjpegView): Response? {
        var viewer: MjpegView? = p0[0]
        // Make your network call here and return result
        val response : Response = get(url = this.indexUrl)
        val statusCode = response.statusCode
        if (!isRunning) {
            if (statusCode == 200) {
                viewer!!.isAdjustHeight = true
                viewer!!.mode1 = MjpegView.MODE_FIT_WIDTH
                viewer!!.setUrl(this.streamUrl)
                viewer!!.isRecycleBitmap1 = true
                println("[INFO] doInBackground startStream")
                viewer!!.startStream()
                isRunning = true
            } else {
                Log.e(tag, "no stream to handle! statusCode: $statusCode")
            }
        } else {
            Log.e(tag, "already running")
        }
        return response
    }

    override fun onPostExecute(result: Response) {
        super.onPostExecute(result)
        val statusCode = result.statusCode
        Log.v(tag, "statusCode $statusCode")
    }
}
