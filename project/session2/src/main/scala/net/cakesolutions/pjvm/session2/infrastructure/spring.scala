package net.cakesolutions.pjvm.session2.infrastructure

import org.springframework.context.support.GenericXmlApplicationContext
import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Props, OneForOneStrategy, Actor}
import net.cakesolutions.pjvm.session2.{Started, Stop, Start}

case class GetBean(clazz: Class[_], beanName: Option[String] = None)

class SpringContextActor extends Actor {

  override def supervisorStrategy() = OneForOneStrategy() {
    case _ => Resume
  }

  protected def receive = {
    case Start() =>
      // Instantiate GenericXmlApplicationContext("classpath*:/META-INF/spring/module-context.xml")
      // Use it to create the BeanLookupActor, under name "beanLookup"
      // Reply Started() to the sender when all is done
      //{
      val applicationContext = new GenericXmlApplicationContext("classpath*:/META-INF/spring/module-context.xml")
      context.actorOf(
        props = Props(new BeanLookupActor(applicationContext)),
        name = "beanLookup")

      //}

      sender ! Started()
    case Stop() =>
      // send the Stop() message to every child
      //{
      context.children.foreach(_ ! Stop())
      //}
  }
}

class BeanLookupActor(applicationContext: GenericXmlApplicationContext) extends Actor {

  protected def receive = {
    case GetBean(clazz, beanName) =>
      // clazz is Class[_]
      // beanName is Option[String]

      // * if beanName is Some(name), lookup the bean by its type and name
      // * if beanName is None,       lookup the bean by its type only
      // hint: pattern match on the beanName

      //{
      beanName match {
        case None => sender ! applicationContext.getBean(clazz)
        case Some(name) => sender ! applicationContext.getBean(name, clazz)
      }
      //}

    case Stop() =>
      applicationContext.close()
  }

}
