USE MUSICSTORAGE;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS SURVEYROW;
DROP TABLE IF EXISTS SURVEY;
DROP TABLE IF EXISTS NEWS;
DROP TABLE IF EXISTS ADDRESSES;
DROP TABLE IF EXISTS BANNERAD;
DROP TABLE IF EXISTS SALEBRALBUM;
DROP TABLE IF EXISTS SALEBRTRACK;
DROP TABLE IF EXISTS CREDITCARDINFO;
DROP TABLE IF EXISTS REVIEW;
DROP TABLE IF EXISTS SALE;
DROP TABLE IF EXISTS CLIENT;
DROP TABLE IF EXISTS ALBUM;
DROP TABLE IF EXISTS MUSIC_TRACK;
DROP TABLE IF EXISTS RSS;
DROP TABLE IF EXISTS INVOICEDETAIL;
DROP TABLE IF EXISTS INVOICE;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE ALBUM (
    ALBUMID INT NOT NULL auto_increment,
    ALBUMTITLE VARCHAR(255),
    RELEASEDATE TIMESTAMP,
    ARTIST VARCHAR(255),
    RECORDLABEL VARCHAR(255),
    NUMBEROFTRACKS INT,
    DATEENTERED TIMESTAMP,
    ALBUMIMAGEFILENAMEBIG VARCHAR(255),
    ALBUMIMAGEFILENAMESMALL VARCHAR(255),
    IMAGECONTENTBIG LONGBLOB,
    IMAGECONTENTSMALL LONGBLOB,
    COSTPRICE FLOAT,
    LISTPRICE FLOAT,
    SALEPRICE FLOAT,
    PST FLOAT,
    GST FLOAT,
    HST FLOAT,
    REMOVALSTATUS BIT(1),
    REMOVALDATE TIMESTAMP,
    PRIMARY KEY (ALBUMID)
);

CREATE TABLE MUSIC_TRACK(
    INVENTORYID INT NOT NULL auto_increment ,
    ALBUMID INT,
    TRACKTITLE VARCHAR(255),
    ARTIST VARCHAR(255),
    SONGWRITER VARCHAR(255),
    PLAYLENGTH FLOAT,
    SELECTIONNUMBER INT,
    MUSICCATEGORY VARCHAR(255),
    ALBUMIMAGEFILENAMEBIG VARCHAR(255),
    ALBUMIMAGEFILENAMESMALL VARCHAR(255),
    IMAGECONTENTBIG LONGBLOB,
    IMAGECONTENTSMALL LONGBLOB,
    COSTPRICE FLOAT,
    LISTPRICE FLOAT,
    SALEPRICE FLOAT,
    PST FLOAT,
    GST FLOAT,
    HST FLOAT,
    DATEENTERED TIMESTAMP,
    PARTOFALBUM BIT(1),
    REMOVALSTATUS BIT(1),
    REMOVALDATE TIMESTAMP,
    CONSTRAINT fkalbumid FOREIGN KEY (ALBUMID) REFERENCES ALBUM(ALBUMID), 
    PRIMARY KEY (INVENTORYID)
);

CREATE TABLE CLIENT (
    CLIENTID INT NOT NULL auto_increment,
    TITLE VARCHAR(255),
    LASTNAME VARCHAR(255),
    FIRSTNAME VARCHAR(255),
    COMPANYNAME VARCHAR(255),
    ADDRESS1 VARCHAR(255),
    ADDRESS2 VARCHAR(255),
    CITY VARCHAR(255),
    PROVINCE VARCHAR(255),
    COUNTRY VARCHAR(255),
    POSTALCODE VARCHAR(255),
    HOMETELEPHONE BIGINT,
    CELLTELEPHONE BIGINT,
    EMAIL VARCHAR(255),
    GENREOFLASTSEARCH VARCHAR(255),
    PRIMARY KEY (CLIENTID)
);

CREATE TABLE REVIEW (
    REVIEWID INT NOT NULL auto_increment,
    REVIEWDATE TIMESTAMP,
    CLIENTNAME VARCHAR(255),
    RATING INT,
    REVIEWTEXT MEDIUMTEXT,
    APROVALSTATUS BIT(1),
    CLIENTID INT,
    INVENTORYID INT,
    CONSTRAINT fkclientid FOREIGN KEY (CLIENTID) REFERENCES CLIENT (CLIENTID),
    CONSTRAINT fkinventoryid FOREIGN KEY (INVENTORYID) REFERENCES MUSIC_TRACK(INVENTORYID),
    PRIMARY KEY (REVIEWID)
);

CREATE TABLE CREDITCARDINFO(
    CREDITCARDID INT NOT NULL auto_increment,
    CREDITCARDBRAND  VARCHAR(255),
    CREDITCARDNUMBER BIGINT,
    EXPIRATIONMONTH SMALLINT,
    EXPIRATIONYEAR  SMALLINT,
    CLIENTID INT,
    CONSTRAINT fkclientid1 FOREIGN KEY (CLIENTID) REFERENCES CLIENT (CLIENTID),
    PRIMARY KEY (CREDITCARDID)
);

CREATE TABLE SALE(
    SALEID INT NOT NULL auto_increment,
    CLIENTID INT,
    CONSTRAINT fkclientid4 FOREIGN KEY (CLIENTID) REFERENCES CLIENT (CLIENTID),
    SALEDATE TIMESTAMP,
    PRIMARY KEY (SALEID)
);

CREATE TABLE INVOICEDETAIL(
    INVOICEID INT NOT NULL auto_increment,
    SALEDATE TIMESTAMP,
    SALEID INT ,
    TOTALNETVALUE FLOAT,
    PST FLOAT,
    GST FLOAT,
    HST FLOAT,
    TOTALGROSSVALUE FLOAT,
    INVENTORYID INT, 
    ALBUMID INT,
    CONSTRAINT fkinventoryid3 FOREIGN KEY (INVENTORYID) REFERENCES MUSIC_TRACK (INVENTORYID), 
    CONSTRAINT fkalbumid3 FOREIGN KEY (ALBUMID) REFERENCES ALBUM (ALBUMID),
    CONSTRAINT fksaleid3 FOREIGN KEY (SALEID) REFERENCES SALE (SALEID),
    CONSTRAINT CK_Task_OneNonNull CHECK ((INVENTORYID is null and ALBUMID is not null) OR (ALBUMID is null and INVENTORYID is not null)),
    PRIMARY KEY (INVOICEID)
);

CREATE TABLE BANNERAD (
    URL MEDIUMTEXT,
    BANNERADDID INT NOT NULL auto_increment,
    FILENAME VARCHAR(255),
    PRIMARY KEY (BANNERADDID)
);

CREATE TABLE NEWS(
    NEWSID INT NOT NULL auto_increment,
    NEWTITLE VARCHAR(255),
    NEWSTEXT MEDIUMTEXT,
    CREATEDDATE TIMESTAMP,
    LASTDISPLAYEDDTAE TIMESTAMP,
    PRIMARY KEY (NEWSID)
);

CREATE TABLE SURVEY(
    SURVEYID INT NOT NULL auto_increment,
    SURVEYTITLE VARCHAR(255),
    QUESTION VARCHAR(255),
    ANSERW1 VARCHAR(255),
    ANSERW1VOTES INT,
    ANSERW2 VARCHAR(255),
    ANSERW2VOTES INT,
    ANSERW3 VARCHAR(255),
    ANSERW3VOTES INT,
    ANSERW4 VARCHAR(255),
    ANSERW4VOTES INT,
    DATESURVEYRCREATED TIMESTAMP,
    DATELASTUSED TIMESTAMP,
    SURVERYENDED BIT(1),
    PRIMARY KEY (SURVEYID)
);

CREATE TABLE RSS(
    ID INT NOT NULL auto_increment,
    URL VARCHAR(255),
    PRIMARY KEY (ID)
);



INSERT INTO NEWS (NEWSID, NEWTITLE, NEWSTEXT, CREATEDDATE, LASTDISPLAYEDDTAE)
VALUES 
(1, "DRAKE COMES OUT WITH ANOTHER BANGER", "Drake lives in symbiosis with Kardashian-style celebrity culture, using veiled lyrics and social media posts to carefully extend or withdraw access to his narrative. Incidentally, one of the most evocative things he has written recently is the caption to an Instagram post he made celebrating his son’s birthday and sharing photos of him with the public for the first time. “It doesn’t matter what has happened in the past or what is happening around us now, you can always make the choice to break free of the wheel of suffering and panic and open up to your own light,” he wrote. The venue was appropriate for an artist whose success was formed in part on his ability to fill songs with pithy captions. But the message was a rarer thing: earnest, tenderhearted, with the focused sobriety of someone who has been meditating or studying Eckhart Tolle. Unfortunately, very little of that sentiment has made its way into his music, which remains guarded and skin-deep, even as it grows, like his houses, bigger and more expensive. (Pitchfork, Rawiya Kameir)", TIMESTAMP("2020-03-08", "11:03:11"), TIMESTAMP("2020-03-10", "11:03:11")), 
(2, "DA BABY COLLABS WITH OTHER ARTISTS TO MAKE A HIT ALBUM", "Ever the savvy marketer, DaBaby does manage a few highlights that seem packaged to go viral. “Nasty” pairs a gleaming lilt from Ashanti with a fun, dynamic Megan Thee Stallion verse. It doesn’t have the explosive power of “Cash Shit,” Megan and DaBaby’s last collaboration, but the song is still mesmerizing, with DaBaby’s absurd, precise eroticism in full force. The album’s payoff arrives on the title track, a two-minute opus that stitches together four beat switches and contorts DaBaby’s flow over and over. It builds, it thrills, it makes you feel like you can run through a wall—everything a DaBaby song can and should do, when he asks it of himself. (Pitchfork, Dani Blum)", TIMESTAMP("2019-06-23", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11")), 
(3, "DANIEL CAESAR DROPS SURPRISE ALBUMS THAT SHOCKS THE WORLD", "There are few R&B singers who would sample J. Robert Oppenheimer, the American theoretical physicist who helmed the design and research of the world’s first atomic bomb. But Toronto singer Daniel Caesar spotlights a quote from the scientist describing the Hindu deity Vishnu as the “destroyer of worlds” at the start of “Entropy,” setting the mood for his new album, Case Study 01. Coming from a love ballad crooner that previously held a reputation for having an insane amount of people proposing at his live shows, this might seem a little morbid and heady. But considering that 2017’s Freudian, was, well, named after Sigmund Freud as a tie-in to his grappling of ego and id within his romantic serenades, the 24-year-old artist now delves further into his philosophical explorations. (Pitchfork, Michelle Kim)", TIMESTAMP("2021-01-23", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11")), 
(4, "FROM YOUTUBER TO ARTIST: JOJI RELEASES REVOLUTIONARY ALBUM IN BALLDS 1", "George Miller’s nearly seven years of trolling the internet as the equal parts viral and vile YouTube personas Filthy Frank and Pink Guy were accented by his quiet SoundCloud releases as Joji. Comprised of emotional croons and lo-fi, sample-driven production, Miller’s side project eventually caught the attention of 88rising, a largely Asian music collective home to acts like Rich Brian, NIKI, and Keith Ape. A debut EP and an 88rising world tour later, Joji has seemingly cleared the leap from bizarre internet comedian to full-fledged musician, officially retiring his YouTube channel and all. Now, with all the shiny trappings of a label and a fanbase hungry for self-deprecating zingers like “Yeah Right” rather than Pink Guy’s stir fry raps, his first full-length project BALLADS 1 makes an effort to push past the confines of his bedroom walls and tedious heartache. (Pitchfork, Braudie Blais-Billie)", TIMESTAMP("2021-02-08", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11")), 
(5, "J COLE COMES OUT WITH HIT ALBUM TAKING THE HIP HOP WORLD BY SURPRISE", "Listening to a J. Cole album can feel like listening to a very intense young lawyer attempt to win a difficult case. Throughout his career, Cole’s raps have often been self-serious and polemical, with their success depending on the overall strength of his argumentation above all else. And while many of his individual claims can be convincing, you often get to the end of a song and think something like: Wait, did he really just argue that corporations take taxes and use them to buy and spread guns? Few artists stake so much on their ability to persuade an audience of their worldview, particularly when that worldview is so absolutist. You do not listen to J. Cole to enjoy his wit or his stories, but to partake in his wisdom, which often involves an element of moral panic: On his new addiction-themed album, KOD, he loves to suggest that people should abstain from things—smoking, drinking, online dating. Sometimes, he’s persuasive, but just as often, he simply seems self-righteous. (Pitchfork, Jonah Bromwich)", TIMESTAMP("2021-02-08", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"));

INSERT INTO SURVEY (SURVEYID, SURVEYTITLE, QUESTION, ANSERW1, ANSERW1VOTES, ANSERW2, ANSERW2VOTES, ANSERW3, ANSERW3VOTES, ANSERW4, ANSERW4VOTES, DATESURVEYRCREATED, DATELASTUSED, SURVERYENDED)
VALUES 
(1, "Teens and music in todays generation", "Which genre of music do you listen to most?", "HIP HOP", 43, "R&B", 23, "Rock", 13, "Indie Music", 28, TIMESTAMP("2020-02-08", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"), 0),
(2, "Most popular artist for elders", "Which artist are you most familiar with?", "Drake", 43, "Ariana Grande", 81, "Justin Bieber", 61, "Beyonce", 88, TIMESTAMP("2021-01-03", "11:03:11"), TIMESTAMP("2021-02-20", "11:03:11"), 1),
(3, "Device of music", "How do you listen to music on an daily basis?", "Speaker", 27, "Headphones", 81, "Earbuds", 65, "Other", 3, TIMESTAMP("2019-11-09", "11:03:11"), TIMESTAMP("2021-02-19", "11:03:11"), 1),
(4, "Platform of music", "What app or platform do you use to listen to music?", "Radio", 12, "Spotify", 99, "Apple Music", 57, "Other", 23, TIMESTAMP("2021-02-03", "12:24:14"),TIMESTAMP("2021-02-19", "06:13:12"), 1);


INSERT INTO ALBUM (ALBUMID, ALBUMTITLE, RELEASEDATE, ARTIST, RECORDLABEL, NUMBEROFTRACKS, DATEENTERED, ALBUMIMAGEFILENAMEBIG, ALBUMIMAGEFILENAMESMALL, COSTPRICE,  LISTPRICE, SALEPRICE, PST,GST,HST,REMOVALSTATUS, REMOVALDATE ) values
(1, "My Turn", TIMESTAMP("2020-02-28",  "00:00:00"), "Lil Baby", "Capitol Records", 20, TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 19.99, 17.99, 15.99, 0.42, 0.45,0.49, 1, null),
(2, "DAMN", TIMESTAMP("2017-04-14",  "00:00:00"), "Kendrick Lamar", "Top Dawg Entertainment", 14, TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",21.99, 22.99, 17.99, 0.42, 0.45,0.49, 1, null),
(3, "THE GOAT", TIMESTAMP("2020-05-15",  "00:00:00"), "Polo G", "Columbia Records", 16, TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",15.23, 17.99, 11.01, 0.42, 0.45,0.49, 1, null),
(4, "Scorpion", TIMESTAMP("2018-06-29",  "00:00:00"), "Drake", "Cash Money", 25, TIMESTAMP("2021-02-13",  "00:00:00"), "scorpion_big.png","scorpion_small.jpg",25.12, 25.00, 18.75, 0.42, 0.45,0.49, 1, null),
(5, "CASE STUDY 01", TIMESTAMP("2019-06-28",  "00:00:00"), "Daniel Caesar", "Golden Child", 10, TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",22.45, 20.00, 16.78, 0.5, 0.35,0.25, 1, null),
(6, "Ctrl", TIMESTAMP("2017-06-09",  "00:00:00"), "SZA", "Top Dawg", 14, TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",12.34, 11.99, 7.65, 0.5, 0.35,0.25, 1, null),
(7, "IGOR", TIMESTAMP("2019-05-17",  "00:00:00"), "Tyler", "The Creator, A Boy is a Gun", 12, TIMESTAMP("2021-02-13",  "00:00:00"), "IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg",18.50, 18.0, 15.21, 0.5, 0.35,0.25, 1, null),
(8, "BALLADS 1", TIMESTAMP("2018-10-26",  "00:00:00"), "Joji", "88rising", 12, TIMESTAMP("2021-02-13",  "00:00:00"), "Ballads-1-Big.jpg","Ballads-1-Small.jpg",17.50, 18.00, 10.16, 0.54, 0.24,0.17, 1, null),
(9,  "Night Visions", TIMESTAMP("2012-09-04",  "00:00:00"), "Imagine Dragons", "Interscope", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "nightVisions-Big.jpeg","nightVisions-Small.jpg",12.45, 13.99, 8.99, 0.54, 0.24,0.17, 1, null),
(10, "Pray for the Wicked", TIMESTAMP("2018-06-22",  "00:00:00"), "Panic at the Disco", "Fueled by Ramen", 11, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 14.00, 13.50, 10.50, 0.5, 0.35,0.25, 1, null),
(11, "American Beauty/American Psycho", TIMESTAMP("2015-01-16",  "00:00:00"), "Fallout Boy", "Island", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg",17.35, 17.00, 12.13, 0.5, 0.35,0.25, 1, null),
(12, "Native", TIMESTAMP("2013-03-25",  "00:00:00"), "One Republic", "Mosley", 19, TIMESTAMP("2021-02-13",  "00:00:00"), "nativeBig.jpg","nativeSmall.png",21.33, 22.23, 14.61, 0.54, 0.24,0.17, 1, null),
(13, "Endgame", TIMESTAMP("2011-03-25",  "00:00:00"), "Rise Against", "DGC", 13, TIMESTAMP("2021-02-13",  "00:00:00"), "endGame-Big.jpg","endGame-small.jpg",19.11, 20.21, 15.95, 0.54, 0.24,0.17, 1, null),
(14, "Artpop", TIMESTAMP("2013-11-06",  "00:00:00"), "Lady Gaga", "Streamline", 14, TIMESTAMP("2021-02-13",  "00:00:00"), "artPop-Big.jpg","artPop-Small.jpg",16.93, 13.12, 10.13, 0.66, 0.12,0.27, 1, null),
(15, "Teenage Dream", TIMESTAMP("2010-08-24",  "00:00:00"), "Katy Perry" ,"Capitol", 12, TIMESTAMP("2021-02-13",  "00:00:00"), "TeenageDream-Big.jpg","TeenageDream-Small.jpg",19.00, 17.00, 15.99, 0.66, 0.12,0.27, 1, null),
(16, "Pink Friday: Roman Reloaded", TIMESTAMP("2012-04-02",  "00:00:00"), "Nicki Minaj", "Young Money", 23, TIMESTAMP("2021-02-13",  "00:00:00"), "Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg",23.31, 20.00, 19.35, 0.66, 0.12,0.27, 1, null),
(17, "Laundry Service", TIMESTAMP("2001-11-13",  "00:00:00"), "Shakira", "Epic", 13, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",18.99, 17.99, 13.99, 0.66, 0.12,0.27, 1, null),
(18, "All That We Have Now", TIMESTAMP("2012-08-08",  "00:00:00"),"Fear, and Loathing in Las Vegas", "VAP", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "all-we-have-now-Big.jpg","all-we-have-now-Small.jpg",19.05, 17.95, 15.21, 0.66, 0.12,0.27, 1, null),
(19, "the WORLD Ruler", TIMESTAMP("2007-02-27",  "00:00:00"), "NIGHTMARE", "VAP", 13, TIMESTAMP("2021-02-13",  "00:00:00"), "the-world-ruler-big.jpg","the-World-Rule-Small.jpg",23.99, 20.99, 21.99, 0.45, 0.32,0.03, 1, null),
(20, "AREA Z", TIMESTAMP("2016-06-29",  "00:00:00"), "JAM Project", "Lantis", 15, TIMESTAMP("2021-02-13",  "00:00:00"), "areaz-Big.jpeg","area-z-small.jpg",17.00, 18.99, 15.35, 0.45, 0.32,0.03, 1, null),
(21, "Fixion", TIMESTAMP("2016-01-05",  "00:00:00"), "THE ORAL CIGARETTES", "A-Sketch", 10, TIMESTAMP("2021-02-13",  "00:00:00"), "Fixion-Big.jpg","Fixion-Small.jpg",21.99, 16.99, 17.59, 0.45, 0.32,0.03,  1, null),
(22, "Shingeki no Kiseki", TIMESTAMP("2017-05-17",  "00:00:00"), "Linked Horizon", "Pony Canyon", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg",15.00, 13.00, 11.00, 0.45, 0.32,0.03, 1, null);


INSERT INTO MUSIC_TRACK (INVENTORYID, ALBUMID, TRACKTITLE, ARTIST, SONGWRITER, PLAYLENGTH, SELECTIONNUMBER, MUSICCATEGORY, COSTPRICE, LISTPRICE, SALEPRICE, PST,GST,HST,DATEENTERED , ALBUMIMAGEFILENAMEBIG, ALBUMIMAGEFILENAMESMALL,PARTOFALBUM,  REMOVALSTATUS, REMOVALDATE) values
(1, 1, "Get Ugly", "Lil Baby", "Dominique Jones", 2.35, 1, "Hip hop", 5.66, 5.00, 4.23, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(2, 1, "Woah", "Lil Baby", "Dominique Jones", 3.03, 5, "Hip hop", 6.75, 7.00, 4.12, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(3, 1, "Emotionally Scarred", "Lil Baby", "Dominique Jones", 7.34, 8, "Hip hop", 5.12, 5.00, 3.23, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(4, 1, "Same Thing", "Lil Baby", "Dominique Jones", 2.42, 7, "Hip hop", 3.66, 2.13, 2.34, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(5, 1, "All In", "Lil Baby", "Dominique Jones", 2.36, 22, "Hip hop", 4.75, 3.00, 2.23, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(6, 2, "DNA", "Kendrick Lamar", "Kendrick Duckworth", 3.06, 2, "Hip hop", 7.67, 7.00, 5.23, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(7, 2, "ELEMENT", "Kendrick Lamar", "Kendrick Duckworth", 3.29, 4, "Hip hop", 7.21, 7.50, 4.75, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(8, 2,  "HUMBLE", "Kendrick Lamar", "Kendrick Duckworth", 2.57, 8, "Hip hop", 7.45, 8.00, 5.20, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(9, 2,  "FEAR", "Kendrick Lamar", "Kendrick Duckworth", 7.41, 12, "Hip hop", 6.50, 7.00, 3.22, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"),"damn_big.jpg","damn_small.png", 0, 1, null ),
(10, 2, "DUCKWORTH", "Kendrick Lamar", "Kendrick Duckworth", 4.09, 14, "Hip hop", 8.42, 9.00, 5.43, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(11, 3, "Martin & Gina", "Polo G", "Taurus Bartlett", 2.13, 3, "Hip hop", 7.07, 7.30, 5.23, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",0, 1, null ),
(12, 3, "Beautiful Pain", "Polo G", "Taurus Bartlett", 2.51, 9, "Hip hop", 5.91, 6.50, 2.75, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"),"the_goat_big.png","the_goat_small.png", 0, 1, null ),
(13, 3, "DND", "Polo G", "Taurus Bartlett", 3.00, 13, "Hip hop", 7.45, 8.45, 4.20, 0.25, 0.37,0.19,   TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",0, 1, null ),
(14, 3, "Chinatown", "Polo G", "Taurus Bartlett", 2.53, 14, "Hip hop", 6.78, 7.00, 3.22, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",0, 1, null ),
(15, 3, "Trials & Tribulations", "Polo G", "Taurus Bartlett", 2.57, 15, "Hip hop", 7.32, 7.30, 5.13, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"the_goat_big.png","the_goat_small.png", 0, 1, null ),
(16, 4, "Survival", "Drake", "Aubrey Graham", 2.16, 1, "Hip hop", 8.12, 8.30, 6.20, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"scorpion_big.png","scorpion_small.jpg", 0, 1, null ),
(17, 4, "Nonstop", "Drake", "Aubrey Graham", 3.58, 1, "Hip hop", 5.23, 5.30, 2.20, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"), "scorpion_big.png","scorpion_small.jpg",0, 1, null ),
(18, 4, "Mob Ties", "Drake", "Aubrey Graham", 3.25, 1, "Hip hop", 4.32, 5.00, 2.23, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"scorpion_big.png","scorpion_small.jpg", 0, 1, null ),
(19, 4, "Can't Take a Joke", "Drake", "Aubrey Graham", 2.43, 1,  "Hip hop", 5.78, 6.00, 3.20, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"scorpion_big.png","scorpion_small.jpg", 0, 1, null ),
(20, 4, "Nice for What", "Drake", "Aubrey Graham", 3.30, 1,  "Hip hop", 8.09, 7.30, 7.40, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"), "scorpion_big.png","scorpion_small.jpg",0, 1, null ),
(21, 5, "ENTROPY", "Daniel Caesar", "Daniel Caesar", 4.21, 1, "R&B", 10.52, 10.00, 7.40, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",0, 1, null ),
(22, 5, "CYANIDE", "Daniel Caesar", "Daniel Caesar", 3.14, 2, "R&B", 9.92, 9.00, 6.20, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"),"CaseStudy01-Big.jpg","CaseStudy01-Small.jpg", 0, 1, null ),
(23, 5, "LOVE AGAIN", "Daniel Caesar", "Daniel Caesar", 3.34, 3, "R&B", 11.12, 11.00, 9.40, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",0, 1, null ),
(24, 5, "OPEN UP", "Daniel Caesar", "Daniel Caesar", 4.26, 5, "R&B", 8.82, 8.00, 6.43, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",0, 1, null ),
(25, 5, "COMPLEXITIES", "Daniel Caesar", "Daniel Caesar", 3.50, 9, "R&B", 9.32, 9.00, 5.40, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"),"CaseStudy01-Big.jpg","CaseStudy01-Small.jpg", 0, 1, null ),
(26, 6, "Supermodel", "SZA", "SZA", 3.01, 1, "R&B", 11.12, 11.00, 10.50, 0.11, 0.22,0.09, TIMESTAMP("2021-02-13",  "00:00:00"),"Crtl-Big.jpg","Crtl-Small.jpg", 0, 1, null ),
(27, 6, "Love Galore", "SZA", "SZA", 4.35, 2, "R&B", 9.62, 9.00, 8.57, 0.11, 0.22,0.09, TIMESTAMP("2021-02-13",  "00:00:00"),"Crtl-Big.jpg","Crtl-Small.jpg", 0, 1, null ),
(28, 6, "Doves in the Wind", "SZA", "SZA", 4.26, 3, "R&B", 9.67, 9.00, 7.40, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",0, 1, null ),
(29, 6, "Drew Barrymore", "SZA", "SZA", 3.51, 4, "R&B", 10.52, 10.00, 7.35, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",0, 1, null ),
(30, 6, "Prom", "SZA", "SZA", 3.16, 5, "R&B", 8.42, 8.00, 6.40, 0.17, 0.27,0.07,  TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",0, 1, null ),
(31, 7, "EARFQUAKE", "Tyler, The Creator", "Tyler, The Creator", 3.10, 2, "R&B", 12.41, 11.00, 9.42, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"),"IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg", 0, 1, null ),
(32, 7, "I THINK", "Tyler, The Creator", "Tyler, The Creator", 3.32, 3, "R&B", 8.99, 9.00, 5.99, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"),"IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg", 0, 1, null ),
(33, 7, "RUNNING OUT OF TIME", "Tyler, The Creator", "Tyler, The Creator", 2.57, 5, "R&B", 10.34, 10.00, 8.43, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"),"IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg", 0, 1, null ),
(34, 7, "A BOY IS A GUN", "Tyler, The Creator", "Tyler, The Creator", 3.30, 7, "R&B", 11.32, 11.00, 10.21, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg",0, 1, null ),
(35, 7, "ARE WE STILL FRIENDS?", "Tyler, The Creator", "Tyler, The Creator", 4.25, 12, "R&B", 9.76, 9.00, 7.20, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg",0, 1, null ),
(36, 8, "SLOW DANCING IN THE DARK", "Joji", "Joji", 2.08, 2, "R&B", 10.02, 10.00, 8.17, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(37, 8, "TEST DRIVE", "Joji", "Joji", 3.29, 3, "R&B", 7.78, 8.00, 4.78, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(38, 8, "YEAH RIGHT", "Joji", "Joji", 2.59, 6, "R&B", 8.67, 8.50, 5.29, 0.06, 0.06,0.06,  TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(39, 8, "NO FUN", "Joji", "Joji", 2.54, 8, "R&B", 7.42, 7.00, 3.63, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(40, 8, "ATTENTION", "Joji", "Joji", 2.48, 1, "R&B", 9.92, 9.00, 7.14, 0.06, 0.06,0.06,  TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(41, 9, "It's Time", "Imagine Dragons", "Ben McKee, Andrew Tolman, Brittany Tolman, Dan Reynolds, Wayne Sermon, Daniel Platzman", 4.00, 3, "Rock", 9.50, 9.00, 7.44, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(42, 9, "Radioactive", "Imagine Dragons", "Alexander Grant, Ben McKee, Josh Mosser, Daniel Platzman, Dan Reynolds, Wayne Sermon", 3.06, 1, "Rock", 10.50, 10.00, 7.86, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"), "nightVisions-Big.jpeg","nightVisions-Small.jpg",0, 1, null ),
(43, 9, "Hear Me", "Imagine Dragons", "Ben McKee,Dan Platzman, Dan Reynolds Wayne Sermon", 3.55, 7, "Rock", 7.87, 8.00, 5.64, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(44, 9, "Demons", "Imagine Dragons", "Ben McKee, Adam Baachaoui, Daniel Platzman, Dan Reynolds, Wayne Sermon, Alexander Grant, Josh Mosser", 2.57, 4, "Rock", 9.70, 10.10, 6.46, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(45, 9, "On Top of the World", "Imagine Dragons", "Ben McKee, Dan Platzman, Dan Reynolds, Wayne Sermon, Alexander Grant", 3.12, 5, "Rock", 8.50, 8.00, 5.34, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(46, 10, "Say Amen (Saturday Night)", "Panic at the Disco", "Brendon Urie, Jake Sinclair, Sam Hollander, Lauren Pritchard, Imad Royal, Andrew Greene, Mike Deller, Brian Profilio, Thomas Brenneck, Daniel Foder, Jared Tankel, Nathan Abshire, Suzy Shinn, Tom Peyton, Tobias Wincorn", 3.09, 2, "Rock", 7.50, 8.00, 3.34, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 0, 1, null ),
(47, 10, "High Hopes", "Panic at the Disco", "Brendon Urie, Jake Sinclair, Jenny Owen Youngs, Lauren Pritchard, Sam Hollander, William Lobban-Bean, Jonas Jeberg, Taylor Parks, Ilsey Juber", 3.10, 4, "Rock", 9.50, 10.00, 5.45, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 0, 1, null ),
(48, 10, "Hey Look Ma, I Made It", "Panic at the Disco", "Brendon Urie, Dillon Francis, Michael Angelakos, Sam Hollander, Jake Sinclair, Morgan Kibby", 2.49, 3, "Rock", 10.02, 10.01, 8.82, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 0, 1, null ),
(49, 11, "American Beauty/American Psycho", "Fallout Boy", "Pete Wentz, Patrick Stump, Joseph Trohman, Andy Hurley, Sebastian Akchoté-Bozovic, Nikki Sixx", 3.13,2, "Rock", 9.55, 9.50, 7.82, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg", 0, 1, null ),
(50, 11, "Centuries", "Fallout Boy", "Michael Fonseca, Raja Kumari, J.R. Rotem, Justin Tranter, Andy Hurley, Patrick Stump, Joe Trohman, Suzanne Vega, Pete Wentz", 3.48,3, "Rock", 8.51, 8.50, 4.65, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg", 0, 1, null ),
(51, 11, "Uma Thurman", "Fallout Boy", "Patrick Stump, Pete Wentz, Joe Trohman, Andy Hurley, Liam O'Donnell, Jarrel Young, Waqaas Hashmi", 3.31,5, "Rock", 10.22, 10.04, 6.52, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg", 0, 1, null ),
(52, 11, "Irresistible", "Fallout Boy", "Pete Wentz, Patrick Stump, Joe Trohman, Andy Hurley", 3.26,1, "Rock", 8.77, 8.72, 5.63, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg",0, 1, null ),
(53, 12, "If I Lose Myself", "One Republic", "Ryan Tedder, Brent Kutzle, Zach Filkins, Benjamin Levin", 4.01,3, "Rock", 9.47, 9.45, 5.43, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(54, 12, "Counting Stars", "One Republic", "Ryan Tedder", 4.19,1, "Rock", 9.97, 9.99, 6.94, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(55, 12, "Something I Need", "One Republic", "Ryan Tedder Benny Blanco", 4.01,11, "Rock", 10.43, 10.45, 7.21, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(56, 12, "Love Runs Out", "One Republic", "Ryan Tedder, Brent Kutzle, Drew Brown, Zach Filkins, Eddie Fisher", 3.44,2, "Rock", 8.77, 8.45, 5.43, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(57, 12, "I Lived", "One Republic", "Ryan Tedder, Noel Zancanella",   3.55,5, "Rock", 9.56, 9.55, 7.43, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "nativeBig.jpg","nativeSmall.png",0, 1, null ),
(58, 13, "Help Is on the Way", "Rise Against", "Rise Against", 3.57,2, "Rock", 9.86, 9.30, 7.12, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"endGame-Big.jpg","endGame-small.jpg", 0, 1, null ),
(59, 13, "Make It Stop (September's Children)", "Rise Against", "Rise Against", 3.55,3, "Rock", 9.03, 9.01, 6.19, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"endGame-Big.jpg","endGame-small.jpg", 0, 1, null ),
(60, 13, "Satellite", "Rise Against", "Rise Against", 3.58,5, "Rock", 9.66, 9.20, 6.91, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"endGame-Big.jpg","endGame-small.jpg", 0, 1, null ),
(61, 14, "Applause", "Lady Gaga", "Stefani Germanotta“, Paul “DJ White Shadow“ Blair, Dino Zisis, Nick Monson, Martin Bresso, Nicolas Mercier, Julien Arias, William Grigahcine", 3.32,15, "Pop", 6.66, 6.50, 3.13, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"artPop-Big.jpg","artPop-Small.jpg", 0, 1, null ),
(62, 14, "Do What U Want", "Lady Gaga", "Stefani Germanotta, Paul Blair, Robert Kelly, Martin Bresso, William Grigahcine", 3.47,7, "Pop", 10.33, 10.22, 9.11, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"artPop-Big.jpg","artPop-Small.jpg", 0, 1, null ),
(63, 14, "G.U.Y.", "Lady Gaga", "Stefani Germanotta, Anton Zaslavski", 3.52,3, "Pop", 10.22, 10.11, 9.01, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"artPop-Big.jpg","artPop-Small.jpg", 0, 1, null ),
(64, 15, "California Gurls", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Benjamin Levin, Bonnie McKee, Calvin Broadus, Brian Wilson, Mike Love", 3.56,3, "Pop", 7.25, 7.50, 4.43, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"), "TeenageDream-Big.jpg","TeenageDream-Small.jpg",0, 1, null ),
(65, 15, "Teenage Dream", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Benjamin Levin, Bonnie McKee", 3.47,1, "Pop", 7.75, 7.80, 4.32, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"), "TeenageDream-Big.jpg","TeenageDream-Small.jpg",0, 1, null ),
(66, 15, "Firework", "Katy Perry", "Katy Perry, Mikkel S. Eriksen, Tor Erik Hermansen, Sandy Wilhelm, Ester Dean", 3.48,4, "Pop", 7.40, 7.50, 4.23, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"TeenageDream-Big.jpg","TeenageDream-Small.jpg", 0, 1, null ),
(67, 15, "E.T.", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Joshua Coleman, Kanye West", 3.51,8, "Pop", 7.30, 7.35, 4.11, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"), "TeenageDream-Big.jpg","TeenageDream-Small.jpg",0, 1, null ),
(68, 15, "Last Friday Night (T.G.I.F.)", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Bonnie McKee", 3.50,2, "Pop", 7.60, 7.65, 4.37, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"TeenageDream-Big.jpg","TeenageDream-Small.jpg", 0, 1, null ),
(69, 15, "The One That Got Away", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin", 3.47,7, "Pop", 7.55, 7.75, 4.69, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"TeenageDream-Big.jpg","TeenageDream-Small.jpg", 0, 1, null ),
(70, 15, "Hummingbird Heartbeat", "Katy Perry", "Katy Perry, Christopher “Tricky“ Stewart, Stacy Barthe, Monte Neuble", 3.32,11, "Pop", 7.45, 7.55, 4.57, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"), "TeenageDream-Big.jpg","TeenageDream-Small.jpg",0, 1, null ),
(71, 16, "Starships", "Nicki Minaj", "Onika Maraj, Nadir Khayat, Carl Falk, Rami Yacoub, Wayne Hector, Bilal Hajji", 3.30,10, "Pop", 6.45, 6.55, 3.57, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(72, 16, "Right by My Side", "Nicki Minaj", "Onika Maraj, Warren “Oak“ Felder, Andrew “Pop“ Wansel, Ester Dean, Jameel Roberts, Ronald “Flippa“ Colson",   4.25,8, "Pop", 6.75, 6.95, 3.09, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(73, 16, "Beez in the Trap", "Nicki Minaj", "Onika Maraj, Maurice Jordan, Tauheed Epps", 4.28,4, "Pop", 6.99, 7.00, 3.97, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"), "Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg",0, 1, null ),
(74, 16, "Pound the Alarm", "Nicki Minaj", "Onika Maraj, RedOne, Carl Falk, Rami Yacoub, Bilal Hajji, Achraf Jannusi", 3.25,11, "Pop", 6.85, 7.00, 3.31, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(75, 16, "Va Va Voom", "Nicki Minaj", "Onika Maraj, Lukasz Gottwald, Allan Grigg, Max Martin, Henry Walter", 3.03,21, "Pop", 6.95, 7.05, 3.13, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(76, 17, "Whenever, Wherever","Shakira", "Shakira, Tim Mitchell, Gloria Estefan", 3.16,3, "Pop", 7.55, 8.00, 3.17, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",0, 1, null ),
(77, 17, "Underneath Your Clothes", "Shakira", "Shakira, Lester Mendez", 3.44,2, "Pop", 7.85, 8.0, 3.37, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",0, 1, null ),
(78, 17, "Objection (Tango)", "Shakira", "Shakira", 3.42,1, "Pop", 7.55, 8.00, 3.42, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"landryService-Big.jpg","landryService-Small.jpg", 0, 1, null ),
(79, 17, "Te Dejo Madrid", "Shakira", "Shakira, Tim Mitchell, George Noriega", 3.07,8, "Pop", 7.65, 8.00, 2.83, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",0, 1, null ),
(80, 17, "Que Me Quedes Tú", "Shakira", "Shakira, Luis Fernando Ochoa", 4.48,10, "Pop", 7.75, 8.00, 3.43, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"landryService-Big.jpg","landryService-Small.jpg", 0, 1, null ),
(81, 18, "Shinzou wo Sasageyo", "Linked Horizon", "REVO", 5.41,11, "Anime", 9.75, 10.00, 5.34, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(82, 18, "Guren no Yumiya", "Linked Horizon", "REVO", 5.10,4, "Anime", 9.65, 10.00, 5.31, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(83, 18, "Saigo no Senka", "Linked Horizon", "REVO", 5.28,5, "Anime", 9.55, 10.00, 5.32, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(84, 18, "Jiyuu no Tsubasa", "Linked Horizon", "REVO", 5.31,7, "Anime", 9.45, 10.00, 5.39, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(85, 19, "Kizukeyo Baby", "THE ORAL CIGARETTES", "Takuya Yamanaka", 4.14,1, "Anime", 10.00, 11.00, 6.61, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"the-world-ruler-big.jpg","the-World-Rule-Small.jpg", 0, 1, null ),
(86, 19, "Kyouran Hey Kids!!", "THE ORAL CIGARETTES", "Takuya Yamanaka", 4.10,2, "Anime", 9.80, 10.00, 6.51, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "the-world-ruler-big.jpg","the-World-Rule-Small.jpg",0, 1, null ),
(87, 19, "A-E-U-I", "THE ORAL CIGARETTES", "Takuya Yamanaka", 3.57,9, "Anime", 9.60, 10.00, 6.21, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"the-world-ruler-big.jpg","the-World-Rule-Small.jpg", 0, 1, null ),
(88, 19, "STAY ONE", "THE ORAL CIGARETTES", "Takuya Yamanaka", 3.33,4, "Anime", 9.40, 10.00, 6.11, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"the-world-ruler-big.jpg","the-World-Rule-Small.jpg", 0, 1, null ),
(89, 20, "Survive", "JAM Project", "Hironobu Kageyama", 3.46,7, "Anime", 7.45, 8.00, 3.61, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"areaz-Big.jpeg","area-z-small.jpg", 0, 1, null ),
(90, 20, "THE HERO !!", "JAM Project", "Hiroshi Kitadani", 4.17,10, "Anime", 7.47, 8.00, 3.67, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"areaz-Big.jpeg","area-z-small.jpg", 0, 1, null ),
(91, 20, "WE ARE ONE", "JAM Project", "Masaaki Endo", 6.01,6, "Anime", 7.46, 8.00, 3.62, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"areaz-Big.jpeg","area-z-small.jpg", 0, 1, null ),
(92, 20, "NAWABARI", "JAM Project", "Hironobu Kageyama", 5.24,2, "Anime", 7.49, 8.00, 3.69, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "areaz-Big.jpeg","area-z-small.jpg",0, 1, null ),
(93, 21, "the WORLD", "NIGHTMARE", "Jonathan Cain", 4.02,3, "Anime", 6.49, 6.50, 2.91, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(94, 21, "BOYS BE SUSPISOUS", "NIGHTMARE", "Jonathan Cain", 4.03,1, "Anime", 6.49, 6.50, 2.91, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(95, 21, "Criminal baby", "NIGHTMARE", "Jonathan Cain", 3.34,4, "Anime", 6.49, 6.50, 2.91, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(96, 21, "Alice", "NIGHTMARE", "Jonathan Cain", 4.15,5, "Anime", 6.49, 6.50, 2.91, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(97, 22, "Just Awake", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.39,6, "Anime", 7.65, 8.00, 3.94, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null ),
(98, 22, "Crossover", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.12,3, "Anime", 7.60, 8.00, 3.49, 0.23, 0.19, 0.31, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null ),
(99, 22, "Acceleration", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.08,1, "Anime", 7.43, 8.00, 3.12, 0.23, 0.19, 0.31, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null ),
(100, 22,"Scream Hard as You Can", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.56,2, "Anime", 7.11, 8.00, 3.57, 0.23, 0.19, 0.31, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null );


INSERT INTO CLIENT (CLIENTID, TITLE, LASTNAME, FIRSTNAME, COMPANYNAME, ADDRESS1, ADDRESS2, CITY, PROVINCE, COUNTRY, POSTALCODE, HOMETELEPHONE, CELLTELEPHONE, EMAIL, GENREOFLASTSEARCH) VALUES
(1, "Rev", "Burle", "Alli", "Wolf Group", "08957 Rutledge Trail", "495 Sheridan Parkway", "Ponoka", "Alberta", "Canada", "T4J", 1468741332, 7241183761, "aburle0@tinyurl.com", "Pop"),
(2, "Ms", "Aish", "Quincey", "Sauer“O“Connell and Feeney", "5208 Messerschmidt Plaza", "5044 Canary Point", "Greensboro", "North Carolina", "United States", "27415", 3365404602, 4957162459, "qaish1@fema.gov", "Pop"),
(3, "Mrs", "Anyon", "Amara", "Gottlieb, Toy and Ankunding", "668 Oriole Circle", "7 Bay Trail", "Atlanta", "Georgia", "United States", "30316", 4041939405, 7598581468, "aanyon2@army.mil", "Pop"),
(4, "Honorable", "Shellum", "Lucien", "Cummings Group", "81203 Warrior Point", "26 Lyons Circle", "Savannah", "Georgia", "United States", "31422", 4789255309, 5812443576, "lshellum3@thetimes.co.uk", "Pop"),
(5, "Rev", "Glasard", "Andy", "Johnson, Medhurst and Huels", "3791 Vidon Place", "2 Mallory Junction", "Baie-Saint-Paul", "Québec", "Canada", "G3Z", 9956132649, 6136927654, "aglasard4@godaddy.com", "Hip hop"),
(6, "Mr", "Mayberry", "Chic", "McLaughlin, Kemmer and Dietrich", "1431 Delladonna Court", "3939 Cherokee Point", "Casselman", "Ontario", "Canada", "G8A", 9313712912, 3166935628, "cmayberry5@canalblog.com", "Hip hop"),
(7, "Rev", "Siviour", "Marcos", "Miller, Veum and Windler", "25 Meadow Vale Point", "2492 Garrison Alley", "Albanel", "Québec", "Canada", "G8M", 5392188528, 9392987138, "msiviour6@dion.ne.jp", "Hip hop"),
(8, "Dr", "Aslie", "Chase", "Bartoletti Group", "47215 Scoville Trail", "48 Northfield Plaza", "Amarillo", "Texas", "United States", "79176", 2818742780, 1131223695, "caslie7@people.com.cn", "Hip hop"),
(9, "Rev", "Borrowman", "Helen-elizabeth", "Roob-Crist", "63298 Dryden Street", "08 3rd Terrace", "Knoxville", "Tennessee", "United States", "37995", 8655788675, 1432457839, "hborrowman8@php.net", "Rock"),
(10,"Mr", "Metham", "Kania", "Gleichner Inc", "9810 Westport Point", "9 Florence Place", "Ajax", "Ontario", "Canada", "L1Z", 8299069589, 4363009404, "kmetham9@ucoz.ru", "Rock"),
(11,"Rev", "Zimmerman", "Marquita", "Kunde Group", "57769 Maywood Parkway", "4 Northland Junction", "Grand Bank", "Newfoundland and Labrador", "Canada", "E8K", 4091189935, 8278455755, "mzimmermana@phoca.cz", "Rock"),
(12,"Ms", "Seathwright", "Arline", "DuBuque, Schumm and Hettinger", "03 Mesta Place", "6 Morning Court", "Saint Petersburg", "Florida", "United States", "33710", 7278361158, 8797428462, "aseathwrightb@feedburner.com", "Rock"),
(13,"Mr", "Brounsell", "Kelley", "Marvin, Russel and Purdy", "91 Rowland Drive", "4544 South Drive", "Kansas City", "Kansas", "United States", "66112", 8161198258, 7665016517, "kbrounsellc@jiathis.com", "Anime"),
(14,"Rev", "Mazonowicz", "Shayne", "Dickens-Jakubowski", "31 Barby Alley", "517 Prairie Rose Road", "Corona", "California", "United States", "92878", 9517400149, 1384334954, "smazonowiczd@ezinearticles.com", "Anime"),
(15,"Mr", "Drohun", "Tedie", "Stiedemann Inc", "6704 Stuart Road", "22540 Annamark Hill", "Burgeo", "Newfoundland and Labrador", "Canada", "N9A", 8409115038, 2029736271, "tdrohune@jiathis.com", "Anime"),
(16,"Mrs", "Capon", "See", "Lynch LLC", "99572 Hudson Court", "59 Laurel Lane", "Richmond", "Virginia", "United States", "23285", 8047730443, 9796802414, "scaponf@last.fm", "Anime"),
(17,"Rev", "Kildale", "Francisco", "Mayer LLC", "85 Vera Road", "360 Schiller Terrace", "Maskinongé", "Québec", "Canada", "T7A", 3692271272, 8946119827, "fkildaleg@answers.com", "R&B"),
(18,"Dr", "Poulett", "Delly", "Pollich, Jacobson and Block", "217 Annamark Point", "71 Lotheville Park", "Parrsboro", "Nova Scotia", "Canada", "L2A", 4471914490, 1529172744, "dpouletth@businesswire.com", "R&B"),
(19,"Dr", "Sacker", "Belle", "Schulist-Blanda", "6 Huxley Hill", "58 Bowman Avenue", "Saint Louis","Missouri", "United States", "63136", 3147308600, 3817116947, "bsackeri@europa.eu", "R&B"),
(20,"Mr", "Canby", "Grover", "Bergstrom, Schinner and Hagenes", "986 Norway Maple Hill", "0 Clove Center", "Mobile", "Alabama", "United States", "36670", 2519196520, 1782694487, "gcanbyj@creativecommons.org", "R&B");

INSERT INTO REVIEW (REVIEWID, REVIEWDATE, CLIENTNAME, RATING, REVIEWTEXT, APROVALSTATUS, CLIENTID, INVENTORYID) 
VALUE 
(1, TIMESTAMP("2020-01-01", "11:03:11"), "Alli", 9, "Great product! This Music works extremely well. It wetly improves my tennis by a lot.", 0, 1, 1),
(2, TIMESTAMP("2020-03-05", "08:16:34"), "Alli", 6, "Song isn’t that good to be honest", 1, 1, 1),
(3, TIMESTAMP("2020-11-13", "04:13:55"), "Quincey", 4, "Definitely not a great song", 1, 2, 2),
(4, TIMESTAMP("2021-01-09", "10:30:31"), "Alli", 5, "Heard better music in my day but its alright", 1, 1, 2),
(5, TIMESTAMP("2017-02-18", "04:20:00"), "Amara", 9, "one of my hobbies is programming. and when i'm programming this music works great", 1, 3, 3),
(6, TIMESTAMP("2019-04-16", "11:03:11"), "Lucien", 8, "Song isn’t that good to be honest", 0, 4, 4),
(7, TIMESTAMP("2018-06-22", "10:30:31"), "Andy", 7, "This Music works excessively well. It speedily improves my baseball by a lot. talk about surprise!!!", 1, 5, 5),
(8, TIMESTAMP("2018-04-14", "04:20:00"), "Andy", 9, "This Music works so well. It delightedly improves my football by a lot.", 0, 5, 6),
(9, TIMESTAMP("2020-08-22", "08:16:34"), "Andy", 9, "Song isn’t that good to be honest", 1, 5, 7),
(10, TIMESTAMP("2016-12-07", "10:30:31"), "Quincey", 8, "Song is so good. You must take a listen, you will not regret it I am sure", 1, 2, 8),
(11, TIMESTAMP("2020-03-08", "11:03:11"), "Alli", 9, "this Music is hyper.", 0, 1, 9),
(12, TIMESTAMP("2019-05-01", "04:13:55"), "Quincey",7 , "My coworker told me about this song. It is pretty good", 1, 2, 10),
(13, TIMESTAMP("2020-06-03", "08:16:34"), "Amara", 9, "Solid song. I put this on replay", 0, 3, 11),
(14, TIMESTAMP("2020-03-05", "11:03:11"), "Amara", 8, "A replay type of song wow", 1, 3, 12),
(15, TIMESTAMP("2020-03-05", "04:13:55"), "Quincey", 9, "AMAZING! Highly recommend taking a listen", 1, 2, 13);

INSERT INTO BANNERAD (BANNERADDID, FILENAME, URL) VALUES
(1, "walmartbanner.png”", "https://www.walmart.com/browse/home/personalized-doormats/4044_133224_9107110_2596420?adid=22222222224428692885&wmlspartner=wmtlabs&wl0=b&wl1=s&wl2=c&wl3=74492020453699&wl4=kwd-74492109399488&wl5=5433&wl6=&wl7=&wl14=walmart&veh=sem&msclkid=dc49902e8f93102a4985f00051104fc4"),
(2, "amazonbanner.png", "https://www.amazon.com/"),
(3, "hmbanner.png", "https://www2.hm.com/en_ca/index.html"),
(4, "logitechbanner.png", "https://www.cdw.com/product/Logitech-F310-Gamepad/2168504?cm_cat=bing&cm_ite=2168504&cm_pla=NA-NA-Logitech_PD&cm_ven=acquirgy&ef_id=b24db09c7341170d30d2d0b2ada242e5:G:s&s_kwcid=AL!4223!10!73255048915844!73254985619399"),
(5, "disneybanner.png", "https://disneyparks.disney.go.com/ca/");

INSERT INTO SALE (SALEID, CLIENTID, SALEDATE) VALUES
(1, 1,TIMESTAMP("2021-02-13",  "00:00:00")),
(2, 1,TIMESTAMP("2021-02-13",  "00:00:00")),
(3, 1,TIMESTAMP("2021-02-13",  "00:00:00")),
(4, 2,TIMESTAMP("2021-02-13",  "00:00:00")),
(5, 2,TIMESTAMP("2021-02-13",  "00:00:00")),
(6, 3,TIMESTAMP("2021-02-13",  "00:00:00")),
(7, 3,TIMESTAMP("2021-02-13",  "00:00:00")),
(8, 4,TIMESTAMP("2021-02-13",  "00:00:00")),
(9, 5,TIMESTAMP("2021-02-13",  "00:00:00")),
(10, 6,TIMESTAMP("2021-02-13",  "00:00:00")),
(11, 6,TIMESTAMP("2021-02-13",  "00:00:00")),
(12, 7,TIMESTAMP("2021-02-13",  "00:00:00")),
(13, 8,TIMESTAMP("2021-02-13",  "00:00:00")),
(14, 9,TIMESTAMP("2021-02-13",  "00:00:00")),
(15, 10,TIMESTAMP("2021-02-13", "00:00:00")),
(16, 11,TIMESTAMP("2021-02-13",  "00:00:00")),
(17, 12,TIMESTAMP("2021-02-13",  "00:00:00")),
(18, 13,TIMESTAMP("2021-02-13",  "00:00:00")),
(19, 14,TIMESTAMP("2021-02-13",  "00:00:00")),
(20, 15,TIMESTAMP("2021-02-13",  "00:00:00")),
(21, 16,TIMESTAMP("2021-02-13",  "00:00:00")),
(22, 17,TIMESTAMP("2021-02-13",  "00:00:00")),
(23, 18,TIMESTAMP("2021-02-13",  "00:00:00")),
(24, 19,TIMESTAMP("2021-02-13",  "00:00:00")),
(25, 19,TIMESTAMP("2021-02-13",  "00:00:00"));

INSERT INTO INVOICEDETAIL (INVOICEID, SALEDATE, SALEID, TOTALNETVALUE, TOTALGROSSVALUE, PST, GST, HST, INVENTORYID) VALUES
(4, TIMESTAMP("2021-02-13",  "00:00:00"),3, 6.45, 10.41,0.49, 0.03, 0.20, 45),
(5, TIMESTAMP("2021-02-13",  "00:00:00"),3, 6.48, 10.32,0.41, 0.33, 0.26, 67),
(6, TIMESTAMP("2021-02-13",  "00:00:00"),4, 4.52, 8.12,0.35, 0.09, 0.11, 51),
(7, TIMESTAMP("2021-02-13",  "00:00:00"),5, 8.25, 12.43,0.36, 0.09, 0.11, 12),
(8, TIMESTAMP("2021-02-13",  "00:00:00"),6, 8.61, 12.30,0.37, 0.09, 0.11, 91),
(11, TIMESTAMP("2021-02-13",  "00:00:00"),7, 4.97, 9.83,0.28, 0.09, 0.11, 73),
(12, TIMESTAMP("2021-02-13",  "00:00:00"),8, 6.80, 11.41,0.13, 0.09, 0.11, 43),
(17, TIMESTAMP("2021-02-13",  "00:00:00"),11, 4.45, 9.34,0.47, 0.09, 0.11, 49),
(19, TIMESTAMP("2021-02-13",  "00:00:00"),12, 6.85, 10.23,0.08, 0.21, 0.43, 31),
(20, TIMESTAMP("2021-02-13",  "00:00:00"),13, 3.75, 7.97,0.38, 0.12, 0.42, 21),
(22, TIMESTAMP("2021-02-13",  "00:00:00"),15, 8.76, 11.56,0.25, 0.14, 0.23, 34),
(24, TIMESTAMP("2021-02-13",  "00:00:00"),17, 7.63, 10.53,0.43, 0.39, 0.17, 46),
(25, TIMESTAMP("2021-02-13",  "00:00:00"),18, 7.41, 10.79,0.23, 0.26, 0.18, 55),
(27, TIMESTAMP("2021-02-13",  "00:00:00"),20, 6.12, 10.76,0.37, 0.19, 0.06, 43),
(28, TIMESTAMP("2021-02-13",  "00:00:00"),20, 4.65, 8.17,0.04, 0.06, 0.04, 11),
(29, TIMESTAMP("2021-02-13",  "00:00:00"),20, 9.45, 12.33,0.41, 0.39, 0.27, 81),
(31, TIMESTAMP("2021-02-13",  "00:00:00"),20, 5.98, 9.89,0.08, 0.05, 0.07, 66),
(32, TIMESTAMP("2021-02-13",  "00:00:00"),21, 4.11,9.67,0.27, 0.29, 0.16, 71),
(35, TIMESTAMP("2021-02-13",  "00:00:00"),24, 6.65, 11.73,0.21, 0.11, 0.21, 61);

INSERT INTO INVOICEDETAIL (INVOICEID, SALEDATE, SALEID, TOTALNETVALUE, TOTALGROSSVALUE, PST, GST, HST, ALBUMID) VALUES
(1, TIMESTAMP("2021-02-13",  "00:00:00"),1, 18.49, 23.59,0.33, 0.13, 0.12, 1),
(2, TIMESTAMP("2021-02-13",  "00:00:00"),1, 16.46, 21.44,0.47, 0.24, 0.16, 11),
(3, TIMESTAMP("2021-02-13",  "00:00:00"),2, 17.69, 22.65,0.52, 0.39, 0.03, 2),
(9, TIMESTAMP("2021-02-13",  "00:00:00"),7, 18.14, 23.73,0.34, 0.09, 0.11, 21),
(10, TIMESTAMP("2021-02-13",  "00:00:00"),7, 17.45, 21.53,0.32, 0.09, 0.11, 17),
(13, TIMESTAMP("2021-02-13",  "00:00:00"),9, 17.11, 21.10,0.12, 0.09, 0.11, 1),
(14, TIMESTAMP("2021-02-13",  "00:00:00"),10, 15.97, 20.01, 0.23, 0.09, 0.11, 9),
(15, TIMESTAMP("2021-02-13",  "00:00:00"),11, 16.93, 21.84,0.25, 0.09, 0.11, 19),
(16, TIMESTAMP("2021-02-13",  "00:00:00"),11, 18.65, 22.87,0.27, 0.09, 0.11, 3),
(18, TIMESTAMP("2021-02-13",  "00:00:00"),11, 15.47, 20.61,0.48, 0.09, 0.11, 16),
(21, TIMESTAMP("2021-02-13",  "00:00:00"),14, 17.11, 21.67,0.45, 0.13, 0.37, 5),
(23, TIMESTAMP("2021-02-13",  "00:00:00"),16, 17.25, 21.39,0.04, 0.36, 0.20, 8),
(36, TIMESTAMP("2021-02-13",  "00:00:00"),25, 15.45, 19.52,0.41, 0.19, 0.05, 22),
(37, TIMESTAMP("2021-02-13",  "00:00:00"),25, 17.45, 21.53,0.45, 0.19, 0.01, 21),
(26, TIMESTAMP("2021-02-13",  "00:00:00"),19, 20.11, 25.99,0.24, 0.29, 0.15, 5),
(30, TIMESTAMP("2021-02-13",  "00:00:00"),20, 16.56, 22.55,0.49, 0.43, 0.21, 1),
(33, TIMESTAMP("2021-02-13",  "00:00:00"),22, 16.43, 19.65,0.34, 0.05, 0.17, 16),
(34, TIMESTAMP("2021-02-13",  "00:00:00"),23, 19.45, 24.53, 0.24, 0.29, 0.13, 13);

INSERT INTO CREDITCARDINFO (CREDITCARDID, CREDITCARDBRAND, CREDITCARDNUMBER, EXPIRATIONMONTH,  EXPIRATIONYEAR , CLIENTID ) VALUES
(1,"Mastercard", 1234564748673759,7,2021,1),
(2,"Visa", 1111111111111111,11,2022,2),
(3,"Citibank", 3333333333333333,12,2029,1),
(4,"Chase", 1234564748673759,5,2030,3),
(5,"American Express", 1234564748673759,7,2027,4),
(6,"Capital One", 8574867439577723,3,2021,5),
(7, "Bank of America", 8893344334394958,1,2021,6),
(8, "Synchrony Financial", 0583598567054938,4,2028,7),
(9,"Discover", 1139584333546849,5,2028,8),
(10, "Wells Fargo", 0583695877865948,7,2025,9),
(11,"Mastercard", 0011284585746899,8,2029,10),
(12,"Visa", 6694578569467968,2,2031,11),
(13,"Citibank", 0064576958473958,1,2023,12),
(14,"Citibank", 0052958634896765,10,2024,13),
(15,"Capital One", 0049786477219356,2,2023,14), 
(16,"Synchrony Financial",  8563846944824806,3,2023,15),
(17,"Discover", 1122885395730912,4,2026,16),
(18,"American Express", 9538764833953832,5,2025,17),
(19,"Synchrony Financial", 9900442354267521,8,2027,18),
(20,"Chase", 4423222334651267,12,2021,18),
(21,"Chase", 3342997832110392,3,2024,19),
(22, "Discover", 4536897443569987,11,2025,19);

INSERT INTO RSS (ID, URL) VALUES
(1,"musicalmose.com/track"),
(2, "musicalmose.com/album"),
(3, "musicalmose.com/download"),
(4, "musicalmose.com/help"),
(5, "musicalmose.com/review");
