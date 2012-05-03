package net.cakesolutions.pjvm.session2.app

import akka.actor.Actor
import org.springframework.transaction.annotation.Transactional
import org.hibernate.SessionFactory
import net.cakesolutions.pjvm.session2.domain.User
import org.springframework.beans.factory.annotation.{Autowired, Configurable}
import net.cakesolutions.pjvm.session2.importer.UserReader
import scalaz.effects._
import scalaz.Scalaz
import Scalaz._
import java.io._

case class Generate(count: Int)

case class Import(source: String)

@Configurable
@Transactional
class UserActor extends Actor {
  @Autowired
  var sessionFactory: SessionFactory = _

  protected def receive = {
    case Generate(count) =>
      // I will not insult your intelligence with hints: generate count number of Users
      for (i <- 1 to count) {
        //{
        val user = new User
        user.setFirstName("First " + i)
        user.setLastName("Last " + i)
        user.setUsername("user" + i)

        sessionFactory.getCurrentSession.saveOrUpdate(user)
        //}
      }
    case Import(source) =>
      val reader = new UserReader
      // saveOrUpdate all users we get from calling reader.read(input)
      //{
      val importOperation =
        for {
          input <- bufferFile(new File(source))
          operation <- io { reader.read(input) } >>= { users => io ( users.foreach(sessionFactory.getCurrentSession.saveOrUpdate(_)) ) }
          _ <- closeReader(input)
        } yield operation

      importOperation.unsafePerformIO
      //}
  }
}
