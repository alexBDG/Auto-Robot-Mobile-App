package com.example.autorobot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.autorobot.databinding.FragmentThirdBinding


/*
* A simple [Fragment] subclass as the third destination in the navigation.
*/
class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentThirdBinding.inflate(inflater, container, false)
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

        binding.ipNumberFirst.setText(ipNumberFirst.toString())
        binding.ipNumberSecond.setText(ipNumberSecond.toString())
        binding.ipNumberThird.setText(ipNumberThird.toString())
        binding.ipNumberFourth.setText(ipNumberFourth.toString())
        binding.portNumber.setText(portNumber.toString())
        binding.url.setText(url)

        binding.buttonThird.setOnClickListener {
            findNavController().navigate(R.id.action_ThirdFragment_to_FirstFragment)

            val editor = sharedPref?.edit()
            editor?.putInt("ipNumberFirst", binding.ipNumberFirst.text.toString().toInt())
            editor?.putInt("ipNumberSecond", binding.ipNumberSecond.text.toString().toInt())
            editor?.putInt("ipNumberThird", binding.ipNumberThird.text.toString().toInt())
            editor?.putInt("ipNumberFourth", binding.ipNumberFourth.text.toString().toInt())
            editor?.putInt("portNumber", binding.portNumber.text.toString().toInt())
            editor?.putString("url", binding.url.text.toString())
            editor?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
