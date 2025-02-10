package com.example.agora.screens.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.agora.databinding.FragmentCommentsBinding

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val commentsViewModel = ViewModelProvider(this).get(CommentsViewModel::class.java)

        _binding = FragmentCommentsBinding.inflate(inflater, container, false)

        commentsViewModel.text.observe(viewLifecycleOwner) {
            binding.textComments.text = it
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
