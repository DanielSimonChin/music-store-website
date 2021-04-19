/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.ReviewJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;

import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Review;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests public methods for review controller
 *
 * @author Alessandro Dare
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ReviewUnitTest {

    private final static Logger LOG = LoggerFactory.getLogger(ReviewUnitTest.class);

    @Inject
    private ReviewJpaController controller;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    @Inject
    private AlbumJpaController albumJpaController;
    @Resource(lookup = "java:app/jdbc/myMusic")
    private DataSource ds;

    public ReviewUnitTest() {
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
                .addPackage(LoginBean.class.getPackage())
                .addPackage(PreRenderViewBean.class.getPackage())
                .addPackage(JavaEE8Resource.class.getPackage())
                .addPackage(LocaleChanger.class.getPackage())
                .addPackage(ReviewJpaController.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Review.class.getPackage())
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
     * Tests that all reviews given are for selected track
     *
     * @author Alessandro Dare
     */
    @Test
    public void testRealtedTrackReviews() {
        MusicTrack musicTrack = musicTrackJpaController.findMusicTrack(1);
        List<Review> reviews = this.controller.getTrackReviews(musicTrack);
        assertEquals(checkRealtedTracks(musicTrack, reviews), true);
    }

    /**
     * checks that all reviews are related to the selected tracks
     *
     * @author Alessandro Dare
     * @param musicTrack MusicTrack
     * @param reviews List<Review>
     * @return true if all reviews are related false if not
     */
    private boolean checkRealtedTracks(MusicTrack musicTrack, List<Review> reviews) {
        for (Review review : reviews) {
            if (review.getInventoryid().getInventoryid() != musicTrack.getInventoryid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests that no reviews a given to a new track
     *
     * @author Alessandro Dare
     */
    @Test
    public void testRealtedTrackReviewsCount() {
        MusicTrack musicTrack = new MusicTrack();
        List<Review> reviews = this.controller.getTrackReviews(musicTrack);
        assertEquals(reviews.size(), 0);
    }

    /**
     * tests all reviews generated a from the selected album
     *
     * @author Alessandro Dare
     */
    @Test
    public void testRealtedAblumReviews() {
        Album album = this.albumJpaController.findAlbum(1);
        List<Review> reviews = this.controller.getAlbumTrackReviews(album);
        assertEquals(checkRealtedAlbums(album, reviews), true);
    }

    /**
     * checks all reviews are given to related album
     *
     * @author Alessandro Dare
     * @param album Album
     * @param reviews List<Review>
     * @return true if their related false if not
     */
    private boolean checkRealtedAlbums(Album album, List<Review> reviews) {
        for (Review review : reviews) {
            if (review.getInventoryid().getAlbumid().getAlbumid() != album.getAlbumid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * tests that a new receive will no reviews
     *
     * @author Alessandro Dare
     */
    @Test
    public void testRealtedAlbumReviewsCount() {
        Album album = new Album();
        List<Review> reviews = this.controller.getAlbumTrackReviews(album);
        assertEquals(reviews.size(), 0);
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
