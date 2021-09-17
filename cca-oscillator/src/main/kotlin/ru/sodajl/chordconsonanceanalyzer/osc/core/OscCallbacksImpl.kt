package ru.sodajl.chordconsonanceanalyzer.osc.core

class OscCallbacksImpl(
	private val oscData: OscData
) : OscCallbacks {

	override fun onStart() {

	}

	override fun onStop() {
		oscData.resetFiltersToInitialState()
	}
}