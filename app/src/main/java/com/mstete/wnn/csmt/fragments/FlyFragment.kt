package com.mstete.wnn.csmt.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mstete.wnn.csmt.R
import com.mstete.wnn.csmt.databinding.FragmentFlyBinding
import java.io.File
import java.io.IOException

class FlyFragment : Fragment(R.layout.fragment_fly) {

    private lateinit var binding: FragmentFlyBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var link = ""
    private var isLinkRewrote = true

    private var mCameraPhotoPath: String? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFlyBinding.bind(view)

        sharedPreferences =
            requireActivity().getSharedPreferences("shPref", AppCompatActivity.MODE_PRIVATE)

        link = sharedPreferences.getString("galaxyName", "").toString()
        isLinkRewrote = sharedPreferences.getBoolean("isGalaxyNameChanged", true)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setSettings()
        initResultLauncher()

        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest
            ): Boolean {
                return rewriteLink(view!!, request.url.toString())
            }

            private fun rewriteLink(view: WebView, url: String): Boolean {
                return if (url.startsWith("mailto:")) {
                    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                    true
                } else if (url.startsWith("tg:") || url.startsWith("https://t.me")
                    || url.startsWith("https://telegram.me")
                ) {
                    try {
                        view.context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(view.hitTestResult.extra)
                            )
                        )
                    } catch (_: Exception) {
                    }
                    true
                } else {
                    false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (isLinkRewrote) {
                    if (url != null) {
                        sharedPreferences.edit().putString("galaxyName", url).apply()
                    }
                    sharedPreferences.edit().putBoolean("isGalaxyNameChanged", false).apply()
                    CookieManager.getInstance().flush()
                }
                CookieManager.getInstance().flush()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            fun checkPermissions() {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    ), 1
                )
            }

            @SuppressLint("QueryPermissionsNeeded")
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                val permStat = ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if (permStat == PackageManager.PERMISSION_GRANTED) {
                    mFilePathCallback?.onReceiveValue(null)
                    mFilePathCallback = filePathCallback

                    var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent!!.resolveActivity(requireContext().packageManager) != null) {
                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                        } catch (_: IOException) {
                        }

                        if (photoFile != null) {
                            mCameraPhotoPath = "file:" + photoFile.absolutePath
                            takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile)
                            )
                        } else {
                            takePictureIntent = null
                        }
                    }

                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = "image/*"

                    val intentArray: Array<Intent?> =
                        takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

                    resultLauncher.launch(chooserIntent)

                    return true
                } else checkPermissions()
                return false
            }

            @Throws(IOException::class)
            private fun createImageFile(): File? {
                val timeStamp: String = System.currentTimeMillis().toString()
                val imageFileName = "JPEG_" + timeStamp + "_"
                val storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                return File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
                )
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progressBar.isActivated = true
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = newProgress
                if (newProgress == 100) {
                    binding.progressBar.visibility = View.GONE
                    binding.progressBar.isActivated = false
                }
            }
        }
        binding.webView.setDownloadListener { url, _, _, _, _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        binding.webView.loadUrl(link)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Quit")
                        .setMessage("Are you sure?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes") { _, _ ->
                            requireActivity().finish()
                        }
                        .create()
                        .show()
                }
            }
        })
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    var results: Array<Uri>? = null
                    if (data == null) {
                        if (mCameraPhotoPath != null) {
                            results = arrayOf(Uri.parse(mCameraPhotoPath))
                        }
                    } else {
                        val dataString: String? = data.dataString
                        if (dataString != null) {
                            results = arrayOf(Uri.parse(dataString))
                        }
                    }
                    mFilePathCallback!!.onReceiveValue(results)
                    mFilePathCallback = null
                }
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSettings() {
        binding.webView.apply {
            settings.apply {
                userAgentString = binding.webView.settings.userAgentString
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(false)
                allowFileAccess = true
                allowContentAccess = true
                loadWithOverviewMode = true
                useWideViewPort = true
                javaScriptCanOpenWindowsAutomatically = true
            }
        }
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.requestFocus(View.FOCUS_DOWN)
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.acceptCookie()
        cookieManager.setAcceptThirdPartyCookies(binding.webView, true)
        cookieManager.flush()
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onResume() {
        super.onResume()
        CookieManager.getInstance().flush()
    }
}