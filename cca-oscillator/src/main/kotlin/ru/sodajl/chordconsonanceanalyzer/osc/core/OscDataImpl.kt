package ru.sodajl.chordconsonanceanalyzer.osc.core

import com.jsyn.unitgen.ContinuousRamp
import com.jsyn.unitgen.UnitBinaryOperator
import com.jsyn.unitgen.UnitFilter
import com.jsyn.unitgen.UnitOscillator
import ru.sodajl.chordconsonanceanalyzer.osc.calcAmpForHarmonics

class OscDataImpl(
	private val oscillationUnits: List<UnitOscillator>,
	private val filterUnits: List<UnitFilter>,
	private val operatorUnits: List<UnitBinaryOperator>
) : OscData {

	override fun setFrequency(frequency: Double) {
		oscillationUnits.forEachIndexed { index, unitOscillator ->
			unitOscillator.frequency.set(frequency * (index + 1))
		}
	}

	override fun getFrequency(): Double {
		return oscillationUnits.get(0).frequency.get()
	}

	override fun setDuration(duration: Double) {
		filterUnits.forEach { unitFilter ->
			(unitFilter as ContinuousRamp).time.set(duration)
		}
	}

	override fun getDuration(): Double {
		return (filterUnits.get(0) as ContinuousRamp).time.get()
	}

	override fun resetFiltersToInitialState() {
		val previousInputField = Class
			.forName("com.jsyn.unitgen.ContinuousRamp")
			.getDeclaredField("previousInput")
		previousInputField.setAccessible(true)
		filterUnits.forEachIndexed { index, unitFilter ->
			previousInputField.set(unitFilter as ContinuousRamp, Double.MIN_VALUE)
			unitFilter.current.set(calcAmpForHarmonics(index + 1))
		}
	}

	override fun isHasFinitePlayback(): Boolean = filterUnits.get(0) is ContinuousRamp
}