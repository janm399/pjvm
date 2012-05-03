package net.cakesolutions.pjvm.session2.api

import cc.spray.typeconversion
import cc.spray.http.{HttpContent, ContentType, MediaTypes}
import MediaTypes._
import typeconversion._
import net.cakesolutions.pjvm.session2.domain.User

trait CustomMarshallers {

  implicit object LongMarshaller extends SimpleMarshaller[Long] {
    val canMarshalTo = ContentType(`text/plain`) :: Nil

    def marshal(value: Long, contentType: ContentType) = contentType match {
      case x@ContentType(`text/plain`, _) => HttpContent(x, value.toString)
      case _ => throw new IllegalArgumentException
    }
  }

  implicit object UserMarshaller extends SimpleMarshaller[User] {
    val canMarshalTo = ContentType(`text/plain`) :: Nil

    def marshal(value: User, contentType: ContentType) = contentType match {
      case x@ContentType(`text/plain`, _) => HttpContent(x, value.toString)
      case _ => throw new IllegalArgumentException
    }
  }

  implicit val userMarhaller = new ListMarshaller[User]

  class ListMarshaller[T](implicit m: Marshaller[T]) extends SimpleMarshaller[List[T]] {
    val canMarshalTo = ContentType(`text/plain`) :: Nil

    def marshal(value: List[T], contentType: ContentType) = contentType match {
      case x@ContentType(`text/plain`, _) => {
        val sb = new StringBuilder()
        value.foreach {
          e =>
            if (sb.length > 0) sb.append(',')
            sb.append(e.toString)
        }
        sb.insert(0, """{"aaData":[ """);
        sb.append("""]}""")
        HttpContent(x, sb.toString())
      }
      case _ => throw new IllegalArgumentException
    }
  }

}
