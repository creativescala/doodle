import doodle.core._
import doodle.jvm.{draw, animate}

object Main extends App {
  val name = {
    val arg = args.headOption getOrElse "RainbowSierpinski"
    if(arg.indexOf('.') < 0) s"doodle.examples.$arg" else arg
  }

  val success =
    process[Image](name, draw) ||
    process[Animation](name, animate)

  if(!success) {
    println(s"$name was neither an Image or an Animation")
  }

  def process[T](name: String, handler: T => Unit): Boolean =
    try {
      handler(Class.forName(name + "$").getField("MODULE$").get(null).asInstanceOf[T])
      true
    } catch {
      case exn: java.lang.ClassNotFoundException => println(exn); false
      case exn: java.lang.ClassCastException     => println(exn); false
    }
}
