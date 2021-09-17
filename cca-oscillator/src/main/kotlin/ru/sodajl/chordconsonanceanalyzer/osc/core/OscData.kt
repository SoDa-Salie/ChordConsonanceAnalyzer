package ru.sodajl.chordconsonanceanalyzer.osc.core

interface OscData {

	fun setFrequency(frequency: Double)

	fun getFrequency(): Double

	fun setDuration(duration: Double)

	fun getDuration(): Double

	fun resetFiltersToInitialState()

	fun isHasFinitePlayback(): Boolean
}