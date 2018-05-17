package westbahn;

import javax.persistence.*;

@Embeddable
public interface Zahlung {
	
	public void zahlungDurchfuehren();

}

