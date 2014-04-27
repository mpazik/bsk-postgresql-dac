package models

import play.api.db.slick.Config.driver.simple._

case class Klasa(id: Int, name: String, yearOfCreation: Int)

class KlasaTable(tag: Tag) extends Table[Klasa](tag, "klasa") {
  def id = column[Int]("id_klasa", O.PrimaryKey)
  def name = column[String]("nazwa")
  def yearOfCreation = column[Int]("rok_utworzenia")

  def * = (id, name, yearOfCreation) <> (Klasa.tupled, Klasa.unapply _)
}
