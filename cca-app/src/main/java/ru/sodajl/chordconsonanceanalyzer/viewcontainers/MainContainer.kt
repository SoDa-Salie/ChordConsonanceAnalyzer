package ru.sodajl.chordconsonanceanalyzer.viewcontainers

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import ru.sodajl.chordconsonanceanalyzer.androidaudiodevice.JSynAndroidAudioDevice
import ru.sodajl.chordconsonanceanalyzer.databinding.ActivityMainBinding
import ru.sodajl.chordconsonanceanalyzer.osc.core.OscData
import ru.sodajl.chordconsonanceanalyzer.osc.core.OscFunctions
import ru.sodajl.chordconsonanceanalyzer.osc.factory.OscCreator
import ru.sodajl.chordconsonanceanalyzer.osc.factory.OscPlaybackModes
import ru.sodajl.chordconsonanceanalyzer.osc.factory.OscWaveForms

class MainContainer : Activity() {

	private lateinit var binding: ActivityMainBinding
	val LOG_TAG = "myLogs"
	private lateinit var oscCreatorInfinite: OscCreator
	private lateinit var oscCreatorFinite: OscCreator
	private lateinit var device: JSynAndroidAudioDevice
	private lateinit var playbackMode: OscPlaybackModes



	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		requestWindowFeature(Window.FEATURE_ACTION_BAR)
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

		//ViewBinding как способ вытащить сконфигурированные вьюхи
		binding = ActivityMainBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)

		device = JSynAndroidAudioDevice()
		playbackMode = OscPlaybackModes.INFINITE
		oscCreatorInfinite = OscCreator(
			15,
			OscWaveForms.SINE,
			OscPlaybackModes.INFINITE,
			220.0,
			4.0,
			device
		)

		oscCreatorFinite = OscCreator(
			15,
			OscWaveForms.SINE,
			OscPlaybackModes.FINITE,
			220.0,
			4.0,
			device
		)

		binding.btnPlay.setOnClickListener {
			val oscData: OscData
			val oscFunctions: OscFunctions
			if (playbackMode == OscPlaybackModes.INFINITE) {
				oscData = oscCreatorInfinite.getOscData()
				oscFunctions = oscCreatorInfinite.getOscFunctions()
				oscFunctions.performStart()
			} else {
				oscData = oscCreatorFinite.getOscData()
				oscFunctions = oscCreatorFinite.getOscFunctions()
				oscFunctions.performStartFor(oscData.getDuration().toLong())
			}
			/*binding.btnPlay.isEnabled = oscFunctions.getLifecycleState() == OscLifecycleStates.STOPPED.name*/
			binding.btnPlaybackMode.isEnabled = false
		}

		binding.btnStop.setOnClickListener {
			val oscData: OscData
			val oscFunctions: OscFunctions
			if (playbackMode == OscPlaybackModes.INFINITE) {
				oscData = oscCreatorInfinite.getOscData()
				oscFunctions = oscCreatorInfinite.getOscFunctions()
				oscFunctions.performStop()
			} else {
				oscData = oscCreatorFinite.getOscData()
				oscFunctions = oscCreatorFinite.getOscFunctions()
				oscFunctions.performStop()
			}
			/*binding.btnStop.isEnabled = oscFunctions.getLifecycleState() == OscLifecycleStates.STARTED.name*/
			binding.btnPlaybackMode.isEnabled = true
		}

		binding.btnDecreaseFrequency.setOnClickListener {
			val oscData: OscData
			if (playbackMode == OscPlaybackModes.INFINITE) oscData = oscCreatorInfinite.getOscData()
			else oscData = oscCreatorFinite.getOscData()
			val currentFrequency = oscData.getFrequency()
			oscData.setFrequency(currentFrequency - 5.0)
		}

		binding.btnIncreaseFrequency.setOnClickListener {
			val oscData: OscData
			if (playbackMode == OscPlaybackModes.INFINITE) oscData = oscCreatorInfinite.getOscData()
			else oscData = oscCreatorFinite.getOscData()
			val currentFrequency = oscData.getFrequency()
			oscData.setFrequency(currentFrequency + 5.0)
		}

		binding.btnPlaybackMode.setOnClickListener {
			if(playbackMode == OscPlaybackModes.INFINITE) playbackMode = OscPlaybackModes.FINITE
			else playbackMode = OscPlaybackModes.INFINITE
		}
	}

	override fun onResume() {
		super.onResume()
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
			View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	}

	override fun onStop() {
		super.onStop()
		Log.d(LOG_TAG, "onStop")
		val oscFunctions: OscFunctions
		if (playbackMode == OscPlaybackModes.INFINITE) {
			oscFunctions = oscCreatorInfinite.getOscFunctions()
			oscFunctions.performStop()
		} else {
			oscFunctions = oscCreatorFinite.getOscFunctions()
			oscFunctions.performStop()
		}
	}
}