package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models.{Class, ClassTable}

object Data extends Controller {
  val classTable = TableQuery[ClassTable]
  implicit val classFormat = Json.format[Class]

  def years() = DBAction {
    implicit rs =>
      Ok(Json.toJson(classTable.map(_.yearOfCreation).list.distinct.sorted.reverse))
  }

  def classes() = DBAction {
    implicit rs =>
      Ok(Json.obj("classes" -> classTable.list))
  }

  def oneClass(id: Int) = DBAction {
    implicit rs =>
      Ok(Json.obj(
        "class" -> Json.toJson(classTable.where(_.id === id).first)
      ))
  }

  def addClass() = DBAction(parse.json) {
    implicit rs =>
      try {
        val newClass = (rs.request.body \ "class").as[Class]
        val oneClass: Class = (classTable returning classTable) += newClass
        Ok(Json.obj("class" -> Json.toJson(oneClass)))
      } catch {
        case _: Throwable => BadRequest("Wrong data format")
      }
  }

  def updateClass(id: Int) = DBAction(parse.json) {
    implicit rs =>
      try {
        println(rs.request.body)
        val newClass = (rs.request.body \ "class").as[Class]
        newClass.id = Some(id)
        println(newClass)
        classTable.where(_.id === id).update(newClass)
        Ok("Succeed")
      } catch {
        case e: Throwable =>
          println(e)
          BadRequest("Wrong data format")
      }
  }

  def deleteClass(id: Int) = DBAction {
    implicit rs =>
      classTable.where(_.id === id).delete
      Ok("Succeed")
  }
}
