import org.junit.Assert
import org.junit.Test
import ru.sodajl.chordconsonanceanalyzer.osc.calcAmpForHarmonics
import ru.sodajl.chordconsonanceanalyzer.osc.factory.OscCreator

class OscUtilsTest {

	@Test()
	fun `calcAmpForHarmonic() test for 10 value`() {
		val harmonicIndexValue = 10
		val expected = 0.02
		Assert.assertEquals(expected, calcAmpForHarmonics(harmonicIndexValue), 0.0001)
	}

	@Test()
	fun `calcAmpForHarmonic() test for 0 value`() {
		val harmonicIndexValue = 0
		try {
			calcAmpForHarmonics(harmonicIndexValue)
			Assert.fail("Expected ArithmeticException")
		} catch (e: ArithmeticException) {
			Assert.assertEquals("harmonicIndex should be positive!", e.message)
		}
	}

	@Test()
	fun `calcAmpForHarmonic() test for -10 value`() {
		val harmonicIndexValue = -10
		try {
			calcAmpForHarmonics(harmonicIndexValue)
			Assert.fail("Expected ArithmeticException")
		} catch (e: ArithmeticException) {
			Assert.assertEquals("harmonicIndex should be positive!", e.message)
		}
	}
}

