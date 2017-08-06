package com.ubs.opsit.interviews

/**
  * Creates berlin time by adding rectangular lamps with top two rows
  * indicating hours, two rows at the bottom indicate minutes
  */
class TimeConverterImpl
  extends TimeConverter {

  /**
    * Converts a given string to berlin time format
    *
    * @param aTime required format HH:mm:ss
    * @return time in berlin format
    */
  override def convertTime(aTime: String): String = {
    if (aTime == null || aTime == "") throw new IllegalArgumentException(TimeConverterImpl.INVALID_TIME_ERROR)

    if (!(aTime.matches(TimeConverterImpl.VALID_TIME_FORMAT) || aTime.matches(TimeConverterImpl.VALID_ISO_TIME_FORMAT)))
      throw new IllegalArgumentException(TimeConverterImpl.INVALID_TIME_FORMAT)

    val array = aTime.split(TimeConverterImpl.TIME_SEPARATOR)
    val hours: Int = array(0)
    val minutes: Int = array(1)
    val seconds: Int = array(2)

    TimeConverterImpl.constructTime(hours, minutes, seconds)
  }
}

object TimeConverterImpl {

  private val INVALID_TIME_ERROR = "Invalid input time."
  private val VALID_TIME_FORMAT = "(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)"
  private val VALID_ISO_TIME_FORMAT = "(?:[01]\\d|2[24]):(?:\\d[00]):(?:\\d[00])"
  private val INVALID_TIME_FORMAT = "Invalid input time format."
  private val TIME_SEPARATOR = ":"

  val mod = (x: Int, i: Int) => {
    i % x
  }
  val div = (x: Int, i: Int) => {
    i / x
  }
  val mod2 = mod(2, _: Int)
  val mod5 = mod(5, _: Int)
  val div5 = div(5, _: Int)
  val mod3 = (n: Int) => if (n % 3 == 0) "R" else "Y"

  /**
    * Creates a string representation of berlin time
    *
    * @param hours   hours as an Integer
    * @param minutes minutes as an Integer
    * @param seconds seconds as an Integer
    * @return formatted berlin time
    */
  private[interviews] def constructTime(hours: Int, minutes: Int, seconds: Int) = {
    Array(mod2(seconds) match { case 0 => "Y" case _ => "O" }, (div5(hours), 4, "R") ~> toHours, (mod5(hours), 4, "R") ~> toHours,
      ((div5(minutes), 11, mod3) ~> toMinutes), (mod5(minutes), 4, "Y") ~> toHours).mkString("\n")
  }

  /**
    * Takes occurences and nonoccurences as parameters and pads nonoccurences with Os
    * and occurences with n times of lamp indicator
    *
    * @param occurences number of hrs/mins overlapping or matching with the given hrs/mins
    * @param original   expected number of lamps on each row
    * @param ind        indicator for hrs/min
    * @return constructed string for each row
    */
  private[interviews] def toHours(occurences: Int, original: Int, ind: String): String = {
    (ind ~> occurences) + ("O" ~> (original - occurences))
  }

  /**
    * Takes occurences and nonoccurences as parameters and pads nonoccurences with Os
    * Checks for quarter, half and last quarter of an hour
    *
    *
    * @param occurences number of mins overlapping or matching with the given mins
    * @param original   expected number of lamps on each row
    * @param func   takes a lambda functions as an argument
    * @return constructed string for each row
    */
  private[interviews] def toMinutes(occurences: Int, original: Int, func:(Int => String)): String = {
    val str = (1 to occurences).map(func) mkString("")
    val str1 =   ("O" ~> (original - occurences))
    str + str1
  }
}