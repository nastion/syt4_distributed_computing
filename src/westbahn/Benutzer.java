package westbahn;

import java.io.Serializable;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.constraints.Email;


@NamedNativeQueries({
	@NamedNativeQuery(
		name = "getAllReservationsForEMail",
		query = "select * from reservierung left outer join benutzer on reservierung.benutzer = benutzer.ID "
				+ "where benutzer.eMail = :eMail",
        resultClass = Reservierung.class
	),
	@NamedNativeQuery(
		name = "getAllUsersWithMonthTicket",
		query = "select * from benutzer natural join ticket where ticket.DTYPE = 'Zeitkarte' "
				+ "and ticket.typ = 0",
		resultClass = Benutzer.class
	),
	@NamedNativeQuery(
		name = "getAllTicketsWithoutReservation",
		query = "select ticket.DTYPE, ticket.ID, ticket.ticketOption, ticket.gueltigAb, ticket.typ, ticket.strecke_ID, reservierung.ID "
				+ "from reservierung left outer join benutzer on benutzer.ID = benutzer "
				+ "right outer join ticket on ticket.ID = benutzer.tickets_ID "
				+ "left outer join strecke on strecke.ID = ticket.strecke_ID "
				+ "where strecke.ende_ID = :ende and strecke.start_ID = :start having reservierung.ID IS NULL;",
		resultClass = Ticket.class
	)
})


@Entity
public class Benutzer implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ID;

	private String vorName;

	private String nachName;

	@Column(unique = true)
	@Pattern(regexp="/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$/", 
			 message = "This E-Mail must not be wrong!")
	private String eMail;

	private String passwort;

	private String smsNummer;

	private Long verbuchtePraemienMeilen;

	@OneToOne(cascade = CascadeType.ALL)
	private Ticket tickets;
	
	public Benutzer() {
		
	}
	
	public Benutzer(String vorName, String nachName, String eMail) {
		super();
		this.vorName = vorName;
		this.nachName = nachName;
		this.eMail = eMail;
	}



	public Benutzer(String vorName, String nachName, String eMail, String passwort, String smsNummer,
			Long verbuchtePraemienMeilen, Ticket tickets) {
		super();
		this.vorName = vorName;
		this.nachName = nachName;
		this.eMail = eMail;
		this.passwort = passwort;
		this.smsNummer = smsNummer;
		this.verbuchtePraemienMeilen = verbuchtePraemienMeilen;
		this.tickets = tickets;
	}


	public String getUser() {
		if (this.tickets.getClass().equals(westbahn.Zeitkarte.class))
			return this.vorName + " " + this.nachName + " (" + this.eMail + ") hat eine "
					+ ((Zeitkarte) this.tickets).getTyp();
		else return this.getName() + " (" + this.eMail + ") hat ein Einzelticket";
	}

	public String getName() {
		return vorName + " " + nachName;
	}

	public String getEMail() {
		return this.eMail;
	}

}
