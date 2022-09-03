package com.example.autorobot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.autorobot.databinding.FragmentSecondBinding
import com.faizkhan.mjpegviewer.MjpegView

import android.content.Intent

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

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        viewer = view.findViewById<MjpegView>(R.id.mjpegid)
        viewer!!.isAdjustHeight = true
        viewer!!.mode1 = MjpegView.MODE_FIT_WIDTH
        viewer!!.setUrl(
            "${ipNumberFirst}.${ipNumberSecond}.${ipNumberThird}.${ipNumberFourth}:" +
            "${portNumber}/${url}"
        )
        viewer!!.isRecycleBitmap1 = true
        viewer!!.startStream()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        //when user leaves application
        viewer!!.stopStream();
    }
}
