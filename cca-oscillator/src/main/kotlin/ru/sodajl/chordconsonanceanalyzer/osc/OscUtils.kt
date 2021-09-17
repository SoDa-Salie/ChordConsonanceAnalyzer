package ru.sodajl.chordconsonanceanalyzer.osc

fun calcAmpForHarmonics(harmonicIndex: Int): Double {
	if (harmonicIndex <= 0) throw ArithmeticException("harmonicIndex should be positive!")
	val ampDivisor = if (harmonicIndex == 1) 1 else (harmonicIndex * 5)
	return 1.0 / ampDivisor
}