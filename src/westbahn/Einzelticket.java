package westbahn;

import javax.persistence.*;

@Entity
public class Einzelticket extends Ticket {
	
	@Id
	public long getId() {
		return super.ID;
	}
	
	@Enumerated(EnumType.ORDINAL)
	private TicketOption ticketOption;

	public Einzelticket() {}
	public Einzelticket(TicketOption ticketOption, Strecke strecke, Zahlung zahlung) {
		super(strecke, zahlung);
		this.ticketOption = ticketOption;
	}
	
	@Override
	public String print() {
		return "Ticket " +super.ID + " ist ein Einzelticket mit der Option " + ticketOption; 
	}

}
