package com.example.project_android_booking_movietickets.fragment.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_android_booking_movietickets.activity.MovieSearchActivity
import com.example.project_android_booking_movietickets.fragment.food.FoodSearchActivity
import com.example.project_android_booking_movietickets.databinding.FragmentHomeManagementBinding
import com.example.project_android_booking_movietickets.fragment.customer.CustomerSearchActivity
import com.example.project_android_booking_movietickets.fragment.showtime.ShowtimeSearchActivity

class HomeManagementFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentHomeManagementBinding

    interface OnFragmentInteractionListener {
        fun onFragmentChange(fragment: Fragment, title: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using binding
        binding = FragmentHomeManagementBinding.inflate(inflater, container, false)

        // Xử lý click cho nút Movie
        binding.imbMovie.setOnClickListener {
            val intent = Intent(requireContext(), MovieSearchActivity::class.java)
            startActivity(intent)
        }

        // Xử lý click cho nút Food
        binding.imbFood.setOnClickListener {
            val intent = Intent(requireContext(), FoodSearchActivity::class.java)
            startActivity(intent)
        }

        // Xử lý click cho nút Showtime
        binding.imbShowtime.setOnClickListener {
            val intent = Intent(requireContext(), ShowtimeSearchActivity::class.java)
            startActivity(intent)
        }

        // Xử lý click cho nút Customer
        binding.imbCustomer.setOnClickListener {
            val intent = Intent(requireContext(), CustomerSearchActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
