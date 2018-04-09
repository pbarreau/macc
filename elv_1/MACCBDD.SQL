#DROP DATABASE IF EXISTS MLR7;

#CREATE DATABASE IF NOT EXISTS MLR7;
#USE MLR7;
# -----------------------------------------------------------------------------
#       TABLE : CLIM
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS CLIM
 (
   ID_CLIM CHAR(32) NOT NULL  ,
   NUM_SALLE CHAR(32) NOT NULL  ,
   NOM_CLIM CHAR(32) NULL  ,
   REF_CLIM VARCHAR(128) NULL  ,
   ADRESSE_MACESP CHAR(32) NULL  ,
   MARQUE CHAR(32) NULL  ,
   REF_TÉLÉ VARCHAR(128) NULL  ,
   LOCALISATION_CLIM CHAR(32) NULL  
   , PRIMARY KEY (ID_CLIM) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE CLIM
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_CLIM_SALLE
     ON CLIM (NUM_SALLE ASC);

# -----------------------------------------------------------------------------
#       TABLE : PROFESSEUR
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS PROFESSEUR
 (
   ID_PROF SMALLINT NOT NULL  ,
   NOM VARCHAR(128) NULL  ,
   PRENOM VARCHAR(128) NULL  ,
   LOGIN VARCHAR(128) NULL  ,
   MDP VARCHAR(128) NULL  
   , PRIMARY KEY (ID_PROF) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : TRAME_IR
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS TRAME_IR
 (
   ID SMALLINT NOT NULL  ,
   ID_CLIM CHAR(32) NOT NULL  ,
   MARCHE CHAR(32) NULL  ,
   ARRET CHAR(32) NULL  ,
   UP CHAR(32) NULL  ,
   DOWN CHAR(32) NULL  
   , PRIMARY KEY (ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE TRAME_IR
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_TRAME_IR_CLIM
     ON TRAME_IR (ID_CLIM ASC);

# -----------------------------------------------------------------------------
#       TABLE : SALLE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS SALLE
 (
   NUM_SALLE CHAR(32) NOT NULL  ,
   TEMPÉRATURE DECIMAL(10,2) NULL  ,
   HYGROMETRIE DECIMAL(10,2) NULL  ,
   DATE_JOUR DATE NULL  ,
   HEURE TIME NULL  
   , PRIMARY KEY (NUM_SALLE) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : FIXER_CONSIGNE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS FIXER_CONSIGNE
 (
   ID_JOUR SMALLINT NOT NULL  ,
   ID_CLIM CHAR(32) NULL  ,
   HORAIRE_EXTINCTION TIME NULL  ,
   TEMPÉ_EXTINCTION DECIMAL(10,2) NULL  
   , PRIMARY KEY (ID_JOUR) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE FIXER_CONSIGNE
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_FIXER_CONSIGNE_CLIM
     ON FIXER_CONSIGNE (ID_CLIM ASC);

# -----------------------------------------------------------------------------
#       TABLE : PLANNING
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS PLANNING
 (
   NUM_SALLE CHAR(32) NOT NULL  ,
   ID_PROF SMALLINT NOT NULL  ,
   JOUR CHAR(32) NULL  ,
   MATIÈRE CHAR(32) NULL  ,
   HEURE CHAR(32) NULL  
   , PRIMARY KEY (NUM_SALLE,ID_PROF) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE PLANNING
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_PLANNING_SALLE
     ON PLANNING (NUM_SALLE ASC);

CREATE  INDEX I_FK_PLANNING_PROFESSEUR
     ON PLANNING (ID_PROF ASC);


# -----------------------------------------------------------------------------
#       CREATION DES REFERENCES DE TABLE
# -----------------------------------------------------------------------------


ALTER TABLE CLIM 
  ADD FOREIGN KEY FK_CLIM_SALLE (NUM_SALLE)
      REFERENCES SALLE (NUM_SALLE) ;


ALTER TABLE TRAME_IR 
  ADD FOREIGN KEY FK_TRAME_IR_CLIM (ID_CLIM)
      REFERENCES CLIM (ID_CLIM) ;


ALTER TABLE FIXER_CONSIGNE 
  ADD FOREIGN KEY FK_FIXER_CONSIGNE_CLIM (ID_CLIM)
      REFERENCES CLIM (ID_CLIM) ;


ALTER TABLE PLANNING 
  ADD FOREIGN KEY FK_PLANNING_SALLE (NUM_SALLE)
      REFERENCES SALLE (NUM_SALLE) ;


ALTER TABLE PLANNING 
  ADD FOREIGN KEY FK_PLANNING_PROFESSEUR (ID_PROF)
      REFERENCES PROFESSEUR (ID_PROF) ;

