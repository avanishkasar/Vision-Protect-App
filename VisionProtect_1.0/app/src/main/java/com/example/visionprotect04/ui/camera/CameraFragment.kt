package com.example.visionprotect04.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.visionprotect04.R
import com.example.visionprotect04.databinding.FragmentCameraBinding
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.toggleProtectionButton.setOnClickListener {
            toggleProtection()
        }

        binding.menuButton.setOnClickListener { view ->
            showMenu(view)
        }
    }

    private fun showMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.camera_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_tutorial -> {
                    findNavController().navigate(R.id.tutorialFragment)
                    true
                }
                R.id.action_social -> {
                    findNavController().navigate(R.id.socialFragment)
                    true
                }
                R.id.action_about -> {
                    findNavController().navigate(R.id.aboutFragment)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun startCamera() {
        // TODO: Implement camera preview
    }

    private fun toggleProtection() {
        val isProtectionActive = binding.blurOverlay.visibility == View.VISIBLE
        binding.blurOverlay.visibility = if (isProtectionActive) View.GONE else View.VISIBLE
        binding.toggleProtectionButton.text = if (isProtectionActive) "Start Protection" else "Stop Protection"
        binding.statusText.text = if (isProtectionActive) "Protection Inactive" else "Protection Active"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 