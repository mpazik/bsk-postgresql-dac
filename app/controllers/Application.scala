package controllers

import play.api.mvc._

object Application extends Controller {

  def index() = Action {
    Ok(views.html.main())
  }
  def page(page: String) = Action {
    Ok(views.html.main())
  }

}