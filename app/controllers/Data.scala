package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models._
import play.api.libs.json.Json._
import play.api.libs.json.JsArray
import scala.Some
import models.Subject
import models.Class

object Data extends Controller {
  def years() = DBAction {
    implicit rs =>
      val tableQuery = TableQuery[ClassTable]
      Ok(Json.toJson(tableQuery.map(_.yearOfCreation).list.distinct.sorted.reverse))
  }
}

trait RestController[T <: IdTable[A], A <: IdRecord] extends Controller {
  val singular: String
  val plural: String
  val table: TableQuery[T]
  implicit val format: Format[A]

  val listWrites = new Writes[List[A]] {
    def writes(as: List[A]) = JsArray(as.map(toJson(_)).toSeq)
  }

  def list = DBAction {
    implicit rs =>
      val list = table.list
      Ok(Json.obj(plural -> Json.toJson(list)))
  }

  def single(id: Int) = DBAction {
    implicit rs =>
      val element = table.where(_.id === id).first
      Ok(Json.obj(singular -> Json.toJson(element)))
  }

  def create = DBAction(parse.json) {
    implicit rs =>
      try {
        val newElement = (rs.request.body \ singular).as[A]
        val returnedElement: A = (table returning table) += newElement
        Ok(Json.obj(singular -> Json.toJson(returnedElement)))

      } catch {
        case _: Throwable => BadRequest("Wrong data format")
      }
  }

  def update(id: Int) = DBAction(parse.json) {
    implicit rs =>
      try {
        val newClass = (rs.request.body \ singular).as[A]
        newClass.id = Some(id)
        table.where(_.id === id).update(newClass)
        Ok(Json.obj(singular -> Json.toJson(newClass)))
      } catch {
        case e: Throwable =>
          println(e)
          BadRequest("Wrong data format")
      }
  }

  def delete(id: Int) = DBAction {
    implicit rs =>
      table.where(_.id === id).delete
      Ok("Succeed")
  }

}

object SubjectController extends RestController[SubjectTable, Subject] {
  val singular = "subject"
  val plural = "subjects"
  val table = TableQuery[SubjectTable]
  val format = Json.format[Subject]
}

object ClassController extends RestController[ClassTable, Class] {
  val singular = "class"
  val plural = "classes"
  val table = TableQuery[ClassTable]
  val format = Json.format[Class]
}
