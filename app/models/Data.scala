package models

import play.api.db.slick.Config.driver.simple._

trait IdRecord {
  var id: Option[Int]
}

abstract class IdTable[T <: IdRecord](tag: Tag, name: String) extends Table[T](tag, name) {
  def id: Column[Int]
}

case class Class(
  var id: Option[Int],
  name: String,
  level: Int,
  yearOfCreation: Int
) extends IdRecord

class ClassTable(tag: Tag) extends IdTable[Class](tag, "klasa") {
  def id = column[Int]("id_klasa", O.PrimaryKey, O AutoInc)
  def name = column[String]("nazwa")
  def level = column[Int]("poziom")
  def yearOfCreation = column[Int]("rok_utworzenia")

  def * = (id.?, name, level, yearOfCreation) <>(Class.tupled, Class.unapply _)
}


case class Subject(
  var id: Option[Int],
  name: String
) extends IdRecord

class SubjectTable(tag: Tag) extends IdTable[Subject](tag, "przedmiot") {
  def id = column[Int]("id_przedmiot", O.PrimaryKey, O AutoInc)
  def name = column[String]("nazwa")

  def * = (id.?, name) <>(Subject.tupled, Subject.unapply _)
}

/*
trait PostgresGeneric[T <: IdTable[A], A] extends Controller {

  val tableReference: TableQuery[T]

  def insert(row: T#TableElementType)(implicit s: Session) =
    tableReference.insert(row)

  def deleteById(id: Long)(implicit s: Session): Boolean =
    tableReference.filter(_.id === id).delete == 1

  def updateById(id: Long, row: T#TableElementType)(implicit s: Session): Boolean =
    tableReference.filter(_.id === id).update(row) == 1

  def selectById(id: Long)(implicit s: Session): Option[T#TableElementType] =
    tableReference.where(_.id === id).firstOption

  def existsById(id: Long)(implicit s: Session): Boolean = {
    (for {
      row <- tableReference
      if row.id === id
    } yield row).firstOption.isDefined
  }
}*/
