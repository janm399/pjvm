package net.cakesolutions.pjvm.session2.app

import akka.actor.Actor
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.{Autowire, Autowired, Configurable}
import org.hibernate.criterion.Projections
import collection.JavaConversions
import JavaConversions._

case class ListEntities(clazz: Class[_], firstResult: Int = 0, maxResults: Int = Int.MaxValue)

case class GetEntity(clazz: Class[_], id: Long)

case class CountEntities(clazz: Class[_])

@Configurable
@Transactional
class EntityActor extends Actor {
  @Autowired
  var sessionFactory: SessionFactory = _

  protected def receive = {
    case GetEntity(clazz, id) =>
      // use the sessionFactory to get an entity of the given
      // clazz with the given id
      // return the entity to the sender

      //{
      val entity = sessionFactory.getCurrentSession.get(clazz, id)
      if (entity == null)
        sender ! None
      else
        sender ! Some(entity)
      //}
    case CountEntities(clazz) =>
      // use Criteria API to query for all entities of type clazz
      // reply to sender with the value of scala.Long, representing the number of entities
      // hint: criteria.uniqueResult().asInstanceOf[java.lang.Long].longValue()

      //{
      val criteria = sessionFactory.getCurrentSession.createCriteria(clazz)
      criteria.setProjection(Projections.rowCount())
      val rowCount = criteria.uniqueResult().asInstanceOf[java.lang.Long].longValue()

      sender ! rowCount
      //}
    case ListEntities(clazz, firstResult, maxResults) =>
      // use Criteria API to query for entities of type clazz
      // don't forget to call setFirstResult and setMaxResults
      // reply to sender with the value of scala.List[_], representing the entities
      // hint: criteria.list().toList
      //{
      val criteria = sessionFactory.getCurrentSession.createCriteria(clazz)
      criteria.setFirstResult(firstResult)
      criteria.setMaxResults(maxResults)

      sender ! criteria.list().toList
      //}
  }
}
