package ru.sodajl.chordconsonanceanalyzer.osc.factory

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.devices.AudioDeviceManager
import com.jsyn.ports.UnitInputPort
import com.jsyn.unitgen.*
import ru.sodajl.chordconsonanceanalyzer.osc.calcAmpForHarmonics
import ru.sodajl.chordconsonanceanalyzer.osc.core.*
import java.util.*
import java.util.logging.Logger

class OscCreator(
	numberOfHarmonics: Int,
	private val waveForm: OscWaveForms,
	private val playbackDurationMode: OscPlaybackModes,
	frequency: Double,
	duration: Double,
	private val audioDevice: AudioDeviceManager
) {

	private val numberOfHarmonics: Int
	private val frequency: Double
	private val duration: Double

	private lateinit var oscData: OscData
	private lateinit var oscFunctions: OscFunctions
	private lateinit var oscCallbacks: OscCallbacks
	private var synthesizer: Synthesizer
	private var lineOut: LineOut
	private lateinit var oscillationUnits: List<UnitOscillator>
	private lateinit var filterUnits: List<UnitFilter>
	private lateinit var operatorUnits: List<UnitBinaryOperator>

	private val logger = Logger.getLogger(OscCreator::class.java.getName())

	init {
		this.frequency = frequency
		this.duration = duration
		this.numberOfHarmonics = numberOfHarmonics

		if (numberOfHarmonics <= 0) {
			logger.throwing(
				OscCreator::class.java.getName(),
				OscCreator::class.java.methods[0].name,
				throw IllegalArgumentException("Number of harmonics should be positive!"))
		} else if (frequency < 20 || frequency > 18000) {
			logger.throwing(
				OscCreator::class.java.getName(),
				OscCreator::class.java.methods[0].name,
				throw IllegalArgumentException("Value of frequency should be in interval 20 .. 18000!"))
		} else if (duration <= 0) {
			logger.throwing(
				OscCreator::class.java.getName(),
				OscCreator::class.java.methods[0].name,
				throw IllegalArgumentException("Value of duration should be positive!"))
		}

		synthesizer = JSyn.createSynthesizer(audioDevice)
		lineOut = LineOut()
		initOscillationUnits()
		initFilterUnits()
		initOperatorUnits()
		initOscData()
		initOscCallbacks()
		initOscFunctions()
		bindAll()
	}

	private fun initOscillationUnits() {
		val units = fillArrayOfUnitOscillators()
		oscillationUnits = Collections.unmodifiableList(units)
	}

	private fun fillArrayOfUnitOscillators(): List<UnitOscillator> {
		var unitOscillator: UnitOscillator
		val units = ArrayList<UnitOscillator>()
		val cls = Class.forName("com.jsyn.unitgen." + waveForm.oscillatorType)
		for (harmonicIndex in 1..numberOfHarmonics) {
			unitOscillator = (cls.newInstance() as UnitOscillator)
			unitOscillator.frequency.set(frequency * harmonicIndex)
			unitOscillator.amplitude.set(calcAmpForHarmonics(harmonicIndex))
			units.add(unitOscillator)
		}
		return units
	}

	private fun initFilterUnits() {
		val units = fillArrayOfUnitFilters()
		filterUnits = Collections.unmodifiableList(units)
	}

	private fun fillArrayOfUnitFilters(): List<UnitFilter> {
		var unitFilter: UnitFilter
		val units = ArrayList<UnitFilter>()
		if (playbackDurationMode == OscPlaybackModes.FINITE) {
			for (harmonicIndex in 1..numberOfHarmonics) {
				unitFilter = ContinuousRamp()
				unitFilter.current.set(calcAmpForHarmonics(harmonicIndex))
				unitFilter.time.set(duration)
				units.add(unitFilter)
			}
		}
		return units
	}

	private fun initOperatorUnits() {
		operatorUnits = ArrayList<UnitBinaryOperator>()
	}

	private fun initOscData() {
		oscData = OscDataImpl(oscillationUnits, filterUnits, operatorUnits)
	}

	private fun initOscCallbacks() {
		oscCallbacks = OscCallbacksImpl(oscData)
	}

	private fun initOscFunctions() {
		oscFunctions = OscFunctionsImpl(synthesizer, lineOut, oscCallbacks)
	}

	fun getOscData(): OscData = oscData
	fun getOscFunctions(): OscFunctions = oscFunctions

	private fun bindAll() {
		addUnitsToSynthesizer()
		connectUnitsToLineout()
		connectFiltersToUnits()
	}

	private fun addUnitsToSynthesizer() {
		oscillationUnits.forEach { unitOscillator -> synthesizer.add(unitOscillator) }
		filterUnits.forEach { unitFilter -> synthesizer.add(unitFilter) }
		operatorUnits.forEach { unitOperator -> synthesizer.add(unitOperator) }
		synthesizer.add(lineOut)
	}

	private fun connectUnitsToLineout() {
		oscillationUnits.forEach { unitOscillator ->
			unitOscillator.output.connect(0, lineOut.input, 0)
			unitOscillator.output.connect(0, lineOut.input, 1)
		}
	}

	private fun connectFiltersToUnits() {
		if (playbackDurationMode == OscPlaybackModes.FINITE) {
			var inputPort: UnitInputPort
			filterUnits.forEachIndexed { index, unitFilter ->
				inputPort = oscillationUnits.get(index).amplitude
				unitFilter.output.connect(inputPort)
			}
		}
	}
}