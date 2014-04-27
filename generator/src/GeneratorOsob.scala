import scala.io.Source
import scala.util.Random

object GeneratorOsob {
  var nameList = List.empty[String]
  var surnameList = List.empty[String]
  var przedmiotNauczyciel = Map.empty[Int, List[Nauczyciel]]
  var uczniowie = List.empty[Uczen]
  val rand = new Random()
  val nameFile = "C:\\Users\\Marek\\Documents\\bsk-dac\\generator\\imiona.txt"
  val surnameFile = "C:\\Users\\Marek\\Documents\\bsk-dac\\generator\\nazwiska.txt"
  var nauczycielId = 0

  def randomElment[A](list: Seq[A]) = list(rand.nextInt(list.size))

  def generateSurname(name: String): String = {
    val temp = randomElment(surnameList)
    if (temp.endsWith("ki") && name.endsWith("a"))
      temp.dropRight(1).concat("a")
    else temp
  }

  def nextRandNumer(range: Int, digits: Int): String = {
    var number = (rand.nextInt(range) + 1).toString
    while (number.length() < digits) {
      number = "0" + number
    }
    number
  }

  def loadNames() {
    nameList = Source.fromFile(nameFile).getLines().toList
    surnameList = Source.fromFile(surnameFile).getLines().toList
  }

  def generatePesel(yearOfEndSchool: Int): String = {
    val additionalYear = if (rand.nextInt(50) == 1) 1 else 0 //if someone is spadochron
    val birthYear = yearOfEndSchool - 18 - additionalYear
    val year = birthYear.toString.substring(2)
    val month = nextRandNumer(12, 2)
    val day = nextRandNumer(30, 2)
    val rest = nextRandNumer(99999, 5)

    year + month + day + rest
  }

  def generateNauczyciel() = {
    val name = randomElment(nameList)
    val surname = generateSurname(name)
    val pesel = generatePesel(2000 - rand.nextInt(50))
    nauczycielId += 1
    new Nauczyciel(nauczycielId, name, surname, pesel)
  }

  def generujLiczbeNauczycieli(ilosc: Int) =
    (1 to ilosc).map(_ => GeneratorOsob.generateNauczyciel()).toList

  def generujNauczycieli() = {
    przedmiotNauczyciel =
      GeneratorKlas.obowiazkowe.keySet.map(_ -> generujLiczbeNauczycieli(5)).toMap ++
        GeneratorKlas.reszta.keySet.map(_ -> generujLiczbeNauczycieli(3)).toMap
    val lista = przedmiotNauczyciel.values.flatten.toList
    new Tabela(lista, "nauczyciel", List("imie", "nazwisko", "pesel"))
  }

  def losowyDoPrzedmiotu(przedmiot: Int) = randomElment(przedmiotNauczyciel(przedmiot)).id

  def pobierzDlaKlasy(przedmioty: Set[Int]) =
    przedmioty.map(p => p -> losowyDoPrzedmiotu(p)).toMap

  def generateUczen(year: Int, klasa: Int) = {
    val name = randomElment(nameList)
    val surname = generateSurname(name)
    val pesel = generatePesel(year)
    new Uczen(name, surname, pesel, klasa)
  }

  def generujUczniow(klasy: List[Klasa]) = {
    def uczniowWKlasie = Random.nextInt(6) + 26
    val lista = for {
      (klasa, id) <- klasy.zipWithIndex
      _ <- 1 to uczniowWKlasie
    } yield GeneratorOsob.generateUczen(klasa.rok, id + 1)
    new Tabela(lista, "uczen", List("id_klasa", "imie", "nazwisko", "pesel"))
  }
}

case class Nauczyciel(id: Int, imie: String, nazwisko: String, pesel: String) extends Rekord{
  def toSQL = s"('$imie', '$nazwisko', '$pesel')"
}

case class Uczen(imie: String, nazwisko: String, pesel: String, klasa: Int) extends Rekord{
  def toSQL = s"($klasa, '$imie', '$nazwisko', '$pesel')"
}
