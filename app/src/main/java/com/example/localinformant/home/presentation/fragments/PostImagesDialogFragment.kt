package com.example.localinformant.home.presentation.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.localinformant.R
import com.example.localinformant.core.presentation.constants.IntentKeys
import com.example.localinformant.core.presentation.util.getStatusBarHeight
import com.example.localinformant.databinding.FragmentDialogPostImagesBinding
import com.example.localinformant.home.presentation.adapters.PostImagesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostImagesDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogPostImagesBinding? = null
    private val binding get() = _binding!!

    private var imageUrls: List<String> = emptyList()
    private var initialPosition: Int = 0

    override fun onStart() {
        super.onStart()

        val statusBarHeight = getStatusBarHeight(requireContext())
        val dialogHeight = resources.displayMetrics.heightPixels - statusBarHeight

        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dialogHeight
            )

            setGravity(Gravity.BOTTOM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogPostImagesBinding.inflate(inflater, container, false)

        if (arguments != null) {
            imageUrls = arguments?.getStringArrayList(IntentKeys.POST_IMAGES) ?: arrayListOf()
            initialPosition = arguments?.getInt(IntentKeys.IMAGE_POSITION) ?: 0
        }

        setupRecyclerViewPostImages()
        setupClickListeners()

        return binding.root
    }

    private fun setupRecyclerViewPostImages() {
        binding.viewPagerDialogPostImages.adapter = PostImagesAdapter(
            requireContext(),
            imageUrls,
            { _, _ -> }
        )

        binding.viewPagerDialogPostImages.post {
            if (imageUrls.size > 1) {
                binding.viewPagerDialogPostImages.setCurrentItem(initialPosition, false)
                setupDots(binding.layoutDialogDots, imageUrls.size, initialPosition)
            }
        }

        binding.viewPagerDialogPostImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (imageUrls.size > 1) {
                    setupDots(binding.layoutDialogDots, imageUrls.size, position)
                }
            }
        })
    }

    private fun setupDots(dotsLayout: LinearLayout, postImagesSize: Int, activeIndex: Int) {
        dotsLayout.removeAllViews()
        val dots: Array<ImageView?> = arrayOfNulls(postImagesSize)

        for (i in 0 until postImagesSize) {
            dots[i] = ImageView(context).apply {
                val params = LinearLayout.LayoutParams(16, 16).also {
                    it.setMargins(8, 0, 8, 0)
                }

                layoutParams = params

                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        if (i == activeIndex) R.drawable.dot_active
                        else R.drawable.dot_inactive
                    )
                )
            }

            dotsLayout.addView(dots[i])
        }
    }

    private fun setupClickListeners() {
        binding.ivCloseDialogPostImages.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.FullScreenDialog

    companion object {
        fun newInstance(
            imageUrls: List<String>,
            initialPosition: Int
        ): PostImagesDialogFragment {
            return PostImagesDialogFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(IntentKeys.POST_IMAGES, ArrayList(imageUrls))
                    putInt(IntentKeys.IMAGE_POSITION, initialPosition)
                }
            }
        }
    }
}