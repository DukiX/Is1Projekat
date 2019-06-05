package entiteti;

import java.sql.Date;
import java.sql.Time;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-05-21T11:07:37")
@StaticMetamodel(Alarmi.class)
public class Alarmi_ { 

    public static volatile SingularAttribute<Alarmi, Boolean> aktivan;
    public static volatile SingularAttribute<Alarmi, Date> datumAlarma;
    public static volatile SingularAttribute<Alarmi, Boolean> periodican;
    public static volatile SingularAttribute<Alarmi, Long> id;
    public static volatile SingularAttribute<Alarmi, Time> vremeAlarma;

}