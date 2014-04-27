import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter}

class Tabela[A <: Rekord](_lista: List[A], tabela: String, pola: List[String]) {
  def lista = _lista
  def SQLHeader =
    s"TRUNCATE $tabela RESTART IDENTITY CASCADE;\n" +
      s"INSERT INTO $tabela (${pola.mkString(", ")}) VALUES \n"

  def toSQL = SQLHeader + lista.map(_.toSQL).mkString(",\n") + ";"

  def zapiszDoPliku(plik: String) {
    val writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(plik), "UTF16"))
    writer.write(toSQL)
    writer.flush()
    writer.close()
  }
}

trait Rekord {
  def toSQL: String
}

