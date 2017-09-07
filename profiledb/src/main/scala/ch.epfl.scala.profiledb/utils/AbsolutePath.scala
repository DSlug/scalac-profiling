/*                                                                                                *\
**      _____            __         ______           __                                           **
**     / ___/_________ _/ /___ _   / ____/__  ____  / /____  _____                                **
**     \__ \/ ___/ __ `/ / __ `/  / /   / _ \/ __ \/ __/ _ \/ ___/    Scala Center                **
**    ___/ / /__/ /_/ / / /_/ /  / /___/ /__/ / / / /_/ /__/ /        https://scala.epfl.ch       **
**   /____/\___/\__,_/_/\__,_/   \____/\___/_/ /_/\__/\___/_/         (c) 2017-2018, LAMP/EPFL    **
**                                                                                                **
\*                                                                                                */

// Slight modification of scalameta io utils
package ch.epfl.scala.profiledb.utils

import java.io.File
import java.nio.file.{Files, Path, Paths}

final class AbsolutePath private (val underlying: Path) extends AnyVal {
  def syntax: String = toString
  def structure: String = s"""AbsolutePath("$syntax")"""
  override def toString: String = underlying.toString

  def toRelative(prefix: AbsolutePath): RelativePath =
    RelativePath(prefix.underlying.relativize(underlying))

  def resolve(other: RelativePath): AbsolutePath =
    AbsolutePath(underlying.resolve(other.underlying))(this)
  def resolve(other: String): AbsolutePath = AbsolutePath(underlying.resolve(other))(this)

  def isFile: Boolean = Files.isRegularFile(underlying)
  def isDirectory: Boolean = Files.isDirectory(underlying)
  def readAllBytes: Array[Byte] = Files.readAllBytes(underlying)
}

object AbsolutePath {
  implicit def workingDirectory: AbsolutePath = new AbsolutePath(Paths.get(sys.props("user.dir")))
  def apply(file: File)(implicit cwd: AbsolutePath): AbsolutePath = apply(file.toPath)(cwd)
  def apply(path: String)(implicit cwd: AbsolutePath): AbsolutePath = apply(Paths.get(path))(cwd)
  def apply(path: Path)(implicit cwd: AbsolutePath): AbsolutePath =
    if (path.isAbsolute) new AbsolutePath(path) else cwd.resolve(path.toString)
}
