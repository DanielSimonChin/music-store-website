/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.converters.AlbumConverter;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.resources.JavaEE8Resource;
import com.gb4w21.musicalmoose.util.LocaleChanger;
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
import jodd.mail.Email;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * Arquillian unit tests for the methods in the AlbumJpaController that involves
 * CriteriaBuilder queries.
 *
 * @author Daniel
 */
@RunWith(Arquillian.class)
public class AlbumControllerUnitTests {
    @Inject
    private AlbumJpaController albumController;
    @Inject
    private MusicTrackJpaController trackController;

    @Resource(lookup = "java:app/jdbc/myMusic")
    private DataSource ds;

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
                .addPackage(LoginBean.class.getPackage())
                .addPackage(PreRenderViewBean.class.getPackage())
                .addPackage(AlbumConverter.class.getPackage())
                .addPackage(JavaEE8Resource.class.getPackage())
                .addPackage(LocaleChanger.class.getPackage())
                .addPackage(AlbumJpaController.class.getPackage())
                .addPackage(MusicTrackJpaController.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Album.class.getPackage())
                .addPackage(MusicTrack.class.getPackage())
                .addPackage(Email.class.getPackage()) 
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/main/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("createMusicTable.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }

    /**
     * Test that the method returns albums from the same genre.
     */
    @Test
    public void testFindRelatedAlbumsWithTrackParam() {
        MusicTrack track = trackController.findMusicTrack(1);

        List<Album> albumResults = albumController.findRelatedAlbums(track);
        boolean checkConditions = true;

        for (Album a : albumResults) {
            //make sure that the returned album is in the same cateogory and is not the same album as the given track.
            if (!a.getMusicTrackList().get(0).getMusiccategory().equals(track.getMusiccategory()) || a.getAlbumid() == track.getAlbumid().getAlbumid()) {
                checkConditions = false;
            }
        }
        assertTrue(checkConditions);
    }

    /**
     * Test that this method returns the first 3 related albums from the same
     * genre.
     */
    @Test
    public void testFindRelatedAlbumsCount() {
        MusicTrack track = trackController.findMusicTrack(1);

        List<Album> albumResults = albumController.findRelatedAlbums(track);

        assertEquals(3, albumResults.size());
    }

    /**
     * Test that this method returns albums from the same genre
     */
    @Test
    public void testFindRelatedAlbumsWithAlbumParam() {
        Album album = albumController.findAlbum(1);
        List<Album> albumResults = albumController.findRelatedAlbums(album);
        boolean checkConditions = true;

        for (Album a : albumResults) {
            //make sure that the returned album is in the same cateogory and is not the same album as the given track.
            if (!a.getMusicTrackList().get(0).getMusiccategory().equals(album.getMusicTrackList().get(0).getMusiccategory()) || a.getAlbumid() == album.getAlbumid()) {
                checkConditions = false;
            }
        }
        assertTrue(checkConditions);
    }

    /**
     * Test that the method only returns albums that have a sale price less than
     * the list price.
     */
    @Test
    public void testGetSpecialAlbums() {
        List<Album> specialAlbums = this.albumController.getSpecialAlbums();
        boolean checkConditions = true;

        for (Album a : specialAlbums) {
            if (a.getSaleprice() >= a.getListprice()) {
                checkConditions = false;
            }
        }
        assertTrue(checkConditions);
    }

    /**
     * Ensure that the method only returns at most 3 albums that are on sale.
     */
    @Test
    public void testGetSpecialAlbumsCount() {
        List<Album> specialAlbums = albumController.getSpecialAlbums();
        assertTrue(specialAlbums.size() <= 3);
    }

    /**
     * Test the the tracks returned are part of the input album
     */
    @Test
    public void testGetAlbumsTracks() {
        Album album = this.albumController.findAlbum(1);
        
        List<MusicTrack> albumTracks = this.albumController.getAlbumTracks(album.getAlbumid());
        
        boolean checkConditions = true;
        for(MusicTrack track : albumTracks){
            //the track's album's id must be the same as the input album
            if(track.getAlbumid().getAlbumid() != album.getAlbumid()){
                checkConditions = false;
            }
        }
        
        assertTrue(checkConditions);
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
