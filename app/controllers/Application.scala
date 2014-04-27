package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models.KlasaTable

object Application extends Controller {
  val classes = TableQuery[KlasaTable]
  
  def index = klasy()

  def klasy = DBAction { implicit rs =>
    Ok(views.html.klasy(classes.list))
  }

  def oceny = Action {
    Ok(views.html.oceny())
  }

  def uprawnienia = Action {
    Ok(views.html.uprawnienia())
  }
}