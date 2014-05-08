package models

import play.api.db.slick.Config.driver.simple._

case class Class(var id: Option[Int], name: String, level: Int, yearOfCreation: Int)

class ClassTable(tag: Tag) extends Table[Class](tag, "klasa") {
  def id = column[Int]("id_klasa", O.PrimaryKey, O AutoInc)
  def name = column[String]("nazwa")
  def level = column[Int]("poziom")
  def yearOfCreation = column[Int]("rok_utworzenia")

  def * = (id.?, name, level, yearOfCreation) <> (Class.tupled, Class.unapply _)
}
