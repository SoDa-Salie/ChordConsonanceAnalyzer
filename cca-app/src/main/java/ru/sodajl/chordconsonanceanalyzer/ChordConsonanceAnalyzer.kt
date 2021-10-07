package ru.sodajl.chordconsonanceanalyzer

import android.app.Application
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router

class ChordConsonanceAnalyzer : Application() {

	companion object {
		lateinit var INSTANCE: ChordConsonanceAnalyzer
	}

	init {
		INSTANCE = this
	}

	lateinit var cicerone: Cicerone<Router>

	override fun onCreate() {
		super.onCreate()
		INSTANCE = this
		this.initCicerone()
	}

	private fun ChordConsonanceAnalyzer.initCicerone() {
		this.cicerone = Cicerone.create()
	}
}