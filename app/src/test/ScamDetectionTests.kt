package test

import org.junit.Test
import org.junit.Assert.*
import com.dlrp.app.detection.KeywordDetector
import com.dlrp.app.detection.UrgencyDetector

class ScamDetectionTests {

    @Test
    fun testKeywordDetection() {
        val detector = KeywordDetector()
        val matches = detector.findMatches("Congratulations! You won a prize.")
        assertTrue(matches.contains("won"))
        assertTrue(matches.contains("prize"))
    }

    @Test
    fun testUrgencyDetection() {
        val detector = UrgencyDetector()
        assertTrue(detector.hasUrgency("Act now before it expires"))
    }
}
