package bdconnector;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

//Classe para teste em ambiente Java
class Main {
  static PostgreSQLJDBC teste = new PostgreSQLJDBC();
  public static void main(String[] args) {
    System.out.println("Incluindo usuario no sistema:");
    teste.insereUsua("Testador Teste Testinho", "teste@teste.com.br", "14325678999", "usuario");
    System.out.println("Eventos por data:");
    teste.consEvenData("2016-01-01", "2039-01-01");
    System.out.println("Eventos por distancia:");
    teste.consEvenDist(1, 10000, "estadia");
    System.out.println("Consulta Usuários pelo nome:");
    teste.consUsuaNome("a");
    System.out.println("Consulta Vagas no sistema:");
    teste.consVagaEsta();
  }
}

public class PostgreSQLJDBC {
  private String dbHost = System.getenv("DBHOST");
  private String dbUser = System.getenv("DBUSER");
  private String dbPass = System.getenv("DBPASS");

  public void populateDB() {
    //Tipo Connection que recebe a conexão
    Connection conn = null;
    //Tipo Statement que recebe e executa as querys
    Statement stmt = null;
    //Tenta estabelecer a conexão com o BD
    try {
      //Estabelece a conexão com o banco de dados utilizando os parâmetros
      //dbHost, dbUser e dbPass
      conn = DriverManager.getConnection(dbHost, dbUser, dbPass);
      //Statement que receberá a query literal a ser executada.
      stmt = conn.createStatement();
    //Se ocorrer uma excessão na conexão imprime o erro.
    } catch (Exception e) {
      System.out.println(e.getMessage());
    //Se tudo ocorrer corretamente executa a próxima etapa
    } finally {
      //Tenta executar a query no banco de dados
      try {
        //Executa a query informada como String
        stmt.executeUpdate(
        "CREATE TABLE ESTADIAS (" +
          "ID  SERIAL PRIMARY KEY," +
          "NOME VARCHAR(50)," +
          "ENDERECO VARCHAR(200)," +
          "EMAIL VARCHAR(100)," +
          "ATIVO CHAR(1) NOT NULL," +
          "DESCRICAO VARCHAR(500)," +
          "CONSTRAINT SK_ESTADIA UNIQUE(NOME, ENDERECO)," +
          "CONSTRAINT CK_ATIVIDADE CHECK(UPPER(ATIVO) IN ('S', 'N'))" +
        ");" +
        "INSERT INTO ESTADIAS (NOME, ENDERECO, EMAIL, ATIVO, DESCRICAO)" +
        "VALUES ('Aquário CAASO', 'USP São Carlos', 'aquario@usp.br', 'S', 'Mesas aconchegantes, espaço amplo, fácil acesso a Universidade. Se você quer dormir na época de provas, esse é seu lugar!')," +
               "('Bar do Amaral', 'Rua Rotary Club, 95', 'bardoamaral@gmail.com', 'S', 'Lar daqueles que sofreram na graduacao')," +
               "('Pombal', 'Rua Lions Club, 200', 'pombaldosestudantes@ig.com.br', 'S', 'Local de eventual visita (as vezes para dormir) daqueles que estudam na UFSCar');");

        stmt.executeUpdate(
          "CREATE TABLE VAGAS (" +
          "ID SERIAL," +
          "ESTADIA INTEGER," +
          "CUSTO NUMERIC(8,2) NOT NULL," +
          "NUMVAGAS SMALLINT NOT NULL," +
          "DATA_INICIO DATE NOT NULL," +
          "DATA_FIM DATE," +
          "ATIVO CHAR(1) NOT NULL," +
          "CONSTRAINT PK_VAGAS PRIMARY KEY(ID)," +
          "CONSTRAINT FK_VAGAS FOREIGN KEY(ESTADIA) REFERENCES ESTADIAS(ID) ON DELETE CASCADE," +
          "CONSTRAINT CK_VAGAS CHECK(DATA_INICIO != DATA_FIM)," +
          "CONSTRAINT CK_PERIODO CHECK(DATA_INICIO < DATA_FIM)," +
          "CONSTRAINT CK_VAGAS2 CHECK(UPPER(ATIVO) IN('S', 'N'))" +
          ");" +
          "INSERT INTO VAGAS (ESTADIA, CUSTO, NUMVAGAS, DATA_INICIO, DATA_FIM, ATIVO)" +
          "VALUES (1, 20.00, 18, '2018-01-01', NULL, 'S')," +
                 "(2, 450.30, 99, '2016-01-01', '2038-12-25', 'S')," +
                 "(2, 600.80, 99, '2016-01-01', '2038-12-25', 'S')," +
                 "(3, 0, 60, '2016-01-01', '2099-01-01', 'S');");

        stmt.executeUpdate(
          "CREATE TABLE TELEFONES_ESTADIA (" +
            "ESTADIA INTEGER," +
            "TELEFONE CHAR(11)," +
            "CONSTRAINT PK_TEL_ESTADIA PRIMARY KEY(ESTADIA, TELEFONE)," +
            "CONSTRAINT FK_TEL_ESTADIA FOREIGN KEY(ESTADIA) REFERENCES ESTADIAS(ID) ON DELETE CASCADE" +
          ");" +
          "INSERT INTO TELEFONES_ESTADIA (ESTADIA, TELEFONE)" +
          "VALUES (1, '1634125678')," +
                 "(1, '1634125679')," +
                 "(2, '1631425679')," +
                 "(3, '1632415679')," +
                 "(3, '16954319999');");

        stmt.executeUpdate(
          "CREATE TABLE USUARIOS (" +
            "NOME VARCHAR(50)," +
            "EMAIL VARCHAR(100) PRIMARY KEY," +
            "CPF CHAR(11) UNIQUE NOT NULL," +
            "TIPO VARCHAR(15)" +
            "CONSTRAINT CK_TIPO CHECK(UPPER(TIPO) IN('ADMINISTRADOR', 'SUPERUSUARIO', 'USUARIO'))" +
          ");" +
          "INSERT INTO USUARIOS (NOME, EMAIL, CPF, TIPO)" +
          "VALUES ('Felipe Araújo Matos', 'felipearmat@usp.br', '12345678901', 'SUPERUSUARIO')," +
                 "('Elaine Parros M. de Sousa', 'parros@icmc.usp.br', '63598799905', 'SUPERUSUARIO')," +
                 "('Eric Azevedo Guimarães', 'ericazevedo@usp.br', '23345678809', 'ADMINISTRADOR')," +
                 "('Vinícius do Nascimento Fontenele', 'viniciusfontenele@usp.br', '54637218976', 'ADMINISTRADOR')," +
                 "('Joãozinho da Silva Sauro', 'josisauro@usp.br', '34765489013', 'USUARIO')");

        stmt.executeUpdate(
        "CREATE TABLE TELEFONES_USUARIO (" +
          "EMAIL VARCHAR(100)," +
          "TELEFONE CHAR(11)," +
          "CONSTRAINT PK_TEL_USUARIO PRIMARY KEY(EMAIL, TELEFONE)," +
          "CONSTRAINT FK_TEL_USUARIO FOREIGN KEY(EMAIL) REFERENCES USUARIOS(EMAIL)" +
        ");" +
          "INSERT INTO TELEFONES_USUARIO (EMAIL, TELEFONE)" +
          "VALUES ('felipearmat@usp.br', '1612345678')," +
                 "('felipearmat@usp.br', '16987654321')," +
                 "('parros@icmc.usp.br', '63598799905')," +
                 "('ericazevedo@usp.br', '23345678809')," +
                 "('viniciusfontenele@usp.br', '54637218976')," +
                 "('josisauro@usp.br', '34765489013')");

        stmt.executeUpdate(
          "CREATE TABLE UNIVERSIDADES (" +
            "ID SERIAL PRIMARY KEY," +
            "NOME VARCHAR(50) UNIQUE NOT NULL," +
            "ENDERECO VARCHAR(200) NOT NULL" +
          ");" +
        "INSERT INTO UNIVERSIDADES (NOME, ENDERECO)" +
        "VALUES ('Universidade Federal de São Carlos', 'Rodovia Washington Luís, s/n')," +
               "('USP - São Carlos','Av. Trab. São Carlense, 400')");

        stmt.executeUpdate(
        "CREATE TABLE EVENTOS (" +
          "ID SERIAL," +
          "NOME VARCHAR(50) NOT NULL," +
          "DATA DATE NOT NULL," +
          "HORA TIME," +
          "DURACAO TIME," +
          "LOCAL VARCHAR(50) NOT NULL," +
          "PRECO NUMERIC(8,2)," +
          "ATIVO CHAR(1) NOT NULL," +
          "CONSTRAINT PK_EVENTO PRIMARY KEY(ID)," +
          "CONSTRAINT SK_EVENTO UNIQUE(NOME, DATA)," +
          "CONSTRAINT CK_DURACAO CHECK(DURACAO > '0:00:00')," +
          "CONSTRAINT CK_ATIVO CHECK(UPPER(ATIVO) IN('S', 'N'))" +
        ");" +
        "INSERT INTO EVENTOS (NOME, DATA, HORA, DURACAO, LOCAL, PRECO, ATIVO)" +
        "VALUES ('TUSCA-DIA1-TENDAUFSCAR', '2019-10-10', '22:00:00', '04:00:00', 'Rodovia Washington Luís, s/n', '340.00', 'S')," +
               "('TUSCA-DIA1-TENDACAASO', '2019-10-10', '22:00:00', '04:00:00', 'Av. Trab. São Carlense, 400', '340.00', 'S')," +
               "('TUSCA-DIA2-FESTA', '2019-10-11', '22:00:00', '04:00:00', 'BANANA BRASIL - Rod. Washington Luiz, 234', '340.00', 'S')," +
               "('COROACAO DO PEDRAO COMO REITOR DA UFSCAR', '2005-01-01', '08:00:00', '01:00:00', 'Praça da Bandeira - Rodovia Washington Luís, s/n', '0.00', '0.00', 'N')," +
               "('SUB. DE CALCULO1', '2019-12-23', '21:00:00', '02:00:00', 'ICMC - Av. Trab. São Carlense, 400', '0.00', 'S');");

        stmt.executeUpdate(
          "CREATE TABLE DISTANCIAS_UES (" +
            "UNIVERSIDADE INTEGER NOT NULL," +
            "ESTADIA INTEGER NOT NULL," +
            "DISTANCIA NUMERIC NOT NULL," +
            "CONSTRAINT PK_DISTANCIA_UES PRIMARY KEY(UNIVERSIDADE, ESTADIA)," +
            "CONSTRAINT FK_DISTANCIA_UES FOREIGN KEY(ESTADIA) REFERENCES ESTADIAS(ID) ON DELETE CASCADE," +
            "CONSTRAINT FK_DISTANCIA_UES2 FOREIGN KEY(UNIVERSIDADE) REFERENCES UNIVERSIDADES(ID) ON DELETE CASCADE" +
          ");" +
        "INSERT INTO DISTANCIAS_UES (UNIVERSIDADE, ESTADIA, DISTANCIA)" +
        "VALUES (1, 1, 3500)," +
               "(1, 2, 20)," +
               "(1, 3, 30)," +
               "(2, 1, 0)," +
               "(2, 2, 3480)," +
               "(2, 3, 3470)");

        stmt.executeUpdate(
          "CREATE TABLE DISTANCIAS_UEV (" +
            "UNIVERSIDADE INTEGER NOT NULL," +
            "EVENTO INTEGER NOT NULL," +
            "DISTANCIA NUMERIC NOT NULL," +
            "CONSTRAINT PK_DISTANCIA_UEV PRIMARY KEY(UNIVERSIDADE, EVENTO)," +
            "CONSTRAINT FK_DISTANCIA_UEV FOREIGN KEY(EVENTO) REFERENCES EVENTOS(ID) ON DELETE CASCADE," +
            "CONSTRAINT FK_DISTANCIA_UEV2 FOREIGN KEY(UNIVERSIDADE) REFERENCES UNIVERSIDADES(ID) ON DELETE CASCADE" +
          ");" +
        "INSERT INTO DISTANCIAS_UEV (UNIVERSIDADE, EVENTO, DISTANCIA)" +
        "VALUES (1, 1, 0)," +
               "(1, 2, 3500)," +
               "(1, 3, 2000)," +
               "(1, 4, 3500)," +
               "(2, 1, 3500)," +
               "(2, 2, 0)," +
               "(2, 3, 3000)," +
               "(2, 4, 0)");

        stmt.executeUpdate(
          "CREATE TABLE DISTANCIAS_EE (" +
            "ESTADIA INTEGER NOT NULL," +
            "EVENTO INTEGER NOT NULL," +
            "DISTANCIA NUMERIC NOT NULL," +
            "CONSTRAINT PK_DISTANCIA_EE PRIMARY KEY(ESTADIA, EVENTO)," +
            "CONSTRAINT FK_DISTANCIA_EE FOREIGN KEY(EVENTO) REFERENCES EVENTOS(ID) ON DELETE CASCADE," +
            "CONSTRAINT FK_DISTANCIA_EE2 FOREIGN KEY(ESTADIA) REFERENCES ESTADIAS(ID) ON DELETE CASCADE" +
          ");" +
        "INSERT INTO DISTANCIAS_EE (ESTADIA, EVENTO, DISTANCIA)" +
        "VALUES (1, 1, 3500)," +
               "(1, 2, 0)," +
               "(1, 3, 2500)," +
               "(1, 4, 0)," +
               "(2, 1, 20)," +
               "(2, 2, 3480)," +
               "(2, 3, 1860)," +
               "(2, 4, 3480)," +
               "(3, 1, 30)," +
               "(3, 2, 3470)," +
               "(3, 3, 1850)," +
               "(3, 4, 3470)");

        //Se a conexão com o BD existir, fecha ela.
        if (conn != null) { conn.close(); }
      //No caso de exceção na query, imprime o erro.
      } catch (SQLException sqlExc) {
        System.out.println(sqlExc.getMessage());
      }
    }
  }

  private int execUpdate(String select) {
    //Statement que executa a query
    Statement stmt = null;
    //Variável que retorna status da query
    int result = 0;
    //Variável para a conexão
    Connection conn = null;
    //Tenta estabelecer conexão com o BD
    try {
      conn = DriverManager.getConnection(dbHost, dbUser, dbPass);
      //Cria o Statement para executar a query
      stmt = conn.createStatement();
    //Se ocorrer uma exceção na conexão imprime o erro.
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      //Tenta executar a query e passar o resultado para uma lista de
      //hashmap com nomes de coluna e valor da tupla
      try {
        result = stmt.executeUpdate(select);
        //Se a conexão existir a encerra.
        if (conn != null) { conn.close(); }
      } catch (SQLException sqlExc) {
        System.out.println(sqlExc.getMessage());
      }
    }
    return result;
  }

  private ArrayList<HashMap<String,Object >> execQuery(String select) {
    //Array onde serão salvas as tuplas
    ArrayList<HashMap<String,Object >> list = new ArrayList<HashMap<String,Object >>();
    //Statement que executa a query
    Statement stmt = null;
    //Variável para a conexão
    Connection conn = null;
    //Tenta estabelecer conexão com o BD
    try {
      conn = DriverManager.getConnection(dbHost, dbUser, dbPass);
      //Cria o Statement para executar a query
      stmt = conn.createStatement();
    //Se ocorrer uma exceção na conexão imprime o erro.
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      //Tenta executar a query e passar o resultado para uma lista de
      //hashmap com nomes de coluna e valor da tupla
      try {
        list = resultSetToArrayList(stmt.executeQuery(select));
        //Se a conexão existir a encerra.
        if (conn != null) { conn.close(); }
      } catch (SQLException sqlExc) {
        System.out.println(sqlExc.getMessage());
      }
    }
    return list;
  }

  public ArrayList<HashMap<String,Object >> mostraTabela(String tabela) {
    //Query que seleciona todos os campos de uma tabela especificada
    String select =
    "SELECT * " +
    "FROM " + tabela;
    //Retorna a lista obtida com a query
    return execQuery(select);
  }

  public ArrayList<HashMap<String,Object >> consUsuaNome(String namePattern) {
    //String com a query que seleciona todos os usuários da
    //tabela USUARIOS cujo nome contenha a substring namePattern
    String select =
    "SELECT * " +
    "FROM USUARIOS ";
    if(!namePattern.equals("TODOS"))
    select += "WHERE nome ilike '%" + namePattern + "%'";
    return execQuery(select);
  }

  public ArrayList<HashMap<String,Object >> consEvenData(boolean ativos) {
    return this.consEvenData("1900-01-01", "2050-01-01", ativos);
  }

  public ArrayList<HashMap<String,Object >> consEvenData(String data1, String data2) {
    return this.consEvenData(data1, data2, true);
  }

  public ArrayList<HashMap<String,Object >> consEvenData(String data1, String data2, boolean ativos) {
    //String com a query que seleciona todos
    //os eventos entre as datas informadas
    String select =
    "SELECT NOME, DATA, HORA, DURACAO, LOCAL, PRECO " +
    "FROM EVENTOS " +
    "WHERE DATA BETWEEN '" + data1 + "' AND '" + data2 + "'";
    //Se o parâmetro ativo for verdadeiro, mostra apenas eventos
    //ativos
    if(ativos)
      select += " AND ATIVO = 'S'";
    select += " ORDER BY DATA";
    //Retorna a lista obtida através da query
    return execQuery(select);
  }

  public ArrayList<HashMap<String,Object >> consEvenDist(int local, int distancia, String tipo) {
    return this.consEvenDist(local, distancia, tipo, true);
  }

  public ArrayList<HashMap<String,Object >> consEvenDist(int local, int distancia, String tipo, boolean ativos) {
    //String com a query que seleciona todos os
    //eventos limitados pela distância de uma local
    String select =
    "SELECT e.NOME, e.DATA, e.HORA, e.DURACAO, e.LOCAL, e.PRECO, d.DISTANCIA AS \"Distância (m)\" " +
    "FROM EVENTOS e ";

    if(tipo.equals("estadia"))
      select += "JOIN DISTANCIAS_EE d ";
    else if(tipo.equals("universidade"))
      select += "JOIN DISTANCIAS_UEV d ";

      select += "ON e.ID=d.EVENTO " +
    "WHERE d." + tipo + "=" + local + " AND d.DISTANCIA<=" + distancia;
    if(ativos)
      select += " AND e.ATIVO = 'S'";
    select += " ORDER BY e.DATA";
    //Retorna a lista obtida
    return execQuery(select);
  }

  public ArrayList<HashMap<String,Object >> consVagaEsta() {
    return this.consVagaEsta(0, 0, 99999999, true);
  }

  public ArrayList<HashMap<String,Object >> consVagaEsta(int vagas) {
    return this.consVagaEsta(vagas, 0, 99999999, true);
  }

  public ArrayList<HashMap<String,Object >> consVagaEsta(int vagas, double precomin) {
    return this.consVagaEsta(vagas, precomin, 99999999, true);
  }

  public ArrayList<HashMap<String,Object >> consVagaEsta(int vagas, double precomin, double precomax) {
    return this.consVagaEsta(vagas, precomin, precomax, true);
  }

  public ArrayList<HashMap<String,Object >> consVagaEsta(int vagas, double precomin, double precomax, boolean ativos) {
    //String com a query que seleciona todos as estadias e a
    //quantidade de vagas em cada, juntamente com outros dados
    String select =
    "SELECT e.NOME, e.ENDERECO, v.NUMVAGAS AS \"QNT\", STRING_AGG(t.TELEFONE, ', ') AS \"TELEFONES\", v.CUSTO, e.DESCRICAO " +
    "FROM ESTADIAS e " +
    "JOIN VAGAS v " +
      "ON v.ESTADIA=e.ID " +
    "JOIN TELEFONES_ESTADIA t " +
      "ON t.ESTADIA=e.ID " +
    "WHERE v.NUMVAGAS>=" + vagas + " AND v.CUSTO >=" + precomin + " AND v.CUSTO <=" + precomax;
    if(ativos){
      select += " AND e.ATIVO = 'S'";
    }
    select += "GROUP BY e.NOME, e.ENDERECO, v.NUMVAGAS, v.CUSTO, e.DESCRICAO ORDER BY v.CUSTO";

    //Retorna a lista obtida
    return execQuery(select);
  }

  public int insereUsua(String nome, String email, String cpf, String tipo) {
    //String contendo valores a serem inseridos
    //na tabela de usuários
    String select =
    "INSERT INTO USUARIOS (NOME, EMAIL, CPF, TIPO)" +
    "VALUES ('" + nome + "', '" + email + "', '" + cpf + "', '" + tipo + "')";
    return execUpdate(select);
  }

  //Método para imprimir uma ArrayList obtida através de uma query
  //Utilizado durante o debug do código.
  public void printArrayList(ArrayList<HashMap<String,Object >> target){
    for (int i = 0; i < target.size(); i++) {
      System.out.print("{");
      for (String name: target.get(i).keySet()){
        String key = name;
        String value;
        if(target.get(i).get(name) == null)
            value = "NULL";
        else
            value = target.get(i).get(name).toString();
        System.out.print(key + ": " + value + ", ");
      }
      System.out.println("}, ");
    }
  }

  //Converte um ResultSet obtido através de um query em
  //uma ArrayList para depois ser tratado na interface.
  public ArrayList<HashMap<String,Object >> resultSetToArrayList(ResultSet rs) throws SQLException{
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    ArrayList<HashMap<String,Object >> list = new ArrayList<HashMap<String,Object >>(50);
    while (rs.next()){
      HashMap<String,Object> row = new HashMap<String,Object>(columns);
      for(int i=1; i<=columns; ++i){
        row.put(md.getColumnName(i),rs.getObject(i));
      }
      list.add(row);
    }
    return list;
  }
}
