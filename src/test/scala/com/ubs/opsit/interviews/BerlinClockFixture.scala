package com.ubs.opsit.interviews

import com.ubs.opsit.interviews.support.BehaviouralTestEmbedder._
import org.assertj.core.api.Assertions.assertThat
import org.jbehave.core.annotations.{Then, When}
import org.junit.Test

/**
  * run a series of acceptance tests to test the berlin clock functionality.
  */
class BerlinClockFixture {
  private val berlinClock = new TimeConverterImpl()
  private var theTime:String = null

  @Test
  @throws[Exception]
  def berlinClockAcceptanceTests():Unit = {
    aBehaviouralTestRunner.usingStepsFrom(this).withStory("berlin-clock.story").run()
  }

  @When("the time is $time")
  def whenTheTimeIs(time: String):Unit = {
    this.theTime = time
  }

  @Then("the clock should look like $")
  def thenTheClockShouldLookLike(theExpectedBerlinClockOutput: String): Unit = {
    assertThat(berlinClock.convertTime(theTime)).isEqualTo(theExpectedBerlinClockOutput)
  }
}
