package ru.sodajl.chordconsonanceanalyzer.osc.factory

enum class OscWaveForms(val oscillatorType: String) {

	SINE("SineOscillator"),
	TRIANGLE("TriangleOscillator"),
	SAWTOOTH("SawtoothOscillator"),
	SQUARE("SquareOscillator"),
	IMPULSE("ImpulseOscillator")
}