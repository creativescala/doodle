import cats.data._
import cats.implicits._
import laika.ast._
import laika.api.bundle._

object CreativeScalaDirectives extends DirectiveRegistry {

  override val description: String =
    "Directive to work with CreativeScala SVG pictures."

  val divWithId: BlockDirectives.Directive =
    BlockDirectives.create("divWithId") {
      import BlockDirectives.dsl._

      attribute(0)
        .as[String]
        .map { (id) =>
          RawContent(
            NonEmptySet.one("html"),
            s"""<div id="${id}"></div>"""
          )
        }
    }

  // Parameters are id and then JS function to call
  val doodle: BlockDirectives.Directive =
    BlockDirectives.create("doodle") {
      import BlockDirectives.dsl._

      (attribute(0).as[String], attribute(1).as[String], cursor)
        .mapN { (id, js, _) =>
          BlockSequence(
            Seq(
              RawContent(
                NonEmptySet.one("html"),
                s"""<div id="${id}"></div>"""
              ),
              RawContent(
                NonEmptySet.one("html"),
                s"""<script>${js}("${id}")</script>"""
              )
            )
          )
        }
    }

  // Insert a figure (image)
  //
  // Parameters:
  // filename: String. The file name of the image
  // key: String. The name of the reference for this image
  // caption: String. The caption for this image
  val figure: BlockDirectives.Directive =
    BlockDirectives.create("figure") {
      import BlockDirectives.dsl._

      (
        attribute("img").as[String].widen,
        attribute("key").as[String].optional,
        attribute("caption").as[String]
      ).mapN { (img, key, caption) =>
        Paragraph(
          Image(
            Target.parse(img),
            None,
            None,
            Some(caption),
            Some(s"Figure $key: caption")
          )
        )
      }
    }

  val footnote: BlockDirectives.Directive =
    BlockDirectives.create("footnote") {
      import BlockDirectives.dsl._

      (attribute(0).as[String], parsedBody).mapN { (id, body) =>
        Footnote(id, body)
      }
    }

  // Insert a reference to a figure
  //
  // Parameters:
  // key: String. The name of the figure being referred to.
  val fref: SpanDirectives.Directive =
    SpanDirectives.create("fref") {
      import SpanDirectives.dsl._

      (attribute(0).as[String]).map { (key) => Text(s"Figure $key") }
    }
  //
  // Insert a reference to a footnote
  //
  // Parameters:
  // key: String. The name of the footnote being referred to.
  val fnref: SpanDirectives.Directive =
    SpanDirectives.create("fnref") {
      import SpanDirectives.dsl._

      (attribute(0).as[String]).map { (key) => Text(s"Footnote $key") }
    }

  val script: BlockDirectives.Directive =
    BlockDirectives.create("script") {
      import BlockDirectives.dsl._

      (attribute(0).as[String]).map { (js) =>
        RawContent(NonEmptySet.one("html"), s"<script>$js</script>")
      }
    }

  val solution: BlockDirectives.Directive =
    BlockDirectives.create("solution") {
      import BlockDirectives.dsl._

      parsedBody.map { body =>
        Section(Header(5, Text("Solution")), body)
      }
    }

  // Insert a reference to a table
  //
  // Parameters:
  // key: String. The name of the figure being referred to.
  val tref: SpanDirectives.Directive =
    SpanDirectives.create("tref") {
      import SpanDirectives.dsl._

      (attribute(0).as[String]).map { (key) => Text(s"Table $key") }
    }

  val compactNavBar: TemplateDirectives.Directive =
    TemplateDirectives.create("compactNavBar") {
      import TemplateDirectives.dsl._

      val leftArrow = "←"
      val rightArrow = "→"

      cursor.map { cursor =>
        val previous =
          cursor.flattenedSiblings.previousDocument
            .map(c => SpanLink(Seq(Text(leftArrow)), InternalTarget(c.path)))
            .getOrElse(Text(""))
        val next = cursor.flattenedSiblings.nextDocument
          .map(c => SpanLink(Seq(Text(rightArrow)), InternalTarget(c.path)))
          .getOrElse(Text(""))
        val here = cursor.root.target.title
          .map(title => SpanLink(Seq(title), InternalTarget(Path.Root)))
          .getOrElse(Text(""))

        TemplateElement(
          BlockSequence(
            Seq(Paragraph(previous), Paragraph(here), Paragraph(next))
          )
        )
      }
    }

  val nextPage: TemplateDirectives.Directive =
    TemplateDirectives.create("nextPage") {
      import TemplateDirectives.dsl._

      val rightArrow = "→"

      cursor.map { cursor =>
        val next = cursor.flattenedSiblings.nextDocument

        val title = next.flatMap(c => c.target.title)
        val path = next.map(c => c.path)

        val link =
          (title, path).mapN { (t, p) =>
            Paragraph(
              SpanLink(Seq(t, Text(rightArrow)), InternalTarget(p))
            ).withStyle("nextPage")
          }

        TemplateElement(link.getOrElse(Text("")))
      }
    }

  val spanDirectives = Seq(fref, fnref, tref)
  val blockDirectives =
    Seq(divWithId, doodle, figure, footnote, script, solution)
  val templateDirectives = Seq(compactNavBar, nextPage)
  val linkDirectives = Seq()
}
