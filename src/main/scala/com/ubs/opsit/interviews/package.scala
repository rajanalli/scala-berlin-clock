package com.ubs.opsit

import scala.collection.immutable.StringOps

/**
  * Contains implicits and other common methods.
  */
package object interviews {

  implicit def string2Int(str: String): Int = Integer.valueOf(str).toInt

  implicit class RowWrapper[A, B, C](f: (A, B, C)) {
    def ~>[T1](fn: (A, B, C) => T1) = fn(f._1, f._2, f._3)
  }

  implicit class StringWrapper(str: String) {
    def ~>(n: Int) = new StringOps(str) * n
  }
}

