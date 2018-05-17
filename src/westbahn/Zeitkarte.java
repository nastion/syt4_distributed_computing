package westbahn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
public class Zeitkarte extends Ticket implements Serializable {
	@Id
	public Long getId() {
		return super.ID;
	}
	
	private Date gueltigAb;

	private ZeitkartenTyp typ;
	
	public Zeitkarte() {}
	
	public Zeitkarte(ZeitkartenTyp typ, Date gueltigAb, Strecke strecke, Zahlung zahlung) {
		super(strecke,zahlung);
		this.typ = typ;
		this.gueltigAb = gueltigAb;
	}

	public ZeitkartenTyp getTyp() {
		return typ;
	}

	@Override
	public String print() {
		return "Ticket mit ID " + super.ID + " ist gültig ab " + gueltigAb.toString() + " und ist eine " + typ;
	}

}
