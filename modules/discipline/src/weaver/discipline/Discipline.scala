package weaver
package discipline

import scala.util.control.NoStackTrace

import org.scalacheck.Prop.Arg
import org.scalacheck.Test
import org.scalacheck.Test.{Exhausted, Failed, Passed, PropException, Proved}
import org.scalacheck.util.Pretty
import org.typelevel.discipline.Laws

trait Discipline { self: FunSuiteAux =>

  import Expectations.Helpers._
  import Discipline._

  def checkAll(name: String, ruleSet: Laws#RuleSet): Unit =
    ruleSet.all.properties.toList.foreach {
      case (id, prop) =>
        test(s"$name: $id") {
          Test.check(prop)(identity).status match {
            case Passed | Proved(_) => success
            case Exhausted          => failure("Property exhausted")
            case Failed(input, _) =>
              failure(s"Property violated \n" + printArgs(input))
            case PropException(input, cause, _) =>
              throw DisciplineException(input, cause)
          }
        }
    }
}

object Discipline {

  private[discipline] case class DisciplineException(
      input: List[Arg[Any]],
      cause: Throwable)
      extends Exception(cause)
      with NoStackTrace {
    override def getMessage() =
      "Property failed with an exception\n" + printArgs(input)
  }

  private def printArgs(args: Seq[Arg[Any]]) =
    args.zipWithIndex.map { case (arg, idx) =>
      s"ARG $idx: " + arg.prettyArg(Pretty.defaultParams)
    }.mkString("\n")
}
