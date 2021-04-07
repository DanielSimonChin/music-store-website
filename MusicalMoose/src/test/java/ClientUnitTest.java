/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class ClientUnitTest {
    private final static Logger LOG = LoggerFactory.getLogger(ClientUnitTest.class);

    @Inject
    private ClientJpaController controller;

    @Resource(lookup = "java:app/jdbc/myMusic")
    private DataSource ds;
    public ClientUnitTest() {
    }
    
     @Deployment
    public static WebArchive deploy() {

        // Use an alternative to the JUnit assert library called AssertJ
        // Need to reference MySQL driver as it is not part of either
        // embedded or remote
        final File[] dependencies = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.assertj:assertj-core",
                        "org.slf4j:slf4j-api",
                        "org.apache.logging.log4j:log4j-slf4j-impl",
                        "org.apache.logging.log4j:log4j-web"
                ).withTransitivity()
                .asFile();

        // The SQL script to create the database is in src/test/resources
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addPackage(ClientJpaController.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Client.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/main/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("createMusicTable.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }
    @Test
    public void testFindClient(){
        Client orignalClient = this.controller.findClient(1);
        Client foudnClient = this.controller.findUser(orignalClient.getUsername(), orignalClient.getPassword());
        assertEquals(compareClients(orignalClient,foudnClient), true);
    }
    private boolean compareClients(Client client1, Client client2){
        if(!client1.getAddress1().equals(client2.getAddress1())){
            return false;
        }
        
        else if(!client1.getEmail().equals(client2.getEmail())){
            return false;
        }
        else if(!client1.getUsername().equals(client2.getUsername())){
            return false;
        }
        else if(!client1.getPassword().equals(client2.getPassword())){
            return false;
        }
        else if(!client1.getPostalcode().equals(client2.getPostalcode())){
            return false;
        }
         else if(!client1.getFirstname().equals(client2.getFirstname())){
            return false;
        }
        else if(!client1.getLastname().equals(client2.getLastname())){
            return false;
        }
        else if(!client1.getCity().equals(client2.getCity())){
            return false;
        }
         else if(!client1.getProvince().equals(client2.getProvince())){
            return false;
        }
        else if(!client1.getCountry().equals(client2.getCountry())){
            return false;
        }
        else if(!client1.getTitle().equals(client2.getTitle())){
            return false;
        }
        else if(!client1.getHometelephone().equals(client2.getHometelephone())){
            return false;
        }
        else if(!client1.getGenreoflastsearch().equals(client2.getGenreoflastsearch())){
            return false;
        }
        else if(!client1.getClientid().equals(client2.getClientid())){
            return false;
        }
        else if((client1.getAddress2() !=null && client2.getAddress2() ==null)||(client1.getAddress2() ==null && client2.getAddress2() !=null)){
            return false;
        }
        else if (client1.getAddress2() !=null && client1.getAddress2() !=null  && !client1.getAddress2().equals(client2.getAddress2())){
           return false;
        }
        else if((client1.getCompanyname()!=null && client2.getCompanyname() ==null)||(client1.getCompanyname() ==null && client2.getCompanyname() !=null)){
            return false;
        } 
        else if (client1.getCompanyname() !=null && client1.getCompanyname() !=null  && !client1.getCompanyname().equals(client2.getCompanyname())){
           return false;
        }
        else if((client1.getCelltelephone()!=null && client2.getCelltelephone() ==null)||(client1.getCelltelephone() ==null && client2.getCelltelephone() !=null)){
            return false;
        }
        else if (client1.getCelltelephone() !=null && client1.getCelltelephone() !=null  && !client1.getCelltelephone().equals(client2.getCelltelephone())){
           return false;
        }
        else if((client1.getIsmanager()&&!client2.getIsmanager())&& (!client1.getIsmanager()&&client2.getIsmanager())){
          return false;
        }
        
     
        return true;
    }
   
    @Test
    public void testFindClientNoResults() {
        Client client = this.controller.findUser("", "");
        assertEquals(null, client);
    }
     /**
     * Restore the database to a known state before testing. This is important
     * if the test is destructive. This routine is courtesy of Bartosz Majsak
     * who also solved my Arquillian remote server problem
     */
    @Before
    public void seedDatabase() {
        final String seedDataScript = loadAsString("createMusicTable.sql");

        try ( Connection connection = ds.getConnection()) {
            for (String statement : splitStatements(new StringReader(
                    seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try ( InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path)) {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader,
            String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }
}
