import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NamedNativeQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import org.junit.runners.MethodSorters;

import westbahn.Bahnhof;
import westbahn.Benutzer;
import westbahn.Einzelticket;
import westbahn.Kreditkarte;
import westbahn.Maestro;
import westbahn.Praemienmeilen;
import westbahn.Reservierung;
import westbahn.Sonderangebot;
import westbahn.StatusInfo;
import westbahn.Strecke;
import westbahn.Ticket;
import westbahn.TicketOption;
import westbahn.Zahlung;
import westbahn.Zeitkarte;
import westbahn.ZeitkartenTyp;
import westbahn.Zug;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Testing {
	private static Session session;
	private static Transaction t;
	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory valid_factory = Validation.buildDefaultValidatorFactory();
		validator = valid_factory.getValidator();
		
	    //creating configuration object  
	    AnnotationConfiguration cfg = new AnnotationConfiguration();  
	    cfg.configure("hibernate.cfg.xml");
	    
	    //creating session factory object  
	    SessionFactory factory = cfg.buildSessionFactory();
	    
	    //creating session object  
	    Testing.session = factory.openSession();  
	      
	    //creating transaction object  
	    Testing.t = Testing.session.beginTransaction();

	    Bahnhof westbhf = new Bahnhof("Wien Westbhf", 100, 100, 100, true);
	    Bahnhof hütteldorf = new Bahnhof("Wien Hütteldorf", 90, 90, 90, false);
	    Bahnhof poelten = new Bahnhof("St. Pölten", 30, 20, 100, true);
	    Bahnhof amstetten = new Bahnhof("Amstetten", 20, 80, 20, false);
	    Bahnhof linz = new Bahnhof("Linz", 100, 80, 0, false);
	    Bahnhof wels = new Bahnhof("Wels", 60, 60, 60, false);
	    Bahnhof attnang = new Bahnhof("Attnang-Puchheim", 70, 80, 90, false);
	    Bahnhof salzburg = new Bahnhof("Salzburg", 120, 200, 150, true);
	    
	    Strecke wien = new Strecke(westbhf, hütteldorf, null);
	    Strecke west_wels = new Strecke(westbhf, salzburg, wels);
	    Strecke west_linz = new Strecke(westbhf, salzburg, linz);
	    Strecke poelten_attnang = new Strecke(poelten, attnang, amstetten);
	    
	    Zug wiener_linien = new Zug(westbhf, hütteldorf, Main.at_time(5, 20), 450, 20, 10);
	    Zug szbg_wels = new Zug(salzburg, wels, Main.at_time(12, 12), 500, 0, 30);
	    Zug poelten_attnang_zug = new Zug(poelten, attnang, Main.at_time(13, 25), 250, 0, 0);
	    
	    Zahlung kredit = new Kreditkarte();
	    Zahlung maestro = new Maestro();
	    Zahlung praemien = new Praemienmeilen();
	    
	    Ticket wochenkarte_heute = new Zeitkarte(ZeitkartenTyp.WOCHENKARTE, new Date(), wien, kredit);
	    Ticket monatskarte_morgen = new Zeitkarte(ZeitkartenTyp.MONATSKARTE, 
	    		Main.get_tomorrow(), west_wels, maestro);
	    Ticket ticket = new Einzelticket(TicketOption.FAHRRAD, west_wels, praemien);
	    
	    Benutzer pfuchs = new Benutzer("Peter", "Fuchs", "pfuchs@student.tgm.ac.at", "1234", "1234", 0l, monatskarte_morgen);
	    Benutzer astrasser = new Benutzer("Alexander", "Strasser", "astrasser@student.tgm.ac.at", "4321", "14432342", 100l, wochenkarte_heute);
	    
	    Reservierung salzburg_res = new Reservierung(Main.get_tomorrow(), 15, 150, StatusInfo.ONTIME, szbg_wels, west_wels, astrasser, maestro);
	    Reservierung wels_res = new Reservierung(Main.date_plus_days(4), 15, 150, StatusInfo.DELAYED, szbg_wels, west_wels, astrasser, kredit);
	    Reservierung poelten_res = new Reservierung(Main.get_tomorrow(), 25, 150, StatusInfo.CANCELLED, poelten_attnang_zug, poelten_attnang, pfuchs, kredit);
	    
	    session.persist(westbhf);
	    session.persist(hütteldorf);
	    session.persist(poelten);
	    session.persist(amstetten);
	    session.persist(linz);
	    session.persist(wels);
	    session.persist(attnang);
	    session.persist(salzburg);
	    
	    session.persist(wien);
	    session.persist(west_wels);
	    session.persist(west_linz);
	    session.persist(poelten_attnang);
	    
	    session.persist(wiener_linien);
	    session.persist(szbg_wels);
	    session.persist(poelten_attnang_zug);

	    session.persist(wochenkarte_heute);
	    session.persist(monatskarte_morgen);
	    session.persist(ticket);
	    
	    session.persist(pfuchs);
	    session.persist(astrasser);
	    
	    session.persist(salzburg_res);
	    session.persist(wels_res);
	    session.persist(poelten_res);

	    t.commit();//transaction is commited
	}
	
	@Test
	void checkPersistency() {
		String name = "Wien Persistency-Bahnhof";
	    Bahnhof testing = new Bahnhof(name, 100, 100, 100, true);
	    session.persist(testing);
	    Query q = session.createQuery("FROM Bahnhof WHERE name like '" + name + "'");
	    List<Bahnhof> l = q.list();
	    for (Bahnhof b : l)
	    	assertTrue(testing.equals(b));
	}
	
	@Test
	void checkPersistency_2() {
		String vorname = "John", nachname = "Doe", email = "john.doe@email.at";
		Benutzer test = new Benutzer(vorname, nachname, email);
		session.persist(test);
		Query q = session.createQuery("FROM Benutzer WHERE vorname like '"+vorname+"' and nachname like '"+nachname+"'");
		List<Benutzer> l = q.list();
		for (Benutzer b: l)
			assertTrue(b.equals(test));
	}
	
	@Test
	void checkPersistency_3() {
		t.begin();
		String name = "Wien Testbahnhof";
	    Bahnhof testbhf = new Bahnhof(name, 100, 100, 100, true);
	    session.persist(testbhf);
	    t.commit();
	    Query q = session.createQuery("FROM Bahnhof WHERE name like '" + name + "'");
	    List<Bahnhof> l = q.list();
		for (Bahnhof b: l)
			assertTrue(b.equals(testbhf));
		testbhf.setKopfBahnhof(false);
		t.begin();
	    session.persist(testbhf);
	    t.commit();

	    q = session.createQuery("FROM Bahnhof WHERE name like '" + name + "'");
	    l = q.list();
	    assertTrue(l.size() == 1);
		for (Bahnhof b: l)
			assertTrue(b.equals(testbhf));
	}
	@Test
	void checkPersistency_4() {
		String name = "Wien Testbahnhof";
	    Bahnhof testbhf = new Bahnhof(name, 100, 100, 100, false);
		t.begin();
	    session.persist(testbhf);
	    t.commit();

	    Query q = session.createQuery("FROM Bahnhof WHERE name like '" + name + "'");
	    List<Bahnhof> l = q.list();
	    assertTrue(l.size() == 2);
	}
	
	/*
	@Test
	void createBahnhof() {
	    Bahnhof testbhf = new Bahnhof("Wien Testbhf", 100, 100, 100, true);
	    assertTrue(testbhf.getClass().equals(Bahnhof.class));
	    assertTrue(testbhf.getName().equals("Wien Testbhf"));
	}
	
	@Test
	void createBahnhofOnlyName() {
		Bahnhof testbhf = new Bahnhof("Wien Testbhf");
		assertTrue(testbhf.getClass().equals(Bahnhof.class));
		assertTrue(testbhf.getName().equals("Wien Testbhf"));
	}
	*/
	
	@Test
	@SuppressWarnings("unchecked")
	void checkBahnhoefe() {
		Query q = session.createSQLQuery("SELECT count(*) FROM bahnhof");
		List<BigInteger> l = q.list();
		for (BigInteger i : l)
			assertTrue(i.intValue()==8 || i.intValue() == 9 || i.intValue() == 10);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void checkStrecken() {
		Query q = session.createSQLQuery("SELECT count(*) FROM strecke;");
		List<BigInteger> l = q.list();
		for (BigInteger i : l)
			assertTrue(i.intValue()==4);
	}

	@Test
	void checkZuege() {
		Query q = session.createSQLQuery("SELECT count(*) FROM zug;");
		List<BigInteger> l = q.list();
		for (BigInteger i : l)
			assertTrue(i.intValue()==3);
	}

	@Test
	void checkTickets() {
		Query q = session.createSQLQuery("SELECT count(*) FROM ticket");
		List<BigInteger> l = q.list();
		for (BigInteger i : l)
			assertTrue(i.intValue()==3);
	}

	@Test
	void checkUser() {
		Query q = session.createSQLQuery("SELECT count(*) FROM benutzer");
		List<BigInteger> l = q.list();
		for (BigInteger i : l)
			assertTrue(i.intValue()==2 || i.intValue() == 3);
	}

	@Test
	void checkReservierungen() {
		Query q = session.createSQLQuery("SELECT count(*) FROM reservierung");
		List<BigInteger> l = q.list();
		for (BigInteger i : l)
			assertTrue(i.intValue()==2 || i.intValue() == 3);
	}
	
	@Test
	void checkNamedQueryA() {
	    Query query = session.getNamedQuery("getAllReservationsForEMail");
	    String email = "astrasser@student.tgm.ac.at";
	    query.setString("eMail", email);
	    List<Reservierung> reservierungen = query.list();
	    assertTrue(reservierungen.size() == 2);
	    for (Reservierung r : reservierungen) {
	    	assertTrue(r.getBenutzer().getEMail().equals(email));
	    	System.out.println(r.showReservierung());
	    }
	}
	
	@Test
	void checkNamedQueryA_2() {
	    Query query = session.getNamedQuery("getAllReservationsForEMail");
	    String email = "pfuchs@student.tgm.ac.at";
	    query.setString("eMail", email);
	    List<Reservierung> reservierungen = query.list();
	    assertTrue(reservierungen.size() == 1);
	    for (Reservierung r : reservierungen) {
	    	assertTrue(r.getBenutzer().getEMail().equals(email));
	    	System.out.println(r.showReservierung());
	    }
	}
	
	@Test
	void checkNamedQueryA_3() {
	    Query query = session.getNamedQuery("getAllReservationsForEMail");
	    String email = "nicht@verfuegbare.email.adresse";
	    query.setString("eMail", email);
	    List<Reservierung> reservierungen = query.list();
	    assertTrue(reservierungen.size() == 0);
	}
	
	@Test
	void checkNamedQueryB() {
		Query query = session.createSQLQuery("SELECT * FROM benutzer").addEntity(Benutzer.class);
		Benutzer fuchs = (Benutzer) query.list().get(0);
		Benutzer strasser = (Benutzer) query.list().get(1);
		query = session.getNamedQuery("getAllUsersWithMonthTicket");
	    List<Benutzer> l = query.list();
	    assertTrue(l.size() == 1);
	    for (Benutzer str : l) {
	    	System.out.println(str.getUser());
	    	assertTrue(str.equals(fuchs));
	    	assertFalse(str.getName().equals(strasser.getName()));
	    }
	}
	
	@Test
	void checkNamedQueryC() {
		Query query = session.getNamedQuery("getAllTicketsWithoutReservation");
	    query.setParameter("start", 1);
	    query.setParameter("ende", 8);
	    List<Ticket> tickets = query.list();
	    assertTrue(tickets.size() == 1);
	    for (Ticket tick : tickets)
	    	System.out.println(tick.print() + " und hat KEINE Reservierung");
	}
	
	@Test
	void checkNamedQueryC_2() {
		t.begin();
		session.createSQLQuery("delete from reservierung where ID = 3;").executeUpdate();
		t.commit();

		Query query = session.getNamedQuery("getAllTicketsWithoutReservation");
	    query.setParameter("start", 1);
	    query.setParameter("ende", 8);
	    List<Ticket> tickets = query.list();
	    assertTrue(tickets.size() == 2);
	    for (Ticket tick : tickets)
	    	System.out.println(tick.print() + " und hat KEINE Reservierung");
	}
	
	@Test
	void checkSameStation() {
		Bahnhof b = new Bahnhof("Wien Testbhf");
		Strecke s = new Strecke(b, b);

		Set<ConstraintViolation<Strecke>> constraintViolations =
			validator.validate( s );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"The starting station must not be the ending station",
			constraintViolations.iterator().next().getMessage()
			);
	}
	
	@Test
	void checkSameStation_2() {
		Bahnhof b = new Bahnhof("Wien Testbhf");
		Bahnhof b2 = new Bahnhof("Wien Testbhf");
		Strecke s = new Strecke(b, b2);

		Set<ConstraintViolation<Strecke>> constraintViolations =
			validator.validate( s );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"The starting station must not be the ending station",
			constraintViolations.iterator().next().getMessage()
			);
	}
	
	@Test
	void checkSameStation_3() {
		Bahnhof b = new Bahnhof("Wien Testbhf");
		Bahnhof b2 = new Bahnhof("Wien Testbhf Neu");
		Strecke s = new Strecke(b, b2);

		Set<ConstraintViolation<Strecke>> constraintViolations =
			validator.validate( s );
		
		assertEquals( 0, constraintViolations.size() );
	}
	
	@Test
	void checkSameStation_4() {
		Bahnhof b = new Bahnhof("Wien Testbhf");
		Zug z = new Zug(b, b);

		Set<ConstraintViolation<Zug>> constraintViolations =
			validator.validate( z );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"The starting station must not be the ending station",
			constraintViolations.iterator().next().getMessage()
			);
	}

	@Test
	void checkSameStation_5() {
		Bahnhof b = new Bahnhof("Wien Testbhf");
		Bahnhof b2 = new Bahnhof("Wien Testbhf");
		Zug z = new Zug(b, b2);

		Set<ConstraintViolation<Zug>> constraintViolations =
			validator.validate( z );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"The starting station must not be the ending station",
			constraintViolations.iterator().next().getMessage()
			);
	}
	
	@Test
	void checkSameStation_6() {
		Bahnhof b = new Bahnhof("Wien Testbhf");
		Bahnhof b2 = new Bahnhof("Salzburg Testbhf");
		Zug z = new Zug(b, b2);

		Set<ConstraintViolation<Zug>> constraintViolations =
			validator.validate( z );
		
		assertEquals( 0, constraintViolations.size() );
	}
	
	@Test
	void checkEMail() {
		Benutzer b = new Benutzer("John", "Testing", "einefalscheEmail.at");
		Set<ConstraintViolation<Benutzer>> constraintViolations =
			validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"This E-Mail must not be wrong!",
			constraintViolations.iterator().next().getMessage()
		);
	}
	
	@Test
	void checkEMail_2() {
		Benutzer b = new Benutzer("John", "Testing", "einefalscheEmail@.at");
		Set<ConstraintViolation<Benutzer>> constraintViolations =
			validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"This E-Mail must not be wrong!",
			constraintViolations.iterator().next().getMessage()
		);
	}

	@Test
	void checkEMail_3() {
		Benutzer b = new Benutzer("John", "Testing", "einefalscheEmail.@ab.at");
		Set<ConstraintViolation<Benutzer>> constraintViolations =
			validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"This E-Mail must not be wrong!",
			constraintViolations.iterator().next().getMessage()
		);
	}

	@Test
	void checkEMail_4() {
		Benutzer b = new Benutzer("John", "Testing", "einefalscheEmail@at.");
		Set<ConstraintViolation<Benutzer>> constraintViolations =
			validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"This E-Mail must not be wrong!",
			constraintViolations.iterator().next().getMessage()
		);
	}
	
	@Test
	void checkDate() {
		Sonderangebot s = new Sonderangebot(new Date(0, 0, 0));
		Set<ConstraintViolation<Sonderangebot>> constraintViolations =
				validator.validate( s );
			
		assertEquals( 1, constraintViolations.size() );
		assertEquals(
			"The Date may not be in the past",
			constraintViolations.iterator().next().getMessage()
		);
	}
	
	/**
	 * This testing method does not work. The Date expects a future Date so the current date is wrong...
	 * Don't know how I should change it especially...
	 */
	@Test
	void checkDate_2() {
		Sonderangebot s = new Sonderangebot(new Date());
		Set<ConstraintViolation<Sonderangebot>> constraintViolations =
				validator.validate( s );
		
		assertEquals( 0, constraintViolations.size() );
	}
	
	@Test
	void checkDate_3() {
		Sonderangebot s = new Sonderangebot(new Date(2019, 11, 30));
		Set<ConstraintViolation<Sonderangebot>> constraintViolations =
				validator.validate( s );
		
		assertEquals( 0, constraintViolations.size() );
	}
	
	@Test
	void checkBahnhofName() {
		Bahnhof b = new Bahnhof("aBcDeFg0123");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 0, constraintViolations.size() );
	}

	@Test
	void checkBahnhofName_1() {
		Bahnhof b = new Bahnhof("abcdefg.");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
	}

	@Test
	void checkBahnhofName_2() {
		Bahnhof b = new Bahnhof("abcdefg@.-asd");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
	}

	@Test
	void checkBahnhofName_3() {
		Bahnhof b = new Bahnhof("abcD 0efg-asdf");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 0, constraintViolations.size() );
	}

	@Test
	void checkBahnhofName_4() {
		Bahnhof b = new Bahnhof("abcdef-Ad.bi");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
	}
	
	@Test
	void checkBahnhofName_5() {
		Bahnhof b = new Bahnhof("a");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
	}

	@Test
	void checkBahnhofName_6() {
		Bahnhof b = new Bahnhof("Lorem ipsum dolor sit amet consetetur sadipscing elitr "
				+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat "
				+ "sed diam voluptua At vero eos et");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 1, constraintViolations.size() );
	}
	
	@Test
	void checkBahnhofName_7() {
		Bahnhof b = new Bahnhof("Lorem, ipsum +dolor sit amet consetetur sadipscing elitr "
				+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat "
				+ "sed diam voluptua At vero eos et");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 2, constraintViolations.size() );
	}

	@Test
	void checkBahnhofName_8() {
		Bahnhof b = new Bahnhof("*");
		Set<ConstraintViolation<Bahnhof>> constraintViolations =
				validator.validate( b );
		
		assertEquals( 2, constraintViolations.size() );
	}
}