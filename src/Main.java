import westbahn.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import org.hibernate.Session;  
import org.hibernate.SessionFactory;  
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;



public class Main {  
	@SuppressWarnings({ "deprecation", "unused", "unchecked" })
	public static void main(String[] args) {  
          
    //creating configuration object  
    AnnotationConfiguration cfg=new AnnotationConfiguration();  
    cfg.configure("hibernate.cfg.xml");//populates the data of the configuration file  
    
    //creating session factory object  
    SessionFactory factory=cfg.buildSessionFactory();
      
    //creating session object  
    Session session=factory.openSession();  
      
    //creating transaction object  
    Transaction t=session.beginTransaction();
    
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
    
    Zug wiener_linien = new Zug(westbhf, hütteldorf, at_time(5, 20), 450, 20, 10);
    Zug szbg_wels = new Zug(salzburg, wels, at_time(12, 12), 500, 0, 30);
    Zug poelten_attnang_zug = new Zug(poelten, attnang, at_time(13, 25), 250, 0, 0);
    
    Zahlung kredit = new Kreditkarte();
    Zahlung maestro = new Maestro();
    Zahlung praemien = new Praemienmeilen();
    
    Ticket wochenkarte_heute = new Zeitkarte(ZeitkartenTyp.WOCHENKARTE, new Date(), wien, kredit);
    Ticket monatskarte_morgen = new Zeitkarte(ZeitkartenTyp.MONATSKARTE, 
    		new Date(2018, 3, Calendar.DAY_OF_MONTH+1), west_wels, maestro);
    
    Benutzer pfuchs = new Benutzer("Peter", "Fuchs", "pfuchs@student.tgm.ac.at", "1234", "1234", 0l, monatskarte_morgen);
    Benutzer astrasser = new Benutzer("Alexander", "Strasser", "astrasser@student.tgm.ac.at", "4321", "14432342", 100l, wochenkarte_heute);
    
    Reservierung salzburg_res = new Reservierung(get_tomorrow(), 15, 150, StatusInfo.ONTIME, szbg_wels, west_wels, astrasser, maestro);
    Reservierung wels_res = new Reservierung(date_plus_days(4), 15, 150, StatusInfo.DELAYED, szbg_wels, west_wels, astrasser, kredit);
    Reservierung poelten_res = new Reservierung(get_tomorrow(), 25, 150, StatusInfo.CANCELLED, poelten_attnang_zug, poelten_attnang, pfuchs, kredit);
    
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
    
    session.persist(pfuchs);
    session.persist(astrasser);
    
    session.persist(salzburg_res);
    session.persist(wels_res);
    session.persist(poelten_res);

    t.commit();//transaction is commited
    
    //a)
    Query query = session.getNamedQuery("getAllReservationsForEMail");
    query.setString("eMail", "astrasser@student.tgm.ac.at");
    List<Reservierung> reservierungen = query.list();
    System.out.println(reservierungen.size());
    for (Reservierung r : reservierungen)
    	System.out.println(r.showReservierung());
    
    //b)
    query = session.getNamedQuery("getAllUsersWithMonthTicket");
    query.setParameter("typ", 0);
    List<Benutzer> l = query.list();
    System.out.println(l.size());
    for (Benutzer str : l) {
    	System.out.println(str.getUser());
    }
    
    //c)
    
    query = session.getNamedQuery("getAllTicketsWithoutReservation");
    query.setParameter("start", 1);
    query.setParameter("ende", 2);
    List<Ticket> tickets = query.list();
    System.out.println(tickets.size());
    for (Ticket tick : tickets)
    	System.out.println(tick.print() + " und hat KEINE Reservierung");
      
    session.close();  
      
    System.out.println("successfully saved");  
      
}

	@SuppressWarnings("deprecation")
	public static Date date_plus_days(int days) {
		Date d = new Date();
		d.setDate(d.getDate()+days);
		return d;
	}
	
	@SuppressWarnings("deprecation")
	public static Date at_time(int hour, int min) {
		Date d = new Date();
		d.setHours(hour);
		d.setMinutes(min);
		return d;
	}
	
	public static Date get_tomorrow() {
		return date_plus_days(1);
	}
}  