package ru.sodajl.chordconsonanceanalyzer.osc.core

interface OscFunctions {

	fun performStart()

	fun performStartFor(seconds: Long)

	fun performStop()

	fun getLifecycleState(): String
}