package com.ubs.opsit.interviews.support

import java.io.{File, IOException}
import java.util

import org.apache.commons.io.FileUtils.listFiles
import org.apache.commons.io.filefilter.{DirectoryFileFilter, WildcardFileFilter}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._


/**
  * A class to help us find stories (files) across a classpath with many roots.  This is especially important
  * when finding files when executed from a Gradle test context.
  */
object ClasspathStoryFinder {
  private val LOG = LoggerFactory.getLogger(ClasspathStoryFinder.getClass)

  def findFilenamesThatMatch(aFilenameWithWildcards: String): util.List[String] = {
    val filenames = new util.ArrayList[String]

    for (file <- findFilesThatMatch(aFilenameWithWildcards)) {
      filenames.add(file.toURI.toString)
    }
    filenames
  }

  private def findFilesThatMatch(aFilenameWithWildcards: String) = {
    val regexFileFilter = new WildcardFileFilter(aFilenameWithWildcards)
    val rootDirsToSearchFrom = getRootDirs
    LOG.info(s"Searching for stories called [${aFilenameWithWildcards}] in [${rootDirsToSearchFrom}]")
    val ret = new util.ArrayList[File]
    for (f <- rootDirsToSearchFrom) {
      ret.addAll(listFiles(f, regexFileFilter, DirectoryFileFilter.DIRECTORY))
    }
    ret
  }

  private def getRootDirs = {
    val ret = new util.ArrayList[File]
    try {
      val roots = ClasspathStoryFinder.getClass.getClassLoader.getResources("")
      while ( {
        roots.hasMoreElements
      }) ret.add(new File(roots.nextElement.getFile))
    } catch {
      case ioe: IOException =>
        LOG.error("Failed to derive classpath from Class Loader", ioe)
    }
    ret
  }
}
