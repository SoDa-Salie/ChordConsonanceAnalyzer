package ru.sodajl.chordconsonanceanalyzer.osc.core

import com.jsyn.Synthesizer
import com.jsyn.unitgen.LineOut
import ru.sodajl.chordconsonanceanalyzer.osc.factory.OscLifecycleStates
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class OscFunctionsImpl(
	private val synthesizer: Synthesizer,
	private val lineOut: LineOut,
	private val oscCallbacks: OscCallbacks
) : OscFunctions {

	private var oscLifecycleState: OscLifecycleStates = OscLifecycleStates.STOPPED
	private val es: ExecutorService = Executors.newFixedThreadPool(2)
	private val logger = Logger.getLogger(OscFunctionsImpl::class.java.getName())

	override fun performStart() {
		if (getLifecycleState() == "STOPPED") {
			synthesizer.start()
			lineOut.start()
			oscLifecycleState = OscLifecycleStates.STARTED
			oscCallbacks.onStart()
		}
	}

	override fun performStartFor(seconds: Long) {
		if (getLifecycleState() == "STOPPED") {
			es.submit {
				synthesizer.start()
				lineOut.start()
				oscCallbacks.onStart()
				oscLifecycleState = OscLifecycleStates.STARTED
				val time = System.currentTimeMillis()
				logger.info(time.toString())
				while (Math.abs(time - System.currentTimeMillis()) < seconds * 1000) {
					TimeUnit.MILLISECONDS.sleep(100)
					logger.info(Math.abs(time - System.currentTimeMillis()).toString())
					if (getLifecycleState() == "STOPPED") return@submit
				}
				performStop()
			}
		}
	}

	override fun performStop() {
		if (getLifecycleState() == "STARTED") {
			oscLifecycleState = OscLifecycleStates.STOPPED
			synthesizer.stop()
			lineOut.stop()
			oscCallbacks.onStop()
		}
	}

	override fun getLifecycleState(): String = oscLifecycleState.name
}