import scala.util.Random

case class Ranga(nazwa: String, waga: Int) extends Rekord {
  def toSQL = s"('$nazwa', $waga)"
}

class Udzial(nauczyciel: Int, przedmiot: Int, ranga: Int, uczen: Int, miejsce: Int) extends Rekord {
  def toSQL = s"($nauczyciel, $przedmiot, $ranga, $uczen, $miejsce)"
}

object Konkursy {
  val rangi = List("szkolny", "gminny", "powiatowy", "wojewódzki", "ogólnopolski", "międzynarodowy")

  val listaRang = rangi.reverse.zipWithIndex.map(t => Ranga(t._1, t._2 + 1)).toList

  def tabelaRank = new Tabela(listaRang, "ranga_konkursu", List("nazwa", "waga"))

  def generujUdzialyWKonkursach(uczniowie: List[Uczen]) = {
    def generujDlaUcznia(uczen: Int) = {
      val ranga = Random.nextInt(rangi.size) + 1
      val przedmiot = GeneratorKlas.losowyDowolnyPrzedmiot
      val nauczyciel = GeneratorOsob.losowyDoPrzedmiotu(przedmiot)
      val miejsce = Random.nextInt(20) + 1
      new Udzial(nauczyciel, przedmiot, ranga, uczen, miejsce)
    }
    def iloscKonkursow = Random.nextInt(3)
    val lista = for {
      (uczen, id) <- uczniowie.zipWithIndex
      _ <- 1 to iloscKonkursow
    } yield generujDlaUcznia(id + 1)

    new Tabela(lista, "udzial_w_konkursie", List("id_nauczyciel", "id_przedmiot",
      "id_ranga_konkursu", "id_uczen", "zajete_miejsce"))
  }
}