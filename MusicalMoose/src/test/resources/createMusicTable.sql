USE MUSICSTORAGE;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS SURVEYROW;
DROP TABLE IF EXISTS SURVEY;
DROP TABLE IF EXISTS NEWS;
DROP TABLE IF EXISTS ADDRESSES;
DROP TABLE IF EXISTS BANNERAD;
DROP TABLE IF EXISTS SALEBRALBUM;
DROP TABLE IF EXISTS SALEBRTRACK;
DROP TABLE IF EXISTS REVIEW;
DROP TABLE IF EXISTS SALE;
DROP TABLE IF EXISTS CLIENT;
DROP TABLE IF EXISTS ALBUM;
DROP TABLE IF EXISTS MUSIC_TRACK;
DROP TABLE IF EXISTS RSS;
DROP TABLE IF EXISTS INVOICEDETAIL;
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
    AVAILABLE BIT(1),
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
    AVAILABLE BIT(1),
    REMOVALDATE TIMESTAMP,
    CONSTRAINT fkalbumid FOREIGN KEY (ALBUMID) REFERENCES ALBUM(ALBUMID), 
    PRIMARY KEY (INVENTORYID)
);

CREATE TABLE CLIENT (
    CLIENTID INT NOT NULL auto_increment,
    USERNAME VARCHAR(50),
    PASSWORD VARCHAR(50),
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
    HOMETELEPHONE VARCHAR(25),
    CELLTELEPHONE VARCHAR(25),
    EMAIL VARCHAR(255),
    GENREOFLASTSEARCH VARCHAR(255),
    ISMANAGER BIT(1),
    CLIENTACTIVE BIT(1),
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


CREATE TABLE SALE(
    SALEID INT NOT NULL auto_increment,
    CLIENTID INT,
    SALEREMOVED BIT(1),
    CONSTRAINT fkclientid4 FOREIGN KEY (CLIENTID) REFERENCES CLIENT (CLIENTID),
    SALEDATE TIMESTAMP,
    PRIMARY KEY (SALEID)
);

CREATE TABLE INVOICEDETAIL(
    INVOICEID INT NOT NULL auto_increment,
    SALEDATE TIMESTAMP,
    SALEID INT ,
    CURRENTCOST FLOAT,
    PROFIT FLOAT,
    INVENTORYID INT, 
    ALBUMID INT,
    INVOICEDETAILREMOVED BIT(1),
    PRODUCTDOWNLOADED INT,
    CONSTRAINT fkinventoryid3 FOREIGN KEY (INVENTORYID) REFERENCES MUSIC_TRACK (INVENTORYID), 
    CONSTRAINT fkalbumid3 FOREIGN KEY (ALBUMID) REFERENCES ALBUM (ALBUMID),
    CONSTRAINT fksaleid3 FOREIGN KEY (SALEID) REFERENCES SALE (SALEID),
    CONSTRAINT CK_Task_OneNonNull CHECK ((INVENTORYID is null and ALBUMID is not null) OR (ALBUMID is null and INVENTORYID is not null)),
    PRIMARY KEY (INVOICEID)
);

CREATE TABLE BANNERAD (
    BANNERADDID INT NOT NULL auto_increment,
    URL MEDIUMTEXT,
    FILENAME VARCHAR(255),
    DISPLAYED BIT(1),
    PAGEPOSITION INT DEFAULT 0,
    PRIMARY KEY (BANNERADDID)
);

CREATE TABLE NEWS(
    NEWSID INT NOT NULL auto_increment,
    NEWTITLE VARCHAR(255),
    NEWSTEXT MEDIUMTEXT,
    CREATEDDATE TIMESTAMP,
    LASTDISPLAYEDDTAE TIMESTAMP,
    URL MEDIUMTEXT,
    DISPLAYED BIT(1),
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
    SURVERYINUSE BIT(1),
    PRIMARY KEY (SURVEYID)
);

CREATE TABLE RSS(
    ID INT NOT NULL auto_increment,
    URL VARCHAR(255),
    RSSREMOVED BIT(1),
    PRIMARY KEY (ID)
);

INSERT INTO NEWS (NEWSID, NEWTITLE, NEWSTEXT, CREATEDDATE, LASTDISPLAYEDDTAE, URL, DISPLAYED)
VALUES 
(1, "Elton John reveals he’s been working on “something” with Metallica", "Elton John has teased an unlikely collaboration with Metallica, after the singer joined forces with the metal icons during the latest coronavirus lockdown.", TIMESTAMP("2020-03-08", "11:03:11"), TIMESTAMP("2020-03-10", "11:03:11"),"https://www.nme.com/en_asia/news/music/elton-john-reveals-hes-been-working-on-something-with-metallica-2910055",1), 
(2, "Red Velvet’s Wendy unveils release date of upcoming solo debut", "Wendy, the main vocalist of K-pop girl group Red Velvet, is set to make her solo debut soon.", TIMESTAMP("2019-06-23", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"),"https://www.nme.com/en_asia/news/music/red-velvet-wendy-solo-debut-2021-april-2897448",1), 
(3, "Big Sean celebrates birthday with live performance of ‘Lucky Me’ and ‘Still I Rise’", "Big Sean celebrated his birthday this week with a live performance of new tracks ‘Lucky Me’ and ‘Still I Rise’ – watch it below.", TIMESTAMP("2021-01-23", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"),"https://www.nme.com/en_asia/news/music/big-sean-celebrates-birthday-with-live-performance-of-lucky-me-and-still-i-rise-2909838",1), 
(4, "Future joins Diddy’s son King Combs on sleek new track ‘Holdin Me Down’", "Future has teamed up with Diddy‘s son King Combs on a sleek new track called ‘Holdin Me Down’ – listen to it below.", TIMESTAMP("2021-02-08", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"),"https://www.nme.com/en_asia/news/music/future-joins-diddys-son-king-combs-on-sleek-new-track-holdin-me-down-2909817",0), 
(5, "5,000 people attend COVID-19 experiment gig in Barcelona", "Five thousand people attended a concert in Barcelona last night (March 27) as part of a COVID-19 experiment. The show, which saw Spanish rock band Love of Lesbian playing at the Palau Sant Jordi arena, is said to be the biggest concert in Europe since the pandemic began last year.", TIMESTAMP("2021-02-08", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"),"https://www.nme.com/en_asia/news/music/5000-people-attend-covid-19-experiment-gig-in-barcelona-2909802",0);

INSERT INTO SURVEY (SURVEYID, SURVEYTITLE, QUESTION, ANSERW1, ANSERW1VOTES, ANSERW2, ANSERW2VOTES, ANSERW3, ANSERW3VOTES, ANSERW4, ANSERW4VOTES, DATESURVEYRCREATED, DATELASTUSED, SURVERYINUSE)
VALUES 
(1, "Teens and music in todays generation", "Which genre of music do you listen to most?", "HIP HOP", 43, "R&B", 23, "Rock", 13, "Indie Music", 28, TIMESTAMP("2020-02-08", "11:03:11"), TIMESTAMP("2020-02-10", "11:03:11"), 1),
(2, "Most popular artist for elders", "Which artist are you most familiar with?", "Drake", 43, "Ariana Grande", 81, "Justin Bieber", 61, "Beyonce", 88, TIMESTAMP("2021-01-03", "11:03:11"), TIMESTAMP("2021-02-20", "11:03:11"), 0),
(3, "Device of music", "How do you listen to music on an daily basis?", "Speaker", 27, "Headphones", 81, "Earbuds", 65, "Other", 3, TIMESTAMP("2019-11-09", "11:03:11"), TIMESTAMP("2021-02-19", "11:03:11"), 0),
(4, "Platform of music", "What app or platform do you use to listen to music?", "Radio", 12, "Spotify", 99, "Apple Music", 57, "Other", 23, TIMESTAMP("2021-02-03", "12:24:14"),TIMESTAMP("2021-02-19", "06:13:12"), 0);

INSERT INTO ALBUM (ALBUMID, ALBUMTITLE, RELEASEDATE, ARTIST, RECORDLABEL, NUMBEROFTRACKS, DATEENTERED, ALBUMIMAGEFILENAMEBIG, ALBUMIMAGEFILENAMESMALL, COSTPRICE,  LISTPRICE, SALEPRICE, PST,GST,HST,AVAILABLE, REMOVALDATE ) values
(1, "My Turn", TIMESTAMP("2020-02-28",  "00:00:00"), "Lil Baby", "Capitol Records", 20, TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 7.99, 17.99, 0, 0.42, 0.45,0.49, 1, null),
(2, "DAMN", TIMESTAMP("2017-04-14",  "00:00:00"), "Kendrick Lamar", "Top Dawg Entertainment", 14, TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",17.99, 22.99, 9.99, 0.42, 0.45,0.49, 1, null),
(3, "THE GOAT", TIMESTAMP("2020-05-15",  "00:00:00"), "Polo G", "Columbia Records", 16, TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",11.23, 17.99, 15.01, 0.42, 0.45,0.49, 1, null),
(4, "Scorpion", TIMESTAMP("2018-06-29",  "00:00:00"), "Drake", "Cash Money", 25, TIMESTAMP("2021-02-13",  "00:00:00"), "scorpion_big.png","scorpion_small.jpg",10.12, 25.00, 0, 0.42, 0.45,0.49, 1, null),
(5, "CASE STUDY 01", TIMESTAMP("2019-06-28",  "00:00:00"), "Daniel Caesar", "Golden Child", 10, TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",11.45, 20.00, 0, 0.5, 0.35,0.25, 1, null),
(6, "Ctrl", TIMESTAMP("2017-06-09",  "00:00:00"), "SZA", "Top Dawg", 14, TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",1.34, 11.99, 7.65, 0.5, 0.35,0.25, 1, null),
(7, "IGOR", TIMESTAMP("2019-05-17",  "00:00:00"), "Tyler", "The Creator, A Boy is a Gun", 12, TIMESTAMP("2021-02-13",  "00:00:00"), "IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg",8.50, 18.0, 0, 0.5, 0.35,0.25, 1, null),
(8, "BALLADS 1", TIMESTAMP("2018-10-26",  "00:00:00"), "Joji", "88rising", 12, TIMESTAMP("2021-02-13",  "00:00:00"), "Ballads-1-Big.jpg","Ballads-1-Small.jpg",6.50, 18.00, 10.16, 0.54, 0.24,0.17, 1, null),
(9,  "Night Visions", TIMESTAMP("2012-09-04",  "00:00:00"), "Imagine Dragons", "Interscope", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "nightVisions-Big.jpeg","nightVisions-Small.jpg",5.45, 13.99, 0, 0.54, 0.24,0.17, 1, null),
(10, "Pray for the Wicked", TIMESTAMP("2018-06-22",  "00:00:00"), "Panic at the Disco", "Fueled by Ramen", 11, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 4.00, 13.50, 0, 0.5, 0.35,0.25, 1, null),
(11, "American Beauty/American Psycho", TIMESTAMP("2015-01-16",  "00:00:00"), "Fallout Boy", "Island", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg",8.35, 17.00, 12.13, 0.5, 0.35,0.25, 1, null),
(12, "Native", TIMESTAMP("2013-03-25",  "00:00:00"), "One Republic", "Mosley", 19, TIMESTAMP("2021-02-13",  "00:00:00"), "nativeBig.jpg","nativeSmall.png",1.33, 22.23, 14.61, 0.54, 0.24,0.17, 1, null),
(13, "Endgame", TIMESTAMP("2011-03-25",  "00:00:00"), "Rise Against", "DGC", 13, TIMESTAMP("2021-02-13",  "00:00:00"), "endGame-Big.jpg","endGame-small.jpg",9.11, 20.21, 0, 0.54, 0.24,0.17, 1, null),
(14, "Artpop", TIMESTAMP("2013-11-06",  "00:00:00"), "Lady Gaga", "Streamline", 14, TIMESTAMP("2021-02-13",  "00:00:00"), "artPop-Big.jpg","artPop-Small.jpg",7.93, 13.12, 0, 0.66, 0.12,0.27, 1, null),
(15, "Teenage Dream", TIMESTAMP("2010-08-24",  "00:00:00"), "Katy Perry" ,"Capitol", 12, TIMESTAMP("2021-02-13",  "00:00:00"), "Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg",10.00, 17.00, 0, 0.66, 0.12,0.27, 1, null),
(16, "Pink Friday: Roman Reloaded", TIMESTAMP("2012-04-02",  "00:00:00"), "Nicki Minaj", "Young Money", 23, TIMESTAMP("2021-02-13",  "00:00:00"), "Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg",9.31, 20.00, 0, 0.66, 0.12,0.27, 1, null),
(17, "Laundry Service", TIMESTAMP("2001-11-13",  "00:00:00"), "Shakira", "Epic", 13, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",7.99, 17.99, 13.99, 0.66, 0.12,0.27, 1, null),
(18, "All That We Have Now", TIMESTAMP("2012-08-08",  "00:00:00"),"Fear, and Loathing in Las Vegas", "VAP", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "all-we-have-now-Big.jpg","all-we-have-now-Small.jpg",9.05, 17.95, 0, 0.66, 0.12,0.27, 1, null),
(19, "the WORLD Ruler", TIMESTAMP("2007-02-27",  "00:00:00"), "NIGHTMARE", "VAP", 13, TIMESTAMP("2021-02-13",  "00:00:00"), "the-world-ruler-big.jpg","the-World-Rule-Small.jpg",2.99, 20.99, 0, 0.45, 0.32,0.03, 1, null),
(20, "AREA Z", TIMESTAMP("2016-06-29",  "00:00:00"), "JAM Project", "Lantis", 15, TIMESTAMP("2021-02-13",  "00:00:00"), "areaz-Big.jpeg","area-z-small.jpg",10.00, 18.99, 0, 0.45, 0.32,0.03, 1, null),
(21, "Fixion", TIMESTAMP("2016-01-05",  "00:00:00"), "THE ORAL CIGARETTES", "A-Sketch", 10, TIMESTAMP("2021-02-13",  "00:00:00"), "Fixion-Big.jpg","Fixion-Small.jpg",4.99, 16.99, 10.59, 0.45, 0.32,0.03,  1, null),
(22, "Shingeki no Kiseki", TIMESTAMP("2017-05-17",  "00:00:00"), "Linked Horizon", "Pony Canyon", 11, TIMESTAMP("2021-02-13",  "00:00:00"), "Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg",5.00, 13.00, 0, 0.45, 0.32,0.03, 1, null);



INSERT INTO MUSIC_TRACK (INVENTORYID, ALBUMID, TRACKTITLE, ARTIST, SONGWRITER, PLAYLENGTH, SELECTIONNUMBER, MUSICCATEGORY, COSTPRICE, LISTPRICE, SALEPRICE, PST,GST,HST,DATEENTERED , ALBUMIMAGEFILENAMEBIG, ALBUMIMAGEFILENAMESMALL,PARTOFALBUM, AVAILABLE, REMOVALDATE) values
(1, 1, "Get Ugly", "Lil Baby", "Dominique Jones", 2.35, 1, "Hip hop", 1.66, 5.00, 4.23, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(2, 1, "Woah", "Lil Baby", "Dominique Jones", 3.03, 5, "Hip hop", 2.75, 7.00, 4.12, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(3, 1, "Emotionally Scarred", "Lil Baby", "Dominique Jones", 7.34, 8, "Hip hop", 1.12, 5.00, 3.23, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(4, 1, "Same Thing", "Lil Baby", "Dominique Jones", 2.42, 7, "Hip hop", 1.66, 2.13, 0, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(5, 1, "All In", "Lil Baby", "Dominique Jones", 2.36, 22, "Hip hop", 1.75, 3.00, 0, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"),"my_turn_big.jpg","my_turn_small.png", 0, 1, null ),
(6, 2, "DNA", "Kendrick Lamar", "Kendrick Duckworth", 3.06, 2, "Hip hop", 1.67, 7.00, 0, 0.15, 0.33,0.13,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(7, 2, "ELEMENT", "Kendrick Lamar", "Kendrick Duckworth", 3.29, 4, "Hip hop", 3.21, 7.50, 0, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(8, 2,  "HUMBLE", "Kendrick Lamar", "Kendrick Duckworth", 2.57, 8, "Hip hop", 2.45, 8.00, 0, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(9, 2,  "FEAR", "Kendrick Lamar", "Kendrick Duckworth", 7.41, 12, "Hip hop", 4.50, 7.00, 0, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"),"damn_big.jpg","damn_small.png", 0, 1, null ),
(10, 2, "DUCKWORTH", "Kendrick Lamar", "Kendrick Duckworth", 4.09, 14, "Hip hop", 3.42, 9.00, 0, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "damn_big.jpg","damn_small.png",0, 1, null ),
(11, 3, "Martin & Gina", "Polo G", "Taurus Bartlett", 2.13, 3, "Hip hop", 2.07, 7.30, 5.23, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",0, 1, null ),
(12, 3, "Beautiful Pain", "Polo G", "Taurus Bartlett", 2.51, 9, "Hip hop", 1.91, 6.50, 0, 0.25, 0.37,0.19,  TIMESTAMP("2021-02-13",  "00:00:00"),"the_goat_big.png","the_goat_small.png", 0, 1, null ),
(13, 3, "DND", "Polo G", "Taurus Bartlett", 3.00, 13, "Hip hop", 5.45, 8.45, 0, 0.25, 0.37,0.19,   TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",0, 1, null ),
(14, 3, "Chinatown", "Polo G", "Taurus Bartlett", 2.53, 14, "Hip hop", 0.78, 7.00, 0, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"), "the_goat_big.png","the_goat_small.png",0, 1, null ),
(15, 3, "Trials & Tribulations", "Polo G", "Taurus Bartlett", 2.57, 15, "Hip hop", 1.32, 7.30, 5.13, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"the_goat_big.png","the_goat_small.png", 0, 1, null ),
(16, 4, "Survival", "Drake", "Aubrey Graham", 2.16, 1, "Hip hop", 4.12, 8.30, 0, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"scorpion_big.png","scorpion_small.jpg", 0, 1, null ),
(17, 4, "Nonstop", "Drake", "Aubrey Graham", 3.58, 1, "Hip hop", 2.23, 5.30, 0, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"), "scorpion_big.png","scorpion_small.jpg",0, 1, null ),
(18, 4, "Mob Ties", "Drake", "Aubrey Graham", 3.25, 1, "Hip hop", 1.32, 5.00, 0, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"scorpion_big.png","scorpion_small.jpg", 0, 1, null ),
(19, 4, "Can't Take a Joke", "Drake", "Aubrey Graham", 2.43, 1,  "Hip hop", 0.78, 6.00, 3.20, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"),"scorpion_big.png","scorpion_small.jpg", 0, 1, null ),
(20, 4, "Nice for What", "Drake", "Aubrey Graham", 3.30, 1,  "Hip hop", 4.09, 7.30, 0, 0.14, 0.23,0.12,    TIMESTAMP("2021-02-13",  "00:00:00"), "scorpion_big.png","scorpion_small.jpg",0, 1, null ),
(21, 5, "ENTROPY", "Daniel Caesar", "Daniel Caesar", 4.21, 1, "R&B", 5.52, 10.00, 0, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",0, 1, null ),
(22, 5, "CYANIDE", "Daniel Caesar", "Daniel Caesar", 3.14, 2, "R&B", 6.92, 9.00, 0, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"),"CaseStudy01-Big.jpg","CaseStudy01-Small.jpg", 0, 1, null ),
(23, 5, "LOVE AGAIN", "Daniel Caesar", "Daniel Caesar", 3.34, 3, "R&B", 8.12, 11.00, 0, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",0, 1, null ),
(24, 5, "OPEN UP", "Daniel Caesar", "Daniel Caesar", 4.26, 5, "R&B", 4.82, 8.00, 0, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"), "CaseStudy01-Big.jpg","CaseStudy01-Small.jpg",0, 1, null ),
(25, 5, "COMPLEXITIES", "Daniel Caesar", "Daniel Caesar", 3.50, 9, "R&B", 2.32, 9.00, 5.40, 0.24, 0.13,0.10,    TIMESTAMP("2021-02-13",  "00:00:00"),"CaseStudy01-Big.jpg","CaseStudy01-Small.jpg", 0, 1, null ),
(26, 6, "Supermodel", "SZA", "SZA", 3.01, 1, "R&B", 7.12, 11.00, 0, 0.11, 0.22,0.09, TIMESTAMP("2021-02-13",  "00:00:00"),"Crtl-Big.jpg","Crtl-Small.jpg", 0, 1, null ),
(27, 6, "Love Galore", "SZA", "SZA", 4.35, 2, "R&B", 6.62, 9.00, 0, 0.11, 0.22,0.09, TIMESTAMP("2021-02-13",  "00:00:00"),"Crtl-Big.jpg","Crtl-Small.jpg", 0, 1, null ),
(28, 6, "Doves in the Wind", "SZA", "SZA", 4.26, 3, "R&B", 2.67, 9.00, 7.40, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",0, 1, null ),
(29, 6, "Drew Barrymore", "SZA", "SZA", 3.51, 4, "R&B", 1.52, 10.00, 7.35, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",0, 1, null ),
(30, 6, "Prom", "SZA", "SZA", 3.16, 5, "R&B", 3.42, 8.00, 0, 0.17, 0.27,0.07,  TIMESTAMP("2021-02-13",  "00:00:00"), "Crtl-Big.jpg","Crtl-Small.jpg",0, 1, null ),
(31, 7, "EARFQUAKE", "Tyler, The Creator", "Tyler, The Creator", 3.10, 2, "R&B", 6.41, 11.00, 0, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"),"IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg", 0, 1, null ),
(32, 7, "I THINK", "Tyler, The Creator", "Tyler, The Creator", 3.32, 3, "R&B", 5.99, 9.00, 0, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"),"IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg", 0, 1, null ),
(33, 7, "RUNNING OUT OF TIME", "Tyler, The Creator", "Tyler, The Creator", 2.57, 5, "R&B", 4.34, 10.00, 0, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"),"IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg", 0, 1, null ),
(34, 7, "A BOY IS A GUN", "Tyler, The Creator", "Tyler, The Creator", 3.30, 7, "R&B", 1.32, 11.00, 0, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg",0, 1, null ),
(35, 7, "ARE WE STILL FRIENDS?", "Tyler, The Creator", "Tyler, The Creator", 4.25, 12, "R&B", 2.76, 9.00, 0, 0.17, 0.27,0.07, TIMESTAMP("2021-02-13",  "00:00:00"), "IGOR-Tyler-Creator-Big.jpg","IGOR-Tyler-Creator-Small.jpg",0, 1, null ),
(36, 8, "SLOW DANCING IN THE DARK", "Joji", "Joji", 2.08, 2, "R&B", 1.02, 10.00, 0, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(37, 8, "TEST DRIVE", "Joji", "Joji", 3.29, 3, "R&B", 2.78, 8.00, 0, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(38, 8, "YEAH RIGHT", "Joji", "Joji", 2.59, 6, "R&B", 3.67, 8.50, 0, 0.06, 0.06,0.06,  TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(39, 8, "NO FUN", "Joji", "Joji", 2.54, 8, "R&B", 1.42, 7.00, 3.63, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(40, 8, "ATTENTION", "Joji", "Joji", 2.48, 1, "R&B", 1.92, 9.00, 0, 0.06, 0.06,0.06,  TIMESTAMP("2021-02-13",  "00:00:00"),"Ballads-1-Big.jpg","Ballads-1-Small.jpg", 0, 1, null ),
(41, 9, "It's Time", "Imagine Dragons", "Ben McKee, Andrew Tolman, Brittany Tolman, Dan Reynolds, Wayne Sermon, Daniel Platzman", 4.00, 3, "Rock", 4.50, 9.00, 0, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(42, 9, "Radioactive", "Imagine Dragons", "Alexander Grant, Ben McKee, Josh Mosser, Daniel Platzman, Dan Reynolds, Wayne Sermon", 3.06, 1, "Rock", 3.50, 10.00, 0, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"), "nightVisions-Big.jpeg","nightVisions-Small.jpg",0, 1, null ),
(43, 9, "Hear Me", "Imagine Dragons", "Ben McKee,Dan Platzman, Dan Reynolds Wayne Sermon", 3.55, 7, "Rock", 1.87, 8.00, 5.64, 0.06, 0.06,0.06, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(44, 9, "Demons", "Imagine Dragons", "Ben McKee, Adam Baachaoui, Daniel Platzman, Dan Reynolds, Wayne Sermon, Alexander Grant, Josh Mosser", 2.57, 4, "Rock", 2.70, 10.10, 0, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(45, 9, "On Top of the World", "Imagine Dragons", "Ben McKee, Dan Platzman, Dan Reynolds, Wayne Sermon, Alexander Grant", 3.12, 5, "Rock", 4.50, 8.00, 0, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"nightVisions-Big.jpeg","nightVisions-Small.jpg", 0, 1, null ),
(46, 10, "Say Amen (Saturday Night)", "Panic at the Disco", "Brendon Urie, Jake Sinclair, Sam Hollander, Lauren Pritchard, Imad Royal, Andrew Greene, Mike Deller, Brian Profilio, Thomas Brenneck, Daniel Foder, Jared Tankel, Nathan Abshire, Suzy Shinn, Tom Peyton, Tobias Wincorn", 3.09, 2, "Rock", 72.50, 8.00, 0, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 0, 1, null ),
(47, 10, "High Hopes", "Panic at the Disco", "Brendon Urie, Jake Sinclair, Jenny Owen Youngs, Lauren Pritchard, Sam Hollander, William Lobban-Bean, Jonas Jeberg, Taylor Parks, Ilsey Juber", 3.10, 4, "Rock", 4.50, 10.00, 0, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 0, 1, null ),
(48, 10, "Hey Look Ma, I Made It", "Panic at the Disco", "Brendon Urie, Dillon Francis, Michael Angelakos, Sam Hollander, Jake Sinclair, Morgan Kibby", 2.49, 3, "Rock", 3.02, 10.01, 5.82, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"prayToTheWicked-Big.jpg","prayToTheWicked-small.jpg", 0, 1, null ),
(49, 11, "American Beauty/American Psycho", "Fallout Boy", "Pete Wentz, Patrick Stump, Joseph Trohman, Andy Hurley, Sebastian Akchoté-Bozovic, Nikki Sixx", 3.13,2, "Rock", 1.55, 9.50, 0, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg", 0, 1, null ),
(50, 11, "Centuries", "Fallout Boy", "Michael Fonseca, Raja Kumari, J.R. Rotem, Justin Tranter, Andy Hurley, Patrick Stump, Joe Trohman, Suzanne Vega, Pete Wentz", 3.48,3, "Rock", 4.51, 8.50, 0, 0.13, 0.16,0.26, TIMESTAMP("2021-02-13",  "00:00:00"),"americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg", 0, 1, null ),
(51, 11, "Uma Thurman", "Fallout Boy", "Patrick Stump, Pete Wentz, Joe Trohman, Andy Hurley, Liam O'Donnell, Jarrel Young, Waqaas Hashmi", 3.31,5, "Rock", 1.22, 10.04, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg", 0, 1, null ),
(52, 11, "Irresistible", "Fallout Boy", "Pete Wentz, Patrick Stump, Joe Trohman, Andy Hurley", 3.26,1, "Rock", 1.77, 8.72, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "americanBeauty-Psyco-Big.jpg","americanBeauty-Psyco-Small.jpg",0, 1, null ),
(53, 12, "If I Lose Myself", "One Republic", "Ryan Tedder, Brent Kutzle, Zach Filkins, Benjamin Levin", 4.01,3, "Rock", 3.47, 9.45, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(54, 12, "Counting Stars", "One Republic", "Ryan Tedder", 4.19,1, "Rock", 2.97, 9.99, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(55, 12, "Something I Need", "One Republic", "Ryan Tedder Benny Blanco", 4.01,11, "Rock", 2.43, 10.45, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(56, 12, "Love Runs Out", "One Republic", "Ryan Tedder, Brent Kutzle, Drew Brown, Zach Filkins, Eddie Fisher", 3.44,2, "Rock", 1.77, 8.45, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"nativeBig.jpg","nativeSmall.png", 0, 1, null ),
(57, 12, "I Lived", "One Republic", "Ryan Tedder, Noel Zancanella",   3.55,5, "Rock", 3.56, 9.55, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "nativeBig.jpg","nativeSmall.png",0, 1, null ),
(58, 13, "Help Is on the Way", "Rise Against", "Rise Against", 3.57,2, "Rock", 1.86, 9.30, 3.12, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"endGame-Big.jpg","endGame-small.jpg", 0, 1, null ),
(59, 13, "Make It Stop (September's Children)", "Rise Against", "Rise Against", 3.55,3, "Rock", 1.03, 9.01, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"endGame-Big.jpg","endGame-small.jpg", 0, 1, null ),
(60, 13, "Satellite", "Rise Against", "Rise Against", 3.58,5, "Rock", 5.66, 9.20, 0, 0.07, 0.27,0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"endGame-Big.jpg","endGame-small.jpg", 0, 1, null ),
(61, 14, "Applause", "Lady Gaga", "Stefani Germanotta“, Paul “DJ White Shadow“ Blair, Dino Zisis, Nick Monson, Martin Bresso, Nicolas Mercier, Julien Arias, William Grigahcine", 3.32,15, "Pop", 1.66, 6.50, 3.13, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"artPop-Big.jpg","artPop-Small.jpg", 0, 1, null ),
(62, 14, "Do What U Want", "Lady Gaga", "Stefani Germanotta, Paul Blair, Robert Kelly, Martin Bresso, William Grigahcine", 3.47,7, "Pop", 5.33, 10.22, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"artPop-Big.jpg","artPop-Small.jpg", 0, 1, null ),
(63, 14, "G.U.Y.", "Lady Gaga", "Stefani Germanotta, Anton Zaslavski", 3.52,3, "Pop", 7.22, 10.11, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"artPop-Big.jpg","artPop-Small.jpg", 0, 1, null ),
(64, 15, "California Gurls", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Benjamin Levin, Bonnie McKee, Calvin Broadus, Brian Wilson, Mike Love", 3.56,3, "Pop", 1.25, 7.50, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"), "Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg",0, 1, null ),
(65, 15, "Teenage Dream", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Benjamin Levin, Bonnie McKee", 3.47,1, "Pop", 2.75, 7.80, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"), "Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg",0, 1, null ),
(66, 15, "Firework", "Katy Perry", "Katy Perry, Mikkel S. Eriksen, Tor Erik Hermansen, Sandy Wilhelm, Ester Dean", 3.48,4, "Pop", 2.40, 7.50, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg", 0, 1, null ),
(67, 15, "E.T.", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Joshua Coleman, Kanye West", 3.51,8, "Pop", 1.30, 7.35, 4.11, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"), "Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg",0, 1, null ),
(68, 15, "Last Friday Night (T.G.I.F.)", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin, Bonnie McKee", 3.50,2, "Pop", 3.60, 7.65, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg", 0, 1, null ),
(69, 15, "The One That Got Away", "Katy Perry", "Katy Perry, Lukasz Gottwald, Max Martin", 3.47,7, "Pop", 3.55, 7.75, 0, 0.08, 0.18,0.03, TIMESTAMP("2021-02-13",  "00:00:00"),"Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg", 0, 1, null ),
(70, 15, "Hummingbird Heartbeat", "Katy Perry", "Katy Perry, Christopher “Tricky“ Stewart, Stacy Barthe, Monte Neuble", 3.32,11, "Pop", 2.45, 7.55, 0, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"), "Teenage_Dream_Big.jpg","Teenage_Dream_Small.jpg",0, 1, null ),
(71, 16, "Starships", "Nicki Minaj", "Onika Maraj, Nadir Khayat, Carl Falk, Rami Yacoub, Wayne Hector, Bilal Hajji", 3.30,10, "Pop", 1.45, 6.55, 0, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(72, 16, "Right by My Side", "Nicki Minaj", "Onika Maraj, Warren “Oak“ Felder, Andrew “Pop“ Wansel, Ester Dean, Jameel Roberts, Ronald “Flippa“ Colson",   4.25,8, "Pop", 3.75, 6.95, 0, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(73, 16, "Beez in the Trap", "Nicki Minaj", "Onika Maraj, Maurice Jordan, Tauheed Epps", 4.28,4, "Pop", 1.99, 7.00, 3.97, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"), "Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg",0, 1, null ),
(74, 16, "Pound the Alarm", "Nicki Minaj", "Onika Maraj, RedOne, Carl Falk, Rami Yacoub, Bilal Hajji, Achraf Jannusi", 3.25,11, "Pop", 2.85, 7.00, 0, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(75, 16, "Va Va Voom", "Nicki Minaj", "Onika Maraj, Lukasz Gottwald, Allan Grigg, Max Martin, Henry Walter", 3.03,21, "Pop", 1.95, 7.05, 0, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"),"Pink Friday Roman Reloaded Big.jpg","Pink Friday Roman Reloaded Small.jpg", 0, 1, null ),
(76, 17, "Whenever, Wherever","Shakira", "Shakira, Tim Mitchell, Gloria Estefan", 3.16,3, "Pop", 0.55, 8.00, 0, 0.06, 0.32, 0.37, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",0, 1, null ),
(77, 17, "Underneath Your Clothes", "Shakira", "Shakira, Lester Mendez", 3.44,2, "Pop", 7.85, 8.0, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",0, 1, null ),
(78, 17, "Objection (Tango)", "Shakira", "Shakira", 3.42,1, "Pop", 7.55, 8.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"landryService-Big.jpg","landryService-Small.jpg", 0, 1, null ),
(79, 17, "Te Dejo Madrid", "Shakira", "Shakira, Tim Mitchell, George Noriega", 3.07,8, "Pop", 7.65, 8.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"), "landryService-Big.jpg","landryService-Small.jpg",0, 1, null ),
(80, 17, "Que Me Quedes Tú", "Shakira", "Shakira, Luis Fernando Ochoa", 4.48,10, "Pop", 7.75, 8.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"landryService-Big.jpg","landryService-Small.jpg", 0, 1, null ),
(81, 18, "Shinzou wo Sasageyo", "Linked Horizon", "REVO", 5.41,11, "Anime", 8.75, 10.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(82, 18, "Guren no Yumiya", "Linked Horizon", "REVO", 5.10,4, "Anime", 5.65, 10.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(83, 18, "Saigo no Senka", "Linked Horizon", "REVO", 5.28,5, "Anime", 4.55, 10.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(84, 18, "Jiyuu no Tsubasa", "Linked Horizon", "REVO", 5.31,7, "Anime", 9.45, 10.00, 0, 0.14, 0.34, 0.35, TIMESTAMP("2021-02-13",  "00:00:00"),"all-we-have-now-Big.jpg","all-we-have-now-Small.jpg", 0, 1, null ),
(85, 19, "Kizukeyo Baby", "THE ORAL CIGARETTES", "Takuya Yamanaka", 4.14,1, "Anime", 6.00, 11.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"the-world-ruler-big.jpg","the-World-Rule-Small.jpg", 0, 1, null ),
(86, 19, "Kyouran Hey Kids!!", "THE ORAL CIGARETTES", "Takuya Yamanaka", 4.10,2, "Anime", 7.80, 10.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "the-world-ruler-big.jpg","the-World-Rule-Small.jpg",0, 1, null ),
(87, 19, "A-E-U-I", "THE ORAL CIGARETTES", "Takuya Yamanaka", 3.57,9, "Anime", 9.60, 10.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"the-world-ruler-big.jpg","the-World-Rule-Small.jpg", 0, 1, null ),
(88, 19, "STAY ONE", "THE ORAL CIGARETTES", "Takuya Yamanaka", 3.33,4, "Anime", 9.40, 10.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"the-world-ruler-big.jpg","the-World-Rule-Small.jpg", 0, 1, null ),
(89, 20, "Survive", "JAM Project", "Hironobu Kageyama", 3.46,7, "Anime", 7.45, 8.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"areaz-Big.jpeg","area-z-small.jpg", 0, 1, null ),
(90, 20, "THE HERO !!", "JAM Project", "Hiroshi Kitadani", 4.17,10, "Anime", 7.47, 8.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"areaz-Big.jpeg","area-z-small.jpg", 0, 1, null ),
(91, 20, "WE ARE ONE", "JAM Project", "Masaaki Endo", 6.01,6, "Anime", 7.46, 8.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"),"areaz-Big.jpeg","area-z-small.jpg", 0, 1, null ),
(92, 20, "NAWABARI", "JAM Project", "Hironobu Kageyama", 5.24,2, "Anime", 7.49, 8.00, 0, 0.34, 0.13, 0.01, TIMESTAMP("2021-02-13",  "00:00:00"), "areaz-Big.jpeg","area-z-small.jpg",0, 1, null ),
(93, 21, "the WORLD", "NIGHTMARE", "Jonathan Cain", 4.02,3, "Anime", 5.49, 6.50, 0, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(94, 21, "BOYS BE SUSPISOUS", "NIGHTMARE", "Jonathan Cain", 4.03,1, "Anime", 1.49, 6.50, 0, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(95, 21, "Criminal baby", "NIGHTMARE", "Jonathan Cain", 3.34,4, "Anime", 1.49, 6.50, 0, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(96, 21, "Alice", "NIGHTMARE", "Jonathan Cain", 4.15,5, "Anime", 2.49, 6.50, 0, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Fixion-Big.jpg","Fixion-Small.jpg", 0, 1, null ),
(97, 22, "Just Awake", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.39,6, "Anime", 5.65, 8.00, 0, 0.44, 0.09, 0.11, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null ),
(98, 22, "Crossover", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.12,3, "Anime", 3.60, 8.00, 0, 0.23, 0.19, 0.31, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null ),
(99, 22, "Acceleration", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.08,1, "Anime", 0.43, 8.00, 0, 0.23, 0.19, 0.31, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null ),
(100, 22,"Scream Hard as You Can", "Fear, and Loathing in Las Vegas", "Fear, and Loathing in Las Vegas", 3.56,2, "Anime", 1.11, 8.00, 3.57, 0.23, 0.19, 0.31, TIMESTAMP("2021-02-13",  "00:00:00"),"Shingeki_no_Kiseki-Big.jpg","Shingeki_no_Kiseki-Small.jpg", 0, 1, null );



INSERT INTO CLIENT (CLIENTID, USERNAME, PASSWORD, TITLE, LASTNAME, FIRSTNAME, COMPANYNAME, ADDRESS1, ADDRESS2, CITY, PROVINCE, COUNTRY, POSTALCODE, HOMETELEPHONE, CELLTELEPHONE, EMAIL, GENREOFLASTSEARCH,ISMANAGER,CLIENTACTIVE) VALUES
(1, "edgeLord12", "tttt2", "Rev", "Burle", "Alli", "Wolf Group", "08957 Rutledge Trail", "495 Sheridan Parkway", "Ponoka", "Alberta", "Canada", "T4J", "1468741332", "7241183761", "aburle0@tinyurl.com", "Pop",0,1),
(2, "jdTrinity", "9876RRwwe", "Ms", "Aish", "Quincey", "Sauer“O“Connell and Feeney", "5208 Messerschmidt Plaza", "5044 Canary Point", "Greensboro", "North Carolina", "United States", "27415", "3365404602", "4957162459", "qaish1@fema.gov", "Pop",0,1),
(3, "honourGuy13", "wwww131", "Mrs", "Anyon", "Amara", "Gottlieb, Toy and Ankunding", "668 Oriole Circle", "7 Bay Trail", "Atlanta", "Georgia", "United States", "30316", "4041939405", "7598581468", "aanyon2@army.mil", "Pop",0,1),
(4, "MissTree222", "rrrr3333", "Honorable", "Shellum", "Lucien", "Cummings Group", "81203 Warrior Point", "26 Lyons Circle", "Savannah", "Georgia", "United States", "31422", "4789255309", "5812443576", "lshellum3@thetimes.co.uk", "Pop",0,1),
(5, "445MeAndYou", "ttt23hhhht2", "Rev", "Glasard", "Andy", "Johnson, Medhurst and Huels", "3791 Vidon Place", "2 Mallory Junction", "Baie-Saint-Paul", "Québec", "Canada", "G3Z", "9956132649", "6136927654", "aglasard4@godaddy.com", "Hip hop",0,1),
(6, "SpanishQueen11", "Qerf32fd", "Mr", "Mayberry", "Chic", "McLaughlin, Kemmer and Dietrich", "1431 Delladonna Court", "3939 Cherokee Point", "Casselman", "Ontario", "Canada", "G8A", "9313712912", "3166935628", "cmayberry5@canalblog.com", "Hip hop",0,1),
(7, "MusicPerson49", "rfrffrfrf321", "Rev", "Siviour", "Marcos", "Miller, Veum and Windler", "25 Meadow Vale Point", "2492 Garrison Alley", "Albanel", "Québec", "Canada", "G8M", "5392188528", "9392987138", "msiviour6@dion.ne.jp", "Hip hop",0,1),
(8, "98wHelloThere", "ZZZzzz44", "Dr", "Aslie", "Chase", "Bartoletti Group", "47215 Scoville Trail", "48 Northfield Plaza", "Amarillo", "Texas", "United States", "79176", "2818742780", "1131223695", "caslie7@people.com.cn", "Hip hop",0,1),
(9, "WhoAmI57", "5468fcfcQWQ", "Rev", "Borrowman", "Helen-elizabeth", "Roob-Crist", "63298 Dryden Street", "08 3rd Terrace", "Knoxville", "Tennessee", "United States", "37995", "8655788675", "1432457839", "hborrowman8@php.net", "Rock",0,1),
(10, "IThink291", "99YYdnej", "Mr", "Metham", "Kania", "Gleichner Inc", "9810 Westport Point", "9 Florence Place", "Ajax", "Ontario", "Canada", "L1Z", "8299069589", "4363009404", "kmetham9@ucoz.ru", "Rock",1,1),
(11, "MrHiiii887", "wjbQQ11", "Rev", "Zimmerman", "Marquita", "Kunde Group", "57769 Maywood Parkway", "4 Northland Junction", "Grand Bank", "Newfoundland and Labrador", "Canada", "E8K", "4091189935", "8278455755", "mzimmermana@phoca.cz", "Rock",0,1),
(12, "rrrrrrrrrr77777", "292UInnd", "Ms", "Seathwright", "Arline", "DuBuque, Schumm and Hettinger", "03 Mesta Place", "6 Morning Court", "Saint Petersburg", "Florida", "United States", "33710", "7278361158", "8797428462", "aseathwrightb@feedburner.com", "Rock",0,1),
(13, "EdgeLord38", "UBRhbr93", "Mr", "Brounsell", "Kelley", "Marvin, Russel and Purdy", "91 Rowland Drive", "4544 South Drive", "Kansas City", "Kansas", "United States", "66112", "8161198258", "7665016517", "kbrounsellc@jiathis.com", "Anime",0,1),
(14, "pika", "thrbej11U", "Rev", "Mazonowicz", "Shayne", "Dickens-Jakubowski", "31 Barby Alley", "517 Prairie Rose Road", "Corona", "California", "United States", "92878", "9517400149", "1384334954", "smazonowiczd@ezinearticles.com", "Anime",0,1),
(15, "pikachu025", "hdjefbQ11", "Mr", "Drohun", "Tedie", "Stiedemann Inc", "6704 Stuart Road", "22540 Annamark Hill", "Burgeo", "Newfoundland and Labrador", "Canada", "N9A", "8409115038", "2029736271", "tdrohune@jiathis.com", "Anime",0,1),
(16, "IHatePikachu520", "dhdd44Q", "Mrs", "Capon", "See", "Lynch LLC", "99572 Hudson Court", "59 Laurel Lane", "Richmond", "Virginia", "United States", "23285", "8047730443", "9796802414", "scaponf@last.fm", "Anime",1,1),
(17, "IamIronMan88", "jsbf29YY", "Rev", "Kildale", "Francisco", "Mayer LLC", "85 Vera Road", "360 Schiller Terrace", "Maskinongé", "Québec", "Canada", "T7A", "3692271272", "8946119827", "fkildaleg@answers.com", "R&B",1,1),
(18, "Miss23Earth", "wwh283S", "Dr", "Poulett", "Delly", "Pollich, Jacobson and Block", "217 Annamark Point", "71 Lotheville Park", "Parrsboro", "Nova Scotia", "Canada", "L2A", "4471914490", "1529172744", "dpouletth@businesswire.com", "R&B",1,1),
(19, "PizzaMan12", "dddnhd22", "Dr", "Sacker", "Belle", "Schulist-Blanda", "6 Huxley Hill", "58 Bowman Avenue", "Saint Louis","Missouri", "United States", "63136", "3147308600", "3817116947", "bsackeri@europa.eu", "R&B",1,1),
(20, "One1Two2", "0033ththt", "Mr", "Canby", "Grover", "Bergstrom, Schinner and Hagenes", "986 Norway Maple Hill", "0 Clove Center", "Mobile", "Alabama", "United States", "36670", "2519196520", "1782694487", "gcanbyj@creativecommons.org", "R&B",1,1),
(21, "DawsonConsumer", "dawsoncollege", "Mr", "Bob", "Orr", "Dawson", "23 Rue Catherine", "0 Clove Center", "Mobile", "Alabama", "United States", "36670", "2519196520", "1782694487", "cst.send@gmail.com", "R&B", 0, 1),
(22, "DawsonManager", "collegedawson", "Mr", "Bob", "Orr", "Dawson", "23 Rue Catherine", "0 Clove Center", "Mobile", "Alabama", "United States", "36670", "2519196520", "1782694487", "cst.receive@gmail.com", "R&B", 1, 1);


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

INSERT INTO BANNERAD (BANNERADDID, FILENAME, URL, DISPLAYED, PAGEPOSITION) VALUES
(1, "walmartbanner.jpg", "https://www.walmart.com/browse/home/personalized-doormats/4044_133224_9107110_2596420?adid=22222222224428692885&wmlspartner=wmtlabs&wl0=b&wl1=s&wl2=c&wl3=74492020453699&wl4=kwd-74492109399488&wl5=5433&wl6=&wl7=&wl14=walmart&veh=sem&msclkid=dc49902e8f93102a4985f00051104fc4", 0, 0),
(2, "amazonbanner.jpg", "https://www.amazon.com/", 0, 0),
(3, "hmbanner.jpg", "https://www2.hm.com/en_ca/index.html", 1, 1),
(4, "logitechbanner.jpg", "https://www.logitech.com/en-ca", 1, 2),
(5, "disneybanner.jpg", "https://disneyparks.disney.go.com/ca/",0, 0);

INSERT INTO SALE (SALEID, CLIENTID, SALEDATE,SALEREMOVED) VALUES
(1, 1,TIMESTAMP("2020-02-13",  "00:00:00"),0),
(2, 1,TIMESTAMP("2020-02-13",  "00:00:00"),0),
(3, 1,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(4, 2,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(5, 2,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(6, 3,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(7, 3,TIMESTAMP("2020-02-13",  "00:00:00"),0),
(8, 4,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(9, 5,TIMESTAMP("2020-02-13 ",  "00:00:00"),0),
(10, 6,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(11, 6,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(12, 7,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(13, 8,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(14, 9,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(15, 10,TIMESTAMP("2021-02-13", "00:00:00"),0),
(16, 11,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(17, 12,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(18, 13,TIMESTAMP("2020-02-13",  "00:00:00"),0),
(19, 14,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(20, 15,TIMESTAMP("2020-02-13",  "00:00:00"),0),
(21, 16,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(22, 17,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(23, 18,TIMESTAMP("2021-02-13",  "00:00:00"),0),
(24, 19,TIMESTAMP("2020-02-13",  "00:00:00"),0),
(25, 19,TIMESTAMP("2021-02-13",  "00:00:00"),0);


INSERT INTO INVOICEDETAIL (INVOICEID, SALEDATE, SALEID, PROFIT, CURRENTCOST,PRODUCTDOWNLOADED, INVENTORYID,INVOICEDETAILREMOVED) VALUES
(4, TIMESTAMP("2021-02-13",  "00:00:00"),3, 6.45, 10.41,2, 45,0),
(5, TIMESTAMP("2021-02-13",  "00:00:00"),3, 6.48, 10.32,0, 67,0),
(6, TIMESTAMP("2021-02-13",  "00:00:00"),4, 4.52, 8.12,1, 51,0),
(7, TIMESTAMP("2021-02-13",  "00:00:00"),5, 8.25, 12.43,3, 71,0),
(8, TIMESTAMP("2021-02-13",  "00:00:00"),6, 8.61, 12.30,1, 71,0),
(11, TIMESTAMP("2021-02-13",  "00:00:00"),7, 4.97, 9.83,2, 71,0),
(12, TIMESTAMP("2021-02-13",  "00:00:00"),8, 6.80, 11.41,3, 43,0),
(17, TIMESTAMP("2021-02-13",  "00:00:00"),11, 4.45, 9.34,3, 49,0),
(19, TIMESTAMP("2021-02-13",  "00:00:00"),12, 6.85, 10.23,1, 31,0),
(20, TIMESTAMP("2021-02-13",  "00:00:00"),13, 3.75, 7.97,0, 21,0),
(22, TIMESTAMP("2021-02-13",  "00:00:00"),15, 8.76, 11.56,0, 34,0),
(24, TIMESTAMP("2021-02-13",  "00:00:00"),17, 7.63, 10.53,1, 46,0),
(25, TIMESTAMP("2020-02-13",  "00:00:00"),18, 7.41, 10.79,1, 55,0),
(27, TIMESTAMP("2020-02-13",  "00:00:00"),20, 6.12, 10.76, 5,43,0),
(28, TIMESTAMP("2020-02-13",  "00:00:00"),20, 4.65, 8.17,2, 11,0),
(29, TIMESTAMP("2020-02-13",  "00:00:00"),20, 9.45, 12.33,4, 81,0),
(31, TIMESTAMP("2020-02-13",  "00:00:00"),20, 5.98, 9.89,1, 71,0),
(32, TIMESTAMP("2020-02-13",  "00:00:00"),21, 4.11,9.67,0, 71,0),
(35, TIMESTAMP("2020-02-13",  "00:00:00"),24, 6.65, 11.73,3, 71,0);

INSERT INTO INVOICEDETAIL (INVOICEID, SALEDATE, SALEID, PROFIT, CURRENTCOST,PRODUCTDOWNLOADED, ALBUMID,INVOICEDETAILREMOVED) VALUES
(1, TIMESTAMP("2020-02-13",  "00:00:00"),1, 18.49, 23.59,3, 1,0),
(2, TIMESTAMP("2020-02-13",  "00:00:00"),1, 16.46, 21.44,3, 11,0),
(3, TIMESTAMP("2020-02-13",  "00:00:00"),2, 17.69, 22.65,3, 1,0),
(9, TIMESTAMP("2020-02-13",  "00:00:00"),7, 18.14, 23.73,0, 21,0),
(10, TIMESTAMP("2020-02-13",  "00:00:00"),7, 17.45, 21.53,0, 17,0),
(13, TIMESTAMP("2020-02-13",  "00:00:00"),9, 17.11, 21.10,0, 1,0),
(14, TIMESTAMP("2021-02-13",  "00:00:00"),10, 15.97, 20.01,1, 9,0),
(15, TIMESTAMP("2021-02-13",  "00:00:00"),11, 16.93, 21.84,3, 19,0),
(16, TIMESTAMP("2021-02-13",  "00:00:00"),11, 18.65, 22.87,2, 3,0),
(18, TIMESTAMP("2021-02-13",  "00:00:00"),11, 15.47, 20.61,5, 16,0),
(21, TIMESTAMP("2021-02-13",  "00:00:00"),14, 17.11, 21.67,6, 5,0),
(23, TIMESTAMP("2021-02-13",  "00:00:00"),16, 17.25, 21.39,7, 8,0),
(36, TIMESTAMP("2021-02-13",  "00:00:00"),25, 15.45, 19.52,4, 22,0),
(37, TIMESTAMP("2021-02-13",  "00:00:00"),25, 17.45, 21.53,1, 21,0),
(26, TIMESTAMP("2021-02-13",  "00:00:00"),19, 20.11, 25.99,0, 5,0),
(30, TIMESTAMP("2021-02-13",  "00:00:00"),20, 16.56, 22.55,0, 1,0),
(33, TIMESTAMP("2021-02-13",  "00:00:00"),22, 16.43, 19.65,0, 1,0),
(34, TIMESTAMP("2021-02-13",  "00:00:00"),23, 19.45, 24.53,2, 13,0);




INSERT INTO RSS (ID, URL,RSSREMOVED) VALUES
(1,"musicalmose.com/track",0),
(2, "musicalmose.com/album",0),
(3, "musicalmose.com/download",0),
(4, "musicalmose.com/help",0),
(5, "musicalmose.com/review",0);

