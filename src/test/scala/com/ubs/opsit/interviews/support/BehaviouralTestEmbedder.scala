package com.ubs.opsit.interviews.support

import java.text.SimpleDateFormat

import org.assertj.core.api.Assertions.assertThat
import org.jbehave.core.ConfigurableEmbedder
import org.jbehave.core.configuration.{Configuration, MostUsefulConfiguration}
import org.jbehave.core.configuration.scala.ScalaContext
import org.jbehave.core.io.CodeLocations.codeLocationFromClass
import org.jbehave.core.io.LoadFromURL
import org.jbehave.core.reporters.Format.{CONSOLE, HTML}
import org.jbehave.core.reporters.{FilePrintStreamFactory, StoryReporterBuilder}
import org.jbehave.core.steps.{InjectableStepsFactory, ParameterConverters}
import org.jbehave.core.steps.scala.ScalaStepsFactory
import org.slf4j.LoggerFactory


/**
  * A class to fully encapsulates all of the JBehave plumbing behind a builder style API.  The expected use for this would be:
  * {code}aBehaviouralTestRunner().usingStepsFrom(this).withStory("your.story").run(){code}
  *
  */
object BehaviouralTestEmbedder {
  private val LOG = LoggerFactory.getLogger(classOf[BehaviouralTestEmbedder])
  private val BAD_USE_OF_API_MESSAGE = "You are trying to set the steps factory twice ... this is a paradox"

  def aBehaviouralTestRunner = new BehaviouralTestEmbedder

  private[support] class SandboxDateConverter() extends ParameterConverters.DateConverter(new SimpleDateFormat("dd-MM-yyyy")) {
  }

  private[support] class SandboxStoryReporterBuilder() extends StoryReporterBuilder {
    withCodeLocation(codeLocationFromClass(classOf[BehaviouralTestEmbedder.SandboxStoryReporterBuilder]))
    withDefaultFormats
    withFormats(HTML, CONSOLE)
    withFailureTrace(true)
    withPathResolver(new FilePrintStreamFactory.ResolveToSimpleName)
  }

}

final class BehaviouralTestEmbedder extends ConfigurableEmbedder {
  private var wildcardStoryFilename: String = null
  private var stepsFactori: InjectableStepsFactory = null

  @throws[Exception]
  override def run(): Unit = {
    val paths = createStoryPaths
    if (paths == null || paths.isEmpty) throw new IllegalStateException("No story paths found for state machine")
    BehaviouralTestEmbedder.LOG.info(s"Running [ ${this.getClass.getSimpleName}] with spring_stories [${paths}]")
    configuredEmbedder.runStoriesAsPaths(paths)
  }

  override def stepsFactory(): InjectableStepsFactory = {
    assertThat(stepsFactori).isNotNull
    stepsFactori
  }

  override def configuration: Configuration = new MostUsefulConfiguration().useStoryLoader(new LoadFromURL).useParameterConverters(new ParameterConverters().addConverters(new BehaviouralTestEmbedder.SandboxDateConverter)).useStoryReporterBuilder(new BehaviouralTestEmbedder.SandboxStoryReporterBuilder)

  private def createStoryPaths = ClasspathStoryFinder.findFilenamesThatMatch(wildcardStoryFilename)

  def withStory(aWildcardStoryFilename: String): BehaviouralTestEmbedder = {
    wildcardStoryFilename = aWildcardStoryFilename
    this
  }

  def usingStepsFrom(stepsSource: Any*): BehaviouralTestEmbedder = {
    assertThat(stepsFactori).isNull()
    stepsFactori = new ScalaStepsFactory(configuration(), new ScalaContext(stepsSource(0).getClass.getName))
    this
  }
}
