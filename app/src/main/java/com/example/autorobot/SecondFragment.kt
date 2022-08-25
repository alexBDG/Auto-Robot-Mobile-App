package com.example.autorobot

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
//    view = findViewById(R.id.mjpegid)
//    view!!.isAdjustHeight = true
//    view!!.mode1 = MjpegView.MODE_FIT_WIDTH
//    view!!.setUrl("http://192.168.0.1")
//    view!!.isRecycleBitmap1 = true
//    view!!.startStream()
////when user leaves application
//    viewer!!.stopStream();

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

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        viewer = view.findViewById<MjpegView>(R.id.mjpegid)
        viewer!!.isAdjustHeight = true
        viewer!!.mode1 = MjpegView.MODE_FIT_WIDTH
        viewer!!.setUrl("http://192.168.1.14:9000/stream.mjpg")
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
