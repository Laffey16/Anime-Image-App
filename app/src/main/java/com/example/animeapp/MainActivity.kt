package com.example.animeapp

import android.Manifest
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.example.animeapp.databinding.ActivityMainBinding
import com.example.animeapp.requests.GelbooruSingleton
import kotlinx.coroutines.*


lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                isReadPermissionGranted =
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: isReadPermissionGranted
                isWritePermissionGranted =
                    permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: isWritePermissionGranted

            }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())

        setFullscreen()
    }


    private fun backgroundAnimation() {
        val animationDrawable: AnimationDrawable = binding.rlLayout.background as AnimationDrawable

        animationDrawable.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(3000)
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        setFullscreen()
    }

    override fun onStart() {
        super.onStart()

        backgroundAnimation()
        makeApiRequest()

        //Pressing refresh button
        binding.refreshButton.setOnClickListener()
        {
            binding.refreshButton.animate().apply {
                rotationBy(360f)
                duration = 1000
            }.start()
            makeApiRequest()
            //Hides Imageview
            binding.ivRandomAnime.visibility = View.GONE
            //Shows the loading screen
            binding.progressBar.visibility = View.VISIBLE
        }

        //If imageview isn't null then hide loading image.
        if (binding.ivRandomAnime.drawable != null) {
            binding.progressBar.visibility = View.GONE
        }

        //If download button clicked then download image.
        binding.downloadButton.setOnClickListener()
        {
            if (binding.ivRandomAnime.drawable != null) {
                ExtraFuncs.downloadImage(applicationContext, binding.ivRandomAnime)
            } else {
                Toast.makeText(
                    applicationContext,
                    "No image to download.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun makeApiRequest() {

        //Checks they're connected to the internet first
        if (!ExtraFuncs.isConnected(context = applicationContext)) {
            Toast.makeText(
                applicationContext,
                "Connection failed: Check internet and try again",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

//        val api: ApiInterface = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiInterface::class.java)

        GlobalScope.launch(Dispatchers.IO)
        {
            try {
                //Sends the request.
                val response = GelbooruSingleton.api.getData("rating:general sort:random score:>=2")
                //Downloads the image to the imageview
                withContext(Dispatchers.Main) {
                    Glide.with(applicationContext)
                        .load(response.post[(0)].file_url)
                        .into(binding.ivRandomAnime)
                    binding.ivRandomAnime.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity.applicationContext,
                    "$e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun setFullscreen() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    }
}