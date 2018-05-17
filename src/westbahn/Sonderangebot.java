package westbahn;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.AssertTrue;

@Entity
public class Sonderangebot {
	@Id
	@GeneratedValue()
	private Long ID;

	private int kontingent = 999;

	//@Future(message = "The Date may not be in the past")
	private Date startZeit;
	
	@AssertTrue(message = "The Date may not be in the past")
	private boolean dateIsNotInPast;
	
	private int dauer = 12;

	private float preisNachlass = 0.5f;

	@ManyToOne
	private Ticket tickets;

	public Sonderangebot(Date startZeit) {
		this.startZeit = startZeit;
		this.dateIsNotInPast = !this.startZeit.before(new Date());
	}
	
	public Sonderangebot(int kontingent, Date startZeit, int dauer, float preisNachlass, Ticket tickets) {
		super();
		this.kontingent = kontingent;
		this.startZeit = startZeit;
		this.dauer = dauer;
		this.preisNachlass = preisNachlass;
		this.tickets = tickets;
		this.dateIsNotInPast = !this.startZeit.before(new Date());
	}

}
