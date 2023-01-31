package com.nexusinfinity.electronicsportal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nexusinfinity.electronicsportal.R
import com.nexusinfinity.electronicsportal.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

}